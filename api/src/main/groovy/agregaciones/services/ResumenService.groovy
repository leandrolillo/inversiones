package agregaciones.services

import agregaciones.clients.CotizacionesClient
import agregaciones.clients.InversionesClient
import core.csv.CSVParser
import groovy.util.logging.Slf4j
import io.micronaut.http.client.exceptions.HttpClientResponseException

import javax.inject.Inject
import javax.inject.Singleton
import java.text.DecimalFormat
import java.text.NumberFormat




@Singleton
@Slf4j
class ResumenService {

    static final Map COLUMNS = [
            "Fecha"             : { CSVParser.parseDate(it) },
            "Tipo"              : null,
            "Clase"             : null,
            "Ctdad. Cuotapartes": {
                DecimalFormat decimalFormat = NumberFormat.getInstance(new Locale("es", "ar"))
                decimalFormat.setParseBigDecimal(true)
                return decimalFormat.parseObject(it)
            },
            "Importe"           : {
                DecimalFormat decimalFormat = NumberFormat.getCurrencyInstance(new Locale("es", "ar"))
                decimalFormat.setParseBigDecimal(true)
                return decimalFormat.parseObject(it.replace(" ", ""))
            },
            "Certificado"       : null,
            "Sec."              : null
    ]

    @Inject
    InversionesClient inversionesClient

    @Inject
    CotizacionesClient cotizacionesClient

    Map codes = ["Alpha Pesos Plus"         : "AlphaPesosPlus",
                 "Alpha Mega"               : "AlphaMega",
                 "Alpha Mercosur"           : "AlphaMercosur",
                 "Alpha Renta Capital Pesos": "AlphaRentaCapitalPesos",
                 "Alpha Renta Capital"      : "AlphaRentaCapital",
                 "Alpha Renta Cobertura"    : "AlphaRentaCobertura",
                 "Alpha Renta Fija Global"  : "AlphaRentaFijaGlobal",
                 "Alpha Retorno Total"      : "AlphaRetornoTotal"]



    void importCSV(String path, String categoria) {
        try {
            String nombre = org.apache.commons.io.FilenameUtils.getBaseName(path)?.replaceAll("(\\p{Ll})(\\p{Lu})", "\$1 \$2")
            categoria = categoria?.replaceAll("(\\p{Ll})(\\p{Lu})", "\$1 \$2")?.capitalize()

            Map inversionTemplate = [nombre: nombre, categoria: categoria, codigo: codes.get(nombre)]
            log.info("Importing insertig or updating inversion $inversionTemplate")

            Map inversion = inversionesClient.list(inversionTemplate).blockingGet()?.find {
                it
            } ?: inversionesClient.insert(inversionTemplate).blockingGet()

            log.info("Using inversion $inversion")

            CSVParser parser = new CSVParser(path)
            parser?.eachRow { currentLine, tokens ->
                Map row = parser.readRow(COLUMNS, tokens)
                String nombreSerieTemporal = "$nombre ${row.Clase}"
                Date fecha = row.Fecha
                BigDecimal cantidad = row."Ctdad. Cuotapartes" * row.Importe / row.Importe.abs()
                BigDecimal total = row.Importe.abs()
                BigDecimal precioUnitario = (total / cantidad).abs()
                String descripcion = "${row.Tipo} $nombreSerieTemporal"

                if (cantidad != null && precioUnitario != null) {
                    Map cotizacionTemplate = [nombre: nombreSerieTemporal, codigo: inversion.codigo]
                    log.info("Using cotizacion template $cotizacionTemplate")

                    Map cotizacion = cotizacionesClient.list(cotizacionTemplate)?.blockingGet()?.find { it } ?:
                            cotizacionesClient.insert(cotizacionTemplate).blockingGet()

                    log.info("Using effective cotizacion $cotizacion")

                    log.info("Querying /cotizaciones/$cotizacion.id/historicos")
                    Map historico = cotizacionesClient.listHistoricos(cotizacion.id, [fecha: fecha])?.blockingGet()?.find { it }

                    if(!historico) {
                        log.info("inserting into /cotizaciones/$cotizacion.id/historicos")
                        cotizacionesClient.insertHistorico(cotizacion.id, [fecha: fecha, valor: precioUnitario]).blockingGet()
                    }

                    log.info("Using effective historico $historico")

                    Map movimiento = inversionesClient.listMovimientos(inversion.id, [fecha: fecha, descripcion: descripcion]).blockingGet()?.find {
                        it
                    } ?: inversionesClient.insertMovimiento(inversion.id, [fecha: fecha, descripcion: descripcion, cantidad: cantidad]).blockingGet()

                    log.info("Using effective movimiento $movimiento")
                }
            }
        } catch (HttpClientResponseException clientException) {
            log.error("Could not import csv $path: ${clientException.response}", clientException)

        }
    }
}

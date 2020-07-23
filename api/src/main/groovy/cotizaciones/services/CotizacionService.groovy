package cotizaciones.services

import cotizaciones.domain.Cotizacion
import cotizaciones.domain.Historico
import grails.gorm.transactions.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Singleton




@Transactional(readOnly = true)
@Singleton
class CotizacionService {
    private static final Logger log = LoggerFactory.getLogger(CotizacionService.class)

    BigDecimal cotizacionAl(String codigo, Date fecha) {
        fecha = fecha ?: new Date()

        log.debug("Fetching cotizacion de $codigo al $fecha")

        BigDecimal cotizacion = Cotizacion.withSession {
            return Historico.createCriteria().get {
                cotizacion {
                    eq("codigo", codigo)
                }


                lte("fecha", fecha)
                order("fecha", "desc")
                maxResults(1)
            }?.valor
        }

        log.debug("Cotizacion de $codigo al $fecha = $cotizacion")

        return cotizacion
    }
}
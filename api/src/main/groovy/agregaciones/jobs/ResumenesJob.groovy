package agregaciones.jobs

import agregaciones.services.ResumenService
import groovy.util.logging.Slf4j
import io.micronaut.scheduling.annotation.Scheduled

import javax.inject.Inject
import javax.inject.Singleton




@Singleton
@Slf4j
class ResumenesJob {

    @Inject
    ResumenService resumenService

    List inversiones = [
            [nombre     : "Mercado pago",
             categoria  : "Renta Fija",
             precios: [nombre: "Pesos"],
             movimientos: [[    fecha: new Date(2019, 02, 13),
                                cantidad: 10000,
                                precio: 1
                           ]]
            ],
            [nombre     : "Sesocio",
             categoria  : "Economía real",
             precios: [nombre: "Pesos"],
             movimientos: [[    fecha: new Date(2019, 02, 13),
                                cantidad: 30000,
                                precio: 1
                           ]]
            ],
            [nombre     : "Crowdium",
             categoria  : "Inmobiliario",
             precios: [nombre: "Pesos"],
             movimientos: [[    fecha: new Date(2019, 02, 13),
                                cantidad: 30000,
                                precio: 1
                           ]]
            ],
            [nombre     : "Plan 27 Cooperativa Agua y Energía",
             categoria  : "Inmobiliario",
             precios: [nombre: "Pesos"],
             movimientos: [[    fecha: new Date(2019, 01, 23),
                                cantidad: 1,
                                precio: 1
                           ]]
            ]
    ]

    @Scheduled(fixedRate = "3m")
    void run() {
        new File("/Users/leandro/huevadas/projects/grails/inversiones/resumenes").eachFile { File categoria ->
            if(categoria.isDirectory() && !categoria.isHidden()) {
                categoria.eachFile { File resumen ->
                    try {
                        log.info("Importing csv file ${resumen.getName()}")
                        resumenService.importCSV(resumen.getAbsolutePath(), categoria.name)
                    } catch (Throwable throwable) {
                        log.error("Could not import csv file ${resumen.getName()}: $throwable.message", throwable)
                    }

                }
            }
        }

//        SerieTemporal pesos = SerieTemporal.findByNombre("Peso Argentino") ?: new SerieTemporal(nombre: "Peso Argentino").save(failOnError: true)
//        SerieTemporal dolares = SerieTemporal.findByNombre("Dolar Estadounidense") ?: new SerieTemporal(nombre: "Dolar Estadounidense").save(failOnError: true)
//
//        inversiones.each {
//            SerieTemporal serieTemporal = null
//
//            if(it.precios) {
//                serieTemporal = SerieTemporal.findByNombre(it.precios.nombre) ?: new SerieTemporal(nombre: it.precios.nombre).save(failOnError: true)
//                it.precios?.historico?.each { Map historicoAsMap ->
//                    Historico historico = Historico.findByFechaAndSerieTemporal(historicoAsMap.fecha, serieTemporal) ?: new Historico(historicoAsMap + [serieTemporal: serieTemporal]).save(failOnError: true)
//                }
//            }
//            Inversion inversion = Inversion.findByNombreAndCategoria(it.nombre, it.categoria) ?: new Inversion(it).save(failOnError: true)
//
//            it.movimientos?.each { Map movimientoAsMap ->
//                Movimiento movimiento = Movimiento.findByFechaAndInversion(movimientoAsMap.fecha, inversion) ?:
//                        new Movimiento(movimientoAsMap + [inversion: inversion, cotizaciones: serieTemporal]).save(failOnError: true)
//            }
//        }
    }
}

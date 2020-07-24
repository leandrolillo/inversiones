package agregaciones.controllers

import agregaciones.clients.CotizacionesClient
import agregaciones.clients.InversionesClient
import cotizaciones.domain.Cotizacion
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Inject



@Controller("/agregaciones/inversiones")
class AgregacionController {
    private static final Logger log = LoggerFactory.getLogger(AgregacionController.class)

    @Inject
    InversionesClient inversionesClient

    @Inject
    CotizacionesClient cotizacionesClient

    @Get
    Single<List<Map>> list() {
        log.info("Clients are $inversionesClient, $cotizacionesClient")
        return inversionesClient.list(null).observeOn(Schedulers.io()).map({ inversiones ->
            inversiones.each { inversion ->
                inversionesClient?.getSaldoValorizado(inversion.id).
                        subscribe(
                                value -> inversion.saldoValorizado = value,
                                error -> inversion.saldoValorizado = "Could not retrieve property")

                inversionesClient?.getSubscripciones(inversion.id).
                        subscribe(
                            value -> inversion.totalSubscripciones = value,
                            error -> inversion.totalSubscripciones = "Could not retrieve property"
                )

                cotizacionesClient?.cotizacionAl(inversion.codigo, null)?.
                        subscribe(
                                value -> inversion.cotizacion = value,
                                error -> inversion.cotizacion = "Could not retrieve property"
                        )

//                inversionesClient?.getRescates(inversion.id).
//                        subscribe(
//                                value -> inversion.totalRescates = value,
//                                error -> inversion.totalRescates = "Could not retrieve property"
//                        )
                inversion.totalRescates = inversionesClient?.getRescates(inversion.id).blockingGet()
                inversion.rendimiento = inversion.saldoValorizado - inversion.totalRescates - inversion.totalSubscripciones
            }
        })

        /**
         * Using blocking code will cause timeouts every other request to this method.
         */
//        return Single.just(inversionesClient.list(null).observeOn(Schedulers.io())?.blockingGet()?.each {
//                it.saldoValorizado = it.cantidad * cotizacionesClient?.cotizacionAl(it.codigo, null).blockingGet()
//                it.totalSubscripciones = inversionesClient?.getSubscripciones(it.id).blockingGet()
//                it.totalRescates = inversionesClient?.getRescates(it.id).blockingGet()
//            })
    }

    @Get("/{id}")
    Map show(Long id) {

    }

}

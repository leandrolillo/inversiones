package agregaciones.controllers

import agregaciones.clients.CotizacionesClient
import agregaciones.clients.InversionesClient
import cotizaciones.domain.Cotizacion
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.reactivex.Single
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
        return inversionesClient.list(null).map({ inversiones ->
            inversiones.each {
                it.saldoValorizado = inversionesClient?.getSaldoValorizado(it.id)
                it.totalSubscripciones = inversionesClient?.getSubscripciones(it.id)
                it.totalRescates = inversionesClient?.getRescates(it.id)
            }
        })

        /**
         * Using blocking code will cause timeouts every other request to this method.
         */
//        return Single.just(inversionesClient.list(null).blockingGet()?.each {
//                it.saldoValorizado = it.cantidad * cotizacionesClient?.cotizacionAl(it.codigo, null)
//                it.totalSubscripciones = inversionesClient?.getSubscripciones(it.id)
//                it.totalRescates = inversionesClient?.getRescates(it.id)
//            })
    }

    @Get("/{id}")
    Map show(Long id) {

    }

}

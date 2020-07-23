package cotizaciones.controllers

import core.rest.RestfulController
import cotizaciones.domain.Cotizacion
import cotizaciones.services.CotizacionService
import io.micronaut.http.annotation.*
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Inject
import javax.annotation.Nullable




@Controller("/cotizaciones")
class CotizacionController extends RestfulController<Cotizacion> {
    private static final Logger log = LoggerFactory.getLogger(CotizacionController.class)

    @Inject
    CotizacionService cotizacionService



    CotizacionController() {
        super(Cotizacion.class)
    }



    @Get("/{codigo}/valor{?fecha}")
    Single<BigDecimal> cotizacionAl(@PathVariable String codigo, @QueryValue @Nullable Date fecha) {
        log.debug("Fetching cotización de $codigo al $fecha")
        return Single.fromCallable { cotizacionService.cotizacionAl(codigo, fecha) }
    }
}

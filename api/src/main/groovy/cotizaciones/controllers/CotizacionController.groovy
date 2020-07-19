package cotizaciones.controllers

import core.rest.RestfulController
import cotizaciones.domain.Cotizacion
import cotizaciones.services.CotizacionService
import io.micronaut.http.annotation.*

import javax.inject.Inject
import javax.annotation.Nullable




@Controller("/cotizaciones")
class CotizacionController extends RestfulController<Cotizacion>{

    @Inject
    CotizacionService cotizacionService

    CotizacionController() {
        super(Cotizacion.class)
    }

    @Get("/{codigo}/valor{?fecha}")
    BigDecimal cotizacionAl(@PathVariable String codigo, @QueryValue @Nullable Date fecha) {
        return cotizacionService.cotizacionAl(codigo, fecha)
    }
}

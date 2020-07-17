package cotizaciones.controllers

import core.rest.RestfulController
import cotizaciones.domain.Cotizacion
import io.micronaut.http.annotation.*




@Controller("/cotizaciones")
class CotizacionController extends RestfulController<Cotizacion>{
    CotizacionController() {
        super(Cotizacion.class)
    }
}

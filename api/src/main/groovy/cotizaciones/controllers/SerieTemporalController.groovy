package cotizaciones.controllers

import core.rest.RestfulController
import cotizaciones.domain.SerieTemporal
import io.micronaut.http.annotation.*
import io.micronaut.http.*

@Controller("/seriesTemporales")
class SerieTemporalController extends RestfulController<SerieTemporal>{
    SerieTemporalController() {
        super(SerieTemporal.class)
    }
}

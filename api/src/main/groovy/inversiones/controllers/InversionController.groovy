package inversiones.controllers

import core.rest.RestfulController
import inversiones.domain.Inversion
import io.micronaut.http.annotation.*


@Controller("/inversiones")
class InversionController extends RestfulController<Inversion> {
    InversionController() {
        super(Inversion.class)
    }
}



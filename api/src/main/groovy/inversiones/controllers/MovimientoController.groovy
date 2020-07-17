package inversiones.controllers

import core.rest.RestfulController
import inversiones.domain.Movimiento
import io.micronaut.http.HttpRequest
import io.micronaut.http.annotation.Controller




@Controller("/inversiones/{inversionId}/movimientos")
class MovimientoController extends RestfulController<Movimiento> {

    MovimientoController() {
        super(Movimiento.class)
    }



    @Override
    List query(List projections, List constraints, Map pagination) {
        Long inversionId = getPathVariable("inversionId") as Long
        constraints.add([propertyName: "inversion.id", value: inversionId])
        return super.query(projections, constraints, pagination)
    }
}

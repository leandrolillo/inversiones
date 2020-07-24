package inversiones.controllers

import core.rest.exceptions.NotFoundException
import core.rest.RestfulController
import groovy.util.logging.Slf4j
import inversiones.domain.Inversion
import inversiones.domain.Movimiento
import io.micronaut.http.annotation.Controller



@Slf4j
@Controller("/inversiones/{inversionId}/movimientos")
class MovimientoController extends RestfulController<Movimiento> {

    MovimientoController() {
        super(Movimiento.class)
    }



    @Override
    Movimiento deserializeResource(Movimiento initialResource, String json) {
        Long inversionId = getPathVariable("inversionId") as Long
        Movimiento movimiento = super.deserializeResource(initialResource, json)
        movimiento.inversion = Inversion.get(inversionId)

        if(!movimiento.inversion)
            throw new NotFoundException("Could not find inversion $inversionId")

        return movimiento
    }


    @Override
    List query(List projections, List constraints, Map pagination) {
        Long inversionId = getPathVariable("inversionId") as Long

        if(Inversion.countById(inversionId) <= 0) {
            throw new NotFoundException("Could not find inversion $inversionId")
        }

        constraints.add([propertyName: "inversion.id", value: inversionId])
        return super.query(projections, constraints, pagination)
    }
}

package cotizaciones.controllers

import core.rest.RestfulController
import core.rest.exceptions.NotFoundException
import cotizaciones.domain.Cotizacion
import cotizaciones.domain.Historico
import groovy.util.logging.Slf4j
import io.micronaut.http.annotation.Controller



@Slf4j
@Controller("/cotizaciones/{cotizacionId}/historicos")
class HistoricoController extends RestfulController<Historico> {

    HistoricoController() {
        super(Historico.class)
    }

    @Override
    Historico deserializeResource(Historico initialResource, String json) {
        log.info("Deserializing $resource")

        Long cotizacionId = getPathVariable("cotizacionId") as Long
        Historico historico = super.deserializeResource(initialResource, json)
        historico.cotizacion = Cotizacion.get(cotizacionId)

        log.info("historico.cotizacion = $historico.cotizacion")

        if(!historico.cotizacion)
            throw new NotFoundException("Could not find Cotizacion $cotizacionId")

        return historico
    }


    @Override
    List query(List projections, List constraints, Map pagination) {
        Long cotizacionId = getPathVariable("cotizacionId") as Long

        if(Cotizacion.countById(cotizacionId) <= 0) {
            throw new NotFoundException("Could not find Cotizacion $cotizacionId")
        }

        constraints.add([propertyName: "cotizacion.id", value: cotizacionId])
        return super.query(projections, constraints, pagination)
    }
}

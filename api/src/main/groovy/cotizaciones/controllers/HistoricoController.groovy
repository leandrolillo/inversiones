package cotizaciones.controllers

import core.rest.RestfulController
import cotizaciones.domain.Historico
import io.micronaut.http.annotation.Controller




@Controller("/cotizaciones/{id}/historicos")
class HistoricoController extends RestfulController<Historico> {

    HistoricoController() {
        super(Historico.class)
    }
}

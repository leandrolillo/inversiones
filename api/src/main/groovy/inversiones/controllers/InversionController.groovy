package inversiones.controllers

import core.rest.RestfulController
import inversiones.domain.Inversion
import inversiones.services.InversionService
import io.micronaut.http.annotation.*
import io.reactivex.Maybe
import io.reactivex.Single
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Inject




@Controller("/inversiones")
class InversionController extends RestfulController<Inversion> {
    private static final Logger log = LoggerFactory.getLogger(InversionController.class)

    @Inject
    InversionService inversionService



    InversionController() {
        super(Inversion.class)
    }

    @Get("/{id}/saldoValorizado")
    Single<BigDecimal> getSaldoValorizado(@PathVariable Long id) {
        return inversionService.getSaldoValorizado(id)
    }

    @Get("/{id}/subscripciones")
    Single<BigDecimal> getSubscripciones(@PathVariable Long id) {
        return inversionService.getTotalSubscripciones(id)
    }

    @Get("/{id}/rescates")
    Single<BigDecimal> getRescates(@PathVariable Long id) {
        return inversionService.getTotalRescates(id)

    }
}



package inversiones.services

import agregaciones.clients.CotizacionesClient
import inversiones.domain.Inversion
import inversiones.domain.Movimiento
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.annotations.NonNull
import io.reactivex.observers.DisposableSingleObserver
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Inject
import javax.inject.Singleton

import grails.gorm.transactions.Transactional
import org.hibernate.criterion.CriteriaSpecification

@Transactional(readOnly = true)
@Singleton
class InversionService {
    private static final Logger log = LoggerFactory.getLogger(InversionService.class)

    @Inject
    CotizacionesClient cotizacionesClient

    Maybe<BigDecimal> getSaldoValorizado(Long id) {
        Inversion inversion = Inversion.get(id)

        return cotizacionesClient.cotizacionAl(inversion.codigo, null).map({ cotizacion ->
            log.debug("Cotizacion $cotizacion")
            cotizacion * (inversion?.cantidad ?: BigDecimal.ZERO)
        })
    }

    Maybe<BigDecimal> getTotalSubscripciones(Long id) {
        List cotizaciones = Movimiento.createCriteria().list {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            eq("inversion.id", id)
            gte("cantidad", BigDecimal.ZERO)
            projections {
                inversion {
                    property("codigo", "codigo")
                }
                property("cantidad", "cantidad")
                property("fecha", "fecha")
            }
        }?.collect {
            cotizacionesClient.cotizacionAl(it.codigo, it.fecha).map { cotizacion -> (it.cantidad ?: BigDecimal.ZERO) * cotizacion }?.toObservable()
        }

        if(!cotizaciones) {
            return Maybe.just(BigDecimal.ZERO)
        }

        log.info("Cotizaciones $cotizaciones")
        return Observable.merge(cotizaciones)?.
                reduce { accumulator, newValue ->
                    accumulator + newValue
                }
    }

    Maybe<BigDecimal> getTotalRescates(Long id) {
        List cotizaciones = Movimiento.createCriteria().list {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            eq("inversion.id", id)
            lte("cantidad", BigDecimal.ZERO)
            projections {
                inversion {
                    property("codigo", "codigo")
                }
                property("cantidad", "cantidad")
                property("fecha", "fecha")
            }
        }?.collect {
            cotizacionesClient.cotizacionAl(it.codigo, it.fecha).map { cotizacion -> (it.cantidad ?: BigDecimal.ZERO) * cotizacion }?.toObservable()
        }

        if(!cotizaciones) {
            return Maybe.just(BigDecimal.ZERO)
        }

        log.info("Cotizaciones $cotizaciones")
        return Observable.merge(cotizaciones)?.
                reduce { accumulator, newValue ->
                    accumulator + newValue?.abs()
                }
    }
}

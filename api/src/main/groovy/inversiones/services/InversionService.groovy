package inversiones.services

import agregaciones.clients.CotizacionesClient
import inversiones.domain.Inversion
import inversiones.domain.Movimiento
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

    Single<BigDecimal> getSaldoValorizado(Long id) {
        Inversion inversion = Inversion.get(id)

        return cotizacionesClient.cotizacionAl(inversion.codigo, null).map({ cotizacion ->
            log.debug("Cotizacion $cotizacion")
            cotizacion * (inversion?.cantidad ?: BigDecimal.ZERO)
        })
    }

    Single<BigDecimal> getTotalSubscripciones(Long id) {
        return Single.just(Movimiento.createCriteria().list {
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
        }?.sum {
            (it.cantidad ?: BigDecimal.ZERO) * cotizacionesClient.cotizacionAl(it.codigo, it.fecha).blockingGet()
        } ?: BigDecimal.ZERO)
    }

    Single<BigDecimal> getTotalRescates(Long id) {
        return Single.just(Movimiento.createCriteria().list {
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
        }?.sum {
            (it.cantidad?.abs() ?: BigDecimal.ZERO) * cotizacionesClient.cotizacionAl(it.codigo, it.fecha).blockingGet()
        } ?: BigDecimal.ZERO)
    }
}

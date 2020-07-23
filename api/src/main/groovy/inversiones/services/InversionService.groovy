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



    Single<BigDecimal> getSaldoValorizado(Long id) {
        log.debug("Fetching saldo Valorizado for inversion $id")
        Inversion inversion = Inversion.get(id)

        return cotizacionesClient.cotizacionAl(inversion.codigo, null).map({ cotizacion ->
            cotizacion * (inversion?.cantidad ?: BigDecimal.ZERO)
        })
    }



    Single<BigDecimal> getTotalSubscripciones(Long id) {
        List movimientos = Movimiento.createCriteria().list {
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
        }
        log.info("Calculando total subscripciones desde movimientos $movimientos")
        return Observable.fromIterable(movimientos)?.
                flatMap(movimiento -> cotizacionesClient.cotizacionAl(movimiento.codigo, movimiento.fecha).toObservable().map {
                    cotizacion -> (movimiento.cantidad ?: BigDecimal.ZERO) * cotizacion
                }
                ).reduce { accumulator, newValue ->
            accumulator + newValue
        }.toSingle(BigDecimal.ZERO)
    }



    Single<BigDecimal> getTotalRescates(Long id) {
        List movimientos = Movimiento.createCriteria().list {
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
        }

        log.info("Calculando total rescates desde movimientos $movimientos")
        return Observable.fromIterable(movimientos)?.
                flatMap(movimiento -> cotizacionesClient.cotizacionAl(movimiento.codigo, movimiento.fecha).toObservable().map {
                    cotizacion -> (movimiento.cantidad ?: BigDecimal.ZERO) * cotizacion
                }
                ).reduce { accumulator, newValue ->
            accumulator + newValue
        }.toSingle(BigDecimal.ZERO)
    }
}

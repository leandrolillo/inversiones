package cotizaciones.services

import cotizaciones.domain.Cotizacion
import cotizaciones.domain.Historico
import grails.gorm.transactions.Transactional

import javax.inject.Singleton




@Transactional(readOnly = true)
@Singleton
class CotizacionService {

    BigDecimal cotizacionAl(String codigo, Date fecha) {
        fecha = fecha ?: new Date()

        Cotizacion.withSession {
            return Historico.createCriteria().get {
                cotizacion {
                    eq("codigo", codigo)
                }


                lte("fecha", fecha)
                order("fecha", "desc")
                maxResults(1)
            }?.valor
        }
    }
}
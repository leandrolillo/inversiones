package cotizaciones.domain

import grails.gorm.annotation.Entity

@Entity
/**
 * Lleva registro de alguna variable (del dominio de los reales) de interés que tiene un valor que cambia en el tiempo
 */
class SerieTemporal {
    String nombre

    static constraints = {
    }

    List<Historico> getHistoricos() {
        return Historico.findAllBySerieTemporal(this)
    }

    BigDecimal precioAl(Date fecha) {
        if(fecha) {
            return Historico.createCriteria().get {
                eq("serieTemporal.id", id)
                lte("fecha", fecha)
                order("fecha", "desc")
                maxResults(1)
            }?.valor
        }

        return BigDecimal.ZERO
    }
}
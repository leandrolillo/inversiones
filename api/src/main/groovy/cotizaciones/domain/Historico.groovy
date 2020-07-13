package cotizaciones.domain

import grails.gorm.annotation.Entity

@Entity
/**
 * Cotización individual para una fecha dada de alguna variable (del dominio de los reales)
 */
class Historico {
    Date fecha
    BigDecimal valor
    SerieTemporal serieTemporal

    static constraints = {
    }
}

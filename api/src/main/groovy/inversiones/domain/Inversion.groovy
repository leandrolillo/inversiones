package inversiones.domain

import grails.gorm.annotation.Entity

@Entity
class Inversion {
    String nombre
    String categoria
    String codigo

    BigDecimal cantidad

    static constraints = {
        nombre unique: "categoria"
    }

    static mapping = {
        cantidad formula: "(SELECT COALESCE(SUM(m.cantidad), 0) from movimiento m where m.inversion_id = id)"
    }

//    static searchable = {
//        only = ['nombre', 'categoria', 'cantidad']
//    }

//    BigDecimal getPrecioUnitario() {
//        Movimiento ultimoMovimiento = Movimiento.findByInversion(this, [sort: "fecha", order:"desc"])
//
//        BigDecimal precioActual = ultimoMovimiento?.cotizaciones?.precioAl(new Date())
//
//        return (precioActual == null ? BigDecimal.ZERO : precioActual)
//    }
//
//    BigDecimal getSaldoValorizado() {
//        return cantidad * precioUnitario
//
//    }
}
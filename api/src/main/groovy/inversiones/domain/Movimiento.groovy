package inversiones.domain

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import grails.gorm.annotation.Entity

@Entity
/**
 * Movimiento registrado en una fecha dada para alguna inversión:
 *  cantidad de unidades movidas (cuotapartes),
 *  precio unitario (sale de la serie temporal más la fecha) y
 *  precio total.
 *
 */
class Movimiento {
    Date fecha
    BigDecimal cantidad
    String descripcion

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    Inversion inversion

//    BigDecimal precio //precio calculado de valor de cuotaparte de la inversion en la fecha
//    BigDecimal total //total = precio * cantidad

//    static mapping = {
//        precio formula: "select valor from inversion i left join serie_temporal precios on i.precios_id = precios.id left join historico h on h.serie_temporal_id = precios.id where i.id = 6 and h.fecha <= fecha order by h.fecha desc limit 1;"
//        total formula: "cantidad * (select valor from historico where id = precio_id)"
//    }

    static constraints = {
        descripcion nullable: true
    }

    public void setInversion(Inversion inversion) {
        this.inversion = inversion
    }


    @JsonProperty("inversion")
    public void setInversion(Long id) {
        this.inversion = Inversion.get(id)
    }
}

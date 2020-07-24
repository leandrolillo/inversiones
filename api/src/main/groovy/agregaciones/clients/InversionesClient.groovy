package agregaciones.clients

import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client
import io.reactivex.*


import javax.annotation.Nullable

@Client("/inversiones")
interface InversionesClient {

    @Get("/{?queryParams*}")
    Single<List<Map>> list(@QueryValue @Nullable Map queryParams)

    @Post
    Single<Map>insert(@Body Map inversion)

    @Get("/{id}")
    Maybe<Map> show(@PathVariable Long id)

    @Get("/{id}/saldoValorizado")
    Single<BigDecimal> getSaldoValorizado(@PathVariable Long id)

    @Get("/{id}/subscripciones")
    Single<BigDecimal> getSubscripciones(@PathVariable Long id)

    @Get("/{id}/rescates")
    Single<BigDecimal> getRescates(@PathVariable Long id)

    @Get("/{inversionId}/movimientos/{?queryParams*}")
    Maybe<List<Map>> listMovimientos(@PathVariable Long inversionId, @QueryValue @Nullable Map queryParams)

    @Post("/{inversionId}/movimientos")
    Single<Map>insertMovimiento(@PathVariable Long inversionId, @Body Map movimiento)

    @Get("/{inversionId}/movimientos/{id}")
    Maybe<Map> showMovimientos(@PathVariable Long inversionId, @PathVariable Long id)

}

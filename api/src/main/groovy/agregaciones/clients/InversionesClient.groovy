package agregaciones.clients

import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client
import io.reactivex.*


import javax.annotation.Nullable

@Client("/inversiones")
interface InversionesClient {

    @Get
    Single<List<Map>> list(@QueryValue @Nullable Map query)

    @Get("/{id}")
    Maybe<Map> show(@PathVariable Long id)

    @Get("/{id}/saldoValorizado")
    Single<BigDecimal> getSaldoValorizado(@PathVariable Long id)

    @Get("/{id}/subscripciones")
    Single<BigDecimal> getSubscripciones(@PathVariable Long id)

    @Get("/{id}/rescates")
    Single<BigDecimal> getRescates(@PathVariable Long id)

    @Get("/{inversionId}/movimientos")
    Maybe<List<Map>> listMovimientos(@PathVariable Long inversionId, @QueryValue @Nullable Map query)

    @Get("/{inversionId}/movimientos/{id}")
    Maybe<Map> showMovimientos(@PathVariable Long inversionId, @PathVariable Long id)

}

package agregaciones.clients

import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client
import io.reactivex.Maybe
import io.reactivex.Single

import javax.annotation.Nullable


@Client("/cotizaciones")
interface CotizacionesClient {

    @Get()
    Single<List<Map>> list(@QueryValue Map queryParams)

    @Get("/{id}")
    Maybe<Map> show(@PathVariable Long id)

    @Get("/{codigo}/valor{?fecha}")
    Single<BigDecimal> cotizacionAl(@PathVariable codigo, @QueryValue @Nullable Date fecha)

    @Get("/cotizaciones/{cotizacionId}/historico")
    Maybe<List<Map>> listHistoricos(@PathVariable Long cotizacionId, @QueryValue @Nullable Map queryParams)

    @Get("/cotizaciones/{cotizacionId}/historico/{id}")
    Maybe<Map> showHistorico(@PathVariable Long cotizacionId, @PathVariable Long id)


}

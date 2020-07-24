package agregaciones.clients

import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client
import io.reactivex.Maybe
import io.reactivex.Single

import javax.annotation.Nullable


@Client("/cotizaciones")
interface CotizacionesClient {

    @Get("/{?queryParams*}")
    Single<List<Map>> list(@QueryValue @Nullable Map queryParams)

    @Post
    Single<Map>insert(@Body Map cotizacion)


    @Get("/{id}")
    Maybe<Map> show(@PathVariable Long id)

    @Get("/{codigo}/valor{?fecha}")
    Single<BigDecimal> cotizacionAl(@PathVariable codigo, @QueryValue @Nullable Date fecha)

    @Get("/{cotizacionId}/historicos/{?queryParams*}")
    Maybe<List<Map>> listHistoricos(@PathVariable Long cotizacionId, @QueryValue @Nullable Map queryParams)

    @Post("/{cotizacionId}/historicos")
    Single<Map>insertHistorico(@PathVariable Long cotizacionId, @Body Map historico)

    @Get("/cotizaciones/{cotizacionId}/historico/{id}")
    Maybe<Map> showHistorico(@PathVariable Long cotizacionId, @PathVariable Long id)
}

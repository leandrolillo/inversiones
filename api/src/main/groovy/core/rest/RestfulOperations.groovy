package core.rest

import io.micronaut.http.HttpRequest
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put




interface RestfulOperations<T> {
    @Get("/")
    List<T> list(Map query)

    @Get("/{id}")
    T show(Long id)

    @Post
    T insert(@Body String instanceJson)

    @Put("/{id}")
    T update(Long id, @Body String instanceJson)

    @Delete("/{id}")
    T delete(Long id)
}

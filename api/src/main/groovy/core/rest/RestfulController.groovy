package core.rest


import groovy.transform.CompileStatic
import io.micronaut.http.annotation.*
import io.micronaut.http.*

import grails.gorm.transactions.Transactional

@Transactional
class RestfulController<T> {

    Class<T> resource

    RestfulController(Class<T> resource) {
        this.resource = resource
    }


    @Get("/")
    HttpResponse index() {
        return HttpResponse.ok(resource.list())
    }



    @Get("/{id}")
    HttpResponse show(Long id) {
        T instance = resource.get(id)

        if(!instance) {
            return HttpResponse.notFound()
        }

        return HttpResponse.ok(instance)
    }



    @Post
    HttpResponse insert(@Body T instance) {
        return HttpResponse.created(instance.save(failOnError: true))
    }



    @Put("/{id}")
    HttpResponse update(Long id, @Body T instance) {
        instance = resource.get(id)

        if(!instance) {
            return HttpResponse.notFound()
        }

        return HttpResponse.ok(instance.save(failOnError: true))

    }



    @Delete("/{id}")
    HttpResponse delete(Long id) {
        T instance = resource.get(id)

        if(!instance) {
            return HttpResponse.notFound()
        }
        Instance

    }
}

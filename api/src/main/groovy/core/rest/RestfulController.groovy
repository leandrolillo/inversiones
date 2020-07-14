package core.rest

import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.http.annotation.*
import io.micronaut.http.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import grails.gorm.transactions.Transactional
import javax.inject.Inject


@Transactional
class RestfulController<T> {
    private static final Logger log = LoggerFactory.getLogger(RestfulController.class)

    @Inject
    ObjectMapper objectMapper

    Class<T> resource

    RestfulController(Class<T> resource) {
        this.resource = resource
    }


    @Get("/")
    HttpResponse index(HttpRequest request) {
        Map pagingOptions = [
                offset : request?.parameters?.getFirst("offset").orElse(null) as Long,
                max: request?.parameters?.getFirst("max").orElse(null) as Long,
                order: request?.parameters?.getFirst("order").orElse(null),
                sort: request?.parameters?.getFirst("sort").orElse(null)
        ]

        return HttpResponse.ok(resource.list(pagingOptions))
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
        instance.validate()
        if(instance.hasErrors()) {
            return HttpResponseFactory.INSTANCE.status(HttpStatus.UNPROCESSABLE_ENTITY, instance.errors)
        }
        return HttpResponse.created(instance.save(failOnError: true))
    }



    @Put("/{id}")
    HttpResponse update(Long id, @Body String instanceJson) {
        T instance = resource.get(id)

        if(!instance) {
            return HttpResponse.notFound()
        }

        instance = objectMapper.readerForUpdating(instance).readValue(instanceJson, resource)

        //TODO: update instance properties

        instance.validate()
        if(instance.hasErrors()) {
            return HttpResponseFactory.INSTANCE.status(HttpStatus.UNPROCESSABLE_ENTITY, instance.errors)
        }

        return HttpResponse.ok(instance.save(failOnError: true))

    }



    @Delete("/{id}")
    HttpResponse delete(Long id) {
        T instance = resource.get(id)

        if(!instance) {
            return HttpResponse.notFound()
        }

        instance.delete(failOnError: true)

        return HttpResponse.ok()
    }
}

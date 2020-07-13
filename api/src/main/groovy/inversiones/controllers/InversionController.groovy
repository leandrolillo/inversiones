package inversiones.controllers

import groovy.transform.CompileStatic
import inversiones.domain.Inversion
import io.micronaut.http.annotation.*
import io.micronaut.http.*

import grails.gorm.transactions.Transactional
import javax.validation.constraints.NotBlank

@CompileStatic
@Controller("/inversiones")
@Transactional
class InversionController {

    @Get("/")
    HttpResponse index() {
        return HttpResponse.ok(Inversion.findAll())
    }

    @Get("/{id}")
    HttpResponse show(String id) {
        Inversion inversion = Inversion.get(id)

        if(inversion) {
            return HttpResponse.ok(inversion)
        }

        return HttpResponse.notFound()
    }

    @Post
    HttpResponse insert(@Body Inversion inversion) {
        return HttpResponse.created(inversion.save(failOnError: true))
    }

    @Put("/{id}")
    HttpResponse update(Integer id, @Body Inversion inversion) {
        return HttpResponse.ok(inversion.save(failOnError: true))

    }

    @Delete("/{id}")
    HttpResponse delete(Integer id) {

    }
}



package inversiones.controllers

import groovy.transform.CompileStatic
import inversiones.domain.Inversion
import io.micronaut.http.annotation.*
import io.micronaut.http.*

import grails.gorm.transactions.Transactional

@CompileStatic
@Controller("/inversiones")
@Transactional
class InversionController {

    @Get("/")
    HttpResponse index() {
        return HttpResponse.ok(Inversion.findAll())
    }

    @Get("/{id}")
    HttpResponse show(Long id) {
        Inversion inversion = Inversion.get(id)
        if(!inversion) {
            return HttpResponse.notFound()
        }

        return HttpResponse.ok(inversion)
    }

    @Post
    HttpResponse insert(@Body Inversion inversion) {
        inversion.validate()
        if(inversion.hasErrors()) {
            return HttpResponse.badRequest(inversion.errors)
        }

        return HttpResponse.created(inversion.save(failOnError: true))
    }

    @Put("/{id}")
    HttpResponse update(Long id, @Body Inversion inversion) {
        inversion = Inversion.get(id)
        if(!inversion) {
            return HttpResponse.notFound()
        }

        return HttpResponse.ok(inversion.save(failOnError: true))

    }

    @Delete("/{id}")
    HttpResponse delete(Long id) {
        Inversion inversion = Inversion.get(id)
        if(!inversion) {
            return HttpResponse.notFound()
        }
        inversion?.delete(saveOnError: true)

        return HttpResponse.ok()
    }
}



package core.rest

import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.http.annotation.*
import io.micronaut.http.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import grails.gorm.transactions.Transactional
import javax.inject.Inject
import org.hibernate.Criteria
import org.hibernate.criterion.*


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
        Map pagination = [
                offset : request?.parameters?.getFirst("offset").orElse(null),
                max: request?.parameters?.getFirst("max").orElse(null),
                order: request?.parameters?.getFirst("order").orElse(null),
                sort: request?.parameters?.getFirst("sort").orElse(null)
        ]

        List projections = request?.parameters?.getFirst("projections").orElse(null)?.split("\\,")?.
                findResults {
                    it?.trim()
                }

        Collection excludeFromConstraints =  (pagination.keySet() +  ["projections"])
        log.debug("excludeFromConstraints $excludeFromConstraints")
        List constraints = request?.parameters?.findAll {
            log.info("${it.key} ${it.key.class} ${it.value}")

            !(it.key in excludeFromConstraints)
        }

        List sort = request?.parameters?.getAll("sort")
        List order = request?.parameters?.getAll("order")

        resource.withSession { session ->
            Criteria criteria = session.createCriteria(resource)

            log.info("Constraints: $constraints")
            for(def constraint : constraints) {
                criteria.add(Restrictions.eq(constraint.key, constraint.value?.first()))
            }

            if(pagination?.offset?.isInteger()) {
                criteria.setFirstResult(pagination?.offset as Integer)
            }

            if(pagination?.max?.isInteger()) {
                criteria.setMaxResults(pagination?.max as Integer)
            }

            for(int index = 0; index < sort?.size() ?: 0; index++) {
                String currentOrder = order[index] ?: "asc"
                switch (currentOrder) {
                    case "asc":
                        criteria.addOrder(Order.asc(sort[index]))
                        break
                    case "desc":
                        criteria.addOrder(Order.desc(sort[index]))
                        break
                }

            }

            log.info("Projections: $projections")

            if(projections) {
                ProjectionList projectionList = Projections.projectionList()
                for(String projection : projections) {
                    projectionList.add(Projections.property(projection))
                }
                criteria.setProjection(projectionList)
            }

            return HttpResponse.ok(criteria.list())
        }



        //return HttpResponse.ok(resource.list(pagingOptions))
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

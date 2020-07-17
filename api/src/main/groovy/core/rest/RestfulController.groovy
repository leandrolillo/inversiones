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
import io.micronaut.http.HttpAttributes
import io.micronaut.http.HttpRequest
import io.micronaut.web.router.UriRouteMatch


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
                order: request?.parameters?.getAll("sort"),
                sort: request?.parameters?.getAll("order")
        ]

        List projections = request?.parameters?.getFirst("projections").orElse(null)?.split("\\,")?.
                findResults {
                    it?.trim()
                }

        Collection excludeFromConstraints =  (pagination.keySet() +  ["projections"])
        List constraints = request?.parameters?.findResults {
            !(it.key in excludeFromConstraints) ? [propertyName: it.key, value:it.value?.first()] : null
        }

        return HttpResponse.ok(query(projections, constraints, pagination, request))
    }

    static String getPathVariable(HttpRequest request, String  pathVariable) {
        Optional<UriRouteMatch> uriRouteMatch = request
                .getAttributes()
                .get(HttpAttributes.ROUTE_MATCH.toString(), UriRouteMatch.class)

        Long inversionId = uriRouteMatch.get().getVariableValues().inversionId as Long
    }

    List query(List projections, List constraints, Map pagination, HttpRequest request) {
        log.debug("Querying $resource with")
        log.debug("constraints: $constraints")
        log.debug("projections: $projections")
        log.debug("pagination: $pagination")
        List result = resource.withSession { session ->
            Criteria criteria = session.createCriteria(resource)

            for(Map constraint : constraints) {
                Criteria effectiveCriteria = criteria
                Class effectiveResource = resource
                String propertyName = constraint.propertyName
                while (propertyName?.contains(".")) {
                    log.debug("Resolving $propertyName of $effectiveResource")
                    String association = propertyName.substring(0, propertyName.indexOf("."))
                    propertyName = propertyName.substring(propertyName.indexOf(".") + 1)
                    effectiveResource = effectiveResource.getDeclaredFields()?.find { it.name == association }?.getType()
                    log.debug("association: $association - propertyName $propertyName of type $effectiveResource")

                    effectiveCriteria = effectiveCriteria.createCriteria(association)
                }
                effectiveCriteria.add(Restrictions.eq(propertyName, constraint.value))
            }

            if(pagination?.offset?.isInteger()) {
                criteria.setFirstResult(pagination?.offset as Integer)
            }

            if(pagination?.max?.isInteger()) {
                criteria.setMaxResults(pagination?.max as Integer)
            }

            for(int index = 0; index < pagination?.sort?.size() ?: 0; index++) {
                String currentOrder = pagination?.order[index] ?: "asc"
                switch (currentOrder) {
                    case "asc":
                        criteria.addOrder(Order.asc(pagination?.sort[index]))
                        break
                    case "desc":
                        criteria.addOrder(Order.desc(pagination?.sort[index]))
                        break
                }

            }

            if(projections) {
                ProjectionList projectionList = Projections.projectionList()
                for(String projection : projections) {
                    projectionList.add(Projections.property(projection), projection)
                }
                criteria.setProjection(projectionList)
                criteria.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP)
            }

            return criteria.list()
        }
        log.debug("result: $result")

        return result
    }



    @Get("/{id}")
    HttpResponse show(Long id, HttpRequest request) {
        T instance = query(null, [[propertyName: "id", value : id]], null, request)
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
    HttpResponse update(Long id, @Body String instanceJson, HttpRequest request) {
        T instance = query(null, [[propertyName: "id", value : id]], null, request)?.find { it }
        if(!instance) {
            return HttpResponse.notFound()
        }

        log.debug("Updating $instance (of class ${instance?.class})")
        instance = objectMapper.readerForUpdating(instance).readValue(instanceJson, resource)

        //TODO: update instance properties

        instance.validate()
        if(instance.hasErrors()) {
            return HttpResponseFactory.INSTANCE.status(HttpStatus.UNPROCESSABLE_ENTITY, instance.errors)
        }

        return HttpResponse.ok(instance.save(failOnError: true))

    }



    @Delete("/{id}")
    HttpResponse delete(Long id, HttpRequest request) {
        T instance = query(null, [[propertyName: "id", value : id]], null, request)?.find { it }
        if(!instance) {
            return HttpResponse.notFound()
        }

        instance.delete(failOnError: true)

        return HttpResponse.ok()
    }
}

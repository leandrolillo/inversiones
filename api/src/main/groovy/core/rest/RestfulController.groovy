package core.rest

import com.fasterxml.jackson.databind.ObjectMapper
import core.rest.exceptions.NotFoundException
import core.rest.exceptions.ValidationJsonError
import core.query.QueryUtil
import io.micronaut.context.MessageSource
import io.micronaut.http.annotation.*
import io.micronaut.http.*
import io.micronaut.http.context.ServerRequestContext
import io.micronaut.http.hateoas.JsonError
import io.micronaut.http.hateoas.Link
import io.micronaut.scheduling.annotation.ExecuteOn
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import grails.gorm.transactions.Transactional
import javax.inject.Inject
import io.micronaut.http.HttpAttributes
import io.micronaut.http.HttpRequest
import io.micronaut.web.router.UriRouteMatch
import org.grails.datastore.mapping.validation.ValidationException


@Transactional(readOnly = true)
class RestfulController<T> implements RestfulOperations<T> {
    private static final Logger log = LoggerFactory.getLogger(RestfulController.class)
    QueryUtil<T> queryUtil

    @Inject
    MessageSource messageSource

    @Inject
    ObjectMapper objectMapper

    Class<T> resource

    RestfulController(Class<T> resource) {
        this.resource = resource
        queryUtil = new QueryUtil<T>(resource)
    }


    @Get("/{?queryParams*}")
    List<T> list(Map queryParams) {
        Map pagination = [
                offset : queryParams?.offset?.find { it },
                max: queryParams?.max?.find { it },
                order: queryParams?.order,
                sort: queryParams?.sort
        ]

        List projections = queryParams?.projections?.split("\\,")?.
                findResults {
                    it?.trim()
                }

        Collection excludeFromConstraints =  (pagination.keySet() +  ["projections"])
        List constraints = queryParams?.findResults {
            !(it.key in excludeFromConstraints) ? [propertyName: it.key, value:it.value?.first()] : null
        }

        return query(projections, constraints, pagination)
    }


    @Get("/{id}")
    //@ExecuteOn("io")
    T show(Long id) {
        log.debug("Fetching $resource $id")
        T instance = queryForResource(id)

        return instance
    }



    @Transactional
    @Post
    T insert(@Body String instanceJson) {
        T instance = deserializeResource(resource.newInstance(), instanceJson)
        return instance.save(failOnError: true)
    }


    @Transactional
    @Put("/{id}")
    T update(Long id, @Body String instanceJson) {
        T instance = queryForResource(id)
        if(!instance) {
            return null
        }

        instance = deserializeResource(instance, instanceJson)
        return instance.save(failOnError: true)

    }


    @Transactional
    @Delete("/{id}")
    T delete(Long id) {
        T instance = queryForResource(id)
        if(!instance) {
            return null
        }

        instance.delete(failOnError: true)

        return instance
    }

    @Error(exception = NotFoundException.class)
    public HttpResponse<JsonError> notFound(NotFoundException notFoundException) {
        return HttpResponse.notFound(new JsonError(notFoundException.message))
    }

    @Error(exception = ValidationException.class)
    public HttpResponse<JsonError> validationException(HttpRequest request, ValidationException validationException)
    {
        ValidationJsonError error = new ValidationJsonError(validationException, messageSource,
                request.getLocale().orElse(Locale.default))
        error.link(Link.SELF, Link.of(request.getUri()))
        return HttpResponse.badRequest(error)
    }

    static String getPathVariable(String pathVariable) {
        HttpRequest request = ServerRequestContext.currentRequest()?.get()
        Optional<UriRouteMatch> uriRouteMatch = request
                .getAttributes()
                .get(HttpAttributes.ROUTE_MATCH.toString(), UriRouteMatch.class)

        return uriRouteMatch.get().getVariableValues().get(pathVariable)
    }

    T deserializeResource(T initialResource, String json) {
        return objectMapper.readerForUpdating(initialResource).readValue(json, resource)
    }

    T queryForResource(Serializable id) {
        return queryUtil.query([], [[propertyName: "id", value : id]], [:])?.find { it }
    }

    List query(List projections, List constraints, Map pagination) {
        return queryUtil.query(projections, constraints, pagination)
    }
}

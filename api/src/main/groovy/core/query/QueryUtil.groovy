package core.query

import org.hibernate.Criteria
import org.hibernate.criterion.*

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class QueryUtil<T> {
    private static final Logger log = LoggerFactory.getLogger(QueryUtil.class)

    Class resource

    QueryUtil(Class<T> resource) {
        this.resource = resource
    }

    List query(List projections, List constraints, Map pagination) {
        projections = projections ?: []
        constraints = constraints ?: []
        pagination = pagination ?: [:]

        log.debug("Querying $resource with constraints: $constraints, projections: $projections and pagination: $pagination")
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
}

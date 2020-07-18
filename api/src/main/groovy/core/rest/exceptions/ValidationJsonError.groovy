package core.rest.exceptions

import io.micronaut.context.MessageSource
import io.micronaut.http.hateoas.JsonError
import org.springframework.validation.FieldError
import org.grails.datastore.mapping.validation.ValidationException

import org.slf4j.Logger
import org.slf4j.LoggerFactory



class ValidationJsonError extends JsonError {
    private static final Logger log = LoggerFactory.getLogger(ValidationJsonError.class)

    List fieldErrors

    ValidationJsonError(ValidationException validationException, MessageSource messageSource, Locale locale) {
        super(validationException.errors?.globalErrors?.toString() ?: "validation errors")
        this.fieldErrors = []

        for(FieldError fieldError: validationException?.errors?.fieldErrors) {
            fieldErrors.add(
                    fieldError.properties?.subMap(["objectName", "field", "rejectedValue"]) +
                    [message: getMessage(messageSource, fieldError, locale)]
            )
        }
    }

    public static String getMessage(MessageSource messageSource, FieldError fieldError, Locale locale) {
        log.debug("Resolving message for $fieldError in $locale")
        MessageSource.MessageContext messageContext = MessageSource.MessageContext.of(locale)
        for(String code : fieldError?.codes) {
            log.debug("Checking errorCode $code")
            Optional<String> message = messageSource.getMessage(code, messageContext)
            if(message.isPresent()) {
                return message.get()
            }
        }

        log.debug("returning default message")
        return fieldError.defaultMessage ?: fieldError.codes?.find { it }
    }
}

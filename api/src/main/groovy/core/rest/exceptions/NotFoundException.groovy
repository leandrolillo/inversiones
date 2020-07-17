package core.rest.exceptions

class NotFoundException extends RuntimeException{
    NotFoundException(String message) {
        super(message)
    }

    NotFoundException(GString message) {
        super(message.toString())
    }
}

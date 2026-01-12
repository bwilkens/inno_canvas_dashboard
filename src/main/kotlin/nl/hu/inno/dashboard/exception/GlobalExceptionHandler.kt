package nl.hu.inno.dashboard.exception

import nl.hu.inno.dashboard.exception.exceptions.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
class RestExceptionHandler {

    @ExceptionHandler(CourseNotFoundException::class)
    fun handleCourseNotFound(ex: CourseNotFoundException, request: WebRequest): ResponseEntity<ExceptionBody> {
        val status = HttpStatus.NOT_FOUND
        val body = ExceptionBody(
            status = status.value(),
            error = status.reasonPhrase,
            message = ex.message,
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity.status(status).body(body)
    }

    @ExceptionHandler(EmptyFileException::class)
    fun handleEmptyFile(ex: EmptyFileException, request: WebRequest): ResponseEntity<ExceptionBody> {
        val status = HttpStatus.BAD_REQUEST
        val body = ExceptionBody(
            status = status.value(),
            error = status.reasonPhrase,
            message = ex.message,
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity.status(status).body(body)
    }

    @ExceptionHandler(FileCannotBeReadException::class)
    fun handleFileCannotBeRead(ex: FileCannotBeReadException, request: WebRequest): ResponseEntity<ExceptionBody> {
        val status = HttpStatus.BAD_REQUEST
        val body = ExceptionBody(
            status = status.value(),
            error = status.reasonPhrase,
            message = ex.message,
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity.status(status).body(body)
    }

    @ExceptionHandler(FileTypeNotSupportedException::class)
    fun handleFileTypeNotSupported(ex: FileTypeNotSupportedException, request: WebRequest): ResponseEntity<ExceptionBody> {
        val status = HttpStatus.UNSUPPORTED_MEDIA_TYPE
        val body = ExceptionBody(
            status = status.value(),
            error = status.reasonPhrase,
            message = ex.message,
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity.status(status).body(body)
    }

    @ExceptionHandler(InvalidParseListException::class)
    fun handleInvalidParseList(ex: InvalidParseListException, request: WebRequest): ResponseEntity<ExceptionBody> {
        val status = HttpStatus.BAD_REQUEST
        val body = ExceptionBody(
            status = status.value(),
            error = status.reasonPhrase,
            message = ex.message,
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity.status(status).body(body)
    }

    @ExceptionHandler(InvalidPathException::class)
    fun handleInvalidPath(ex: InvalidPathException, request: WebRequest): ResponseEntity<ExceptionBody> {
        val status = HttpStatus.NOT_FOUND
        val body = ExceptionBody(
            status = status.value(),
            error = status.reasonPhrase,
            message = ex.message,
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity.status(status).body(body)
    }

    @ExceptionHandler(InvalidPythonEnvironmentException::class)
    fun handleInvalidPythonEnvironment(ex: InvalidPythonEnvironmentException, request: WebRequest): ResponseEntity<ExceptionBody> {
        val status = HttpStatus.BAD_REQUEST
        val body = ExceptionBody(
            status = status.value(),
            error = status.reasonPhrase,
            message = ex.message,
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity.status(status).body(body)
    }

    @ExceptionHandler(InvalidRoleException::class)
    fun handleInvalidRole(ex: InvalidRoleException, request: WebRequest): ResponseEntity<ExceptionBody> {
        val status = HttpStatus.NOT_FOUND
        val body = ExceptionBody(
            status = status.value(),
            error = status.reasonPhrase,
            message = ex.message,
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity.status(status).body(body)
    }

    @ExceptionHandler(PythonGatewayException::class)
    fun handlePythonGateway(ex: PythonGatewayException, request: WebRequest): ResponseEntity<ExceptionBody> {
        val status = HttpStatus.BAD_GATEWAY
        val body = ExceptionBody(
            status = status.value(),
            error = status.reasonPhrase,
            message = ex.message,
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity.status(status).body(body)
    }

    @ExceptionHandler(UserNotAuthorizedException::class)
    fun handleInvalidRole(ex: UserNotAuthorizedException, request: WebRequest): ResponseEntity<ExceptionBody> {
        val status = HttpStatus.FORBIDDEN
        val body = ExceptionBody(
            status = status.value(),
            error = status.reasonPhrase,
            message = ex.message,
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity.status(status).body(body)
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(ex: UserNotFoundException, request: WebRequest): ResponseEntity<ExceptionBody> {
        val status = HttpStatus.NOT_FOUND
        val body = ExceptionBody(
            status = status.value(),
            error = status.reasonPhrase,
            message = ex.message,
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity.status(status).body(body)
    }

    @ExceptionHandler(UserNotInCourseException::class)
    fun handleUserNotInCourse(ex: UserNotInCourseException, request: WebRequest): ResponseEntity<ExceptionBody> {
        val status = HttpStatus.NOT_FOUND
        val body = ExceptionBody(
            status = status.value(),
            error = status.reasonPhrase,
            message = ex.message,
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity.status(status).body(body)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception, request: WebRequest): ResponseEntity<ExceptionBody> {
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        val body = ExceptionBody(
            status = status.value(),
            error = status.reasonPhrase,
            message = ex.message,
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity.status(status).body(body)
    }
}
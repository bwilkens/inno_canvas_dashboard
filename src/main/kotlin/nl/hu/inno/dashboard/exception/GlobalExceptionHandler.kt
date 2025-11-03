package nl.hu.inno.dashboard.exception

import nl.hu.inno.dashboard.dashboard.domain.exception.InvalidParseListException
import nl.hu.inno.dashboard.fileparser.domain.exception.CsvFileCannotBeReadException
import nl.hu.inno.dashboard.fileparser.domain.exception.FileTypeNotSupportedException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
class RestExceptionHandler {

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

    @ExceptionHandler(CsvFileCannotBeReadException::class)
    fun handleCsvFileCannotBeRead(ex: CsvFileCannotBeReadException, request: WebRequest): ResponseEntity<ExceptionBody> {
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
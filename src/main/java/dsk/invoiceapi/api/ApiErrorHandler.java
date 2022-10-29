package dsk.invoiceapi.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;

/**
 * Handle REST API exceptions (attached to all defined controllers).
 */
@ControllerAdvice
public class ApiErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {IllegalArgumentException.class, MultipartException.class, IOException.class})
    protected ResponseEntity<Object> handleError(RuntimeException exception, WebRequest request) {
        // Log the error (including stacktrace).
        logger.error("API error occurred", exception);

        // Return appropriate error message to the user (without stacktrace).
        HttpStatus respStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String respBody = "Error: " + exception.getMessage();
        if ((exception instanceof IllegalArgumentException)
                || (exception instanceof MultipartException)) {
            respStatus = HttpStatus.BAD_REQUEST;
            respBody = "Error: " + "invalid request parameters";
        }

        // Prepare the response headers.
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return handleExceptionInternal(exception, respBody, headers, respStatus, request);
    }
}

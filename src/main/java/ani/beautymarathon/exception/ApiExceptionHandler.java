package ani.beautymarathon.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundEntity(EntityNotFoundException ex) {
        final HttpStatus notFound = HttpStatus.NOT_FOUND;
        final ApiError apiError = new ApiError(
                notFound,
                ex.getMessage()
        );
        log.error("Error: ", ex);
        return constructApiErrorWithHttpStatus(apiError);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        final HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        final ApiError apiError = new ApiError(
                badRequest,
                ex.getMessage()
        );
        log.error("Error: ", ex);
        return constructApiErrorWithHttpStatus(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        final var errs = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> "Field " + fieldError.getField() + " " + fieldError.getDefaultMessage())
                .collect(Collectors.toSet());

        final var apiError = new ApiError(HttpStatus.BAD_REQUEST, "Request validation failed: " + errs);
        log.error("Error: ", ex);
        return constructApiErrorWithHttpStatus(apiError);
    }

    @ExceptionHandler(MoClosedException.class)
    public ResponseEntity<ApiError> handleMoClosed(MoClosedException ex) {
        final HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        final ApiError apiError = new ApiError(
                badRequest,
                ex.getMessage()
        );
        log.error("Error: ", ex);
        return constructApiErrorWithHttpStatus(apiError);
    }

    @ExceptionHandler(WkMeasurementClosedException.class)
    public ResponseEntity<ApiError> handleWkMeasurementClosed(WkMeasurementClosedException ex) {
        final HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        final ApiError apiError = new ApiError(
                badRequest,
                ex.getMessage()
        );
        log.error("Error: ", ex);
        return constructApiErrorWithHttpStatus(apiError);
    }

    @ExceptionHandler(UserDeletedException.class)
    public ResponseEntity<ApiError> handleUserDeleted(UserDeletedException ex) {
        final HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        final ApiError apiError = new ApiError(
                badRequest,
                ex.getMessage()
        );
        log.error("Error: ", ex);
        return constructApiErrorWithHttpStatus(apiError);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleEmailExists(EmailAlreadyExistsException ex) {
        final HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        final ApiError apiError = new ApiError(
                badRequest,
                ex.getMessage()
        );
        log.error("Error: ", ex);
        return constructApiErrorWithHttpStatus(apiError);
    }

    private ResponseEntity<ApiError> constructApiErrorWithHttpStatus(ApiError apiError) {
        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(apiError, headers, apiError.status());
    }
}
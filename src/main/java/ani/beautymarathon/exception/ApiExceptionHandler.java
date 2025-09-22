package ani.beautymarathon.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundEntity(EntityNotFoundException ex) {
        HttpStatus notFound = HttpStatus.NOT_FOUND;
        ApiError apiError = new ApiError(
                notFound,
                ex.getMessage()
        );
        return constructApiErrorWithHttpStatus(apiError);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ApiError apiError = new ApiError(
                badRequest,
                ex.getMessage()
        );
        return constructApiErrorWithHttpStatus(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        var errs = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> "Field " + fieldError.getField() + " " + fieldError.getDefaultMessage())
                .collect(Collectors.toSet());

        final var apiError = new ApiError(HttpStatus.BAD_REQUEST, "Request validation failed: " + errs);
        return constructApiErrorWithHttpStatus(apiError);
    }

    @ExceptionHandler(MoClosedException.class)
    public ResponseEntity<ApiError> handleMoClosed(MoClosedException ex) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ApiError apiError = new ApiError(
                badRequest,
                ex.getMessage()
        );
        return constructApiErrorWithHttpStatus(apiError);
    }

    @ExceptionHandler(WkMeasurementClosedException.class)
    public ResponseEntity<ApiError> handleWkMeasurementClosed(WkMeasurementClosedException ex) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ApiError apiError = new ApiError(
                badRequest,
                ex.getMessage()
        );
        return constructApiErrorWithHttpStatus(apiError);
    }

    @ExceptionHandler(UserDeletedException.class)
    public ResponseEntity<ApiError> handleUserDeleted(UserDeletedException ex) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ApiError apiError = new ApiError(
                badRequest,
                ex.getMessage()
        );
        return constructApiErrorWithHttpStatus(apiError);
    }

    private ResponseEntity<ApiError> constructApiErrorWithHttpStatus(ApiError apiError) {
        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(apiError, headers, apiError.status());
    }

}

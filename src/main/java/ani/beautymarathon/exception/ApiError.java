package ani.beautymarathon.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        HttpStatus status,
        String message,
        String hint
) {
    public ApiError(HttpStatus status, String message){
        this(status, message, null);
    }
}
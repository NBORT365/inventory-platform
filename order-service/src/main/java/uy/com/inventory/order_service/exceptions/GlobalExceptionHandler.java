package uy.com.inventory.order_service.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uy.com.inventory.common.model.ErrorResponse;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({ MethodArgumentNotValidException.class })
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        var msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst().orElse("validation error");
        return ResponseEntity.status(400).body(
                new ErrorResponse(Instant.now(), 400, "Bad Request", msg, req.getRequestURI(), MDC.get("correlationId"), MDC.get("eventId"))
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadReq(IllegalArgumentException ex, HttpServletRequest req) {
        return ResponseEntity.status(400).body(
                new ErrorResponse(Instant.now(), 400, "Bad Request", ex.getMessage(), req.getRequestURI(), MDC.get("correlationId"), MDC.get("eventId"))
        );
    }
}

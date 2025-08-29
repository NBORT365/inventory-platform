package uy.com.inventory.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ErrorResponse {
    Instant timestamp;
    int status;
    String error;
    String message;
    String path;
    String correlationId;
    String eventId;

}

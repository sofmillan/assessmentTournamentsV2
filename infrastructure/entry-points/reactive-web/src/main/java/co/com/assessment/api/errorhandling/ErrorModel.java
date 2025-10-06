package co.com.assessment.api.errorhandling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ErrorModel {
    private LocalDateTime dateTime;
    private String status;
    private Integer statusCode;
    private List<String> errorMessages;
}

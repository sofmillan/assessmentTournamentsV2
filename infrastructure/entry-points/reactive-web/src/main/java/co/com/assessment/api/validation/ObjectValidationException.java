package co.com.assessment.api.validation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class ObjectValidationException extends  RuntimeException{
    private final List<String> details;

}

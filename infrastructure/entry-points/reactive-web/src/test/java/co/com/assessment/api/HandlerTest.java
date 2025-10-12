package co.com.assessment.api;

import co.com.assessment.api.validation.ObjectValidator;
import co.com.assessment.tokenresolver.JwtResolver;
import co.com.assessment.usecase.tournaments.TournamentsUseCase;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class HandlerTest {
    @Mock
    private ObjectValidator objectValidator;
    @Mock
    private TournamentsUseCase tournamentsUseCase;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private JwtResolver jwtResolver;
}

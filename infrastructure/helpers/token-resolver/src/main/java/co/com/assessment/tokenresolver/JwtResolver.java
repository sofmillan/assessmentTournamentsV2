package co.com.assessment.tokenresolver;

import co.com.assessment.model.tournament.exception.SecurityErrorMessage;
import co.com.assessment.model.tournament.exception.SecurityException;
import co.com.assessment.model.tournament.exception.TechnicalErrorMessage;
import co.com.assessment.model.tournament.exception.TechnicalException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTProcessor;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Component
public class JwtResolver {
    private static final Logger log = LoggerFactory.getLogger(JwtResolver.class);
    private final String issuer;

    private final String jwksUrlString;

    private final JWTProcessor<SecurityContext> jwtProcessor;

    public JwtResolver(
            @Value("${aws.cognito.issuer}") String issuer,
            @Value("${aws.cognito.jwksUrl}") String jwksUrlString
    ) throws MalformedURLException, URISyntaxException {
        this.issuer = issuer;
        this.jwksUrlString = jwksUrlString;

        ConfigurableJWTProcessor<com.nimbusds.jose.proc.SecurityContext> processor =
                new DefaultJWTProcessor<>();

        URL jwksUrl = new URI(jwksUrlString).toURL();

        JWKSource<SecurityContext> jwkSource = JWKSourceBuilder.create(jwksUrl)
                .build();

        JWSKeySelector<SecurityContext> keySelector =
                new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, jwkSource);
        processor.setJWSKeySelector(keySelector);

        processor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier<>(
                new JWTClaimsSet.Builder().issuer(issuer).build(),
                null
        ));

        this.jwtProcessor = processor;
    }

    public String validateAndExtractSub(String token) {
        JWTClaimsSet claims = this.processToken(token);

        Object subClaim = claims.getClaim("sub");
        if (subClaim == null) {
            throw new SecurityException(SecurityErrorMessage.INVALID_CREDENTIALS);
        }

        return subClaim.toString();
    }
    public boolean validate(String token) {
        this.processToken(token);
        return true;
    }

    private JWTClaimsSet processToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new SecurityException(SecurityErrorMessage.INVALID_CREDENTIALS);
        }

        try {
            return jwtProcessor.process(token, null);
        } catch (ParseException e) {
            log.error("Failed to parse JWT token structure.", e);
            throw new SecurityException(SecurityErrorMessage.INVALID_CREDENTIALS);
        } catch (BadJOSEException e) {
            log.error("JWT validation failed: " + e.getMessage(), e);
            throw new SecurityException(SecurityErrorMessage.INVALID_CREDENTIALS);
        } catch (JOSEException e) {
            log.error("JOSE error during JWT processing: " + e.getMessage(), e);
            throw new TechnicalException(TechnicalErrorMessage.TECHNICAL_ERROR);
        } catch (Exception e) {
            log.error("An unexpected error occurred during JWT processing: " + e.getMessage(), e);
            throw new TechnicalException(TechnicalErrorMessage.TECHNICAL_ERROR);
        }
    }
}

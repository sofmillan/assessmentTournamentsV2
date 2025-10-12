package co.com.assessment.tokenresolver;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.SecurityContext;
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

import java.net.URL;
import java.text.ParseException;
@Component
public class JwtResolver {
    private static final String ISSUER = "";
    private static final String JWKS_URL = "";

    private final JWTProcessor<SecurityContext> jwtProcessor;

    public JwtResolver() throws MalformedURLException {
        ConfigurableJWTProcessor<com.nimbusds.jose.proc.SecurityContext> processor =
                new DefaultJWTProcessor<>();

        URL jwksUrl = new URL(JWKS_URL);

        JWKSource<SecurityContext> jwkSource = JWKSourceBuilder.create(jwksUrl)
                .build();

        JWSKeySelector<SecurityContext> keySelector =
                new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, jwkSource);
        processor.setJWSKeySelector(keySelector);

        processor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier<>(
                new JWTClaimsSet.Builder().issuer(ISSUER).build(),
                null
        ));

        this.jwtProcessor = processor;
    }

    public String validateAndExtractSub(String token) {
        JWTClaimsSet claims = this.processToken(token);

        Object subClaim = claims.getClaim("sub");
        if (subClaim == null) {
            throw new JwtValidationException("JWT is valid but is missing the 'sub' claim.");
        }

        return subClaim.toString();
    }
    public boolean validate(String token) {
        this.processToken(token);
        return true;
    }

    private JWTClaimsSet processToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new JwtValidationException("Token must not be null or empty.");
        }

        try {
            return jwtProcessor.process(token, null);
        } catch (ParseException e) {
            // Thrown if the token structure is invalid
            throw new JwtValidationException("Failed to parse JWT token structure.", e);
        } catch (BadJOSEException e) {
            // Thrown if validation fails (e.g., bad signature, expired, wrong issuer)
            throw new JwtValidationException("JWT validation failed: " + e.getMessage(), e);
        } catch (JOSEException e) {
            // Thrown if key fetching fails
            throw new JwtValidationException("JOSE error during JWT processing: " + e.getMessage(), e);
        } catch (Exception e) {
            // General catch for unexpected errors
            throw new JwtValidationException("An unexpected error occurred during JWT processing: " + e.getMessage(), e);
        }
    }

    public static class JwtValidationException extends RuntimeException {
        public JwtValidationException(String message) {
            super(message);
        }
        public JwtValidationException(String message, Throwable cause) {
            super(message, cause);
        }
}
}

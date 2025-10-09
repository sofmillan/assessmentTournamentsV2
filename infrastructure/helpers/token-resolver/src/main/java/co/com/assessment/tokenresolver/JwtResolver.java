package co.com.assessment.tokenresolver;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.util.Set;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTProcessor;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtResolver {
    // IMPORTANT: Replace these placeholders with your actual Cognito User Pool details
    private static final String USER_POOL_ID = "us-east-";
    private static final String AWS_REGION = "us-east-1";
    private static final String CLIENT_ID = "2lhel19t5efm0r";

    // The issuer URI should match the 'iss' claim in the token
    //private static final String ISSUER = String.format("https://cognito-idp.%s.amazonaws.com/%s", AWS_REGION, USER_POOL_ID);
    private static final String ISSUER = "https://cognito-idp.us-east-1.amazonaws.com/us-east-1_dyZ";

    // The JWKS URI where Cognito stores the public keys
    private static final String JWKS_URL = "https://cognito-idp.us-east-1.amazonaws.com/us-east-1_d/.well-known/jwks.json";

    private final JWTProcessor<SecurityContext> jwtProcessor;

    /**
     * Initializes the JWT processor, setting up the key selector to fetch public keys
     * from the Cognito JWKS endpoint.
     * * @throws MalformedURLException if the JWKS URL is invalid.
     */
    public JwtResolver() throws MalformedURLException {
        // 1. Create a JWT processor instance
        ConfigurableJWTProcessor<com.nimbusds.jose.proc.SecurityContext> processor =
                new DefaultJWTProcessor<>();

        // 2. Set the JWS Key Selector to retrieve keys from Cognito's JWKS endpoint
        // This is necessary because Cognito tokens are signed using RS256 (asymmetric)
        URL jwksUrl = new URL(JWKS_URL);

        // RemoteJWKSet dynamically fetches and caches the public keys from the URL
        JWKSource<SecurityContext> jwkSource = JWKSourceBuilder.create(jwksUrl)
                // You can customize the cache and rate limiting here,
                // but the defaults are generally good for Cognito
                .build();

        // The rest of your setup remains largely the same:
        JWSKeySelector<SecurityContext> keySelector =
                new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, jwkSource);
        processor.setJWSKeySelector(keySelector);

        // 3. Set the claims verifier for security checks (exp, iss, aud, token_use)
        // Check 1: Issuer ('iss' claim) must match the Cognito User Pool
        // Check 2: Audience ('aud' claim) must match the App Client ID
        // Check 3: Set<String> of required claims (e.g., "token_use")
        processor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier<>(
                new JWTClaimsSet.Builder().issuer(ISSUER).build(),
                null// Expected audience
        ));

        this.jwtProcessor = processor;
    }

    public String validateAndExtractSub(String token) {
        // Validation is done by the helper method
        JWTClaimsSet claims = this.processToken(token);

        // Extraction of the 'sub' claim
        Object subClaim = claims.getClaim("sub");
        if (subClaim == null) {
            throw new JwtValidationException("JWT is valid but is missing the 'sub' claim.");
        }

        return subClaim.toString();
    }

    // -------------------------------------------------------------------------------------------------
    // 💡 NEW PUBLIC METHOD: Only Validates and Returns the ClaimsSet
    // -------------------------------------------------------------------------------------------------

    /**
     * Securely validates a Cognito JWT (signature, expiration, issuer, audience)
     * and returns the full claims set.
     * @param token The raw JWT string.
     * @return The validated JWTClaimsSet object containing all claims.
     * @throws JwtValidationException If the token is invalid (bad signature, expired, incorrect claims).
     */
    public boolean validate(String token) {
        System.out.println("PROCESA TOKEN");
        this.processToken(token);
        return true;
    }

    // -------------------------------------------------------------------------------------------------
    // 💡 NEW PRIVATE HELPER METHOD: Contains the core Nimbus processing logic
    // -------------------------------------------------------------------------------------------------

    /**
     * Helper method to perform the core validation and return the claims set.
     * @param token The raw JWT string.
     * @return The validated JWTClaimsSet object.
     * @throws JwtValidationException If the token is invalid.
     */
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


    /**
     * Custom exception to wrap validation errors.
     */
    public static class JwtValidationException extends RuntimeException {
        public JwtValidationException(String message) {
            super(message);
        }
        public JwtValidationException(String message, Throwable cause) {
            super(message, cause);
        }
}
}

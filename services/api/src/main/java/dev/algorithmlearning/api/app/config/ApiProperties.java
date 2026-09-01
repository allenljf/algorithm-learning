package dev.algorithmlearning.api.app.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app")
public record ApiProperties(Cors cors, Security security) {

    public record Cors(List<String> allowedOrigins) {
    }

    public record Security(String jwtKey, String jwtIssuer, String jwtAudience, String refreshHashKey) {
    }
}

package dev.algorithmlearning.api.app.config;

import java.util.List;
import java.time.Clock;
import dev.algorithmlearning.api.app.security.JwtAuthenticationFilter;
import dev.algorithmlearning.api.auth.application.AllowedOriginValidator;
import dev.algorithmlearning.api.auth.application.AuthRateLimiter;
import dev.algorithmlearning.api.auth.application.AuthService;
import dev.algorithmlearning.api.auth.application.AuthSessionRepository;
import dev.algorithmlearning.api.auth.application.AuthSessionService;
import dev.algorithmlearning.api.auth.application.AuthUserRepository;
import dev.algorithmlearning.api.auth.application.InMemoryAuthRateLimiter;
import dev.algorithmlearning.api.auth.domain.Argon2PasswordHasher;
import dev.algorithmlearning.api.auth.domain.JwtTokenService;
import dev.algorithmlearning.api.auth.domain.RefreshTokenService;
import dev.algorithmlearning.api.tags.application.TagRepository;
import dev.algorithmlearning.api.tags.application.TagService;
import dev.algorithmlearning.api.problems.application.ProblemRepository;
import dev.algorithmlearning.api.problems.application.ProblemService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
class SecurityConfiguration {

    @Bean
    SecurityFilterChain apiSecurityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/health/**").permitAll()
                        .requestMatchers("/api/v1/auth/register", "/api/v1/auth/login", "/api/v1/auth/refresh", "/api/v1/auth/logout").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(ApiProperties apiProperties) {
        var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(apiProperties.cors().allowedOrigins());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Request-ID"));
        configuration.setExposedHeaders(List.of("X-Request-ID"));
        configuration.setAllowCredentials(true);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean Clock clock() { return Clock.systemUTC(); }
    @Bean Argon2PasswordHasher argon2PasswordHasher() { return new Argon2PasswordHasher(); }
    @Bean JwtTokenService jwtTokenService(ApiProperties properties, Clock clock) { return new JwtTokenService(properties.security().jwtKey(), properties.security().jwtIssuer(), properties.security().jwtAudience(), clock); }
    @Bean RefreshTokenService refreshTokenService(ApiProperties properties) { return new RefreshTokenService(properties.security().refreshHashKey()); }
    @Bean AuthSessionService authSessionService(AuthSessionRepository repository, RefreshTokenService tokens, Clock clock) { return new AuthSessionService(repository, tokens, clock); }
    @Bean AuthService authService(AuthUserRepository users, AuthSessionService sessions, Argon2PasswordHasher passwords, JwtTokenService jwt, Clock clock) { return new AuthService(users, sessions, passwords, jwt, clock); }
    @Bean AuthRateLimiter authRateLimiter(Clock clock) { return new InMemoryAuthRateLimiter(clock); }
    @Bean AllowedOriginValidator allowedOriginValidator(ApiProperties properties) { return new AllowedOriginValidator(properties.cors().allowedOrigins()); }
    @Bean TagService tagService(TagRepository tags, Clock clock) { return new TagService(tags, clock); }
    @Bean ProblemService problemService(ProblemRepository problems, TagRepository tags, Clock clock) { return new ProblemService(problems, tags, clock); }
}

package dev.algorithmlearning.api.app.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.algorithmlearning.api.app.security.CurrentUser;
import dev.algorithmlearning.api.auth.api.AuthController;
import dev.algorithmlearning.api.auth.application.AllowedOriginValidator;
import dev.algorithmlearning.api.auth.application.AuthRateLimiter;
import dev.algorithmlearning.api.auth.application.AuthService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AuthValidationAuthTest {

    @Test
    void returnsValidationProblemInsteadOfServerErrorForShortRegistrationPassword() throws Exception {
        var controller = new AuthController(org.mockito.Mockito.mock(AuthService.class),
                org.mockito.Mockito.mock(AuthRateLimiter.class),
                new AllowedOriginValidator(List.of("https://allenljf-algorithm.web.app")), new CurrentUser());
        var mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ApiExceptionHandler())
                .build();

        mvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"valid@example.invalid\",\"password\":\"too-short\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("validation_error"));
    }
}

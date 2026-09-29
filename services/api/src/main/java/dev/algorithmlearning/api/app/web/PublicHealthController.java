package dev.algorithmlearning.api.app.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class PublicHealthController {

    @GetMapping("/api/actuator/health/readiness")
    void readiness(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.getRequestDispatcher("/actuator/health/readiness").forward(request, response);
    }
}

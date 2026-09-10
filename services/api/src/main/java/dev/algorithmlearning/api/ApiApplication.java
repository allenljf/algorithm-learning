package dev.algorithmlearning.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
	@ConfigurationPropertiesScan
public class ApiApplication {

	public static void main(String[] args) {
		var migrationOnlyMode = MigrationOnlyMode.fromEnvironmentValue(System.getenv("APP_MIGRATION_ONLY"));
		var application = new SpringApplication(ApiApplication.class);
		application.setWebApplicationType(migrationOnlyMode.webApplicationType());
		var context = application.run(args);
		if (migrationOnlyMode.exitsAfterStartup()) {
			System.exit(SpringApplication.exit(context));
		}
	}

}

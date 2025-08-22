package org.readtogether.common.initializers;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresTestContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @SuppressWarnings("resource")
    public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("read-together-app-db")
            .withUsername("default")
            .withPassword("default");

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {

        if (!POSTGRES.isRunning()) {
            POSTGRES.start();
        }

        TestPropertyValues.of(
                "spring.datasource.url=" + POSTGRES.getJdbcUrl(),
                "spring.datasource.username=" + POSTGRES.getUsername(),
                "spring.datasource.password=" + POSTGRES.getPassword(),
                "spring.datasource.driver-class-name=org.postgresql.Driver",
                // ensure Liquibase manages schema
                "spring.jpa.hibernate.ddl-auto=none"
        ).applyTo(applicationContext.getEnvironment());
    }
}

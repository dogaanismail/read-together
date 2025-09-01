package org.readtogether.common.initializers;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

public class LocalStackTestContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @SuppressWarnings("resource")
    public static final LocalStackContainer LOCALSTACK = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:latest"))
            .withServices(LocalStackContainer.Service.S3);

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {

        if (!LOCALSTACK.isRunning()) {
            LOCALSTACK.start();
        }

        TestPropertyValues.of(
                "storage.provider=s3",
                "storage.s3.endpoint=" + LOCALSTACK.getEndpointOverride(LocalStackContainer.Service.S3),
                "storage.s3.region=" + LOCALSTACK.getRegion(),
                "storage.s3.bucket=test-bucket",
                "storage.s3.access-key=test",
                "storage.s3.secret-key=test",
                "storage.s3.server-side-encryption=AES256"
        ).applyTo(applicationContext.getEnvironment());
    }
}
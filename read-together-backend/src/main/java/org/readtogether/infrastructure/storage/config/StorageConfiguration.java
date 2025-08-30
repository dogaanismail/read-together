package org.readtogether.infrastructure.storage.config;

import lombok.extern.slf4j.Slf4j;
import org.readtogether.infrastructure.storage.service.StorageService;
import org.readtogether.infrastructure.storage.service.impl.LocalStorageService;
import org.readtogether.infrastructure.storage.service.impl.S3StorageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@Configuration
@EnableAsync
public class StorageConfiguration {

    private final StorageProperties storageProperties;

    public StorageConfiguration(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Bean
    @Primary
    public StorageService storageService() {

        String provider = storageProperties.getProvider().toLowerCase();

        log.info("Initializing storage service with provider: {}", provider);

        return switch (provider) {
            case "s3" -> {
                log.info("Using AWS S3 storage service");
                yield new S3StorageService(storageProperties);
            }
            case "local" -> {
                log.info("Using local file storage service");
                yield new LocalStorageService(storageProperties);
            }
            default -> {
                log.warn("Unknown storage provider '{}', falling back to local storage", provider);
                yield new LocalStorageService(storageProperties);
            }
        };
    }

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("AsyncExecutor-");
        executor.initialize();
        return executor;
    }
}

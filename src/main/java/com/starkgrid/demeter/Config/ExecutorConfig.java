package com.starkgrid.demeter.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {

    @Value("${demeter.platform.pool-size:0}")
    private int configuredPoolSize;

    /**
     * Virtual threads: great for high I/O workloads.
     * We’ll use this for:
     *  - Watcher event handling
     *  - File metadata reads
     *  - Hermes HTTP calls
     */
    @Bean(destroyMethod = "shutdown")
    public ExecutorService virtualExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    /**
     * Platform threads: limited, good for CPU-bound work.
     * We’ll use this for:
     *  - Hash calculations (SHA-256)
     *  - Any heavier in-memory processing
     */
    @Bean(destroyMethod = "shutdown")
    @Value("${demeter.platform.pool-size:0}")
    public ExecutorService platformExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();
        int poolSize =  (configuredPoolSize > 0)
                    ? configuredPoolSize
                    : Math.max(4, cores);
        return Executors.newFixedThreadPool(poolSize);
    }
}

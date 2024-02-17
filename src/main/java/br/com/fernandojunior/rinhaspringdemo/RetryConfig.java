package br.com.fernandojunior.rinhaspringdemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableRetry
public class RetryConfig {


    @Bean
    public RetryTemplate retryTemplate(){
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(3000l);

        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        Map<Class<? extends Throwable>, Boolean> includeExceptions = new HashMap<>();
        includeExceptions.put(CannotAcquireLockException.class, true);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(5, includeExceptions);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

}

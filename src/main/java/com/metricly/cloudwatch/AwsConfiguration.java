package com.metricly.cloudwatch;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfiguration {

    @Value("${aws.keyId}")
    private String keyId;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Bean
    public AWSCredentialsProvider credentialsProvider() {
        return new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(keyId, secretKey)
        );
    }

    @Bean
    public AmazonCloudWatch cloudWatchClient(AWSCredentialsProvider credentialsProvider) {
        return AmazonCloudWatchClient.builder()
                .withCredentials(credentialsProvider)
                .build();
    }

}

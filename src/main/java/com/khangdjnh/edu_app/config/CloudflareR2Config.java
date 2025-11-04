package com.khangdjnh.edu_app.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
@EnableConfigurationProperties(CloudflareR2Properties.class)
public class CloudflareR2Config {

    private final CloudflareR2Properties props;

    public CloudflareR2Config(CloudflareR2Properties props) {
        this.props = props;
    }

    @Bean
    public S3Client s3Client() {
        S3Configuration serviceConfig = S3Configuration.builder()
                .pathStyleAccessEnabled(true)   // R2 tương thích S3, dùng path style
                .chunkedEncodingEnabled(false)
                .build();

        return S3Client.builder()
                .endpointOverride(URI.create(props.getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey())))
                .region(Region.of(props.getRegion()))
                .serviceConfiguration(serviceConfig)
                .build();
    }
}

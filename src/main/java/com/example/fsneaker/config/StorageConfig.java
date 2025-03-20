package com.example.fsneaker.config;

//import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.auth.AWSStaticCredentialsProvider;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.regions.Regions;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {

//    @Value("${amazonProperties.accessKey}")
//    private String accessKey;
//
//    @Value("${amazonProperties.secretKey}")
//    private String accessSecret;
//    @Bean
//    public AmazonS3 s3Client() {
//        AWSCredentials credentials = new BasicAWSCredentials(accessKey, accessSecret);
//        return AmazonS3ClientBuilder.standard()
//                .withCredentials(new AWSStaticCredentialsProvider(credentials))
//                .withRegion(Regions.AP_SOUTHEAST_1).build();
//    }

}

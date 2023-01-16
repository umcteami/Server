package com.umc.i.utils.S3Storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@PropertySource("classpath:application.yml")
@ConfigurationProperties("aws")
public class S3Config {
    // @Value("${cloud.aws.credentials.access-key}")
    // private String accessKey;

    // @Value("${cloud.aws.credentials.secret-key:QYlTKs+jeWPOcu2igPEl+4u0xaQ6J07q6yktp3uP}")
    // private String secretKey;

    // @Value("${cloud.aws.region.static}")
    // private String region;

    @Autowired
    private Environment env;

    private String accessKey = "AKIA44FRIU24BINQZONF";
    private String secretKey= "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";
    // private String regionStatic;
   
    @Bean
    @Primary
    public BasicAWSCredentials awsCredentialsProvider(){
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKey, secretKey);
        return basicAWSCredentials;
    }

    @Bean
    public AmazonS3 amazonS3() {
        AmazonS3 s3Builder = AmazonS3ClientBuilder.standard()
                .withRegion("ap-northeast-2")
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentialsProvider()))
                .build();
        return s3Builder;
    }

    // @Bean
    // public AmazonS3 amazonS3() {
    //     System.out.println(accessKey);
    //     BasicAWSCredentials awsCreds = new BasicAWSCredentials(env.getProperty("cloud.aws.credentials.access-key"), env.getProperty("cloud.aws.credentials.secret-key"));
    //     return AmazonS3ClientBuilder
    //             .standard()
    //             .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
    //             .withRegion(env.getProperty("cloud.aws.region.static"))
    //             .build();
    // }
}
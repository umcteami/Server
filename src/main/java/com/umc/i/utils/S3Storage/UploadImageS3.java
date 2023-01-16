package com.umc.i.utils.S3Storage;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@PropertySource("classpath:application.yml")
@ConfigurationProperties("aws")
public class UploadImageS3 {
    @Autowired
    private final AmazonS3 amazonS3;

    // @Value("${aws.s3.image.bucket:i-image}")
    // private String bucket;

    @Autowired
    private Environment env;

    // 업로드
    public String upload(File uploadFile, String filePath, String saveFileName) {
        String fileName = filePath + "/" + saveFileName;
        amazonS3.putObject(new PutObjectRequest(env.getProperty("aws.s3.image.bucket.i-image"), fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead)); // public으로 권한 설정
        
        return fileName;
    }
}

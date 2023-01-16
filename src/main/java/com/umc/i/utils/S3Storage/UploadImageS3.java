package com.umc.i.utils.S3Storage;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UploadImageS3 {
    @Autowired
    private final AmazonS3 amazonS3;

    // @Value("${aws.s3.image.bucket:i-image}")
    // private String bucket;
    private String bucket = "i-image";

    // 업로드
    public String upload(MultipartFile uploadFile, String filePath, String saveFileName) throws AmazonServiceException, SdkClientException, IOException {
        String fileName = filePath + "/" + saveFileName;
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(uploadFile.getContentType());
        objectMetadata.setContentLength(uploadFile.getSize());
    
        try {
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, uploadFile.getInputStream(), objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead)); // public으로 권한 설정
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        return fileName;
    }
}
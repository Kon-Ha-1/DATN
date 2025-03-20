//package com.example.fsneaker.service;
//
//
//import com.amazonaws.HttpMethod;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
//import com.amazonaws.services.s3.model.S3Object;
//import com.amazonaws.services.s3.model.S3ObjectInputStream;
//import com.amazonaws.util.IOUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//
//@Service
//@Slf4j
//public class StorageService {
//
//    @Value("${amazonProperties.bucketName}")
//    private  String bucketName;
//
//    @Autowired
//    private  AmazonS3 s3Client;
//
//    public  String uploadFile(MultipartFile file, String fileName) {
//        File fileObj = convertMultiPartFileToFile(file);
//        s3Client.putObject(bucketName, fileName, fileObj);
//
//        java.util.Date expiration = new java.util.Date();
//        long expTimeMillis = expiration.getTime() + (7 * 24 * 60 * 60 * 1000);
//        expiration.setTime(expTimeMillis);
//
//        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, fileName)
//                .withMethod(HttpMethod.GET)
//                .withExpiration(expiration);
//        String preSignedUrl = s3Client.generatePresignedUrl(generatePresignedUrlRequest).toString();
//        fileObj.delete();
//        return preSignedUrl;
//    }
//
//    private  File convertMultiPartFileToFile(MultipartFile file) {
//        File convertedFile = new File(file.getOriginalFilename());
//        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
//            fos.write(file.getBytes());
//        } catch (IOException e) {
//            log.error("Error converting multipartFile to file", e);
//        }
//        return convertedFile;
//    }
//}

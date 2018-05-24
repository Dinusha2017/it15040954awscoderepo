package com.sliit.ctse.it15040954awscoderepo.services;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

@Service
public class AmazonClient {

    private AmazonS3 s3Client;

    @Value("${amazonProperties.endpointUrl}")
    private String endpointUrl;

    @Value("${amazonProperties.bucketName}")
    private String bucketName;

    @Value("${amazonProperties.accessKey}")
    private String accessKey;

    @Value("${amazonProperties.secretKey}")
    private String secretKey;

    @Value("${amazonProperties.region}")
    private String region;

    @PostConstruct
    private void setAmazonCredentials() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(this.accessKey, this.secretKey);
        s3Client =
                AmazonS3ClientBuilder.standard().withRegion(region).withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
    }

    private File convertMultipartFileToFile(MultipartFile file) throws IOException{
        File convertedFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();
        return convertedFile;
    }

    private String generateFileName(MultipartFile multipartFile){
        return new Date().getTime() + "-" + multipartFile.getOriginalFilename().replace(" ", "_");
    }

    private void uploadPhotoTos3Bucket(String filename, File photo) {
        s3Client.putObject(new PutObjectRequest(bucketName, filename, photo));
    }

    public void uploadPhoto(MultipartFile multipartFile){
        try{
            File file = convertMultipartFileToFile(multipartFile);
            String filename = generateFileName(multipartFile);
            String fileUrl = endpointUrl + "/" + bucketName + "/" + filename;
            uploadPhotoTos3Bucket(filename, file);
            file.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

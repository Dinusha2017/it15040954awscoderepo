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

    //Bind application property to variable during application initialization using @Value
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

    //Use @PostConstruct to run method after the constructor is called, as variables marked with @Value are null in the constructor
    //and these variables must be initialized before they can be used
    //This method sets Amazon Credentials to the amazon client object
    @PostConstruct
    private void setAmazonCredentials() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(this.accessKey, this.secretKey);
        s3Client = AmazonS3ClientBuilder.standard().withRegion(region).withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
    }

    //converts Multipart file to file, so that the resultant file can be passed into the method that uploads photo to S3 Bucket,
    //as this method accepts a parameter in the file format
    private File convertMultipartFileToFile(MultipartFile pic) throws IOException{
        File convertedFile = new File(pic.getOriginalFilename());
        FileOutputStream fileOutputStream = new FileOutputStream(convertedFile);
        fileOutputStream.write(pic.getBytes());
        fileOutputStream.close();
        return convertedFile;
    }

    public void uploadPhoto(MultipartFile multipartFile){
        try{
            //convert Multipart file to file
            File photo = convertMultipartFileToFile(multipartFile);

            //generate a unique name for each photo, as the same photo and photos with the same name can be uploaded
            String filename = new Date().getTime() + "-" + multipartFile.getOriginalFilename().replace(" ", "_");

            //upload photo to S3 Bucket
            s3Client.putObject(new PutObjectRequest(bucketName, filename, photo));

            photo.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

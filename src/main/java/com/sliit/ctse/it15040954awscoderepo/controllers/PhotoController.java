package com.sliit.ctse.it15040954awscoderepo.controllers;

import com.sliit.ctse.it15040954awscoderepo.services.AmazonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class PhotoController {

    private AmazonClient amazonClient;

    @Autowired
    PhotoController(AmazonClient amazonClient){
        this.amazonClient = amazonClient;
    }

    @CrossOrigin
    @PostMapping("/photo")
    public void uploadPhoto(@RequestPart(value = "pic") MultipartFile pic){
        this.amazonClient.uploadPhoto(pic);
    }
}

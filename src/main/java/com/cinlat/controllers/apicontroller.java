package com.cinlat.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.Label;
import software.amazon.awssdk.services.rekognition.model.NotificationChannel;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;



@Controller
@RequestMapping("/")
public class apicontroller {	
	     static String datos="";
	     static RekognitionClient rekClient;
	     static String moutainImage="G:\\Spring\\CinlatRekognite\\src\\main\\resources\\static\\img\\carro.jpg";
	     static NotificationChannel channel;
	     static String topicArn ="" ;
	     static String roleArn ="" ;
	 

	      
	   

	     
	  
	 public void DetectLabels(MultipartFile file) {
		 Region region = Region.US_EAST_1;
         rekClient = RekognitionClient.builder()
                 .region(region)
                 .credentialsProvider(ProfileCredentialsProvider.create())
                 .build();
	         datos = DetectLabels.detectImageLabels(rekClient, file);
	         
	 }

	 @GetMapping(path = {""})
		public String saludar2() {			 
			
			return "index";
		}
	     
	     
	@GetMapping(path = {"/rekognite","/holamundo"})
	public String saludar() {		  	         		        
		
		
		return "cinlatrekognite";
	}

	
}

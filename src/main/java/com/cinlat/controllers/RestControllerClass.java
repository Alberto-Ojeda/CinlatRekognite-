package com.cinlat.controllers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cinlat.models.PersonaModel;
import com.cinlat.service.FileService;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.NotificationChannel;


@Controller


public class RestControllerClass {
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
	  @Autowired
	    FileService fileService;

	    @GetMapping("/api")
	    public String index() {
	        return "UploadFile";
	    }
	    

	    @PostMapping("/uploadFile")
	    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes,Model model) {
	    	  DetectLabels(file);
		        
	  		model.addAttribute("titulo", datos);
	  		System.out.println(datos+"prueba");
	  		fileService.uploadFile(file);

	        redirectAttributes.addFlashAttribute("message",
	            "You successfully uploaded " + file.getOriginalFilename() + "!");

	        return "cinlatrekognite";
	    }
	}

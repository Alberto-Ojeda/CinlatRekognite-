package com.cinlat;
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import com.cinlat.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import java.io.*;
import java.util.Properties;
import  software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.model.NotificationChannel;
import software.amazon.awssdk.services.s3.S3Client;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RekognitionTest {

    private static RekognitionClient rekClient;
    private static  S3Client s3;
    private static NotificationChannel channel;
    private static String facesImage="";
    private static String celebritiesImage ="";
    private static String faceImage2 ="";
    private static String celId="";
    private static String moutainImage="";
    private static String collectionName="";
    private static String ppeImage="";
    private static String bucketName="";
    private static String textImage="";
    private static String modImage="" ;
    private static String faceVid="" ;
    private static String topicArn ="" ;
    private static String roleArn ="" ;
    private static String modVid = "";
    private static String textVid="";
    private static String celVid="";


    @BeforeAll
    public static void setUp() throws IOException {

        Region region = Region.US_EAST_1;
        rekClient = RekognitionClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        s3 = S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = RekognitionTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            prop.load(input);
            facesImage = prop.getProperty("facesImage");
            celebritiesImage = prop.getProperty("celebritiesImage");
            faceImage2 = prop.getProperty("faceImage2");
            celId = prop.getProperty("celId");
            moutainImage = prop.getProperty("moutainImage");
            collectionName = prop.getProperty("collectionName");
            ppeImage = prop.getProperty("ppeImage");
            bucketName = prop.getProperty("bucketName");
            textImage= prop.getProperty("textImage");
            modImage= prop.getProperty("modImage");
            faceVid = prop.getProperty("faceVid");
            topicArn= prop.getProperty("topicArn");
            roleArn = prop.getProperty("roleArn");
            modVid= prop.getProperty("modVid");
            textVid = prop.getProperty("textVid");
            celVid= prop.getProperty("celVid");


            // Required for tests that involve videos
            channel = NotificationChannel.builder()
                    .snsTopicArn(topicArn)
                    .roleArn(roleArn)
                    .build();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
  }

    @Test
    @Order(1)
    public void whenInitializingAWSRekognitionService_thenNotNull() {
        assertNotNull(rekClient);
        System.out.println("Test 1 passed");
    }

/*    @Test
    @Order(2)
    public void DetectFaces() {
        DetectFaces.detectFacesinImage(rekClient, facesImage);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void RecognizeCelebrities() {
        RecognizeCelebrities.recognizeAllCelebrities(rekClient, celebritiesImage);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void CompareFaces() {
        CompareFaces.compareTwoFaces(rekClient, 70F, facesImage, faceImage2);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void CelebrityInfo() {
        CelebrityInfo.getCelebrityInfo(rekClient, celId);
        System.out.println("Test 5 passed");
    }
*/
    @Test
    @Order(6)
    public void DetectLabels() {

        com.cinlat.controllers.DetectLabels.detectImageLabels(rekClient, moutainImage);
        System.out.println("Test 6 passed");
    }

}

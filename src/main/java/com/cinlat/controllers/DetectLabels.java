// snippet-sourcedescription:[DetectLabels.java demonstrates how to capture labels (like water and mountains) in a given image.]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[Amazon Rekognition]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[05/19/2022]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.cinlat.controllers;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.Label;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class DetectLabels {
static String datos ="";
   

    // snippet-start:[rekognition.java2.detect_labels.main]
    public static String detectImageLabels(RekognitionClient rekClient, MultipartFile file) {

        try {

            InputStream sourceStream = file.getInputStream();
           // InputStream sourceStream = new FileInputStream(sourceImage); URL("https://th.bing.com/th/id/OIP.2v2HD2lWTwayUGVLjkYTgQHaHa?pid=ImgDet&rs=1").openStream();
            SdkBytes sourceBytes = SdkBytes.fromInputStream(sourceStream);

            // Create an Image object for the source image.
            Image souImage = Image.builder()
                    .bytes(sourceBytes)
                    .build();

            DetectLabelsRequest detectLabelsRequest = DetectLabelsRequest.builder()
                    .image(souImage)
                    .maxLabels(1)
                    .build();

            DetectLabelsResponse labelsResponse = rekClient.detectLabels(detectLabelsRequest);
            List<Label> labels = labelsResponse.labels();

            System.out.println("Detected labels for the given photo");
            for (Label label: labels) {
            	datos=(label.name() + ": " + label.confidence().toString());
                System.out.println(label.name() + ": " + label.confidence().toString()+ "hola");
            }

        } catch (RekognitionException | FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
		return datos;
    }
    // snippet-end:[rekognition.java2.detect_labels.main]
}

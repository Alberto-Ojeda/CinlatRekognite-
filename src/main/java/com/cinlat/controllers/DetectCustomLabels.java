/*aws rekognition start-project-version \
  --project-version-arn "arn:aws:rekognition:us-east-2:135414464878:project/rooms_1/version/rooms_1.2022-07-06T14.22.10/1657135198138" \
  --min-inference-units 1 \
  --region us-east-2
*/
  //Copyright 2021 Amazon.com, Inc. or its affiliates. All Rights Reserved.
//PDX-License-Identifier: MIT-0 (For details, see https://github.com/awsdocs/
//amazon-rekognition-custom-labels-developer-guide/blob/master/LICENSE-SAMPLECODE.)
package com.cinlat.controllers;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.S3Object;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.DetectCustomLabelsRequest;
import
 software.amazon.awssdk.services.rekognition.model.DetectCustomLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.CustomLabel;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
import software.amazon.awssdk.services.rekognition.model.BoundingBox;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;
// Calls DetectCustomLabels on an image. Displays bounding boxes or
// image level labels found in the image.
public class DetectCustomLabels extends JPanel {
	 private transient BufferedImage image;
	 private transient DetectCustomLabelsResponse response;
	 private transient Dimension dimension;


public static final Logger logger =
	 Logger.getLogger(DetectCustomLabels.class.getName());
	 // Finds custom labels in an image stored in an S3 bucket.
	 public DetectCustomLabels(RekognitionClient rekClient,
	 S3Client s3client,
	 String projectVersionArn,
	 String bucket,
	 String key,
	 Float minConfidence) throws RekognitionException,
	 NoSuchBucketException, NoSuchKeyException, IOException {
	 logger.log(Level.INFO, "Processing S3 bucket: {0} image {1}", new Object[]
	 { bucket, key });
	 // Get image from S3 bucket and create BufferedImage
	 GetObjectRequest requestObject =
	 GetObjectRequest.builder().bucket(bucket).key(key).build();
	 ResponseBytes<GetObjectResponse> result = s3client.getObject(requestObject,
	 ResponseTransformer.toBytes());
	 ByteArrayInputStream bis = new ByteArrayInputStream(result.asByteArray());
	 image = ImageIO.read(bis);
	 // Set image size
	 setWindowDimensions();
	 // Construct request parameter for DetectCustomLabels
	 S3Object s3Object = S3Object.builder().bucket(bucket).name(key).build();
	 Image s3Image = Image.builder().s3Object(s3Object).build();
	 DetectCustomLabelsRequest request =
	 DetectCustomLabelsRequest.builder().image(s3Image)
	 
	 .projectVersionArn(projectVersionArn).minConfidence(minConfidence).build();
	 response = rekClient.detectCustomLabels(request);
	 logFoundLabels(response.customLabels());
	 drawLabels();
	 }
 // Finds custom label in a local image file.
 public DetectCustomLabels(RekognitionClient rekClient,
	 String projectVersionArn,
	 String photo,
	 Float minConfidence)
	 throws IOException, RekognitionException {
	 logger.log(Level.INFO, "Processing local file: {0}", photo);
	 // Get image bytes and buffered image
	 InputStream sourceStream = new FileInputStream(new File(photo));
	 SdkBytes imageBytes = SdkBytes.fromInputStream(sourceStream);
	 ByteArrayInputStream inputStream = new
	 ByteArrayInputStream(imageBytes.asByteArray());
	 image = ImageIO.read(inputStream);
	 setWindowDimensions();
	 // Construct request parameter for DetectCustomLabels
	 Image localImageBytes = Image.builder().bytes(imageBytes).build();
	 DetectCustomLabelsRequest request =
	 DetectCustomLabelsRequest.builder().image(localImageBytes)
	 .projectVersionArn(projectVersionArn).minConfidence(minConfidence).maxResults(1).build();
	 String label="";
	 response = rekClient.detectCustomLabels(request);
	 logFoundLabels(response.customLabels());
	 List<CustomLabel> customLabels = response.customLabels();
	 for (CustomLabel customLabel : customLabels) {
		 label = customLabel.name();
		 }
	 System.out.println(label);

	 drawLabels();
	 }
 // Sets window dimensions to 1/2 screen size, unless image is smaller
 public void setWindowDimensions() {
	 dimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
	 dimension.width = (int) dimension.getWidth() / 2;
	 if (image.getWidth() < dimension.width) {
	 dimension.width = image.getWidth();
	 }
	 dimension.height = (int) dimension.getHeight() / 2;
	 if (image.getHeight() < dimension.height) {
	 dimension.height = image.getHeight();
	 }
	 setPreferredSize(dimension);
	 }
	 // Draws bounding boxes (if present) and label text.
	 public void drawLabels() {
	 int boundingBoxBorderWidth = 5;
	 int imageHeight = image.getHeight(this);
	 int imageWidth = image.getWidth(this);
	 // Set up drawing
	 Graphics2D g2d = image.createGraphics();
	 g2d.setColor(Color.GREEN);
	 g2d.setFont(new Font("Tahoma", Font.PLAIN, 50));
	 Font font = g2d.getFont();
	 FontRenderContext frc = g2d.getFontRenderContext();
	 g2d.setStroke(new BasicStroke(boundingBoxBorderWidth));
	 List<CustomLabel> customLabels = response.customLabels();
	 int imageLevelLabelHeight = 0;
	 for (CustomLabel customLabel : customLabels) {
	 String label = customLabel.name();
	 int textWidth = (int) (font.getStringBounds(label, frc).getWidth());
	 int textHeight = (int) (font.getStringBounds(label, frc).getHeight());
	 // Draw bounding box, if present
	 if (customLabel.geometry() != null) {
	 BoundingBox box = customLabel.geometry().boundingBox();
	 float left = imageWidth * box.left();
	 float top = imageHeight * box.top();
	 // Draw black rectangle
	 g2d.setColor(Color.BLACK);
	 g2d.fillRect(Math.round(left + (boundingBoxBorderWidth)),
	 Math.round(top + (boundingBoxBorderWidth)),
	 textWidth + boundingBoxBorderWidth, textHeight +
	 boundingBoxBorderWidth);
	 // Write label onto black rectangle
	 g2d.setColor(Color.GREEN);
	
	g2d.drawString(label, left + boundingBoxBorderWidth, (top +
	 textHeight));
	 // Draw bounding box around label location
	 g2d.drawRect(Math.round(left), Math.round(top),
	 Math.round((imageWidth * box.width())),
	 Math.round((imageHeight * box.height())));
	 }
	 // Draw image level labels.
	 else {
	 // Draw black rectangle
	 g2d.setColor(Color.BLACK);
	 g2d.fillRect(10, 10 + imageLevelLabelHeight, textWidth,
	 textHeight);
	 g2d.setColor(Color.GREEN);
	 g2d.drawString(label, 10, textHeight + imageLevelLabelHeight);
	 imageLevelLabelHeight += textHeight;
   }
  }
	 g2d.dispose();
 }
	 // Log the labels found by DetectCustomLabels
 private void logFoundLabels(List<CustomLabel> customLabels) {
	 logger.info("Custom labels found:");
	 if (customLabels.isEmpty()) {
	 logger.log(Level.INFO, "No Custom Labels found. Consider lowering min confidence.");
 }
	 else {
	 for (CustomLabel customLabel : customLabels) {
	 logger.log(Level.INFO, " Label: {0} Confidence: {1}",
	 new Object[] { customLabel.name(),
	 customLabel.confidence() } );
  }
 }
}
	 // Draws the image containing the bounding boxes and labels.
 @Override
 public void paintComponent(Graphics g) {
	 Graphics2D g2d = (Graphics2D) g; // Create a Java2D version of g.
	 // Draw the image.
	 g2d.drawImage(image, 0, 0, dimension.width, dimension.height, this);
	 }
 
 
 
 public static void main(String args2[]) throws Exception {
	 String photo = null;
	 String bucket = null;
	 String projectVersionArn = null;
	 final String USAGE = "\n" + "Usage: " + "arn:aws:rekognition:us-east-2:135414464878:project/rooms_1/version/rooms_1.2022-07-06T14.22.10/1657135198138" +  "paso.jpg" + "test-etiquetado-cinlat-1-0" + "\n\n";
	// Collect the arguments. If 3 arguments are present, the image is assumed
	// to be
	 // in an S3 bucket.
	String args[]= {"arn:aws:rekognition:us-east-2:135414464878:project/rooms_1/version/rooms_1.2022-07-06T14.22.10/1657135198138","G:\\Spring\\CinlatRekognite\\src\\main\\resources\\static\\img\\holis.jpg"};
	 if (args.length < 2 || args.length > 3) {
	 System.out.println(USAGE);
	 System.exit(1);
	 }
	 projectVersionArn = args[0];
	 photo = args[1];
	 if (args.length == 3) {
	 bucket = args[2];
	 }
	 float minConfidence = 14;
	 DetectCustomLabels panel = null;
	 try {
	 // Get the Rekognition client
	 RekognitionClient rekClient = RekognitionClient.builder().build();
	 S3Client s3client = S3Client.builder().build();
	 // Create frame and panel.
	 JFrame frame = new JFrame("Custom Labels");
	 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 if (args.length == 2) {
	 // Analyze local image
	 
	panel = new DetectCustomLabels(rekClient, projectVersionArn, photo,
	 minConfidence);
	 
	 }
	 else {
	 // Analyze image in S3 bucket
	 panel = new DetectCustomLabels(rekClient, s3client,
	 projectVersionArn, bucket, photo, minConfidence);
	 }
	 
	 
	 frame.setContentPane(panel);
	 frame.pack();
	 frame.setVisible(true);
	 
	 } 
	 
	 catch (RekognitionException rekError) {
	 String errorMessage = "Rekognition client error: " +
	 rekError.getMessage();
	 logger.log(Level.SEVERE, errorMessage);
	 System.out.println(errorMessage);
	 System.exit(1);
	 } catch (FileNotFoundException fileError) {
	 String errorMessage = "File not found: " + photo;
	 logger.log(Level.SEVERE, errorMessage);
	 System.out.println(errorMessage);
	 System.exit(1);
	 } catch (IOException fileError) {
	 String errorMessage = "Input output exception: " +
	 fileError.getMessage();
	 logger.log(Level.SEVERE, errorMessage);
	 System.out.println(errorMessage);
	 System.exit(1);
	 } catch (NoSuchKeyException bucketError) {
	 String errorMessage = String.format("Image not found: %s in bucket %s.", photo, bucket);
	 logger.log(Level.SEVERE, errorMessage);
	 System.out.println(errorMessage);
	 System.exit(1);
	 } catch (NoSuchBucketException bucketError) {
	 String errorMessage = "Bucket not found: " + bucket;
	 logger.log(Level.SEVERE, errorMessage);
	 System.out.println(errorMessage);
	 System.exit(1);
	 }
	 }
}

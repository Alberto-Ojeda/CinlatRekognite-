//Copyright 2021 Amazon.com, Inc. or its affiliates. All Rights Reserved.
//PDX-License-Identifier: MIT-0 (For details, see https://github.com/awsdocs/

package com.cinlat.controllers;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DescribeProjectVersionsRequest;
import software.amazon.awssdk.services.rekognition.model.DescribeProjectVersionsResponse;
import software.amazon.awssdk.services.rekognition.model.ProjectVersionDescription;
import software.amazon.awssdk.services.rekognition.model.ProjectVersionStatus;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
import software.amazon.awssdk.services.rekognition.model.StartProjectVersionRequest;
import software.amazon.awssdk.services.rekognition.model.StartProjectVersionResponse;
import software.amazon.awssdk.services.rekognition.waiters.RekognitionWaiter;
import java.net.URI;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StartModel {
	public static final Logger logger = Logger.getLogger(StartModel.class.getName());
	
	public static int findForwardSlash(String modelArn, int n) {
	
		int start = modelArn.indexOf('/');
	 
		while (start >= 0 && n > 1) {
	 
			start = modelArn.indexOf('/', start + 1);
	
			n -= 1;
	 
		}
	 
		return start;
	 
	}

		public static void startMyModel(RekognitionClient rekClient, String projectArn,String modelArn, Integer minInferenceUnits)	throws Exception, RekognitionException {
	 try {
	 
	 logger.log(Level.INFO, "Starting model: {0}", modelArn);
	 
	 StartProjectVersionRequest startProjectVersionRequest =
	 StartProjectVersionRequest.builder()
	 .projectVersionArn(modelArn)
	 .minInferenceUnits(minInferenceUnits)
	 .build();
	 StartProjectVersionResponse response =
	 rekClient.startProjectVersion(startProjectVersionRequest);
	 logger.log(Level.INFO, "Status: {0}", response.statusAsString() );
	//Get the model version
	int start = findForwardSlash(modelArn, 3) + 1;
	int end = findForwardSlash(modelArn, 4);
	String versionName = modelArn.substring(start, end);
	// wait until model starts
	DescribeProjectVersionsRequest describeProjectVersionsRequest =
	DescribeProjectVersionsRequest.builder()
	.versionNames(versionName)
	.projectArn(projectArn)
	.build();
	RekognitionWaiter waiter = rekClient.waiter();
	WaiterResponse<DescribeProjectVersionsResponse> waiterResponse = waiter
	
	.waitUntilProjectVersionRunning(describeProjectVersionsRequest);
	Optional<DescribeProjectVersionsResponse> optionalResponse =
	waiterResponse.matched().response();
	DescribeProjectVersionsResponse describeProjectVersionsResponse =
	optionalResponse.get();
	for (ProjectVersionDescription projectVersionDescription :
	describeProjectVersionsResponse
	.projectVersionDescriptions()) {
	if(projectVersionDescription.status() ==
	ProjectVersionStatus.RUNNING) {
	logger.log(Level.INFO, "Model is running" );
	
	}
	else {
	String error = "Model training failed: " +
	projectVersionDescription.statusAsString() + " "
	+ projectVersionDescription.statusMessage() + " " +
	modelArn;
	logger.log(Level.SEVERE, error);
	throw new Exception(error);
	}
	
	}
	} catch (RekognitionException e) {
	logger.log(Level.SEVERE, "Could not start model: {0}", e.getMessage());
	throw e;
	}
	}
	
	
	}
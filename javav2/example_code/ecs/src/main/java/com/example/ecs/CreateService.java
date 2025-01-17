//snippet-sourcedescription:[CreateService.java demonstrates how to create a service for the Amazon Elastic Container Service (Amazon ECS) service.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Elastic Container Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[06/20/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.ecs;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.AwsVpcConfiguration;
import software.amazon.awssdk.services.ecs.model.NetworkConfiguration;
import software.amazon.awssdk.services.ecs.model.CreateServiceRequest;
import software.amazon.awssdk.services.ecs.model.LaunchType;
import software.amazon.awssdk.services.ecs.model.CreateServiceResponse;
import software.amazon.awssdk.services.ecs.model.EcsException;

/**
 To run this Java V2 code example, ensure that you have setup your development environment,
 including your credentials.

 For information, see this documentation topic:
 https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateService {
    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "  CreateService " +
                "   <clusterName> <serviceName> <securityGroups> <subnets> <taskDefinition>\n\n" +
                "Where:\n" +
                "  clusterName - the name of the ECS cluster.\n" +
                "  serviceName - the name of the ECS service to create.\n" +
                "  securityGroups - the name of the security group.\n" +
                "  subnets - the name of the subnet.\n" +
                "  taskDefinition - the name of the task definition.\n" ;

        if (args.length != 5) {
            System.out.println(usage);
            System.exit(1);
        }

        String clusterName = args[0];
        String serviceName = args[1];
        String securityGroups = args[2];
        String subnets = args[3];
        String taskDefinition = args[4];;
        Region region = Region.US_EAST_1;
        EcsClient ecsClient = EcsClient.builder()
                .region(region)
                .build();

        String serviceArn  = CreateNewService(ecsClient, clusterName, serviceName, securityGroups, subnets, taskDefinition);
        System.out.println("The ARN of the service is "+serviceArn);
        ecsClient.close();
    }

    public static String CreateNewService(EcsClient ecsClient,
                                          String clusterName,
                                          String serviceName,
                                          String securityGroups,
                                          String subnets,
                                          String taskDefinition) {

        try {
            AwsVpcConfiguration vpcConfiguration = AwsVpcConfiguration.builder()
                    .securityGroups(securityGroups)
                    .subnets(subnets)
                    .build();

            NetworkConfiguration configuration = NetworkConfiguration.builder()
                    .awsvpcConfiguration(vpcConfiguration)
                    .build();

            CreateServiceRequest serviceRequest = CreateServiceRequest.builder()
                .cluster(clusterName)
                .networkConfiguration(configuration)
                .desiredCount(1)
                .launchType(LaunchType.FARGATE)
                .serviceName(serviceName)
                .taskDefinition(taskDefinition)
                .build();

            CreateServiceResponse response = ecsClient.createService(serviceRequest) ;
            return response.service().serviceArn();

    } catch (EcsException e) {
        System.err.println(e.awsErrorDetails().errorMessage());
        System.exit(1);
    }
    return "";
  }
}

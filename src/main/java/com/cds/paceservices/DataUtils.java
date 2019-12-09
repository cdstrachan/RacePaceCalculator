package com.cds.paceservices;

import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;

public class DataUtils {

    private static final Logger log = LoggerFactory.getLogger(PaceCalculatorController.class);

    public void writeRequestRecord(String requestData) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(com.amazonaws.regions.Regions.US_EAST_2).build();

        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable("Audit");

        String pkGUID = UUID.randomUUID().toString();

        final Map<String, Object> infoMap = new HashMap<String, Object>();
        infoMap.put("timestamp", DateTime.now().toString());
        infoMap.put("requestPayload", requestData);

        try {
            System.out.println("Adding a new item...");
            PutItemOutcome outcome = table
                    .putItem(new Item().withPrimaryKey("pkGUID", pkGUID).withMap("request", infoMap));

            log.info("PutItem succeeded:\n" + outcome.getPutItemResult());

        } catch (Exception e) {
            log.info("Unable to add item");
            log.info(e.getMessage());
        }
    }
}

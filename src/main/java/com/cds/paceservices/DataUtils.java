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

    // utility function to create a log record
    public void writeRequestRecord(String requestData) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(com.amazonaws.regions.Regions.US_EAST_2).build();

        // connect to the database
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("Audit");

        // create a GUID
        String pkGUID = UUID.randomUUID().toString();

        // dum the TO into a mappable object
        final Map<String, Object> infoMap = new HashMap<String, Object>();
        infoMap.put("requestPayload", requestData);

        // save the log record
        try {
            log.info("Adding a new item...");
            /*
             * PutItemOutcome outcome = table.putItem(new Item().withPrimaryKey("pkGUID",
             * pkGUID) .withString("timeStamp",
             * DateTime.now().toString()).withMap("request", infoMap));
             */
            PutItemOutcome outcome = table.putItem(new Item().withPrimaryKey("pkGUID", pkGUID)
                    .withString("timeStamp", DateTime.now().toString()).withString("requestData", requestData));

            log.info("Adding a new item...succeeded:" + outcome.getPutItemResult());

        } catch (Exception e) {
            log.info("Unable to add item to dynamodb");
            log.info(e.getMessage());
        }
    }
}
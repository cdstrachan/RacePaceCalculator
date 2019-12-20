package com.cds.paceservices;

import java.util.UUID;

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

    public void writeRequestRecord(String requestData, String source, String eventName) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(com.amazonaws.regions.Regions.US_EAST_2).build();

        // connect to database
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("Audit");

        // create GUID
        String pkGUID = UUID.randomUUID().toString();

        // write the log record
        try {
            log.info("PutItem to DynamoDB...");
            PutItemOutcome outcome = table.putItem(new Item().withPrimaryKey("pkGUID", pkGUID)
                    .withString("timeStamp", DateTime.now().toString()).withString("source", source)
                    .withString("eventName", eventName).withString("requestJSON", requestData));

            log.info("PutItem succeeded:\n" + outcome.getPutItemResult());

        } catch (Exception e) {
            log.info("PutItem: failed");
            log.info(e.getMessage());
        }
    }
}

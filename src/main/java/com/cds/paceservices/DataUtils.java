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

    public void writeRequestRecord(String requestData, String source) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(com.amazonaws.regions.Regions.US_EAST_2).build();

        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable("Audit");

        String pkGUID = UUID.randomUUID().toString();
        final Map<String, Object> infoMap = new HashMap<String, Object>();

        try {
            log.info("Adding a new item to DynamoDB...");
            PutItemOutcome outcome = table.putItem(
                    new Item().withPrimaryKey("pkGUID", pkGUID).withString("timeStamp", DateTime.now().toString())
                            .withString("source", source).withString("requestJSON", requestData));

            log.info("PutItem succeeded:\n" + outcome.getPutItemResult());

        } catch (Exception e) {
            log.info("Unable to add item");
            log.info(e.getMessage());
        }
    }
}

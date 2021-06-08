package com.ronny;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class lookupSpotPrice {
    Double price = 0.00;
    Double averagePrice = 0.00;

    public boolean lookupSpot() {
        System.out.println("Lookup request received...");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter dateInFileFormat = DateTimeFormatter.ofPattern("dd.MM.yyy");
        DateTimeFormatter hourFormat = DateTimeFormatter.ofPattern("HH");
        LocalDateTime now = LocalDateTime.now();

        String currentDateFile = "/usr/local/kjula/spotfiles/spotPrice_" + dateFormat.format(now) + ".json";
        String dateInFile = dateInFileFormat.format(now);
        int currentHour = Integer.parseInt(hourFormat.format(now));
        File spotFile = new File(currentDateFile);
        boolean exists = spotFile.exists();
        System.out.println(exists);
        if (!exists)
            return false;

        JSONParser parser = new JSONParser();
        try {
            JSONObject pricesForDay = (JSONObject) parser.parse(new FileReader(currentDateFile));
            String validDate = (String) pricesForDay.get("valid_date");
            Double averageValue = (Double) pricesForDay.get("average_price");

            JSONArray hours = (JSONArray) pricesForDay.get("hours");

            for (Object hour : hours) {
                JSONObject byHour = (JSONObject) hour;
                long hourValue = (long) byHour.get("hour");
                double priceValue = (double) byHour.get("price");
                if (hourValue == currentHour) {
                    price =  (double)Math.round(priceValue*100)/100;
                    averagePrice = (double)Math.round(averageValue*100)/100;
                    exists = true;
                    break;
                }
            }
        } catch (Exception pe) {
            //  System.out.println("position: " + pe.getPosition());
            System.out.println(pe);
        }

        return exists;
    }
}

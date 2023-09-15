package com.example.carrental.helper;

import lombok.experimental.Helper;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CommonHelper {

    private final SimpleDateFormat iso8601DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static final Map<String, String> bookingStatus = new HashMap<>(){
        {
            put("STATUS_PENDING", "PENDING");
            put("STATUS_ACCEPTED", "ACCEPTED");
            put("STATUS_DENIED", "DENIED");
        }
    };

    public Date getDateInISO8601(String date) {
        try {
            return iso8601DateFormat.parse(date);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}

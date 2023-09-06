package com.example.carrental.helper;

import lombok.experimental.Helper;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class CommonHelper {

    private final SimpleDateFormat iso8601DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public Date getDateInISO8601(String date) {
        try {
            return iso8601DateFormat.parse(date);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}

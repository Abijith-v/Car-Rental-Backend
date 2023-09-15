package com.example.carrental.service;

import com.example.carrental.helper.CommonHelper;
import com.example.carrental.model.ConfirmedBooking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class BookingRequestBackgroundProcessor {

    @Autowired
    private BookingService bookingService;

    @Scheduled(cron = "0 0 */6 * * ?") // Run every 6 hours
    public void processPendingBookingRequests() {
        List<ConfirmedBooking> pendingRequests = bookingService.getPendingRequests();
        Date currentDateTime = new Date();

        for (ConfirmedBooking request : pendingRequests) {
            // Check if the request has exceeded a certain time threshold (e.g., 6 hours)
            if (request.getDateOfBooking().before(new Date(currentDateTime.getTime() - (60 * 1000)))) {
                System.out.println("Denying request ID " + request.getId() + "due to timeout");
                bookingService.updateBookingRequestStatus(request, CommonHelper.bookingStatus.get("STATUS_DENIED"));
            }
        }
    }
}

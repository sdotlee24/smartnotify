package com.thirty.smartnotify.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirty.smartnotify.services.GmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class GmailController {

    private final GmailService gmailService;

    public GmailController(GmailService gmailService) {
        this.gmailService = gmailService;
    }


    @GetMapping("/")
    public String getEmails() throws IOException {

        return "Here they are!";
    }

    @GetMapping("/sent")
    public String parseEmail() {
        return "Developing...";
    }

    @PostMapping("/sent")
    public ResponseEntity<String> parseTest(@RequestBody String payload) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(payload);
            System.out.println(payload);
            System.out.println("arrived");
            return ResponseEntity.ok("Message received and processed.");
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error handling data.");
        }
        //TODO: Approach: 1. save company (via sender email, subject, etc) in db
        // 2. when the next email comes (next stage) update the status of application in db
        // Can use postgres.

    }
}

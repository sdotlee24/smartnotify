package com.thirty.smartnotify.services;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.WatchRequest;
import com.google.api.services.gmail.model.WatchResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

@Component
public class GmailWatcher {
    private final Gmail gmail;


    public GmailWatcher(Gmail gmail) {
        this.gmail = gmail;
    }
    @PostConstruct
    public void setGmailWatch() {
        try {
            WatchRequest watchRequest = new WatchRequest().setLabelIds(Arrays.asList("INBOX", "CATEGORY_UPDATES"))
                    .setTopicName("projects/smart-notify30/topics/newEmail");

            WatchResponse watchResponse = gmail.users().watch("me",  watchRequest).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

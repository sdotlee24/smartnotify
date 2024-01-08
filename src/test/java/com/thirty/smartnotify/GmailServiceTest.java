package com.thirty.smartnotify;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.thirty.smartnotify.repositories.ApplicationRepository;
import com.thirty.smartnotify.services.GmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


public class GmailServiceTest {

    @Mock
    private Gmail gmail;

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private GmailService gmailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testJobApplicationKeywords() {
        String text = "Hello, name \n" +
                "Thanks for your application to {Company name}";

        Boolean res = gmailService.containsJobApplicationKeywords(text);
        assertTrue(res);
    }

    @Test
    void testGetSenderEmail() throws IOException {
        Message mockMessage = new Message();

    }



}

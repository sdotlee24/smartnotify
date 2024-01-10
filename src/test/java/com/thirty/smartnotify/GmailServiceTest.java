package com.thirty.smartnotify;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.thirty.smartnotify.repositories.ApplicationRepository;
import com.thirty.smartnotify.services.GmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class GmailServiceTest {

    @Mock
    private Gmail gmail;

    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private MessagePartHeader mockHeader1;
    @Mock
    private MessagePartHeader mockHeader2;
    @Mock
    private Message mockMessage;
    @Mock
    private MessagePartBody mockBody;

    @InjectMocks
    private GmailService gmailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(mockHeader1.getName()).thenReturn("Fake_name");
        when(mockHeader1.getValue()).thenReturn("foo@foo.com");
        when(mockHeader2.getName()).thenReturn("X-Google-Sender-Delegation");
        when(mockHeader2.getValue()).thenReturn("real@foo.com");

        when(mockMessage.getPayload()).thenReturn(mock(MessagePart.class));
        when(mockMessage.getPayload().getParts()).thenReturn(List.of(mock(MessagePart.class)));
        when(mockMessage.getPayload().getParts().getFirst().getBody()).thenReturn(mockBody);
        when(mockMessage.getPayload().getHeaders()).thenReturn(List.of(mockHeader1, mockHeader2));
        when(gmailService.getSenderEmail(List.of(mockHeader1, mockHeader2))).thenReturn("foo@gmail.com");

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
        String res = gmailService.getSenderEmail(List.of(mockHeader1, mockHeader2));
        assertEquals("real@foo.com", res);
    }

    /**
     * Tests the case where message contents = null
     * @throws IOException
     */
    @Test
    void testParseMessage1() throws IOException {
        mockBody.setData(null);
        String res = gmailService.parseMessage(mockMessage);
        assertEquals("Mail has no body", res);

    }
    @Test
    void testParseMessage2() throws IOException {
        mockBody.setData("abcde");
        
    }



}

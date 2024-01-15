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
import org.mockito.Spy;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


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
    @Spy
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
        when(mockMessage.getPayload().getParts().get(0).getBody()).thenReturn(mockBody);
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
        assertEquals("foo@gmail.com", res);
    }

    /**
     * Tests the case where message contents = null
     * @throws IOException
     */
    @Test
    void testParseMessage1() throws IOException {
        when(mockMessage.getPayload().getParts().get(0).getBody().getData()).thenReturn(null);
        String res = gmailService.parseMessage(mockMessage);
        assertEquals("Mail has no body", res);

    }

    /**
     * Tests the case where job application did not contain keywords.
     * @throws IOException
     */
    @Test
    void testParseMessage2() throws IOException {
        when(mockMessage.getPayload().getParts().get(0).getBody().getData()).thenReturn("SGVsbsf");
        String res = gmailService.parseMessage(mockMessage);
        assertEquals("Didn't contain keywords", res);
        
    }

    @Test
    void testParseMessage3() throws IOException {
        when(mockMessage.getPayload().getParts().get(0).getBody().getData()).thenReturn("SGVsbG8gdXNlciwgd2UgaGF2ZSByZWNlaXZlZCB5b3VyIGFwcGxpY2F0aW9uLg==");
        doReturn("NULL").when(gmailService).getCompanyName(anyString());
        String res = gmailService.parseMessage(mockMessage);
        assertEquals("Could not find company name in text", res);
    }

    @Test
    void testParseMessage4() throws IOException {
        when(mockMessage.getPayload().getParts().get(0).getBody().getData()).thenReturn("SGVsbG8gdXNlciwgd2UgaGF2ZSByZWNlaXZlZCB5b3VyIGFwcGxpY2F0aW9uLg==");
        doReturn("SpaceX").when(gmailService).getCompanyName(anyString());

    }



}

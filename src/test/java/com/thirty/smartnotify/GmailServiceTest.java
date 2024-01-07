package com.thirty.smartnotify;

import com.google.api.services.gmail.Gmail;
import com.thirty.smartnotify.domain.Application;
import com.thirty.smartnotify.model.StatusEnum;
import com.thirty.smartnotify.repositories.ApplicationRepository;
import com.thirty.smartnotify.services.GmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


public class GmailServiceTest {

    @Mock
    private Gmail gmail;

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private GmailService gmailService;

    @BeforeEach
    void setUp() {

    }
    @Test
    void testFindApplicationBySenderEmail() {
        Application newApp = new Application("sender@gg.com", "google", StatusEnum.APPLIED);
        applicationRepository.save(newApp);

        List<Application> res = applicationRepository.findApplicationBySenderEmail("sender@gg.com");
        assertFalse(res.isEmpty());
        assertEquals(newApp.getSenderEmail(), res.get(0).getSenderEmail());
        assertEquals(newApp.getStatus(), res.get(0).getStatus());
        assertEquals(newApp.getCompanyName(), res.get(0).getCompanyName());
    }

    @Test
    void testUpdateApplicationByEmail() {
        List<Application> applications = Arrays.asList(
                new Application("sender@gg.com", "google", StatusEnum.APPLIED),
                new Application("truck@ff.com", "lebron", StatusEnum.PENDING),
                new Application("truck@ff.com", "curGoat", StatusEnum.REJECTED)
        );

        applicationRepository.saveAll(applications);

        int updateCount = applicationRepository.updateApplicationBySenderEmail("sender@gg.com", StatusEnum.PENDING);
        List<Application> updatedApp = applicationRepository.findApplicationBySenderEmail("sender@gg.com");
        assertEquals(1, updateCount);
        assertEquals(StatusEnum.PENDING, updatedApp.get(0).getStatus());
        assertEquals("google", updatedApp.get(0).getCompanyName());

        updateCount = applicationRepository.updateApplicationBySenderEmail("fakeEmail@gmail.com", StatusEnum.PENDING);
        assertEquals(0, updateCount);

        updateCount = applicationRepository.updateApplicationBySenderEmail("truck@ff.com", StatusEnum.APPLIED);
        updatedApp = applicationRepository.findApplicationBySenderEmail("sender@gg.com");
        assertEquals(1, updateCount);
        assertEquals(StatusEnum.PENDING, updatedApp.get(0).getStatus());
        assertEquals("google", updatedApp.get(0).getCompanyName());

    }

}

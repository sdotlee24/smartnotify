package com.thirty.smartnotify;

import com.thirty.smartnotify.domain.Application;
import com.thirty.smartnotify.model.StatusEnum;
import com.thirty.smartnotify.repositories.ApplicationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(classes = SmartNotifyApplication.class)
@ActiveProfiles("test")
public class ApplicationRepositoryTest {
    @Autowired
    private ApplicationRepository applicationRepository;

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
        assertEquals(2, updateCount);
        assertEquals(StatusEnum.PENDING, updatedApp.get(0).getStatus());
        assertEquals("google", updatedApp.get(0).getCompanyName());

    }

}

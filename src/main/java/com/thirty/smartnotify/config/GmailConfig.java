package com.thirty.smartnotify.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.api.client.json.JsonFactory;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;


@Configuration
public class GmailConfig {

    private final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_READONLY, GmailScopes.GMAIL_MODIFY);
    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();


    private Credential getCredentials(final NetHttpTransport netHttpTransport) throws IOException {
        InputStream in = GmailConfig.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(in));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(netHttpTransport, jsonFactory, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File("tokens"))).setAccessType("offline").build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8080).setCallbackPath("/callback").build();

        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

    }
    @Bean
    public Gmail gmail() throws GeneralSecurityException, IOException {

        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        return new Gmail.Builder(httpTransport, jsonFactory, getCredentials(httpTransport)).setApplicationName("SmartNotify").build();
    }
}

package com.thirty.smartnotify.services;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.google.api.services.gmail.model.ModifyMessageRequest;
import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import com.thirty.smartnotify.domain.Application;
import com.thirty.smartnotify.model.StatusEnum;
import com.thirty.smartnotify.repositories.ApplicationRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Service
public class GmailService {
    private final Gmail gmail;
    private final ApplicationRepository applicationRepository;

    public GmailService(Gmail gmail, ApplicationRepository applicationRepository) {
        this.gmail = gmail;
        this.applicationRepository = applicationRepository;
    }
    public String parseNewestMessage() {
        try {
            String err = "";
            Message newMsg = getNewestMessage();
            if (! isMessageUnread(newMsg)) {
                return "There is no new message in the mailbox";
            }
            err = markMessageRead(newMsg);
            if (!err.isEmpty()) {
                return err;
            }

            err = parseMessage(newMsg);
            if (err.equals("Didn't contain keywords")) {
                //TODO query if sender's email exists in db, if this is the case its either
                // 1. a promotion (advertisement) 2. a followup email regarding application
                String senderEmail = getSenderEmail(newMsg.getPayload().getHeaders());
                List<Application> application = applicationRepository.findApplicationBySenderEmail(senderEmail);
                //first querying by email (instead of company name) to limit api calls to gpt. (need to call api to parse for company name)
                if (!application.isEmpty()) {
                    String body = newMsg.getPayload().getParts().get(0).getBody().getData();
                    String companyName = getCompanyName(body);
                    if (companyName.equals("NULL")) {
                        return "Couldn't parse mail";
                    }
                    if (body.contains(companyName)) {
                        //updateStatus contains # rows updated
                        int updateStatus = applicationRepository.updateApplicationBySenderEmail(senderEmail, StatusEnum.PENDING);
                    }
                }

            }
        } catch (IOException e) {
            System.out.println(e);
        }
        return "Could not parse message";
    }
    public String parseMessage(Message msg) throws IOException {

        if (msg == null) {
            return "Could not find new message";
        }

        List<MessagePartHeader> headers = msg.getPayload().getHeaders();
        String sender = getSenderEmail(headers);
        String contents = msg.getPayload().getParts().get(0).getBody().getData();

        if (contents == null) {
            return "Mail has no body";
        }
        String decodedContent = new String(Base64.getDecoder().decode(contents), StandardCharsets.UTF_8);
        if (! containsJobApplicationKeywords(decodedContent.toLowerCase())) {
            return "Didn't contain keywords";
        }

        String companyName = getCompanyName(contents);
        if (companyName.equals("NULL")) {
            return "Could not find company name in text";
        }

        //storing data (sender email, company name, application status) in db.
        Application newApp = new Application(sender, companyName, StatusEnum.APPLIED);
        try {
            applicationRepository.save(newApp);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            System.out.println(e);
        }
        return "";
    }

    /**
     * Second layer of filtering, uses openai's gpt-3.5-turbo model to extract, if present, the company name from the email.
     * @param body The text portion of a given email.
     * @return The name of the company if found; NULL if not found.
     */
    public String getCompanyName(String body) {
        OpenAiService service = new OpenAiService(System.getenv("OPENAI_API_KEY"));
        String context = "Your job is to extract the company name from the provided text. The output should only contain the company name, or \"NULL\" if there is no company name in the text";
        ChatMessage config = new ChatMessage("system", context);
        ChatMessage prompt = new ChatMessage("user", body);

        List<ChatMessage> gptInput = List.of(config, prompt);
        String res = "";
        try {
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .messages(gptInput)
                    .model("gpt-3.5-turbo")
                    .maxTokens(100)
                    .build();
            ChatCompletionChoice output = service.createChatCompletion(chatCompletionRequest).getChoices().get(0);
            res = output.getMessage().getContent();
        } catch (OpenAiHttpException e) {
            System.out.println(e);
        }

        return res;
    }

    /**
     * First layer of filtering, to get rid of unrelated emails. This is to reduce the amount of calls to openai's api/token usage.
     * @param body String containing the text portion of the email.
     * @return True if the text contains certain buzzwords. False if it is deemed to be an unrelated email.
     */
    public Boolean containsJobApplicationKeywords(String body) {
        String[] keywords = {"apply", "application", "applying", "interview"};
        for (String keyword: keywords) {
            if (body.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    public String getSenderEmail(List<MessagePartHeader> headers) {
        String sender = "";
        for (MessagePartHeader h: headers) {
            if (h.getName().equals("X-Google-Sender-Delegation")) {
                sender = h.getValue();
            }
        }
        return sender;
    }

    public Message getNewestMessage() {
        try {
            ListMessagesResponse a = gmail.users().messages().list("me").execute();
            List<Message> messages = a.getMessages();
            if (messages != null && !messages.isEmpty()) {
                return gmail.users().messages().get("me", messages.get(0).getId()).execute();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        return null;
    }

    /**
     * Verifies that the message has already been read or not, based on the presence of the "UNREAD" label.
     * @param msg The Message object that we want to validate.
     * @return Boolean: True if Message contains the label "UNREAD". False if not.
     */
    private Boolean isMessageUnread(Message msg) {
        List<String> labels = msg.getLabelIds();
        return labels.contains("UNREAD");
    }

    public String markMessageRead(Message msg) {
        try {
            gmail.users().messages().modify(
                            "me",
                            msg.getId(),
                            new ModifyMessageRequest().setRemoveLabelIds(Collections.singletonList("UNREAD")))
                    .execute();
            return "";
        } catch (IOException e) {
            return "Failed to delete Label.";
        }
    }
    public String testMethod(String x) {
        String y = "";
        System.out.println("hello");
        return y;
    }
}

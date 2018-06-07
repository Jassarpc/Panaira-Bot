package com.madageekscar.panaira.endpoints;

import com.madageekscar.panaira.searchengine.GoogleSearch;
import com.restfb.*;
import com.restfb.types.GraphResponse;
import com.restfb.types.send.*;
import com.restfb.types.webhook.WebhookEntry;
import com.restfb.types.webhook.WebhookObject;
import com.restfb.types.webhook.messaging.MessagingItem;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@WebServlet("/Webhook")
public class Webhook extends HttpServlet {

    private final String ACCESS_TOKEN = "EAAdyB0buNY0BAJW9MntJRRg8HH0VZCZBIY64JEMMbV1NdPeNGZBxiuNpYpQWbVKdGfZBWx8NPi7ZBwASg1o7S2zFOs28rHxyppCqnSzIZCcTB1vo61zkkFtspnYHDN0xDecuGxgvnrVNtfrxR4wVWEFZAKhKpI07uKnZAtY0RaMa3gZDZD";
    private final String VERIFY_TOKEN = "panairabot";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Parse the query params
        String mode = req.getParameter("hub.mode");
        String token = req.getParameter("hub.verify_token");
        String challenge = req.getParameter("hub.challenge");
        if (mode != null && token != null) {
            // Checks the mode and token sent is correct
            if (mode.equals("subscribe") && token.equals(VERIFY_TOKEN)) {
                resp.setStatus(200);
                resp.getWriter().write(challenge);
            } else {
                // Responds with '403 Forbidden' if verify tokens do not match
                resp.setStatus(403);
                System.err.println("TOKEN VERIFICATION FAILED!");
            }
        } else {
            resp.setStatus(200);
            resp.getWriter().write("TOKEN VERIFICATION FAILED!");
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String body = req.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
        DefaultJsonMapper mapper = new DefaultJsonMapper();
        WebhookObject webhookObject = mapper.toJavaObject(body, WebhookObject.class);

        for (WebhookEntry entry : webhookObject.getEntryList()) {
            if (!entry.getMessaging().isEmpty()) {
                for (MessagingItem item : entry.getMessaging()) {
                    String senderId = item.getSender().getId();
                    IdMessageRecipient recipient = new IdMessageRecipient(senderId);

                    //gestion des message
                    if (item.getMessage() != null) {

                        if (item.getMessage().getText() != null) {

                            Message message = new Message(item.getMessage().getText());
                            String keyword = message.getText();

                            if (keyword.contains("hey panaira")) {
                                showChoicesTemplate(recipient);
                            } else {
                                showGoogleSearchResults(recipient, GoogleSearch.search(keyword, 5));
                            }




                        }
                    }

                    //gestion des payloads
                    if (item.getPostback() != null) {

                        String payload = item.getPostback().getPayload();

                        if (payload.contains("GET_STARTED")) {
                            showChoicesTemplate(recipient);

                        }else if (payload.contains("GOOGLE_SEARCH")) {
                            Message message = new Message("Ohh Google Search Triggered! My boss is " +
                                    "implementing this feature. Be patient");
                            sendMessage(recipient, message);
                        }
                        else if (payload.contains("YOUTUBE_SEARCH")) {
                            Message message = new Message("Ohh Youtube Search Triggered! My boss is " +
                                    "implementing this feature. Be patient");
                            sendMessage(recipient, message);
                        }

                    }

                }
            }
        }
    }

    public void sendMessage(IdMessageRecipient idMR, Message message) {
        FacebookClient sendClient = new DefaultFacebookClient(ACCESS_TOKEN, Version.VERSION_2_6);
        sendClient.publish("me/messages", GraphResponse.class, Parameter.with("recipient", idMR),
                Parameter.with("message", message));
    }

    private void showChoicesTemplate(IdMessageRecipient recipient) {

        ButtonTemplatePayload payload = new ButtonTemplatePayload("What do you want ?");
        PostbackButton googleButton = new PostbackButton("GOOGLE SEARCH", "GOOGLE_SEARCH");
        PostbackButton youtubeButton = new PostbackButton("YOUTUBE SEARCH", "YOUTUBE_SEARCH");

        payload.addButton(googleButton);
        payload.addButton(youtubeButton);

        TemplateAttachment templateAttachment = new TemplateAttachment(payload);
        Message imageMessage = new Message(templateAttachment);

        sendMessage(recipient, imageMessage);
    }

    private void showGoogleSearchResults(IdMessageRecipient recipient, Map<String, String> results) {


        WebButton btn;

        ButtonTemplatePayload payload = new ButtonTemplatePayload("SEARCH RESULTS");

        for (Map.Entry<String, String> entry : results.entrySet()) {

            System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
            btn = new WebButton(entry.getKey(), entry.getValue());
            payload.addButton(btn);
        }

        TemplateAttachment templateAttachment = new TemplateAttachment(payload);
        Message message = new Message(templateAttachment);

        sendMessage(recipient, message);


    }
}

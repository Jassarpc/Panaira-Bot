package com.madageekscar.panaira.endpoints;

import com.madageekscar.panaira.results.GoogleResult;
import com.madageekscar.panaira.results.YoutubeResult;
import com.madageekscar.panaira.searchengine.GoogleSearch;
import com.madageekscar.panaira.searchengine.YoutubeSearch;
import com.madageekscar.panaira.utils.LinkFetcher;
import com.pdfcrowd.Pdfcrowd;
import com.restfb.*;
import com.restfb.types.GraphResponse;
import com.restfb.types.send.*;
import com.restfb.types.send.media.MediaTemplateAttachmentElement;
import com.restfb.types.webhook.WebhookEntry;
import com.restfb.types.webhook.WebhookObject;
import com.restfb.types.webhook.messaging.MessagingItem;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;


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
                                showStartMessage(recipient);


                            }else if(keyword.contains("youtube")) {
                                keyword = keyword.substring(7);
                                System.out.println("YOUTUBE KEYWORD ===>" + keyword);

                                showYoutubeResults(recipient, YoutubeSearch.search(keyword));

                                //notImplementedYet(recipient, "YOUTUBE KO" +" => " + keyword);
                            }
                            else {
                                showGoogleResults(recipient, GoogleSearch.search(keyword, 1));
                                //notImplementedYet(recipient, "GOOGLE KO !! => "+ keyword);
                            }

                        }
                    }

                    //gestion des payloads
                    if (item.getPostback() != null) {

                        String payload = item.getPostback().getPayload();

                        if (payload.contains("GET_STARTED")) {
                            showStartMessage(recipient);

                        }else if (payload.contains("GOOGLE_SEARCH")) {
                           notImplementedYet(recipient, "\"Ohh Google Search Triggered! My boss is \" +\n" +
                                   "                \"implementing this feature. Be patient");
                        }
                        else if (payload.contains("YOUTUBE_SEARCH")) {
                           notImplementedYet(recipient, "Youtube Search NOT implemented yest");
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

    private void showStartMessage(IdMessageRecipient recipient) {
        String guide = "Just give a keyword to perform a google search\n" +
                "For youtube search, put 'youtube' before your keyword. eg: " +
                "youtube world cup russia";

        Message startMessage = new Message(guide);
        sendMessage(recipient, startMessage);
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


    private void showGoogleResults(IdMessageRecipient recipient, List<GoogleResult> results) {

        GenericTemplatePayload payload = new GenericTemplatePayload();

        Bubble resultBubble;
        WebButton webButton;
        //PostbackButton getPdfButton = new PostbackButton("GET PDF", "GET_PDF");

        for (GoogleResult g : results) {

            if(g.getUrl().contains("http")) {
                System.out.println("Titre : " + g.getTitle() + " Lien : " + g.getUrl());
                resultBubble = new Bubble(g.getTitle());
                resultBubble.setImageUrl("https://lazandraha.com/google-logo.jpg");
                webButton = new WebButton("View", g.getUrl());
                resultBubble.addButton(webButton);

                webButton = new WebButton("GET PDF", getPdfFromGoogleSearch(g.getUrl()));
                resultBubble.addButton(webButton);

                //resultBubble.addButton(getPdfButton);
                payload.addBubble(resultBubble);
            }
        }

        TemplateAttachment templateAttachment = new TemplateAttachment(payload);
        Message message = new Message(templateAttachment);
        sendMessage(recipient, message);

    }


    private void showYoutubeResults(IdMessageRecipient recipient, List<YoutubeResult> results) {
        GenericTemplatePayload payload = new GenericTemplatePayload();

        Bubble resultBubble;
        WebButton webButton;

        for (YoutubeResult y : results) {

            if(y.getVideoUrl().contains("http")) {
                System.out.println("Title => " + y.getVideoTitle());
                System.out.println("Url => " + y.getVideoUrl());
                System.out.println("Thumbnail => " + y.getVideoThumbnail());
                System.out.println("=============================================");

                resultBubble = new Bubble(y.getVideoTitle());
                webButton = new WebButton("Watch", y.getVideoUrl());
                webButton.setWebviewHeightRatio(WebviewHeightEnum.compact);
                webButton.setMessengerExtensions(true, y.getVideoUrl());
                resultBubble.addButton(webButton);


                webButton = new WebButton("Download", y.getVideoUrl());


                resultBubble.setSubtitle(y.getVideoDesc());
                resultBubble.setImageUrl(y.getVideoThumbnail());


                resultBubble.addButton(webButton);
                payload.addBubble(resultBubble);
            }
        }

        TemplateAttachment templateAttachment = new TemplateAttachment(payload);
        Message message = new Message(templateAttachment);
        sendMessage(recipient, message);
    }




    private void testWebview(IdMessageRecipient recipient) {

        ButtonTemplatePayload payload = new ButtonTemplatePayload("What do you want ?");

        WebButton btn = new WebButton("Open WebView", "https://openclassrooms.com");
        btn.setWebviewHeightRatio(WebviewHeightEnum.compact);
        btn.setMessengerExtensions(true, btn.getUrl());

        payload.addButton(btn);

        TemplateAttachment templateAttachment = new TemplateAttachment(payload);
        Message imageMessage = new Message(templateAttachment);


        sendMessage(recipient, imageMessage);



    }

    private String getPdfFromGoogleSearch(String url)  {

        return LinkFetcher.fetch(url);

    }



    private void notImplementedYet(IdMessageRecipient recipient, String text ) {

        Message message = new Message(text);
        sendMessage(recipient, message);

    }


}

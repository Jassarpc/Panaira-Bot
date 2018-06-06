package com.madageekscar.panaira.endpoints;

import com.restfb.*;
import com.restfb.types.GraphResponse;
import com.restfb.types.send.IdMessageRecipient;
import com.restfb.types.send.Message;
import com.restfb.types.send.SendResponse;
import com.restfb.types.webhook.WebhookEntry;
import com.restfb.types.webhook.WebhookObject;
import com.restfb.types.webhook.messaging.MessagingItem;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet("/Webhook")
public class Webhook extends HttpServlet {

    private final String ACCESS_TOKEN = "EAAdyB0buNY0BAJW9MntJRRg8HH0VZCZBIY64JEMMbV1NdPeNGZBxiuNpYpQWbVKdGfZBWx8NPi7ZBwASg1o7S2zFOs28rHxyppCqnSzIZCcTB1vo61zkkFtspnYHDN0xDecuGxgvnrVNtfrxR4wVWEFZAKhKpI07uKnZAtY0RaMa3gZDZD";
    private final String VERIFY_TOKEN = "panairabot";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

        super.doGet(req, resp);
    }

    private void do_flush(HttpServletResponse resp) {
        try {
            resp.getWriter().flush();
            resp.getWriter().close();
        } catch (IOException e) {
            e.printStackTrace();
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
                    if (item.getMessage() != null && item.getMessage().getText() != null) {
                        Message simpleTextMessage = new Message("Echo: " + item.getMessage().getText());
                        FacebookClient sendClient = new DefaultFacebookClient(ACCESS_TOKEN, Version.VERSION_2_6);
                        sendClient.publish("me/messages", GraphResponse.class, Parameter.with("recipient", recipient),
                                Parameter.with("message", simpleTextMessage));
                    }

                    if (item.getPostback() != null) {
                    }
                }
            }
        }
    }

    public void sendMessage(IdMessageRecipient idMR, Message message) {
        FacebookClient fbClient = new DefaultFacebookClient(ACCESS_TOKEN, Version.VERSION_2_6);
        SendResponse sendResponse = fbClient.publish("me/messages",
                SendResponse.class, Parameter.with("recipient", idMR), Parameter.with("message", message));

    }

    public void handleMessage() {

    }

    public void handlePostBack() {

    }

    public void callSendAPI() {

    }
}

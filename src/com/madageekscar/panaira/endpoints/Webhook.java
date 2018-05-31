package com.madageekscar.panaira.endpoints;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet("/Webhook")
public class Webhook extends HttpServlet {

    private final String ACCESS_TOKEN = "";
    private final String VERIFY_TOKEN = "panairabot";
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            // Parse the query params
            String mode = req.getParameter("hub.mode");
            String token = req.getParameter("hub.verify_token");
            String challenge = req.getParameter("hub.challenge");

            // Checks if a token and mode is in the query string of the request
            if (mode != null && token != null) {

                // Checks the mode and token sent is correct
                if (mode.equals("subscribe") && token.equals(VERIFY_TOKEN)) {

                    // Responds with the challenge token from the request
                    System.out.println("WEBHOOK_VERIFIED");
                    resp.setStatus(200);
                    resp.getWriter().write("WEBHOOK_VERIFIED");
                } else {
                    // Responds with '403 Forbidden' if verify tokens do not match
                    resp.setStatus(403);
                    System.err.println("TOKEN VERIFICATION FAILED!");
                }
            }
        do_flush(resp);
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}

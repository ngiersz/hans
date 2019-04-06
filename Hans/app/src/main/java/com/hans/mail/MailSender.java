package com.hans.mail;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender extends AsyncTask<String, Integer, Void> {

    // TODO: properties in another file
    final String fromEmail = "";
    final String password = "";

    @Override
    protected Void doInBackground(String... strings) {
        String emailTo = strings[0];
        String subject = strings[1];
        String body = strings[2];
        try {
            sendEmail(emailTo, subject, body);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendEmail(String emailTo, String subject, String messageBody) throws MessagingException {
        Session session = Session.getInstance(getProperties(), getAuthenticator());

        MimeMessage message = createEmailMessage(session, emailTo, subject, messageBody);
        Log.d("mail", "Sending email...");
        Transport.send(message);
        Log.d("mail", "Mail to " + emailTo + " was send.");
    }

    private MimeMessage createEmailMessage(Session session, String toEmail, String subject, String body){
        MimeMessage msg = new MimeMessage(session);
        try {
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress(fromEmail, "Hans"));

            msg.setSubject(subject, "UTF-8");

            msg.setText(body, "UTF-8");

            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            Log.d("mail", "Message is ready");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }

    private Properties getProperties() {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.starttls.enable", "true");
        return prop;
    }

    private Authenticator getAuthenticator() {
        return new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };
    }
}

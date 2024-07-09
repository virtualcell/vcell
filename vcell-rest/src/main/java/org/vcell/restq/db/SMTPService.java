package org.vcell.restq.db;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@ApplicationScoped
public class SMTPService {

    @ConfigProperty(name = "vcell.smtp.hostName")
    String smtpHostName;

    @ConfigProperty(name = "vcell.smtp.port")
    String smtpPost;

    @ConfigProperty(name = "vcell.smtp.emailAddress")
    String smtpEmail;


    public void sendEmail(String emailAddress, String subject, String body) throws MessagingException {
        // Create a mail session
        java.util.Properties props = new java.util.Properties();
        props.put("mail.smtp.host", smtpHostName);
        props.put("mail.smtp.port", "" + Integer.parseInt(smtpPost));
        Session session = Session.getDefaultInstance(props, null);

        // Construct the message
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(smtpEmail));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(emailAddress));
        msg.setSubject(subject);
        msg.setText(body);

        // Send the message
        Transport.send(msg);
    }

}

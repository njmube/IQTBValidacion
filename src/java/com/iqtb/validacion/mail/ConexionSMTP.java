/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.mail;

import com.sun.mail.smtp.SMTPTransport;
import com.sun.net.ssl.internal.ssl.Provider;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Security;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author danielromero
 */
public class ConexionSMTP {

    private Session session;
    private SMTPTransport transport;
    private String server;
    private String port;
    private boolean sslConnection;
    private boolean requireAuthentication;
    private String username;
    private String password;
    private Message message;

    public ConexionSMTP(String server, String port, boolean sslConnection, boolean requireAuthentication) {
        this.server = server;
        this.port = port;
        this.sslConnection = sslConnection;
        this.requireAuthentication = requireAuthentication;
    }

    public void getSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", server);
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", port);
//    props.put("mail.smtp.debug", Boolean.valueOf(debug));
        try {
            props.put("mail.smtp.localhost", InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            props.put("mail.smtp.localhost", "localhost");
        }
//    if (timeOut != 0) {
//      props.put("mail.smtp.connectiontimeout", Integer.valueOf(timeOut));
//      props.put("mail.smtp.timeout", Integer.valueOf(timeOut));
//    }
        if ((sslConnection) && (requireAuthentication)) {
            props.put("mail.smtp.starttls.enable", "true");
            Security.addProvider(new Provider());
        }
        if (requireAuthentication) {
            props.put("mail.smtp.user", username);
            props.put("mail.smtp.auth", "true");
            Authenticator auth = new AutenticarEmail(username, password);
            session = Session.getInstance(props, auth);
        } else {
            session = Session.getDefaultInstance(props, null);
        }

//        return session;
    }

    public void connect()
            throws MessagingException {
        transport = ((SMTPTransport) session.getTransport("smtp"));
        transport.connect();
    }

    public void closeConnection()
            throws MessagingException {
        if (transport != null) {
            transport.close();
        }
    }

    public String sendMessage()
            throws MessagingException {
        if (transport.isConnected()) {
            transport.sendMessage(message, message.getAllRecipients());
            return transport.getLastServerResponse();
        }
        return "No existe una conexion con " + server;
    }

    public void createMessage(String destinatario, String asunto, String contenido) throws MessagingException {
        BodyPart texto = new MimeBodyPart();
        texto.setText("Esté correo electrónico es enviado desde la pagína Validación de CFDIs.\n" + contenido);

        MimeMultipart multiParte = new MimeMultipart();
        multiParte.addBodyPart(texto);

        message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username)); //remitente
        message.addRecipient(
                Message.RecipientType.TO,
                new InternetAddress(destinatario)); //destinatario
        message.setSubject(asunto); //asunto
        message.setContent(multiParte);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}

package com.iqtb.validacion.mail;

import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author danielromero
 */
public class Email {
    
    public static boolean envioEmail(String remitente, String contrasenia, String destinatario, String asunto, String contenido) throws Exception{
        boolean respuesta = false;
        try {
            // se obtiene el objeto Session. La configuración es para una cuenta de gmail.
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.setProperty("mail.smtp.starttls.enable", "true");
            props.setProperty("mail.smtp.port", "587");
            props.setProperty("mail.smtp.user", remitente);
            props.setProperty("mail.smtp.auth", "true");
            
            Session session = Session.getDefaultInstance(props, null);
//            session.setDebug(true);
            
            // Se compone la parte del texto
            BodyPart texto = new MimeBodyPart();
            texto.setText("Esté correo electrónico es enviado desde la pagína IQTB Validación.\n" +contenido);

            // Se compone el adjunto con la imagen
//            BodyPart adjunto = new MimeBodyPart();
//            adjunto.setDataHandler(
//                new DataHandler(new FileDataSource("/Users/danielromero/AAA010101AAA.xml")));
//            adjunto.setFileName("xml.xml");
            

            // Una MultiParte para agrupar texto e imagen.
            MimeMultipart multiParte = new MimeMultipart();
            multiParte.addBodyPart(texto);
//            multiParte.addBodyPart(adjunto);
            
            // Se compone el correo, dando to, from, subject y el
            // contenido.
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remitente)); //remitente
            message.addRecipient(
                Message.RecipientType.TO,
                new InternetAddress(destinatario)); //destinatario
            message.setSubject(asunto); //asunto
            message.setContent(multiParte);

            // Se envia el correo.
            Transport t = session.getTransport("smtp");
            t.connect(remitente, contrasenia);
            t.sendMessage(message, message.getAllRecipients());
            t.close();
            respuesta = true;
        } catch (Exception e) {
            respuesta = false;
            throw e;
        }
       return respuesta;
    }
    
    public static boolean sendEmailAuth(String remitente, String contrasenia, String destinatario, String asunto, String contenido, String host, String ssl, String puerto) throws Exception{
        boolean respuesta = false;
        try {
            // se obtiene el objeto Session. La configuración es para una cuenta de gmail.
            Properties props = new Properties();
            props.put("mail.smtp.host", host); //"smtp.gmail.com"
            props.setProperty("mail.smtp.starttls.enable", ssl);
            props.setProperty("mail.smtp.port", puerto);
            props.setProperty("mail.smtp.user", remitente);
            props.setProperty("mail.smtp.auth", "true");
            
            Session session = Session.getDefaultInstance(props, null);
//            session.setDebug(true);
            
            // Se compone la parte del texto
            BodyPart texto = new MimeBodyPart();
            texto.setText("Este es un correo de &lt;font color='red'&gt;&lt;b&gt;PRUEBA&lt;/b&gt;&lt;/font&gt;\n" +contenido);

            // Se compone el adjunto con la imagen
//            BodyPart adjunto = new MimeBodyPart();
//            adjunto.setDataHandler(
//                new DataHandler(new FileDataSource("/Users/danielromero/AAA010101AAA.xml")));
//            adjunto.setFileName("xml.xml");
            

            // Una MultiParte para agrupar texto e imagen.
            MimeMultipart multiParte = new MimeMultipart();
            multiParte.addBodyPart(texto);
//            multiParte.addBodyPart(adjunto);
            
            // Se compone el correo, dando to, from, subject y el
            // contenido.
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remitente)); //remitente
            message.addRecipient(
                Message.RecipientType.TO,
                new InternetAddress(destinatario)); //destinatario
            message.setSubject(asunto); //asunto
            message.setContent(multiParte);

            // Se envia el correo.
            Transport t = session.getTransport("smtp");
            t.connect(remitente, contrasenia);
            t.sendMessage(message, message.getAllRecipients());
            t.close();
            respuesta = true;
        } catch (Exception e) {
            respuesta = false;
            throw e;
        }
       return respuesta;
    }
    
}

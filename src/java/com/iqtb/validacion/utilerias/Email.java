package com.iqtb.validacion.utilerias;

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
    
    public static boolean envioEmail(String remitente, String destinatario, String asunto, String contenido) throws Exception{
        boolean respuesta = false;
        try {
            // se obtiene el objeto Session. La configuración es para una cuenta de gmail.
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.setProperty("mail.smtp.starttls.enable", "true");
            props.setProperty("mail.smtp.port", "587");
            props.setProperty("mail.smtp.user", "pruebas.email001@gmail.com");
            props.setProperty("mail.smtp.auth", "true");
            
            Session session = Session.getDefaultInstance(props, null);
            session.setDebug(true);
            
            // Se compone la parte del texto
            BodyPart texto = new MimeBodyPart();
            texto.setText("Su nueva contraseña es 123 ");

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
            message.setFrom(new InternetAddress("pruebas.email001@gmail.com")); //remitente
            message.addRecipient(
                Message.RecipientType.TO,
                new InternetAddress("d_alejandrok90@hotmail.com")); //destinatario
            message.setSubject("Se Ha restablecido su contraseña"); //asunto
            message.setContent(multiParte);

            // Se envia el correo.
            Transport t = session.getTransport("smtp");
            t.connect("pruebas.email001@gmail.com", "passpruebas");
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

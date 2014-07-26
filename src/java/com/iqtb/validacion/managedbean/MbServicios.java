/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoConfiguracionServicios;
import com.iqtb.validacion.dao.DaoServicio;
import com.iqtb.validacion.dao.DaoUsuario;
import static com.iqtb.validacion.encrypt.Encrypt.encrypt;
import com.iqtb.validacion.mail.ConexionSMTP;
import com.iqtb.validacion.pojo.ConfiguracionesServicios;
import com.iqtb.validacion.pojo.Servicios;
import com.iqtb.validacion.pojo.Usuarios;
import static com.iqtb.validacion.util.Bitacoras.errorBitacora;
import static com.iqtb.validacion.util.Bitacoras.insertBitacora;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import javax.servlet.ServletContext;
import net.sourceforge.lightcrypto.CryptoException;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author danielromero
 */
@ManagedBean
@ViewScoped
public class MbServicios {

    private Servicios servSMTP;
    private ConfiguracionesServicios hostSMTP;
    private ConfiguracionesServicios portSMTP;
    private ConfiguracionesServicios emailSMTP;
    private ConfiguracionesServicios userSMTP;
    private ConfiguracionesServicios passSMTP;
    private ConfiguracionesServicios sslSMTP;
    private ConfiguracionesServicios nombreSMTP;
    private String codigoEmail;
    private String passEncrypt;
    private String hash;
    private String hashSMTP;
    private UploadedFile file;
    private UploadedFile icono;
    private Usuarios usuario;
    private final String sessionUsuario;
    private FacesMessage msg;
    private static final Logger logger = Logger.getLogger(MbServicios.class);
    
    public MbServicios() {
        servSMTP = new Servicios();
        usuario = new Usuarios();
        this.sessionUsuario = ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid();
        try {
            servSMTP = new DaoServicio().getServicoByNombre("SERVIDOR_SMTP");
        } catch (Exception ex) {
            logger.error("Error al obtener el servicio ERROR: "+ex);
        }
        try {
            usuario = new DaoUsuario().getByUserid(sessionUsuario);
        } catch (Exception e) {
            logger.error("Error al obtener el Usuario ERROR: "+e);
        }
        configSMTP();
    }
    
    public void conexionSMTP() {
        boolean con = false;
        boolean auth;
        String codigo;
//        logger.info("conexionSMTP");
        try {
            if (hostSMTP.getValor() != null && !hostSMTP.getValor().isEmpty()) {
                if (portSMTP.getValor() != null && !portSMTP.getValor().isEmpty()) {
                    if (emailSMTP.getValor() != null && !emailSMTP.getValor().isEmpty()) {
                        if (codigoEmail != null && !codigoEmail.isEmpty()) {
                            if (userSMTP.getValor() != null && !userSMTP.getValor().trim().isEmpty() || passSMTP.getValor() != null && !passSMTP.getValor().trim().isEmpty()) {
                                auth = true;
                                codigo = hostSMTP.getValor() + hostSMTP.getValor();
                                passEncrypt = encrypt(passSMTP.getValor());
                                logger.info("conexionSMTP - Generando codigo de confirmación");
                                hash = encrypt(codigo);
                                logger.info("conexionSMTP - Encryptando codigo de confirmación");
                                String asunto = "Prueba de conexión SMTP";
                                String contenido = "Código de confirmación: " + hash;
                                boolean ssl = Boolean.valueOf(sslSMTP.getValor());
                                ConexionSMTP conSMTP = new ConexionSMTP(hostSMTP.getValor(), portSMTP.getValor(), ssl, auth);
                                conSMTP.setUsername(userSMTP.getValor());
                                conSMTP.setPassword(passSMTP.getValor());
                                conSMTP.getSession();
                                conSMTP.connect();
                                conSMTP.createMessage(codigoEmail, asunto, contenido);
                                String conn;
                                conn = conSMTP.sendMessage();
                                logger.debug("conn " + conn);
                                conSMTP.closeConnection();
                                if (conn.trim().charAt(0) == '2') {
                                    con = true;
                                    logger.info("conexionSMTP - Prueba de Conexion SMTP exitosa. Código de confirmación fue enviado a " + codigoEmail);
                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Prueba de conexión exitosa. Por favor, ingresa el código de confirmación.");

                                } else {
                                    logger.error("conexionSMTP - Prueba de Conexion SMTP Fallida.");
                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexión Falló. Por favor, revise los datos.");
                                }
                            } else {
                                auth = false;
                                codigo = hostSMTP.getValor() + portSMTP.getValor();
                                passEncrypt = encrypt(this.passSMTP.getValor());
                                logger.info("conexionSMTP - Generando codigo de confirmación");
                                this.hash = encrypt(codigo);
                                logger.info("conexionSMTP - Encryptando codigo de confirmación");
                                String asunto = "Prueba de conexión SMTP";
                                String contenido = "Código de confirmación: " + this.hash;
                                boolean ssl = Boolean.valueOf(sslSMTP.getValor());
                                ConexionSMTP conSMTP = new ConexionSMTP(hostSMTP.getValor(), portSMTP.getValor(), ssl, auth);
                                conSMTP.setUsername(emailSMTP.getValor());
                                conSMTP.getSession();
                                conSMTP.connect();
                                conSMTP.createMessage(codigoEmail, asunto, contenido);
                                String conn;
                                conn = conSMTP.sendMessage();
                                logger.debug("conn " + conn);
                                conSMTP.closeConnection();
                                if (conn.trim().charAt(0) == '2') {
                                    con = true;
                                    logger.info("conexionSMTP - Prueba de Conexion SMTP exitosa. Código de confirmación fue enviado a " + codigoEmail);
                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Prueba de conexión exitosa. Por favor, ingresa el código de confirmación.");

                                } else {
                                    logger.error("conexionSMTP - Prueba de Conexion SMTP Fallida.");
                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexión Falló. Por favor, revise los datos.");
                                }
                            }

                        } else {
                            logger.warn("conexionSMTP - Por favor, ingrese un valor para Enviar código a");
                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Error Por favor, ingrese un valor para Enviar código a.");
                        }
                    } else {
                        logger.warn("conexionSMTP - Por favor, ingrese un valor para De");
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Error Por favor, ingrese un valor para De.");
                    }
                } else {
                    logger.warn("conexionSMTP - Por favor, ingrese un valor para Puerto");
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Error Por favor, ingrese un valor para Puerto.");
                }
            } else {
                logger.warn("conexionSMTP - Por favor, ingrese un valor para Servidor");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Error Por favor, ingrese un valor para Servidor.");
            }
        } catch (CryptoException e) {
            logger.error("conexionSMTP - ERROR: " + e);
            String descripcion = "[SERVICIO] conexionSMTP - CryptoException ERROR: " + e;
            errorBitacora(servSMTP.getIdServicio(), descripcion, "ERROR");
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexión Fallo. Por favor, revise los datos.");
        } catch (IOException e) {
            logger.error("conexionSMTP - ERROR: " + e);
            String descripcion = "[SERVICIO] conexionSMTP - ERROR: " + e;
            errorBitacora(servSMTP.getIdServicio(), descripcion, "ERROR");
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexión Fallo. Por favor, revise los datos.");
        } catch (MessagingException ex) {
            logger.error("conexionSMTP - MessagingException ERROR: " + ex);
            String descripcion = "[SERVICIO] conexionSMTP - ERROR: " + ex;
            errorBitacora(servSMTP.getIdServicio(), descripcion, "ERROR");
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexión Fallo. Por favor, revise los datos.");
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("conexion", con);
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
    }
    
    public void updateSMTP() {
        boolean smtp = false;

        try {
            if (this.hashSMTP != null && !this.hashSMTP.trim().equals("")) {
                if (this.hashSMTP.equals(this.hash)) {
                    logger.info("updateSMTP - " + this.usuario.getUserid() + " Código de confirmacion es correcto");
                    DaoConfiguracionServicios daoConfigServicios = new DaoConfiguracionServicios();
                    daoConfigServicios.updateConfigServicios(hostSMTP);
                    logger.info("updateSMTP - " + usuario.getUserid() + " modifico "+servSMTP.getNombre()+ " host " + hostSMTP.getValor());
                    daoConfigServicios.updateConfigServicios(portSMTP);
                    logger.info("updateSMTP - " + usuario.getUserid() + " modifico "+servSMTP.getNombre()+ " port " + portSMTP.getValor());
                    daoConfigServicios.updateConfigServicios(nombreSMTP);
                    logger.info("updateSMTP - " + usuario.getUserid() + " modifico "+servSMTP.getNombre()+ " nombre " + nombreSMTP.getValor());
                    daoConfigServicios.updateConfigServicios(emailSMTP);
                    logger.info("updateSMTP - " + usuario.getUserid() + " modifico "+servSMTP.getNombre()+ " email " + emailSMTP.getValor());
                    daoConfigServicios.updateConfigServicios(userSMTP);
                    logger.info("updateSMTP - " + usuario.getUserid() + " modifico "+servSMTP.getNombre()+ " usuario " + userSMTP.getValor());
                    passSMTP.setValor(passEncrypt);
                    daoConfigServicios.updateConfigServicios(passSMTP);
                    logger.info("updateSMTP - " + usuario.getUserid() + " modifico "+servSMTP.getNombre()+ " pass " + passSMTP.getValor());
                    smtp = daoConfigServicios.updateConfigServicios(sslSMTP);
                    logger.info("updateSMTP - " + usuario.getUserid() + " modifico "+servSMTP.getNombre()+ " ssl " + sslSMTP.getValor());
                    if (smtp) {
                        String descripcion = "[SERVICIOS] Usuario: " + usuario.getUserid() + " modifico las configuraciones de SMTP.";
                        insertBitacora(usuario.getIdUsuario(), servSMTP.getIdServicio(), descripcion, "INFO");
                        logger.info("updateSMTP - Usuario: " + this.usuario.getUserid() + " modifico las configuraciones de SMTP");
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Servidor SMTP configurado.");
                    } else {
                        logger.error("updateSMTP - Usuario: " + this.usuario.getUserid() + " Error al guardar las configuraciones para le servidor SMTP.");
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error", "Error al guardar las configuraciones para le servidor SMTP.");
                    }
                } else {
                    logger.warn("Error código de confirmación no es válido.");
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error código de confirmación no es válido.");
                }
            } else {
                logger.warn("introdusca el código de confirmación.");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, introdusca el código de confirmación.");
            }

        } catch (Exception e) {
            logger.error("updateSMTP - ERROR: " + e);
            String descripcion = "[SERVICIOS] updateSMTP - ERROR: " + e;
            errorBitacora(servSMTP.getIdServicio(), descripcion, "ERROR");
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar las configuraciones para le servidor SMTP.");
        }
        hashSMTP = "";
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("smtp", smtp);
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
    }
    
    public String reloadEmail() {
        return "/Configuracion/servicioSMTP?faces-redirect=true";
    }
    
    private void configSMTP() {

        try {
            DaoConfiguracionServicios daoConfigServicios = new DaoConfiguracionServicios();
            hostSMTP = daoConfigServicios.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), "HOST_SMTP");
            portSMTP = daoConfigServicios.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), "PORT_SMTP");
            emailSMTP = daoConfigServicios.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), "EMAIL_SMTP");
            userSMTP = daoConfigServicios.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), "USER_SMTP");
            passSMTP = daoConfigServicios.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), "USER_SMTP");
            sslSMTP = daoConfigServicios.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), "SSL_SMTP");
            nombreSMTP = daoConfigServicios.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), "NOMBRE_SMTP");
        } catch (Exception e) {
            logger.error("configSMTP - ERROR al cargar configuraciones SMTP " + e);
        }
    }
    
    public void actualizarLogo() throws IOException {
        InputStream inputStream=null;
        OutputStream outputStream=null;
        
        try {
            if(file.getSize()<=0){
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Por favor, seleccione un archivo de imagen \".png\""));
                return;
            }
            
            if(!file.getFileName().endsWith(".png")){
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "El archivo debe ser con extensión \".png\""));
                return;
            }
            
            if(file.getSize()>2097152){
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "El archivo no puede ser más de 2MB"));
                return;
            }
            
            ServletContext servletContext=(ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext();
            String carpeta=(String)servletContext.getRealPath("/resources/images");
            
            outputStream=new FileOutputStream(new File(carpeta+"/logo.png"));
            inputStream=file.getInputstream();
            
            int read=0;
            byte[] bytes=new byte[1024];
            
            while((read=inputStream.read(bytes))!=-1){
                outputStream.write(bytes, 0, read);
            }
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto:", "Logo modificado correctamente."));
            logger.info("Logo modificado correctamente.");
        }
        catch(IOException ex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ha ocurrido un error al subir la imagen ERROR: "+ex.getMessage()));
            logger.error("Ha ocurrido un eror al subir la imagen ERROR: "+ex);
        }
        finally{
            if(inputStream!=null){
                inputStream.close();
            }
            
            if(outputStream!=null){
                outputStream.close();
            }
        }
    }
    
    public void actualizarIcono() throws IOException {
        InputStream inputStream=null;
        OutputStream outputStream=null;
        
        try {
            if(file.getSize()<=0){
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Por favor, seleccione un archivo de imagen \".ico\""));
                return;
            }
            
            if(!file.getFileName().endsWith(".ico")){
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "El archivo debe ser con extensión \".ico\""));
                return;
            }
            
            if(file.getSize()>2097152){
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "El archivo no puede ser más de 2MB"));
                return;
            }
            
            ServletContext servletContext=(ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext();
            String carpeta=(String)servletContext.getRealPath("/resources/images");
            
            outputStream=new FileOutputStream(new File(carpeta+"/favico.ico"));
            inputStream=file.getInputstream();
            
            int read=0;
            byte[] bytes=new byte[1024];
            
            while((read=inputStream.read(bytes))!=-1){
                outputStream.write(bytes, 0, read);
            }
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto:", "Icono modificado correctamente."));
            logger.info("Logo modificado correctamente.");
        }
        catch(IOException ex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ha ocurrido un error al subir la imagen ERROR: "+ex.getMessage()));
            logger.error("actualizarIcono - Ha ocurrido un eror al subir la imagen ERROR: "+ex);
        }
        finally{
            if(inputStream!=null){
                inputStream.close();
            }
            
            if(outputStream!=null){
                outputStream.close();
            }
        }
    }

    public ConfiguracionesServicios getHostSMTP() {
        return hostSMTP;
    }

    public void setHostSMTP(ConfiguracionesServicios hostSMTP) {
        this.hostSMTP = hostSMTP;
    }

    public ConfiguracionesServicios getPortSMTP() {
        return portSMTP;
    }

    public void setPortSMTP(ConfiguracionesServicios portSMTP) {
        this.portSMTP = portSMTP;
    }

    public ConfiguracionesServicios getEmailSMTP() {
        return emailSMTP;
    }

    public void setEmailSMTP(ConfiguracionesServicios emailSMTP) {
        this.emailSMTP = emailSMTP;
    }

    public ConfiguracionesServicios getUserSMTP() {
        return userSMTP;
    }

    public void setUserSMTP(ConfiguracionesServicios userSMTP) {
        this.userSMTP = userSMTP;
    }

    public ConfiguracionesServicios getPassSMTP() {
        return passSMTP;
    }

    public void setPassSMTP(ConfiguracionesServicios passSMTP) {
        this.passSMTP = passSMTP;
    }

    public ConfiguracionesServicios getSslSMTP() {
        return sslSMTP;
    }

    public void setSslSMTP(ConfiguracionesServicios sslSMTP) {
        this.sslSMTP = sslSMTP;
    }

    public ConfiguracionesServicios getNombreSMTP() {
        return nombreSMTP;
    }

    public void setNombreSMTP(ConfiguracionesServicios nombreSMTP) {
        this.nombreSMTP = nombreSMTP;
    }

    public String getCodigoEmail() {
        return codigoEmail;
    }

    public void setCodigoEmail(String codigoEmail) {
        this.codigoEmail = codigoEmail;
    }

    public String getHashSMTP() {
        return hashSMTP;
    }

    public void setHashSMTP(String hashSMTP) {
        this.hashSMTP = hashSMTP;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public UploadedFile getIcono() {
        return icono;
    }

    public void setIcono(UploadedFile icono) {
        this.icono = icono;
    }
    
}

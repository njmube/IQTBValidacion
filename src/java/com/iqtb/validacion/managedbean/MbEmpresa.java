/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoConfiguracionEmpresa;
import com.iqtb.validacion.dao.DaoEmpresa;
import com.iqtb.validacion.dao.DaoUsuario;
import static com.iqtb.validacion.encrypt.Encrypt.encrypt;
import com.iqtb.validacion.mail.ConexionSMTP;
import com.iqtb.validacion.mail.Email;
import com.iqtb.validacion.pojo.ConfiguracionesEmpresas;
import com.iqtb.validacion.pojo.Empresas;
import com.iqtb.validacion.pojo.Usuarios;
import com.iqtb.validacion.pojo.UsuariosHasEmpresasId;
import com.iqtb.validacion.util.Bitacoras;
import static com.iqtb.validacion.util.Bitacoras.errorBitacora;
import static com.iqtb.validacion.util.Bitacoras.insertBitacora;
import static com.iqtb.validacion.util.Bitacoras.registrarBitacora;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import net.sourceforge.lightcrypto.CryptoException;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;

/**
 *
 * @author danielromero
 */
@ManagedBean
@ViewScoped
public class MbEmpresa implements Serializable {

    private Empresas empresa;
    private List<Empresas> listaEmpresas;
    private List<Empresas> empresasSeleccionadas;
    private Usuarios usuario;
    private boolean xmlValido;
    private boolean xmlInvalido;
    private boolean validoEmail;
    private boolean invalidoEmail;
    private List<String> emails;
    private String newEmail;
    private String configValor;
    private String strHostSMTP;
    private String strPortSMTP;
    private String strNombreSMTP;
    private String strEmailSMTP;
    private String strUserSMTP;
    private String strPassSMTP;
    private String strEmailCodigo;
    private boolean sslSMTP;
    private String passEncryptSMTP;
    private String hash;
    private String hashSMTP;
    private boolean connectSMTP;
    private boolean confirSMTP;
    private String strTipoServidor;
    private String strservidorRecepcion;
    private String strPuertoRecepcion;
    private String strUsuarioRecepcion;
    private String strPassRecepcion;
    private boolean sslRecepcion;
    private String passEncRecepcion;
    private boolean confirmPOP;
    private final String sessionUsuario;
    private FacesMessage msg;
    private static final Logger logger = Logger.getLogger(MbEmpresa.class);

    public MbEmpresa() {
        this.empresa = new Empresas();
        this.listaEmpresas = new ArrayList<Empresas>();
        this.empresasSeleccionadas = new ArrayList<Empresas>();
        xmlValido = false;
        xmlInvalido = false;
        validoEmail = false;
        invalidoEmail = false;
        newEmail = "";
        emails = new ArrayList<String>();
        confirSMTP = false;
        connectSMTP = false;
        confirmPOP = false;

        this.sessionUsuario = ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid();

        try {
            this.usuario = new DaoUsuario().getByUserid(sessionUsuario);
        } catch (Exception e) {
            logger.error("Error al obtener el Usuario ERROR " + e);
        }
    }

    public Empresas getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresas empresa) {
        this.empresa = empresa;
    }

    public List<Empresas> getListaEmpresas() {
        try {
            listaEmpresas = new DaoEmpresa().getEmpresas();
        } catch (Exception e) {
            System.out.println("getListaEmpresas - ERROR " + e);
        }
        return listaEmpresas;
    }

    public void setListaEmpresas(List<Empresas> listaEmpresas) {
        this.listaEmpresas = listaEmpresas;
    }

    public List<Empresas> getEmpresasSeleccionadas() {
        return empresasSeleccionadas;
    }

    public void setEmpresasSeleccionadas(List<Empresas> empresasSeleccionadas) {
        this.empresasSeleccionadas = empresasSeleccionadas;
    }

    public boolean isXmlValido() {
        return xmlValido;
    }

    public void setXmlValido(boolean xmlValido) {
        this.xmlValido = xmlValido;
    }

    public boolean isXmlInvalido() {
        return xmlInvalido;
    }

    public void setXmlInvalido(boolean xmlInvalido) {
        this.xmlInvalido = xmlInvalido;
    }

    public boolean isValidoEmail() {
        return validoEmail;
    }

    public void setValidoEmail(boolean validoEmail) {
        this.validoEmail = validoEmail;
    }

    public boolean isInvalidoEmail() {
        return invalidoEmail;
    }

    public void setInvalidoEmail(boolean invalidoEmail) {
        this.invalidoEmail = invalidoEmail;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public String getStrHostSMTP() {
        return strHostSMTP;
    }

    public void setStrHostSMTP(String strHostSMTP) {
        this.strHostSMTP = strHostSMTP;
    }

    public String getStrPortSMTP() {
        return strPortSMTP;
    }

    public void setStrPortSMTP(String strPortSMTP) {
        this.strPortSMTP = strPortSMTP;
    }

    public String getStrEmailSMTP() {
        return strEmailSMTP;
    }

    public void setStrEmailSMTP(String strEmailSMTP) {
        this.strEmailSMTP = strEmailSMTP;
    }

    public String getStrUserSMTP() {
        return strUserSMTP;
    }

    public void setStrUserSMTP(String strUserSMTP) {
        this.strUserSMTP = strUserSMTP;
    }

    public String getStrPassSMTP() {
        return strPassSMTP;
    }

    public void setStrPassSMTP(String strPassSMTP) {
        this.strPassSMTP = strPassSMTP;
    }

    public String getStrEmailCodigo() {
        return strEmailCodigo;
    }

    public void setStrEmailCodigo(String strEmailCodigo) {
        this.strEmailCodigo = strEmailCodigo;
    }

    public String getStrNombreSMTP() {
        return strNombreSMTP;
    }

    public void setStrNombreSMTP(String strNombreSMTP) {
        this.strNombreSMTP = strNombreSMTP;
    }

    public boolean isSslSMTP() {
        return sslSMTP;
    }

    public void setSslSMTP(boolean sslSMTP) {
        this.sslSMTP = sslSMTP;
    }

    public String getHashSMTP() {
        return hashSMTP;
    }

    public void setHashSMTP(String hashSMTP) {
        this.hashSMTP = hashSMTP;
    }

    public boolean isConnectSMTP() {
        return connectSMTP;
    }

    public void setConnectSMTP(boolean connectSMTP) {
        this.connectSMTP = connectSMTP;
    }

    public String getStrTipoServidor() {
        return strTipoServidor;
    }

    public void setStrTipoServidor(String strTipoServidor) {
        this.strTipoServidor = strTipoServidor;
    }

    public String getStrservidorRecepcion() {
        return strservidorRecepcion;
    }

    public void setStrservidorRecepcion(String strservidorRecepcion) {
        this.strservidorRecepcion = strservidorRecepcion;
    }

    public String getStrPuertoRecepcion() {
        return strPuertoRecepcion;
    }

    public void setStrPuertoRecepcion(String strPuertoRecepcion) {
        this.strPuertoRecepcion = strPuertoRecepcion;
    }

    public String getStrUsuarioRecepcion() {
        return strUsuarioRecepcion;
    }

    public void setStrUsuarioRecepcion(String strUsuarioRecepcion) {
        this.strUsuarioRecepcion = strUsuarioRecepcion;
    }

    public String getStrPassRecepcion() {
        return strPassRecepcion;
    }

    public void setStrPassRecepcion(String strPassRecepcion) {
        this.strPassRecepcion = strPassRecepcion;
    }

    public boolean isSslRecepcion() {
        return sslRecepcion;
    }

    public void setSslRecepcion(boolean sslRecepcion) {
        this.sslRecepcion = sslRecepcion;
    }

    public String getConfigValor() {
        return configValor;
    }

    public void setConfigValor(String configValor) {
        this.configValor = configValor;
    }

    public void resetEmpresa() {
        this.empresa = new Empresas();
    }

    public boolean insertEmpresa() {
        boolean insert = false;

        try {
            if (empresa.getNombre() != null && !empresa.getNombre().trim().isEmpty()) {
                if (empresa.getRfc() != null && !empresa.getRfc().trim().isEmpty()) {
                    if (empresa.getCalle() != null && !empresa.getCalle().trim().isEmpty()) {
                        DaoEmpresa daoEmpresa = new DaoEmpresa();
                        if (daoEmpresa.getEmpresaByRFC(empresa.getRfc()) == null) {
                            insert = true;
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Los datos de la Empresa correctos");
//                            insert = daoEmpresa.updateEmpresa(this.empresa);
//                            if (insert) {
//                                insertConfigEmpresa();
//                                String descripcion = "[EMPRESA] Usuario: " + this.usuario.getUserid() + " registro la Empresa " + this.empresa.getRfc() + ".";
//                                registrarBitacora(this.usuario.getIdUsuario(), 4, this.empresa.getIdEmpresa(), descripcion, "INFO");
//
//                                logger.info("insertEmpresa - " + this.usuario.getUserid() + " registro la Empresa " + this.empresa.getRfc());
//                                this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Los datos de la Empresa han sido Registrados.");
//
//                            } else {
//                                logger.error("insertEmpresa - Error al registar los datos de la Empresa " + this.empresa.getRfc());
//                                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al registar los datos de la Empresa.");
//                            }
                        } else {
                            logger.warn("insertEmpresa - Ya se encuetra registrado este RFC " + empresa.getRfc());
                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "RFC " + empresa.getRfc() + " ya se encuentra registrado.");
                        }
                    } else {
                        logger.warn("insertEmpresa - Calle de la Empresa es requerido");
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Calle.");
                    }
                } else {
                    logger.warn("insertEmpresa - RFC de la Empresa es requerido");
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para RFC.");
                }
            } else {
                logger.warn("insertEmpresa - Nombre de la Empresa es requerido");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Nombre.");
            }

        } catch (Exception e) {
            String descripcion = "[EMPRESA] updateEmpresa - ERROR: " + e;
            insertBitacora(usuario.getIdUsuario(), 4, descripcion, "ERROR");
            logger.error("insertEmpresa - ERROR: " + e);
        }

//        RequestContext context = RequestContext.getCurrentInstance();
//        context.addCallbackParam("insert", insert);
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
        return insert;
    }

    public void updateEmpresa() {
        boolean updateEmpresa = false;

        try {
            if (this.empresa.getNombre() != null && !this.empresa.getNombre().trim().isEmpty()) {
                if (this.empresa.getCalle() != null && !this.empresa.getCalle().trim().isEmpty()) {
                    DaoEmpresa daoEmpresa = new DaoEmpresa();
                    updateEmpresa = daoEmpresa.updateEmpresa(this.empresa);
                    if (updateEmpresa) {
                        String descripcion = "[EMPRESA] Usuario: " + this.usuario.getUserid() + " modifico los datos de la Empresa " + this.empresa.getRfc() + ".";
                        registrarBitacora(this.usuario.getIdUsuario(), 2, this.empresa.getIdEmpresa(), descripcion, "INFO");

                        logger.info("updateEmpresa - " + this.usuario.getUserid() + " ha modificado los datos de la Empresa " + this.empresa.getRfc());
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Los datos de la Empresa han sido Modificados.");

                    } else {
                        logger.error("updateEmpresa - Error al modificar los datos de la Empresa " + this.empresa.getRfc());
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al modificar los datos de la Empresa.");
                    }

                } else {
                    logger.warn("updateEmpresa - Calle de la Empresa es requerido");
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Calle es un campo requerido.");
                }

            } else {
                logger.warn("updateEmpresa - Nombre de la Empresa es requerido");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Nombre es un campo requerido.");
            }

        } catch (Exception e) {
            String descripcion = "[EMPRESA] updateEmpresa - ERROR: " + e;
            insertBitacora(usuario.getIdUsuario(), 4, descripcion, "ERROR");
            logger.error("updateEmpresa - ERROR: " + e);
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("actualiza", updateEmpresa);
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
    }

    public void deleteEmpresas() {
        boolean delete = false;
        for (Empresas emp : empresasSeleccionadas) {
            this.empresa = emp;
            try {
                delete = new DaoEmpresa().deleteEmpresa(empresa);
                if (delete) {
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La Empresa " + empresa.getRfc() + " ha sido eliminada");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    logger.info("deleteEmpresas - Empresa " + empresa.getRfc() + " ha sido eliminada");
                } else {
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se puede eliminar la Empresa " + empresa.getRfc());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    logger.error("deleteEmpresas - No se puede eliminar la Empresa " + empresa.getRfc());
                }
            } catch (Exception e) {
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "ha ocurrido un error al intentar eliminar la Empresa " + empresa.getRfc());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                logger.error("deleteEmpresas - ERROR " + e);
            }
        }
    }

    public String insertNewEmail() {
        String str = "";

        try {

            if (this.newEmail != null && !this.newEmail.trim().isEmpty()) {
                if (this.emails.contains(this.newEmail)) {
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Duplicado", "Dirección de correo electrónico ya está registrada.");
                    logger.warn("inserterNewEmail - dirección de correo electrónico ya existe en la lista " + newEmail);
                } else {
                    this.emails.add(this.newEmail);
                    logger.info("inserterNewEmail - dirección de correo electrónico agregado a la lista " + newEmail);
                    this.newEmail = "";
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Dirección de correo electrónico ha sido agradada.");
                    int i = 0;
                    for (String string : emails) {
                        if (i == 0) {
                            str += string;
                        } else {
                            str += "," + string;
                        }
                        i++;
                    }
                    this.configValor = str.trim();
                    logger.info("String con email: " + this.configValor);
                }
            } else {
                logger.warn("inserterNewEmail - debe insertar un email valido");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, agregue una dirección de correo.");
            }
        } catch (Exception e) {
            String descripcion = "[EMPRESA] insertNewEmail - ERROR: " + e;
            insertBitacora(usuario.getIdUsuario(), 4, descripcion, "ERROR");
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al agregar la dirección de correo.");
            logger.error("inserterNewEmail - ERROR: " + e);
        }
        newEmail = "";
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
        return this.configValor;
    }

    public String removeEmail() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();

        String email = (String) params.get("emailRemove");
        String str = "";
        this.emails.remove(email);
        logger.info("removeEmail - dirección de correo electrónico ha sido removida de la lista " + email);
        int i = 0;
        for (String string : emails) {
            if (i == 0) {
                str += string;
            } else {
                str += "," + string;
            }
            i++;
        }
        this.configValor = str.trim();

        return this.configValor;
    }

    public void conexionSMTP() {
        boolean auth;
        String codigo;
//        logger.info("conexionSMTP");
        try {
            if (strHostSMTP != null && !strHostSMTP.isEmpty()) {
                if (strPortSMTP != null && !strPortSMTP.isEmpty()) {
                    if (strEmailSMTP != null && !strEmailSMTP.isEmpty()) {
                        if (strEmailCodigo != null && !strEmailCodigo.isEmpty()) {
                            if (strUserSMTP != null && !strUserSMTP.trim().isEmpty() || strPassSMTP != null && !strPassSMTP.trim().isEmpty()) {
                                auth = true;
                                codigo = strHostSMTP + strPortSMTP;
                                passEncryptSMTP = encrypt(strPassSMTP);
                                logger.info("conexionSMTP - Generando codigo de confirmación");
                                hash = encrypt(codigo);
                                logger.info("conexionSMTP - Encryptando codigo de confirmación");
                                String asunto = "Prueba de conexión SMTP";
                                String contenido = "Código de confirmación: " + this.hash;
                                ConexionSMTP conSMTP = new ConexionSMTP(strHostSMTP, strPortSMTP, sslSMTP, auth);
                                conSMTP.setUsername(strUserSMTP);
                                conSMTP.setPassword(strPassSMTP);
                                conSMTP.getSession();
                                conSMTP.connect();
                                conSMTP.createMessage(strEmailCodigo, asunto, contenido);
                                String conn;
                                conn = conSMTP.sendMessage();
                                logger.debug("conn " + conn);
                                conSMTP.closeConnection();
                                if (conn.trim().charAt(0) == '2') {
                                    connectSMTP = true;
                                    logger.info("conexionSMTP - Prueba de Conexion SMTP exitosa. Código de confirmación fue enviado a " + strEmailCodigo);
                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Prueba de conexión exitosa. Por favor, ingresa el código de confirmación.");

                                } else {
                                    logger.error("conexionSMTP - Prueba de Conexion SMTP Fallida.");
                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexión Falló. Por favor, revise los datos.");
                                }
                            } else {
                                auth = false;
                                codigo = strHostSMTP + strPortSMTP;
                                logger.info("conexionSMTP - Generando codigo de confirmación");
                                this.hash = encrypt(codigo);
                                logger.info("conexionSMTP - Encryptando codigo de confirmación");
                                String asunto = "Prueba de conexión SMTP";
                                String contenido = "Código de confirmación: " + this.hash;
                                ConexionSMTP conSMTP = new ConexionSMTP(strHostSMTP, strPortSMTP, sslSMTP, auth);
                                conSMTP.setUsername(strEmailSMTP);
                                conSMTP.getSession();
                                conSMTP.connect();
                                conSMTP.createMessage(strEmailCodigo, asunto, contenido);
                                String conn;
                                conn = conSMTP.sendMessage();
                                logger.debug("conn " + conn);
                                conSMTP.closeConnection();
                                if (conn.trim().charAt(0) == '2') {
                                    connectSMTP = true;
                                    logger.info("conexionSMTP - Prueba de Conexion SMTP exitosa. Código de confirmación fue enviado a " + strEmailCodigo);
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
            String descripcion = "[EMPRESA] conexionSMTP - CryptoException ERROR: " + e;
            errorBitacora(2, descripcion, "ERROR");
            logger.error("conexionSMTP - ERROR: " + e);
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexión Fallo. Por favor, revise los datos.");
        } catch (IOException e) {
            String descripcion = "[EMPRESA] conexionSMTP - ERROR: " + e;
            errorBitacora(2, descripcion, "ERROR");
            logger.error("conexionSMTP - ERROR: " + e);
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexión Fallo. Por favor, revise los datos.");
        } catch (MessagingException ex) {
            String descripcion = "[EMPRESA] conexionSMTP - ERROR: " + ex;
            errorBitacora(2, descripcion, "ERROR");
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexión Fallo. Por favor, revise los datos.");
            logger.error("conexionSMTP - MessagingException ERROR: " + ex);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void confirmSMTP() {

        try {
            if (hashSMTP != null && !hashSMTP.trim().equals("")) {
                if (hashSMTP.equals(hash)) {
                    logger.info("updateSMTP - " + usuario.getUserid() + " Código de confirmacion es correcto");
                    confirSMTP = true;
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Código de confirmacion es correcto.");
                } else {
                    logger.warn("Error código de confirmación no es válido.");
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error código de confirmación no es válido.");
                }
            } else {
                logger.warn("introdusca el código de confirmación.");
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, introdusca el código de confirmación.");
            }

        } catch (Exception e) {
            String descripcion = "[EMPRESA] updateSMTP - ERROR: " + e;
            errorBitacora(2, descripcion, "ERROR");
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar las configuraciones para le servidor SMTP.");
            logger.error("updateSMTP - ERROR: " + e);
        }
        this.hashSMTP = "";
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("smtp", confirSMTP);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void conexionPOP() {
        boolean recepcion = false;
        logger.info("conexionPOP");
        try {
            if (!strTipoServidor.equals("")) {
                if (strservidorRecepcion != null && !strservidorRecepcion.trim().isEmpty()) {
                    if (strPuertoRecepcion != null && !strPuertoRecepcion.trim().isEmpty()) {
                        if (strUsuarioRecepcion != null && !strUsuarioRecepcion.trim().isEmpty()) {
                            if (strTipoServidor.equals("pop3")) {
                                recepcion = Email.connect(strservidorRecepcion, strUsuarioRecepcion, strPassRecepcion, strPuertoRecepcion, sslRecepcion);
                                if (recepcion) {
                                    passEncRecepcion = encrypt(strPassRecepcion);
                                    confirmPOP = true;
                                    logger.info("conexionPOP - Conexion POP3 correcto.");
                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Conexion POP3 exitosa.");
                                } else {
                                    logger.error("conexionPOP - Conexion POP3 fallo.");
                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexion POP3 Fallo. Por favor, revise los datos.");
                                }
                            } else {
                                recepcion = Email.connectIMAP(strservidorRecepcion, strUsuarioRecepcion, strPassRecepcion, strPuertoRecepcion, sslRecepcion);
                                if (recepcion) {
                                    passEncRecepcion = encrypt(strPassRecepcion);
                                    confirmPOP = true;
                                    logger.info("Conexion IMAP ok.");
                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Conexion IMAP exitosa.");
                                } else {
                                    logger.error("Conexion IMAP fallo.");
                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexion IMAP Fallo. Por favor, revise los datos.");
                                }
                            }
                        } else {
                            logger.warn("Error Usuario es un campo requerido.");
                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error usuario es un campo requerido.");
                        }
                    } else {
                        logger.warn("Error Puerto es un campo requerido.");
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error puerto es un campo requerido.");
                    }
                } else {
                    logger.warn("Error Servidor es un campo requerido.");
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error servidor es un campo requerido.");
                }
            } else {
                logger.warn("Error debe seleccionar IMAP/POP3.");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, seleccione un cliente de correo IMAP/POP3.");
            }
        } catch (CryptoException e) {
            String descripcion = "[EMPRESA] conexionPOP - CryptoException ERROR:" + e;
            errorBitacora(3, descripcion, "ERROR");
            logger.error("conexionPOP CryptoException ERROR: " + e);
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error: " + e.getMessage());
        } catch (IOException e) {
            String descripcion = "[EMPRESA] conexionPOP - IOException ERROR:" + e;
            errorBitacora(2, descripcion, "ERROR");
            logger.error("conexionPOP IOException ERROR: " + e);
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error: " + e.getMessage());
        } catch (Exception ex) {
            String descripcion = "[EMPRESA] conexionPOP - IOException ERROR:" + ex;
            errorBitacora(2, descripcion, "ERROR");
            logger.error("conexionPOP ERROR " + ex);
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error: " + ex.getMessage());
        }
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
    }

    public void saveEmpresaConfig() {

        boolean insert;
        try {
            insert = new DaoEmpresa().updateEmpresa(empresa);
            if (insert) {
                insertConfigEmpresa();
                String descripcion = "[EMPRESA] Usuario: " + usuario.getUserid() + " registro la Empresa " + empresa.getRfc() + ".";
                registrarBitacora(usuario.getIdUsuario(), 4, empresa.getIdEmpresa(), descripcion, "INFO");

                logger.info("insertEmpresa - " + usuario.getUserid() + " registro la Empresa " + empresa.getRfc());
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Los datos de la Empresa han sido Registrados.");

            } else {
                logger.error("insertEmpresa - Error al registar los datos de la Empresa " + this.empresa.getRfc());
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al registar los datos de la Empresa.");
            }
        } catch (Exception ex) {
            logger.error("saveEmpresaConfig - ERROR "+ex);
        }

    }

    public String onFlowProcess(FlowEvent event) {

        if (event.getNewStep().equals("notificacion")) {
            if (insertEmpresa()) {
                return event.getNewStep();
            } else {
                return event.getOldStep();
            }
        }
        if (event.getNewStep().equals("pop")) {
            if (confirSMTP) {
                connectSMTP = false;
                return event.getNewStep();
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, Configure un servidor para envio de correo electrónico.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return event.getOldStep();
            }
        }
        if (event.getNewStep().equals("confirm")) {
            if (confirmPOP) {
                return event.getNewStep();
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, Configure un servidor para recepción de correo electrónico.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return event.getOldStep();
            }
        }
        return event.getNewStep();
    }

    public String existeSeleccionEmpresa() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();

        String parametro = (String) params.get("nombreParametro");
        System.out.println("parametro: " + parametro);

        boolean estadoEmpresa = false;
        if (this.empresasSeleccionadas.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione una empresa.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            if (parametro != null) {
                if (parametro.equals("eliminar")) {
                    estadoEmpresa = true;
                }
            } else if (empresasSeleccionadas.size() > 1) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione solo una empresa.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                for (Empresas empresas : empresasSeleccionadas) {
                    this.empresa = empresas;
                }
                estadoEmpresa = true;
            }

        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("estadoEmpresa", estadoEmpresa);

        return "/Empresa/empresas?faces-redirect=true";
    }

    private void insertConfigEmpresa() {
        try {
            ConfiguracionesEmpresas configEmpresa = new ConfiguracionesEmpresas();
            configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
            configEmpresa.setPropiedad("NOTIFICAR_XML_VALIDO");
            String strXmlValido = String.valueOf(xmlValido);
            configEmpresa.setValor(strXmlValido);
            configEmpresa.setDescripcion("Enviar notificación de XML válido al remitente.");
            configEmpresa.setTipo("BOOLEANO");
            if (new DaoConfiguracionEmpresa().insertConfiguracionEmpresa(configEmpresa)) {
                logger.info("insertConfigEmpresa - Se inserto la configuracion NOTIFICAR_XML_VALIDO para la empresa " + empresa.getRfc());
            } else {
                logger.error("insertConfigEmpresa - Error al insertar la configuracion NOTIFICAR_XML_VALIDO para la empresa " + empresa.getRfc());
            }

            configEmpresa = new ConfiguracionesEmpresas();
            configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
            configEmpresa.setPropiedad("NOTIFICAR_XML_INVALIDO");
            String strXmlInvalido = String.valueOf(xmlInvalido);
            configEmpresa.setValor(strXmlInvalido);
            configEmpresa.setDescripcion("Enviar notificación de XML inválido al remitente.");
            configEmpresa.setTipo("BOOLEANO");
            if (new DaoConfiguracionEmpresa().insertConfiguracionEmpresa(configEmpresa)) {
                logger.info("insertConfigEmpresa - Se inserto la configuracion NOTIFICAR_XML_INVALIDO para la empresa " + empresa.getRfc());
            } else {
                logger.error("insertConfigEmpresa - Error al insertar la configuracion NOTIFICAR_XML_INVALIDO para la empresa " + empresa.getRfc());
            }

            configEmpresa = new ConfiguracionesEmpresas();
            configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
            configEmpresa.setPropiedad("XML_VALIDO_EMAIL");
            String strValidoEmail = String.valueOf(validoEmail);
            configEmpresa.setValor(strValidoEmail);
            configEmpresa.setDescripcion("Enviar notificación de XML válido al correo registrado.");
            configEmpresa.setTipo("BOOLEANO");
            if (new DaoConfiguracionEmpresa().insertConfiguracionEmpresa(configEmpresa)) {
                logger.info("insertConfigEmpresa - Se inserto la configuracion XML_VALIDO_EMAIL para la empresa " + empresa.getRfc());
            } else {
                logger.error("insertConfigEmpresa - Error al insertar la configuracion XML_VALIDO_EMAIL para la empresa " + empresa.getRfc());
            }

            configEmpresa = new ConfiguracionesEmpresas();
            configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
            configEmpresa.setPropiedad("XML_INVALIDO_EMAIL");
            String strInvalidoEmail = String.valueOf(xmlInvalido);
            configEmpresa.setValor(strInvalidoEmail);
            configEmpresa.setDescripcion("Enviar notificación de XML inválido al correo registrado.");
            configEmpresa.setTipo("BOOLEANO");
            if (new DaoConfiguracionEmpresa().insertConfiguracionEmpresa(configEmpresa)) {
                logger.info("insertConfigEmpresa - Se inserto la configuracion XML_INVALIDO_EMAIL para la empresa " + empresa.getRfc());
            } else {
                logger.error("insertConfigEmpresa - Error al insertar la configuracion XML_INVALIDO_EMAIL para la empresa " + empresa.getRfc());
            }

            configEmpresa = new ConfiguracionesEmpresas();
            configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
            configEmpresa.setPropiedad("EMAILS_RECIBEN_NOTIFICACION");
            configEmpresa.setValor(configValor);
            configEmpresa.setDescripcion("Lista de correos que reciben notificaciones (separados por coma).");
            configEmpresa.setTipo("TEXTO");
            if (new DaoConfiguracionEmpresa().insertConfiguracionEmpresa(configEmpresa)) {
                logger.info("insertConfigEmpresa - Se inserto la configuracion EMAILS_RECIBEN_NOTIFICACION para la empresa " + empresa.getRfc());
            } else {
                logger.error("insertConfigEmpresa - Error al insertar la configuracion EMAILS_RECIBEN_NOTIFICACION para la empresa " + empresa.getRfc());
            }

            configEmpresa = new ConfiguracionesEmpresas();
            configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
            configEmpresa.setPropiedad("EMAIL_RECEPCION");
            configEmpresa.setValor(strUsuarioRecepcion);
            configEmpresa.setDescripcion("Direccion de recepcion de correo.");
            configEmpresa.setTipo("TEXTO");
            if (new DaoConfiguracionEmpresa().insertConfiguracionEmpresa(configEmpresa)) {
                logger.info("insertConfigEmpresa - Se inserto la configuracion EMAIL_RECEPCION para la empresa " + empresa.getRfc());
            } else {
                logger.error("insertConfigEmpresa - Error al insertar la configuracion EMAIL_RECEPCION para la empresa " + empresa.getRfc());
            }

            configEmpresa = new ConfiguracionesEmpresas();
            configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
            configEmpresa.setPropiedad("RECEPCION_PASS");
            configEmpresa.setValor(passEncRecepcion);
            configEmpresa.setDescripcion("Pass de recepcion de correo.");
            configEmpresa.setTipo("TEXTO");
            if (new DaoConfiguracionEmpresa().insertConfiguracionEmpresa(configEmpresa)) {
                logger.info("insertConfigEmpresa - Se inserto la configuracion RECEPCION_PASS para la empresa " + empresa.getRfc());
            } else {
                logger.error("insertConfigEmpresa - Error al insertar la configuracion RECEPCION_PASS para la empresa " + empresa.getRfc());
            }

            configEmpresa = new ConfiguracionesEmpresas();
            configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
            configEmpresa.setPropiedad("HOST_RECEPCION");
            configEmpresa.setValor(strservidorRecepcion);
            configEmpresa.setDescripcion("Host de recepcion de correo.");
            configEmpresa.setTipo("TEXTO");
            if (new DaoConfiguracionEmpresa().insertConfiguracionEmpresa(configEmpresa)) {
                System.out.println("insertConfigEmpresa - Se inserto la configuracion HOST_RECEPCION para la empresa " + empresa.getRfc());
            } else {
                System.out.println("insertConfigEmpresa - Error al insertar la configuracion HOST_RECEPCION para la empresa " + empresa.getRfc());
            }

            configEmpresa = new ConfiguracionesEmpresas();
            configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
            configEmpresa.setPropiedad("PUERTO_RECEPCION");
            configEmpresa.setValor(strPuertoRecepcion);
            configEmpresa.setDescripcion("Indica el puerto a utilizar para la recepcion.");
            configEmpresa.setTipo("TEXTO");
            if (new DaoConfiguracionEmpresa().insertConfiguracionEmpresa(configEmpresa)) {
                System.out.println("insertConfigEmpresa - Se inserto la configuracion PUERTO_RECEPCION para la empresa " + empresa.getRfc());
            } else {
                System.out.println("insertConfigEmpresa - Error al insertar la configuracion PUERTO_RECEPCION para la empresa " + empresa.getRfc());
            }

            configEmpresa = new ConfiguracionesEmpresas();
            configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
            configEmpresa.setPropiedad("START_TLS");
            configEmpresa.setValor("");
            configEmpresa.setDescripcion("Informa si se utiliza TLS.");
            configEmpresa.setTipo("BOOLEANO");
            if (new DaoConfiguracionEmpresa().insertConfiguracionEmpresa(configEmpresa)) {
                System.out.println("insertConfigEmpresa - Se inserto la configuracion START_TLS para la empresa " + empresa.getRfc());
            } else {
                System.out.println("insertConfigEmpresa - Error al insertar la configuracion START_TLS para la empresa " + empresa.getRfc());
            }

            configEmpresa = new ConfiguracionesEmpresas();
            configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
            configEmpresa.setPropiedad("PROTOCOLO_RECEPCION");
            configEmpresa.setValor(strTipoServidor);
            configEmpresa.setDescripcion("Indica el protocolo a utilizar para recepcion.");
            configEmpresa.setTipo("TEXTO");
            if (new DaoConfiguracionEmpresa().insertConfiguracionEmpresa(configEmpresa)) {
                System.out.println("insertConfigEmpresa - Se inserto la configuracion PROTOCOLO_RECEPCION para la empresa " + empresa.getRfc());
            } else {
                System.out.println("insertConfigEmpresa - Error al insertar la configuracion PROTOCOLO_RECEPCION para la empresa " + empresa.getRfc());
            }

            configEmpresa = new ConfiguracionesEmpresas();
            configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
            configEmpresa.setPropiedad("UTILIZAR_SSL");
            String sslR = String.valueOf(sslRecepcion);
            configEmpresa.setValor(sslR);
            configEmpresa.setDescripcion("Indica si se utiliza SSL.");
            configEmpresa.setTipo("BOOLEANO");
            if (new DaoConfiguracionEmpresa().insertConfiguracionEmpresa(configEmpresa)) {
                System.out.println("insertConfigEmpresa - Se inserto la configuracion UTILIZAR_SSL para la empresa " + empresa.getRfc());
            } else {
                System.out.println("insertConfigEmpresa - Error al insertar la configuracion UTILIZAR_SSL para la empresa " + empresa.getRfc());
            }

            configEmpresa = new ConfiguracionesEmpresas();
            configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
            configEmpresa.setPropiedad("EMAIL_SMTP");
            configEmpresa.setValor(strEmailSMTP);
            configEmpresa.setDescripcion("Direccion de correo electrónico para SMTP.");
            configEmpresa.setTipo("TEXTO");
            if (new DaoConfiguracionEmpresa().insertConfiguracionEmpresa(configEmpresa)) {
                System.out.println("insertConfigEmpresa - Se inserto la configuracion EMAIL_SMTP para la empresa " + empresa.getRfc());
            } else {
                System.out.println("insertConfigEmpresa - Error al insertar la configuracion EMAIL_SMTP para la empresa " + empresa.getRfc());
            }

            configEmpresa = new ConfiguracionesEmpresas();
            configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
            configEmpresa.setPropiedad("PASS_SMTP");
            configEmpresa.setValor(passEncryptSMTP);
            configEmpresa.setDescripcion("Contraseña de correo electrónico para SMTP.");
            configEmpresa.setTipo("TEXTO");
            if (new DaoConfiguracionEmpresa().insertConfiguracionEmpresa(configEmpresa)) {
                System.out.println("insertConfigEmpresa - Se inserto la configuracion PASS_SMTP para la empresa " + empresa.getRfc());
            } else {
                System.out.println("insertConfigEmpresa - Error al insertar la configuracion PASS_SMTP para la empresa " + empresa.getRfc());
            }

            configEmpresa = new ConfiguracionesEmpresas();
            configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
            configEmpresa.setPropiedad("HOST_SMTP");
            configEmpresa.setValor(strHostSMTP);
            configEmpresa.setDescripcion("Servidor SMTP.");
            configEmpresa.setTipo("TEXTO");
            if (new DaoConfiguracionEmpresa().insertConfiguracionEmpresa(configEmpresa)) {
                System.out.println("insertConfigEmpresa - Se inserto la configuracion HOST_SMTP para la empresa " + empresa.getRfc());
            } else {
                System.out.println("insertConfigEmpresa - Error al insertar la configuracion HOST_SMTP para la empresa " + empresa.getRfc());
            }

            configEmpresa = new ConfiguracionesEmpresas();
            configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
            configEmpresa.setPropiedad("USUARIO_SMTP");
            configEmpresa.setValor(strUserSMTP);
            configEmpresa.setDescripcion("Usuario de correo electrónico para SMTP.");
            configEmpresa.setTipo("TEXTO");
            if (new DaoConfiguracionEmpresa().insertConfiguracionEmpresa(configEmpresa)) {
                System.out.println("insertConfigEmpresa - Se inserto la configuracion USUARIO_SMTP para la empresa " + empresa.getRfc());
            } else {
                System.out.println("insertConfigEmpresa - Error al insertar la configuracion USUARIO_SMTP para la empresa " + empresa.getRfc());
            }

            configEmpresa = new ConfiguracionesEmpresas();
            configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
            configEmpresa.setPropiedad("PUERTO_SMTP");
            configEmpresa.setValor(strPortSMTP);
            configEmpresa.setDescripcion("Indica el puerto a utilizar para SMTP.");
            configEmpresa.setTipo("TEXTO");
            if (new DaoConfiguracionEmpresa().insertConfiguracionEmpresa(configEmpresa)) {
                System.out.println("insertConfigEmpresa - Se inserto la configuracion PUERTO_SMTP para la empresa " + empresa.getRfc());
            } else {
                System.out.println("insertConfigEmpresa - Error al insertar la configuracion PUERTO_SMTP para la empresa " + empresa.getRfc());
            }

            configEmpresa = new ConfiguracionesEmpresas();
            configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
            configEmpresa.setPropiedad("NOMBRE_SMTP");
            configEmpresa.setValor(strNombreSMTP);
            configEmpresa.setDescripcion("Nombre configuracion de SMTP.");
            configEmpresa.setTipo("TEXTO");
            if (new DaoConfiguracionEmpresa().insertConfiguracionEmpresa(configEmpresa)) {
                System.out.println("insertConfigEmpresa - Se inserto la configuracion NOMBRE_SMTP para la empresa " + empresa.getRfc());
            } else {
                System.out.println("insertConfigEmpresa - Error al insertar la configuracion NOMBRE_SMTP para la empresa " + empresa.getRfc());
            }

            configEmpresa = new ConfiguracionesEmpresas();
            configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
            configEmpresa.setPropiedad("SSL_SMTP");
            String ssl = String.valueOf(sslSMTP);
            configEmpresa.setValor(ssl);
            configEmpresa.setDescripcion("Informa si se utiliza SSL en SMTP.");
            configEmpresa.setTipo("BOOLEANO");
            if (new DaoConfiguracionEmpresa().insertConfiguracionEmpresa(configEmpresa)) {
                System.out.println("insertConfigEmpresa - Se inserto la configuracion SSL_SMTP para la empresa " + empresa.getRfc());
            } else {
                System.out.println("insertConfigEmpresa - Error al insertar la configuracion SSL_SMTP para la empresa " + empresa.getRfc());
            }

        } catch (Exception e) {
            System.out.println("insertConfigEmpresa - ERROR " + e);
        }
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoConfiguracionEmpresa;
import com.iqtb.validacion.dao.DaoConfiguracionServicios;
import com.iqtb.validacion.dao.DaoEmpresa;
import com.iqtb.validacion.dao.DaoServicio;
import com.iqtb.validacion.dao.DaoUsuario;
import static com.iqtb.validacion.encrypt.Encrypt.encrypt;
import com.iqtb.validacion.mail.ConexionSMTP;
import com.iqtb.validacion.mail.Email;
import com.iqtb.validacion.pojo.Bitacora;
import com.iqtb.validacion.pojo.ConfiguracionesEmpresas;
import com.iqtb.validacion.pojo.ConfiguracionesServicios;
import com.iqtb.validacion.pojo.Empresas;
import com.iqtb.validacion.pojo.Servicios;
import com.iqtb.validacion.pojo.Usuarios;
import static com.iqtb.validacion.util.Bitacoras.insertBitacora;
import static com.iqtb.validacion.util.Bitacoras.registrarBitacora;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import net.sourceforge.lightcrypto.CryptoException;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

/**
 *
 * @author danielromero
 */
@ManagedBean
@ViewScoped
public class MbConfiguracion implements Serializable {

    private Empresas empresa;
    private ConfiguracionesEmpresas configEmpresa;
    private ConfiguracionesEmpresas listaEmail;
    private ConfiguracionesEmpresas validoRemitente;
    private ConfiguracionesEmpresas invalidoRemitente;
    private ConfiguracionesEmpresas validoEmail;
    private ConfiguracionesEmpresas invalidoEmail;
    private String newEmail;
    private List<String> emails;
    private String configValor;
    private ConfiguracionesEmpresas servidorSMTP;
    private ConfiguracionesEmpresas puertoSMTP;
    private ConfiguracionesEmpresas nombreSMTP;
    private ConfiguracionesEmpresas emailSMTP;
    private ConfiguracionesEmpresas usuarioSMTP;
    private ConfiguracionesEmpresas passSMTP;
    private ConfiguracionesEmpresas SSL_SMTP;
    private String enviarCodigo;
    private String hashSMTP;
    private String hash;
    private ConfiguracionesEmpresas servidorRecepcion;
    private ConfiguracionesEmpresas puertoRecepcion;
    private ConfiguracionesEmpresas usuarioRecepcion;
    private ConfiguracionesEmpresas passRecepcion;
    private ConfiguracionesEmpresas tlsRecepcion;
    private ConfiguracionesEmpresas sslRecepcion;
    private ConfiguracionesEmpresas protocoloRecepcion;
    private String tipoServidor;
    private String pw;

    private final String sessionUsuario;
    private Bitacora bitacora;
    private Usuarios usuario;
    private Servicios servicioRecepcion;
    private ConfiguracionesServicios teRecepcion;
    private ConfiguracionesServicios trcRecepcion;
    private ConfiguracionesServicios teValidacion;
    private ConfiguracionesServicios trcValidacion;
    private ConfiguracionesServicios wsValidacion;
    private ConfiguracionesServicios teEnvio;
    private ConfiguracionesServicios trcEnvio;
    private ConfiguracionesServicios mdEnvio;
    private ConfiguracionesServicios expPass;
    private ConfiguracionesServicios inactividad;
    private ConfiguracionesServicios minPass;
    private ConfiguracionesServicios minIntentos;
    private String descripcion;
    private FacesMessage msg;
    private static final Logger logger = Logger.getLogger(MbConfiguracion.class);

    public MbConfiguracion() {
        this.empresa = new Empresas();
        this.configEmpresa = new ConfiguracionesEmpresas();
        this.listaEmail = new ConfiguracionesEmpresas();
        this.emails = new ArrayList<String>();
        this.newEmail = "";
        this.bitacora = new Bitacora();
        this.usuario = new Usuarios();
        validoRemitente = new ConfiguracionesEmpresas();
        invalidoRemitente = new ConfiguracionesEmpresas();
        validoEmail = new ConfiguracionesEmpresas();
        invalidoEmail = new ConfiguracionesEmpresas();

        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        String empresaSeleccionada = (String) httpServletRequest.getSession().getAttribute("empresaSeleccionada");
        this.sessionUsuario = ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid();
        try {
            this.usuario = new DaoUsuario().getByUserid(this.sessionUsuario);
            if (httpServletRequest.getSession().getAttribute("empresaSeleccionada") != null) {
                empresa = new DaoEmpresa().getEmpresaByRFC(empresaSeleccionada);
                configNotificaciones();
                configEmail();
                configSMTP();
                configRecepcion();
                logger.info("Se han cargado las Configuraciones de la Empresa " + empresa.getRfc());
            } else {
                servRecepcion();
                servValidacion();
                servEnvio();
                servAcceso();
                logger.info("Se han cargado las Configuraciones de Servicios");
            }
        } catch (Exception e) {
            logger.error("Error al obtener USUARIO y EMPRESA. ERROR: " + e);
        }
    }

    public Empresas getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresas empresa) {
        this.empresa = empresa;
    }

    public ConfiguracionesEmpresas getConfigEmpresa() {
        try {
            configEmpresa = new DaoConfiguracionEmpresa().getConfiguracionEmpresa(empresa.getIdEmpresa(), "EMAILS_RECIBEN_NOTIFICACION");
        } catch (Exception e) {
            logger.error("getConfigEmpresa - ERROR " + e);
        }
        return configEmpresa;
    }

    public void setConfigEmpresa(ConfiguracionesEmpresas configEmpresa) {
        this.configEmpresa = configEmpresa;
    }

    public ConfiguracionesEmpresas getListaEmail() {
        return listaEmail;
    }

    public void setListaEmail(ConfiguracionesEmpresas listaEmail) {
        this.listaEmail = listaEmail;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public ConfiguracionesEmpresas getValidoRemitente() {
        return validoRemitente;
    }

    public void setValidoRemitente(ConfiguracionesEmpresas validoRemitente) {
        this.validoRemitente = validoRemitente;
    }

    public ConfiguracionesEmpresas getInvalidoRemitente() {
        return invalidoRemitente;
    }

    public void setInvalidoRemitente(ConfiguracionesEmpresas invalidoRemitente) {
        this.invalidoRemitente = invalidoRemitente;
    }

    public ConfiguracionesEmpresas getValidoEmail() {
        return validoEmail;
    }

    public void setValidoEmail(ConfiguracionesEmpresas validoEmail) {
        this.validoEmail = validoEmail;
    }

    public ConfiguracionesEmpresas getInvalidoEmail() {
        return invalidoEmail;
    }

    public void setInvalidoEmail(ConfiguracionesEmpresas invalidoEmail) {
        this.invalidoEmail = invalidoEmail;
    }

    public ConfiguracionesEmpresas getServidorSMTP() {
        return servidorSMTP;
    }

    public void setServidorSMTP(ConfiguracionesEmpresas servidorSMTP) {
        this.servidorSMTP = servidorSMTP;
    }

    public ConfiguracionesEmpresas getPuertoSMTP() {
        return puertoSMTP;
    }

    public void setPuertoSMTP(ConfiguracionesEmpresas puertoSMTP) {
        this.puertoSMTP = puertoSMTP;
    }

    public ConfiguracionesEmpresas getNombreSMTP() {
        return nombreSMTP;
    }

    public void setNombreSMTP(ConfiguracionesEmpresas nombreSMTP) {
        this.nombreSMTP = nombreSMTP;
    }

    public ConfiguracionesEmpresas getEmailSMTP() {
        return emailSMTP;
    }

    public void setEmialSMTP(ConfiguracionesEmpresas emialSMTP) {
        this.emailSMTP = emialSMTP;
    }

    public ConfiguracionesEmpresas getUsuarioSMTP() {
        return usuarioSMTP;
    }

    public void setUsuarioSMTP(ConfiguracionesEmpresas usuarioSMTP) {
        this.usuarioSMTP = usuarioSMTP;
    }

    public ConfiguracionesEmpresas getPassSMTP() {
        return passSMTP;
    }

    public void setPassSMTP(ConfiguracionesEmpresas passSMTP) {
        this.passSMTP = passSMTP;
    }

    public ConfiguracionesEmpresas getSSL_SMTP() {
        return SSL_SMTP;
    }

    public void setSSL_SMTP(ConfiguracionesEmpresas SSL_SMTP) {
        this.SSL_SMTP = SSL_SMTP;
    }

    public String getEnviarCodigo() {
        return enviarCodigo;
    }

    public void setEnviarCodigo(String enviarCodigo) {
        this.enviarCodigo = enviarCodigo;
    }

    public String getHashSMTP() {
        return hashSMTP;
    }

    public void setHashSMTP(String hashSMTP) {
        this.hashSMTP = hashSMTP;
    }

    public String getTipoServidor() {
        return tipoServidor;
    }

    public void setTipoServidor(String tipoServidor) {
        this.tipoServidor = tipoServidor;
    }

    public ConfiguracionesEmpresas getServidorRecepcion() {
        return servidorRecepcion;
    }

    public void setServidorRecepcion(ConfiguracionesEmpresas servidorRecepcion) {
        this.servidorRecepcion = servidorRecepcion;
    }

    public ConfiguracionesEmpresas getPuertoRecepcion() {
        return puertoRecepcion;
    }

    public void setPuertoRecepcion(ConfiguracionesEmpresas puertoRecepcion) {
        this.puertoRecepcion = puertoRecepcion;
    }

    public ConfiguracionesEmpresas getUsuarioRecepcion() {
        return usuarioRecepcion;
    }

    public void setUsuarioRecepcion(ConfiguracionesEmpresas usuarioRecepcion) {
        this.usuarioRecepcion = usuarioRecepcion;
    }

    public ConfiguracionesEmpresas getPassRecepcion() {
        return passRecepcion;
    }

    public void setPassRecepcion(ConfiguracionesEmpresas passRecepcion) {
        this.passRecepcion = passRecepcion;
    }

    public ConfiguracionesEmpresas getTlsRecepcion() {
        return tlsRecepcion;
    }

    public void setTlsRecepcion(ConfiguracionesEmpresas tlsRecepcion) {
        this.tlsRecepcion = tlsRecepcion;
    }

    public ConfiguracionesEmpresas getSslRecepcion() {
        return sslRecepcion;
    }

    public void setSslRecepcion(ConfiguracionesEmpresas sslRecepcion) {
        this.sslRecepcion = sslRecepcion;
    }

    public ConfiguracionesServicios getTeRecepcion() {
        return teRecepcion;
    }

    public void setTeRecepcion(ConfiguracionesServicios teRecepcion) {
        this.teRecepcion = teRecepcion;
    }

    public ConfiguracionesServicios getTrcRecepcion() {
        return trcRecepcion;
    }

    public void setTrcRecepcion(ConfiguracionesServicios trcRecepcion) {
        this.trcRecepcion = trcRecepcion;
    }

    public ConfiguracionesServicios getTeValidacion() {
        return teValidacion;
    }

    public void setTeValidacion(ConfiguracionesServicios teValidacion) {
        this.teValidacion = teValidacion;
    }

    public ConfiguracionesServicios getTrcValidacion() {
        return trcValidacion;
    }

    public void setTrcValidacion(ConfiguracionesServicios trcValidacion) {
        this.trcValidacion = trcValidacion;
    }

    public ConfiguracionesServicios getWsValidacion() {
        return wsValidacion;
    }

    public void setWsValidacion(ConfiguracionesServicios wsValidacion) {
        this.wsValidacion = wsValidacion;
    }

    public ConfiguracionesServicios getTeEnvio() {
        return teEnvio;
    }

    public void setTeEnvio(ConfiguracionesServicios teEnvio) {
        this.teEnvio = teEnvio;
    }

    public ConfiguracionesServicios getTrcEnvio() {
        return trcEnvio;
    }

    public void setTrcEnvio(ConfiguracionesServicios trcEnvio) {
        this.trcEnvio = trcEnvio;
    }

    public ConfiguracionesServicios getMdEnvio() {
        return mdEnvio;
    }

    public void setMdEnvio(ConfiguracionesServicios mdEnvio) {
        this.mdEnvio = mdEnvio;
    }

    public ConfiguracionesServicios getExpPass() {
        return expPass;
    }

    public void setExpPass(ConfiguracionesServicios expPass) {
        this.expPass = expPass;
    }

    public ConfiguracionesServicios getInactividad() {
        return inactividad;
    }

    public void setInactividad(ConfiguracionesServicios inactividad) {
        this.inactividad = inactividad;
    }

    public ConfiguracionesServicios getMinIntentos() {
        return minIntentos;
    }

    public void setMinIntentos(ConfiguracionesServicios minIntentos) {
        this.minIntentos = minIntentos;
    }

    public ConfiguracionesServicios getMinPass() {
        return minPass;
    }

    public void setMinPass(ConfiguracionesServicios minPass) {
        this.minPass = minPass;
    }

    public void reinit() {
        this.newEmail = "";
    }

    public void updateEmpresa() {
        boolean updateEmpresa = false;

        try {
            if (this.empresa.getNombre() != null && !this.empresa.getNombre().trim().isEmpty()) {
                if (this.empresa.getCalle() != null && !this.empresa.getCalle().trim().isEmpty()) {
                    DaoEmpresa daoEmpresa = new DaoEmpresa();
                    updateEmpresa = daoEmpresa.updateEmpresa(this.empresa);
                    if (updateEmpresa) {
                        this.descripcion = "[CONFIG_EMPRESA] Usuario: " + this.usuario.getUserid() + " modifico los datos de la Empresa " + this.empresa.getRfc() + ".";
                        registrarBitacora(this.usuario.getIdUsuario(), 2, this.empresa.getIdEmpresa(), this.descripcion, "INFO");

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
            descripcion = "[CONFIG_EMPRESA] updateEmpresa - ERROR: " + e;
            insertBitacora(usuario.getIdUsuario(), 4, descripcion, "ERROR");
            logger.error("updateEmpresa - ERROR: " + e);
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("actualiza", updateEmpresa);
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
    }

    public String insertNewEmail() {
        String str = "";

        try {
//            logger.info("inserterNewEmail");

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
            descripcion = "[CONFIG_EMPRESA] insertNewEmail - ERROR: " + e;
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

    public void updateCE() {
        boolean addEmail = false;

        try {
            this.configEmpresa.setValor(this.configValor);
            System.out.println("configvalor: " + this.configValor);
            logger.info("direcciones de correo electrónico " + this.configEmpresa.getValor());
            DaoConfiguracionEmpresa daoConfigEmpresa = new DaoConfiguracionEmpresa();

            addEmail = daoConfigEmpresa.updateConfiguracionEmpresa(this.configEmpresa);

            if (addEmail) {
                descripcion = "[CONFIG_EMPRESA] Usuario: " + this.usuario.getUserid() + " modifico la lista de correo que reciben notificaciones.";
                registrarBitacora(usuario.getIdUsuario(), 2, empresa.getIdEmpresa(), descripcion, "INFO");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Dirección de correo electrónico ha sido registrada.");
                logger.info("updateCE - " + this.usuario.getUserid() + " ha modificado las direcciones de correo que reciben notificacioens");
            } else {
                logger.error("updateCE - " + this.usuario.getUserid() + " ha ocurrido un error al modificar correos que reciben notificaciones");
            }

        } catch (Exception e) {
            descripcion = "[CONFIG_EMPRESA] updateCE - ERROR: " + e;
            registrarBitacora(this.usuario.getIdUsuario(), 2, this.empresa.getIdEmpresa(), this.descripcion, "ERROR");
            logger.error("updateCE - ERROR: " + e);
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al agregar la dirección de correo.");
        }
        newEmail = "";
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
    }

    public String valor(String propiedad) {

        try {
            DaoConfiguracionEmpresa daoConfigEmpresa = new DaoConfiguracionEmpresa();
            this.configEmpresa = daoConfigEmpresa.getConfiguracionEmpresa(this.empresa.getIdEmpresa(), propiedad);
        } catch (Exception e) {
            descripcion = "[CONFIG_EMPRESA] getValor - ERROR: " + e;
            registrarBitacora(this.usuario.getIdUsuario(), 2, this.empresa.getIdEmpresa(), this.descripcion, "ERROR");
            logger.error("getValor - ERROR: " + e);
        }
        return configEmpresa.getValor();
    }

    public void checkConfiguracionEmpresa() {
        boolean updateConfig = false;
        logger.info("checkConfiguracionEmpresa");

        try {
            DaoConfiguracionEmpresa daoConfigEmpresa = new DaoConfiguracionEmpresa();
            daoConfigEmpresa.updateConfiguracionEmpresa(this.validoRemitente);
            daoConfigEmpresa.updateConfiguracionEmpresa(this.invalidoRemitente);
            daoConfigEmpresa.updateConfiguracionEmpresa(this.validoEmail);
            updateConfig = daoConfigEmpresa.updateConfiguracionEmpresa(this.invalidoEmail);

            if (updateConfig) {
                descripcion = "[CONFIG_EMPRESA] Usuario: " + this.usuario.getUserid() + " Empresa seleccionada: " + this.empresa.getRfc() + " modifico las notificaciones para XMLs válidos/inválidos.";
                registrarBitacora(usuario.getIdUsuario(), 3, empresa.getIdEmpresa(), descripcion, "INFO");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Los cambios se han guardado.");
                logger.info("Usuario: " + usuario.getUserid() + " modifico las notificaciones para XMLs válidos/inválidos");
            } else {
                logger.error("Usuario: " + usuario.getUserid() + " ha ocurrido un error al modificar las notificaciones para XMLs válidos/inválidos");
            }

        } catch (Exception e) {
            descripcion = "[CONFIG_EMPRESA] checkConfiguracionEmpresa - ERROR: " + e;
            registrarBitacora(usuario.getIdUsuario(), 3, empresa.getIdEmpresa(), descripcion, "INFO");
            logger.error("checkConfiguracionEmpresa - ERROR: " + e);
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", " Ocurrio un error durante la modificación");
        }
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
    }

    public void conexionSMTP() {
        boolean con = false;
        boolean auth;
        String codigo;
//        logger.info("conexionSMTP");
        try {
            if (servidorSMTP.getValor() != null && !servidorSMTP.getValor().isEmpty()) {
                if (puertoSMTP.getValor() != null && !puertoSMTP.getValor().isEmpty()) {
                    if (emailSMTP.getValor() != null && !emailSMTP.getValor().isEmpty()) {
                        if (enviarCodigo != null && !enviarCodigo.isEmpty()) {
                            if (usuarioSMTP.getValor() != null && !usuarioSMTP.getValor().trim().isEmpty() || passSMTP.getValor() != null && !passSMTP.getValor().trim().isEmpty()) {
                                auth = true;
                                codigo = this.servidorSMTP.getValor() + this.puertoSMTP.getValor();
                                this.pw = encrypt(this.passSMTP.getValor());
                                logger.info("conexionSMTP - Generando codigo de confirmación");
                                this.hash = encrypt(codigo);
                                logger.info("conexionSMTP - Encryptando codigo de confirmación");
                                String asunto = "Prueba de conexión SMTP";
                                String contenido = "Código de confirmación: " + this.hash;
                                boolean ssl = Boolean.valueOf(SSL_SMTP.getValor());
                                ConexionSMTP conSMTP = new ConexionSMTP(servidorSMTP.getValor(), puertoSMTP.getValor(), ssl, auth);
                                conSMTP.setUsername(usuarioSMTP.getValor());
                                conSMTP.setPassword(passSMTP.getValor());
                                conSMTP.getSession();
                                conSMTP.connect();
                                conSMTP.createMessage(enviarCodigo, asunto, contenido);
                                String conn;
                                conn = conSMTP.sendMessage();
                                logger.debug("conn " + conn);
                                conSMTP.closeConnection();
                                if (conn.trim().charAt(0) == '2') {
                                    con = true;
                                    logger.info("conexionSMTP - Prueba de Conexion SMTP exitosa. Código de confirmación fue enviado a " + enviarCodigo);
                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Prueba de conexión exitosa. Por favor, ingresa el código de confirmación.");

                                } else {
                                    logger.error("conexionSMTP - Prueba de Conexion SMTP Fallida.");
                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexión Falló. Por favor, revise los datos.");
                                }
                            } else {
                                auth = false;
                                codigo = this.servidorSMTP.getValor() + this.puertoSMTP.getValor();
                                this.pw = encrypt(this.passSMTP.getValor());
                                logger.info("conexionSMTP - Generando codigo de confirmación");
                                this.hash = encrypt(codigo);
                                logger.info("conexionSMTP - Encryptando codigo de confirmación");
                                String asunto = "Prueba de conexión SMTP";
                                String contenido = "Código de confirmación: " + this.hash;
                                boolean ssl = Boolean.valueOf(SSL_SMTP.getValor());
                                ConexionSMTP conSMTP = new ConexionSMTP(servidorSMTP.getValor(), puertoSMTP.getValor(), ssl, auth);
                                conSMTP.setUsername(emailSMTP.getValor());
                                conSMTP.getSession();
                                conSMTP.connect();
                                conSMTP.createMessage(enviarCodigo, asunto, contenido);
                                String conn;
                                conn = conSMTP.sendMessage();
                                logger.debug("conn " + conn);
                                conSMTP.closeConnection();
                                if (conn.trim().charAt(0) == '2') {
                                    con = true;
                                    logger.info("conexionSMTP - Prueba de Conexion SMTP exitosa. Código de confirmación fue enviado a " + enviarCodigo);
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
            this.descripcion = "[CONFIG_EMPRESA] conexionSMTP - CryptoException ERROR: " + e;
            this.bitacora = registrarBitacora(this.usuario.getIdUsuario(), 2, this.empresa.getIdEmpresa(), this.descripcion, "ERROR");
            logger.error("conexionSMTP - ERROR: " + e);
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexión Fallo. Por favor, revise los datos.");
        } catch (IOException e) {
            this.descripcion = "[CONFIG_EMPRESA] conexionSMTP - ERROR: " + e;
            this.bitacora = registrarBitacora(this.usuario.getIdUsuario(), 2, this.empresa.getIdEmpresa(), this.descripcion, "ERROR");
            logger.error("conexionSMTP - ERROR: " + e);
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexión Fallo. Por favor, revise los datos.");
        } catch (MessagingException ex) {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexión Fallo. Por favor, revise los datos.");
            logger.error("conexionSMTP - MessagingException ERROR: " + ex);
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
                    DaoConfiguracionEmpresa daoConfigEmpresa = new DaoConfiguracionEmpresa();
                    daoConfigEmpresa.updateConfiguracionEmpresa(this.servidorSMTP);
                    logger.info("updateSMTP - " + this.usuario.getUserid() + " Configuracion de Empresa se modifico " + servidorSMTP.getPropiedad());
                    daoConfigEmpresa.updateConfiguracionEmpresa(this.puertoSMTP);
                    logger.info("updateSMTP - " + this.usuario.getUserid() + " Configuracion de Empresa se modifico " + puertoSMTP.getPropiedad());
                    daoConfigEmpresa.updateConfiguracionEmpresa(this.nombreSMTP);
                    logger.info("updateSMTP - " + this.usuario.getUserid() + " Configuracion de Empresa se modifico " + nombreSMTP.getPropiedad());
                    daoConfigEmpresa.updateConfiguracionEmpresa(this.emailSMTP);
                    logger.info("updateSMTP - " + this.usuario.getUserid() + " Configuracion de Empresa se modifico " + emailSMTP.getPropiedad());
                    daoConfigEmpresa.updateConfiguracionEmpresa(this.usuarioSMTP);
                    logger.info("updateSMTP - " + this.usuario.getUserid() + " Configuracion de Empresa se modifico " + usuarioSMTP.getPropiedad());
                    this.passSMTP.setValor(this.pw);
                    daoConfigEmpresa.updateConfiguracionEmpresa(this.passSMTP);
                    logger.info("updateSMTP - " + this.usuario.getUserid() + " Configuracion de Empresa se modifico " + passSMTP.getPropiedad());
                    smtp = daoConfigEmpresa.updateConfiguracionEmpresa(this.SSL_SMTP);
                    logger.info("updateSMTP - " + this.usuario.getUserid() + " Configuracion de Empresa se modifico " + SSL_SMTP.getPropiedad());
                    if (smtp) {
                        descripcion = "[CONFIG_EMPRESA] Usuario: " + this.usuario.getUserid() + " modifico las configuraciones de SMTP.";
                        registrarBitacora(this.usuario.getIdUsuario(), 2, this.empresa.getIdEmpresa(), this.descripcion, "INFO");
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
            descripcion = "[CONFIG_EMPRESA] updateSMTP - ERROR: " + e;
            registrarBitacora(this.usuario.getIdUsuario(), 2, this.empresa.getIdEmpresa(), this.descripcion, "INFO");
            this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error", "Error al guardar las configuraciones para le servidor SMTP.");
            logger.error("updateSMTP - ERROR: " + e);
        }
        this.hashSMTP = "";
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("smtp", smtp);
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
    }

    public void conexionPOP() {
        boolean recepcion = false;
        logger.info("conexionPOP");
        try {
            if (!this.tipoServidor.equals("")) {
                if (this.servidorRecepcion.getValor() != null && !this.servidorRecepcion.getValor().trim().isEmpty()) {
                    if (this.puertoRecepcion.getValor() != null && !this.puertoRecepcion.getValor().trim().isEmpty()) {
                        if (this.usuarioRecepcion.getValor() != null && !this.usuarioRecepcion.getValor().trim().isEmpty()) {
                            boolean sslCon = Boolean.valueOf(sslRecepcion.getValor());
                            if (this.tipoServidor.equals("pop3")) {
                                recepcion = Email.connect(servidorRecepcion.getValor(), usuarioRecepcion.getValor(), passRecepcion.getValor(), puertoRecepcion.getValor(), sslCon);
                                if (recepcion) {
                                    this.pw = encrypt(this.passRecepcion.getValor());
                                    logger.info("conexionPOP - Conexion POP3 correcto.");
                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Conexion POP3 exitosa.");
                                } else {
                                    logger.error("conexionPOP - Conexion POP3 fallo.");
                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexion POP3 Fallo. Por favor, revise los datos.");
                                }
                            } else {
                                recepcion = Email.connectIMAP(servidorRecepcion.getValor(), usuarioRecepcion.getValor(), passRecepcion.getValor(), puertoRecepcion.getValor(), sslCon);
                                if (recepcion) {
                                    this.pw = encrypt(this.passRecepcion.getValor());
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
            this.descripcion = "[CONFIG_EMPRESA] conexionPOP - CryptoException ERROR:" + e;
            registrarBitacora(this.usuario.getIdUsuario(), 3, this.empresa.getIdEmpresa(), this.descripcion, "ERROR");
            logger.error("conexionPOP CryptoException ERROR: " + e);
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error: " + e.getMessage());
        } catch (IOException e) {
            this.descripcion = "[CONFIG_EMPRESA] conexionPOP - IOException ERROR:" + e;
            registrarBitacora(this.usuario.getIdUsuario(), 3, this.empresa.getIdEmpresa(), this.descripcion, "ERROR");
            logger.error("conexionPOP IOException ERROR: " + e);
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error: " + e.getMessage());
        } catch (Exception ex) {
            logger.error("conexionPOP ERROR " + ex);
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error: " + ex.getMessage());
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("recepcion", recepcion);
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
    }

    public void updatePOP() {
        boolean pop = false;

        try {
            DaoConfiguracionEmpresa daoConfigEmpresa = new DaoConfiguracionEmpresa();
            daoConfigEmpresa.updateConfiguracionEmpresa(this.servidorRecepcion);
            daoConfigEmpresa.updateConfiguracionEmpresa(this.puertoRecepcion);
            daoConfigEmpresa.updateConfiguracionEmpresa(this.usuarioRecepcion);
            this.passRecepcion.setValor(this.pw);
            daoConfigEmpresa.updateConfiguracionEmpresa(this.passRecepcion);
//            daoConfigEmpresa.updateConfiguracionEmpresa(this.tlsRecepcion);
            this.protocoloRecepcion.setValor(this.tipoServidor);
            daoConfigEmpresa.updateConfiguracionEmpresa(this.protocoloRecepcion);
            pop = daoConfigEmpresa.updateConfiguracionEmpresa(this.sslRecepcion);
            if (pop) {
                this.descripcion = "[CONFIG_EMPRESA] Usuario: " + this.usuario.getUserid() + " modifico las configuraciones de " + this.tipoServidor;
                this.bitacora = registrarBitacora(this.usuario.getIdUsuario(), 2, this.empresa.getIdEmpresa(), this.descripcion, "INFO");
                logger.info("updatePOP - " + this.usuario.getUserid() + "modifico las configuraciones de " + this.tipoServidor);
                this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Configuracion IMAP/POP3 ha sido guardada.");
            } else {
                logger.error("updatePOP - Error update POP/IMAP.");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar la configuracion para IMAP/POP3.");
            }
        } catch (Exception e) {
            this.descripcion = "[CONFIG_EMPRESA] updatePOP - ERROR:" + e;
            registrarBitacora(this.usuario.getIdUsuario(), 3, this.empresa.getIdEmpresa(), this.descripcion, "ERROR");
            logger.error("updatePOP - ERROR: " + e);
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("pop", pop);
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
    }

    public void updateServRecepcion() {
        boolean servRe = false;

        try {
            if (this.teRecepcion.getValor() != null && !this.teRecepcion.getValor().isEmpty()) {
                if (this.trcRecepcion.getValor() != null && !this.trcRecepcion.getValor().isEmpty()) {
                    DaoConfiguracionServicios daoConfigServ = new DaoConfiguracionServicios();
                    daoConfigServ.updateConfigServicios(this.teRecepcion);
                    servRe = daoConfigServ.updateConfigServicios(this.trcRecepcion);
                    if (servRe) {
                        this.descripcion = "[CONFIG_SERVICIOS] Usuario: " + this.usuario.getUserid() + " Las configuraciones para el servicio " + this.servicioRecepcion.getNombre() + " han sido modificadas.";
                        insertBitacora(this.usuario.getIdUsuario(), this.servicioRecepcion.getIdServicio(), this.descripcion, "INFO");
                        logger.info("updateServRecepcion - " + usuario.getUserid() + " Las configuraciones para el servicio " + this.servicioRecepcion.getNombre() + " han sido modificadas.");
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Las configuraciones para el servicio de recepción han sido modificadas.");
                    } else {
                        logger.error("updateServRecepcion - " + usuario.getUserid() + " ERROR al modificar las configuraciones para los servicios de recepcion.");
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar los cambios. Por favor, vuelva a intentarlo.");
                    }
                } else {
                    logger.warn("updateServRecepcion - Error Tiempo de espera es un campo requerido.");
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Tiempo de espera para recargar configuración.");
                }
            } else {
                logger.warn("updateServRecepcion - Tiempo de espera entre cada ciclo.");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Tiempo de espera entre cada ciclo.");
            }

        } catch (Exception e) {
            this.descripcion = "[CONFIG_SERVICIO] updateServRecepcion - ERROR: " + e;
            insertBitacora(this.usuario.getIdUsuario(), this.servicioRecepcion.getIdServicio(), this.descripcion, "ERROR");
            logger.error("updateServRecepcion - ERROR: " + e);
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("servicioRecepcion", servRe);
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
    }

    public void updateServValidacion() {
        boolean servVa = false;

        try {
            if (this.teValidacion.getValor() != null && !this.teValidacion.getValor().isEmpty()) {
                if (this.trcValidacion.getValor() != null && !this.trcValidacion.getValor().isEmpty()) {
                    if (this.wsValidacion.getValor() != null && !this.wsValidacion.getValor().isEmpty()) {
                        DaoConfiguracionServicios daoConfigServicios = new DaoConfiguracionServicios();
                        daoConfigServicios.updateConfigServicios(this.teValidacion);
                        daoConfigServicios.updateConfigServicios(this.trcValidacion);
                        servVa = daoConfigServicios.updateConfigServicios(this.wsValidacion);
                        if (servVa) {
                            this.descripcion = "[CONFIG_SERVICIOS] Usuario: " + this.usuario.getUserid() + " Las configuraciones para el servicio " + this.servicioRecepcion.getNombre() + " han sido modificadas.";
                            insertBitacora(this.usuario.getIdUsuario(), this.servicioRecepcion.getIdServicio(), this.descripcion, "INFO");
                            logger.info("updateServValidacion - " + usuario.getUserid() + " Las configuraciones para el servicio " + this.servicioRecepcion.getNombre() + " han sido modificadas.");
                            this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Las configuraciones para el servicio de validación han sido modificadas.");
                        }

                    } else {
                        logger.warn("updateServValidacion - Error WebService es un campo requerido.");
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Webservice.");
                    }
                } else {
                    logger.warn("updateServValidacion - Error Tiempo de espera es un campo requerido.");
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Tiempo de espera para recargar configuración.");
                }
            } else {
                logger.warn("updateServValidacion - Tiempo de espera entre cada ciclo es un campo requerido.");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Tiempo de espera entre cada ciclo.");
            }
        } catch (Exception e) {
            this.descripcion = "[CONFIG_SERVICIO] updateServValidacion - ERROR: " + e;
            insertBitacora(this.usuario.getIdUsuario(), this.servicioRecepcion.getIdServicio(), this.descripcion, "ERROR");
            logger.error("updateServValidacion - ERROR: " + e);
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("servicioValidacion", servVa);
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
    }

    public void updateServEnvio() {
        boolean servEn = false;

        try {
            if (this.teEnvio.getValor() != null && !this.teEnvio.getValor().isEmpty()) {
                if (this.trcEnvio.getValor() != null && !this.trcEnvio.getValor().isEmpty()) {
                    if (this.mdEnvio.getValor() != null && !this.mdEnvio.getValor().isEmpty()) {
                        DaoConfiguracionServicios daoConfigServicios = new DaoConfiguracionServicios();
                        daoConfigServicios.updateConfigServicios(this.teEnvio);
                        daoConfigServicios.updateConfigServicios(this.trcEnvio);
                        servEn = daoConfigServicios.updateConfigServicios(this.mdEnvio);
                        if (servEn) {
                            this.descripcion = "[CONFIG_SERVICIOS] Usuario: " + this.usuario.getUserid() + " Las configuraciones para el servicio " + this.servicioRecepcion.getNombre() + " han sido modificadas.";
                            insertBitacora(this.usuario.getIdUsuario(), this.servicioRecepcion.getIdServicio(), this.descripcion, "INFO");
                            logger.info("updateServEnvio - " + this.usuario.getUserid() + " Las configuraciones para el servicio " + this.servicioRecepcion.getNombre() + " han sido modificadas.");
                            this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Las configuraciones para el servicio de envío de notificaciones han sido modificadas.");
                        }

                    } else {
                        logger.warn("updateServEnvio - Error minimo de documentos es un campo requerido.");
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Mínimo de documentos encolados.");
                    }
                } else {
                    logger.warn("updateServEnvio - Error Tiempo de espera es un campo requerido.");
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Tiempo de espera para recargar configuración.");
                }
            } else {
                logger.warn("updateServEnvio - Tiempo de espera entre cada ciclo es un campo requerido.");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Tiempo de espera entre cada ciclo.");
            }
        } catch (Exception e) {
            this.descripcion = "[CONFIG_SERVICIO] updateServEnvio - ERROR: " + e;
            insertBitacora(this.usuario.getIdUsuario(), this.servicioRecepcion.getIdServicio(), this.descripcion, "ERROR");
            logger.error("updateServEnvio - ERROR: " + e);
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("servicioEnvio", servEn);
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
    }

    public void updateServAcceso() {
        boolean servAcceso = false;

        try {
            if (this.expPass.getValor() != null && !this.expPass.getValor().isEmpty()) {
                if (this.inactividad.getValor() != null && !this.inactividad.getValor().isEmpty()) {
                    if (this.minPass.getValor() != null && !this.minPass.getValor().isEmpty()) {
                        if (this.minIntentos.getValor() != null && !this.minIntentos.getValor().isEmpty()) {
                            DaoConfiguracionServicios daoConfigServicios = new DaoConfiguracionServicios();
                            daoConfigServicios.updateConfigServicios(this.expPass);
                            daoConfigServicios.updateConfigServicios(this.inactividad);
                            daoConfigServicios.updateConfigServicios(this.minPass);
                            servAcceso = daoConfigServicios.updateConfigServicios(this.minIntentos);
                            if (servAcceso) {
                                this.descripcion = "[CONFIG_SERVICIOS] Usuario: " + this.usuario.getUserid() + " Las configuraciones para el servicio " + this.servicioRecepcion.getNombre() + " han sido modificadas.";
                                insertBitacora(this.usuario.getIdUsuario(), this.servicioRecepcion.getIdServicio(), this.descripcion, "INFO");
                                logger.info("updateServAcceso - " + this.usuario.getUserid() + " Las configuraciones para el servicio " + this.servicioRecepcion.getNombre() + " han sido modificadas.");
                                this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Las configuraciones para Cuentas de usuario han sido modificadas.");
                            }

                        } else {
                            logger.warn("updateServAcceso - Número de intentos fallidos es un campo requerido.");
                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Número de intentos fallidos.");
                        }
                    } else {
                        logger.warn("updateServAcceso - Longitud mínima de contraseñas es un campo requerido.");
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Longitud mínima de contraseñas.");
                    }
                } else {
                    logger.warn("updateServAcceso - Días de inactividad permitidos es un campo requerido.");
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Días de inactividad permitidos.");
                }
            } else {
                logger.warn("updateServAcceso - Dias para expirar la contraseña es un campo requerido.");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Días para expirar contraseña.");
            }
        } catch (Exception e) {
            this.descripcion = "[CONFIG_SERVICIO] updateServAcceso - ERROR: " + e;
            insertBitacora(this.usuario.getIdUsuario(), this.servicioRecepcion.getIdServicio(), this.descripcion, "ERROR");
            logger.error("updateServAcceso - ERROR: " + e);
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("servicioAcceso", servAcceso);
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
    }

    private void configNotificaciones() {
        try {
            this.validoRemitente = new DaoConfiguracionEmpresa().getConfiguracionEmpresa(this.empresa.getIdEmpresa(), "NOTIFICAR_XML_VALIDO");
            logger.info("NOTIFICAR_XML_VALIDO " + validoRemitente.getValor());
            this.invalidoRemitente = new DaoConfiguracionEmpresa().getConfiguracionEmpresa(this.empresa.getIdEmpresa(), "NOTIFICAR_XML_INVALIDO");
            this.validoEmail = new DaoConfiguracionEmpresa().getConfiguracionEmpresa(this.empresa.getIdEmpresa(), "XML_VALIDO_EMAIL");
            this.invalidoEmail = new DaoConfiguracionEmpresa().getConfiguracionEmpresa(this.empresa.getIdEmpresa(), "XML_INVALIDO_EMAIL");
        } catch (Exception e) {
            logger.error("configNotificaciones - ERROR al cargar configuraciones de notificaciones " + e);
        }
    }

    private void configEmail() {
        try {
            this.listaEmail = new ConfiguracionesEmpresas();
            String strEmail;
            DaoConfiguracionEmpresa daoConfigEmpresa = new DaoConfiguracionEmpresa();
            this.listaEmail = daoConfigEmpresa.getConfiguracionEmpresa(this.empresa.getIdEmpresa(), "EMAILS_RECIBEN_NOTIFICACION");

            strEmail = this.listaEmail.getValor();
            String allEmial[];
            allEmial = strEmail.split(",");

            for (String string : allEmial) {
                if (!string.isEmpty()) {
                    this.emails.add(string);
                }
            }
        } catch (Exception e) {
            logger.error("configEmail - ERROR al cargar configuraciones de Email " + e);
        }
    }

    private void configSMTP() {

        try {
            this.servidorSMTP = new DaoConfiguracionEmpresa().getConfiguracionEmpresa(this.empresa.getIdEmpresa(), "HOST_SMTP");
            this.puertoSMTP = new DaoConfiguracionEmpresa().getConfiguracionEmpresa(this.empresa.getIdEmpresa(), "PUERTO_SMTP");
            this.nombreSMTP = new DaoConfiguracionEmpresa().getConfiguracionEmpresa(this.empresa.getIdEmpresa(), "NOMBRE_SMTP");
            this.emailSMTP = new DaoConfiguracionEmpresa().getConfiguracionEmpresa(this.empresa.getIdEmpresa(), "EMAIL_SMTP");
            this.usuarioSMTP = new DaoConfiguracionEmpresa().getConfiguracionEmpresa(this.empresa.getIdEmpresa(), "USUARIO_SMTP");
            this.passSMTP = new DaoConfiguracionEmpresa().getConfiguracionEmpresa(this.empresa.getIdEmpresa(), "PASS_SMTP");
            this.SSL_SMTP = new DaoConfiguracionEmpresa().getConfiguracionEmpresa(this.empresa.getIdEmpresa(), "SSL_SMTP");
        } catch (Exception e) {
            logger.error("configSMTP - ERROR al cargar configuraciones SMTP " + e);
        }
    }

    private void configRecepcion() {
        try {
            this.servidorRecepcion = new DaoConfiguracionEmpresa().getConfiguracionEmpresa(this.empresa.getIdEmpresa(), "HOST_RECEPCION");
            this.puertoRecepcion = new DaoConfiguracionEmpresa().getConfiguracionEmpresa(this.empresa.getIdEmpresa(), "PUERTO_RECEPCION");
            this.usuarioRecepcion = new DaoConfiguracionEmpresa().getConfiguracionEmpresa(this.empresa.getIdEmpresa(), "EMAIL_RECEPCION");
            this.passRecepcion = new DaoConfiguracionEmpresa().getConfiguracionEmpresa(this.empresa.getIdEmpresa(), "RECEPCION_PASS");
//            this.tlsRecepcion = new DaoConfiguracionEmpresa().getConfiguracionEmpresa(this.empresa.getIdEmpresa(), "START_TLS");
            this.sslRecepcion = new DaoConfiguracionEmpresa().getConfiguracionEmpresa(this.empresa.getIdEmpresa(), "UTILIZAR_SSL");
            this.protocoloRecepcion = new DaoConfiguracionEmpresa().getConfiguracionEmpresa(this.empresa.getIdEmpresa(), "PROTOCOLO_RECEPCION");
        } catch (Exception e) {
            logger.error("configRecepcion - ERROR al cargar configuraciones Recepcion " + e);
        }
    }

    private void servRecepcion() {
        try {
            this.servicioRecepcion = new DaoServicio().getServicoByNombre("RECEPTOR");
            this.teRecepcion = new DaoConfiguracionServicios().getConfigServicioByIdServicioPropiedad(this.servicioRecepcion.getIdServicio(), "TIEMPO_ESPERA");
            this.trcRecepcion = new DaoConfiguracionServicios().getConfigServicioByIdServicioPropiedad(this.servicioRecepcion.getIdServicio(), "TIEMPO_REC_CONFIG");
        } catch (Exception e) {
            logger.error("servRecepcion - ERROR al cargar las configuraciones Servicio RECEPTOR " + e);
        }
    }

    private void servValidacion() {
        try {
            this.servicioRecepcion = new DaoServicio().getServicoByNombre("VALIDADOR");
            this.teValidacion = new DaoConfiguracionServicios().getConfigServicioByIdServicioPropiedad(this.servicioRecepcion.getIdServicio(), "TIEMPO_ESPERA");
            this.trcValidacion = new DaoConfiguracionServicios().getConfigServicioByIdServicioPropiedad(this.servicioRecepcion.getIdServicio(), "TIEMPO_REC_CONFIG");
            this.wsValidacion = new DaoConfiguracionServicios().getConfigServicioByIdServicioPropiedad(this.servicioRecepcion.getIdServicio(), "CSD_WEBSERVICE");
        } catch (Exception e) {
            logger.error("servValidacion - ERROR al cargar las configuraciones Servicio VALIDADOR " + e);
        }
    }

    private void servEnvio() {
        try {
            this.servicioRecepcion = new DaoServicio().getServicoByNombre("ENVIADOR");
            this.teEnvio = new DaoConfiguracionServicios().getConfigServicioByIdServicioPropiedad(this.servicioRecepcion.getIdServicio(), "TIEMPO_ESPERA");
            this.trcEnvio = new DaoConfiguracionServicios().getConfigServicioByIdServicioPropiedad(this.servicioRecepcion.getIdServicio(), "TIEMPO_REC_CONFIG");
            this.mdEnvio = new DaoConfiguracionServicios().getConfigServicioByIdServicioPropiedad(this.servicioRecepcion.getIdServicio(), "MIN_DOCTOS");
        } catch (Exception e) {
            logger.error("servEnvio - ERROR al cargar las configuraciones Servicio ENVIADOR " + e);
        }
    }

    private void servAcceso() {
        try {
            this.servicioRecepcion = new DaoServicio().getServicoByNombre("ADMINISTRACION_ACCESO");
            this.expPass = new DaoConfiguracionServicios().getConfigServicioByIdServicioPropiedad(this.servicioRecepcion.getIdServicio(), "DIAS_EXPIRAR_CONT");
            this.inactividad = new DaoConfiguracionServicios().getConfigServicioByIdServicioPropiedad(this.servicioRecepcion.getIdServicio(), "DIAS_INACTIVIDAD");
            this.minPass = new DaoConfiguracionServicios().getConfigServicioByIdServicioPropiedad(this.servicioRecepcion.getIdServicio(), "LONGITUD_MIN_CONT");
            this.minIntentos = new DaoConfiguracionServicios().getConfigServicioByIdServicioPropiedad(this.servicioRecepcion.getIdServicio(), "NUM_INT_FALLIDOS");
        } catch (Exception e) {
            logger.error("servAcceso - ERROR al cargar las configuraciones Servicio ADMINISTRACION_ACCESO " + e);
        }
    }

    public String reloadNotificacion() {
        return "/Configuracion/notificaciones?faces-redirect=true";
    }

    public String reloadEmail() {
        return "/Configuracion/email?faces-redirect=true";
    }

}

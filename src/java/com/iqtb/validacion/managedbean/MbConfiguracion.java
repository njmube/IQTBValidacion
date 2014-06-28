/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoConfiguracionEmpresa;
import com.iqtb.validacion.dao.DaoEmpresa;
import com.iqtb.validacion.dao.DaoUsuario;
import static com.iqtb.validacion.encrypt.Encrypt.encodeBase64;
import static com.iqtb.validacion.mail.Email.sendEmailAuth;
import com.iqtb.validacion.pojo.Bitacora;
import com.iqtb.validacion.pojo.ConfiguracionesEmpresas;
import com.iqtb.validacion.pojo.Empresas;
import com.iqtb.validacion.pojo.Usuarios;
import static com.iqtb.validacion.util.Bitacoras.registrarBitacora;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.tomcat.dbcp.pool.impl.GenericKeyedObjectPool;
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
    
    private final String sessionUsuario;
    private Bitacora bitacora;
    private Usuarios usuario;
    private String descripcion;
    private FacesMessage msg;

    public MbConfiguracion() throws Exception {
        this.empresa = new Empresas();
        this.configEmpresa = new ConfiguracionesEmpresas();
        this.listaEmail = new ConfiguracionesEmpresas();
        this.emails = new ArrayList<String>();
        this.newEmail = new String();
        this.bitacora = new Bitacora();
        this.usuario = new Usuarios();
        this.servidorSMTP = new ConfiguracionesEmpresas();
        this.puertoSMTP = new ConfiguracionesEmpresas();
        this.nombreSMTP = new ConfiguracionesEmpresas();
        this.emailSMTP = new ConfiguracionesEmpresas();
        this.usuarioSMTP = new ConfiguracionesEmpresas();
        this.passSMTP = new ConfiguracionesEmpresas();
        this.SSL_SMTP = new ConfiguracionesEmpresas();

        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        String empresaSeleccionada = (String) httpServletRequest.getSession().getAttribute("empresaSeleccionada");
        this.sessionUsuario = ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid();
        this.usuario = new DaoUsuario().getByUserid(this.sessionUsuario);
        if (httpServletRequest.getSession().getAttribute("empresaSeleccionada") != null) {
            empresa = new DaoEmpresa().getEmpresaByRFC(empresaSeleccionada);
        }

        this.validoRemitente = new DaoConfiguracionEmpresa().getConfiguracionEmpresa(this.empresa.getIdEmpresa(), "NOTIFICAR_XML_VALIDO");
        this.invalidoRemitente = new DaoConfiguracionEmpresa().getConfiguracionEmpresa(this.empresa.getIdEmpresa(), "NOTIFICAR_XML_INVALIDO");
        this.validoEmail = new DaoConfiguracionEmpresa().getConfiguracionEmpresa(this.empresa.getIdEmpresa(), "XML_VALIDO_EMAIL");
        this.invalidoEmail = new DaoConfiguracionEmpresa().getConfiguracionEmpresa(this.empresa.getIdEmpresa(), "XML_INVALIDO_EMAIL");

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
    }

    public Empresas getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresas empresa) {
        this.empresa = empresa;
    }

    public ConfiguracionesEmpresas getConfigEmpresa() {
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

    public String reinit() {
        this.newEmail = new String();

        return null;
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

    public void updateEmpresa() {
        boolean updateEmpresa = false;

        try {
            if (this.empresa.getNombre() != null && !this.empresa.getNombre().trim().isEmpty()) {
                if (this.empresa.getCalle() != null && !this.empresa.getCalle().trim().isEmpty()) {
                    DaoEmpresa daoEmpresa = new DaoEmpresa();
                    updateEmpresa = daoEmpresa.updateEmpresa(this.empresa);
                    if (updateEmpresa) {
                        this.descripcion = "";
                        
                        System.out.println("Se ha modificado los datos de la empresa");
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Los datos de la Empresa han sido Modificados.");

                    }else{
                        System.out.println("Se ha modificado los datos de la empresa");
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al modificar los datos de la Empresa.");
                    }

                } else {
                    System.out.println("Calle de la Empresa es requerido");
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Calle es un campo requerido.");
                }

            } else {
                System.out.println("Nombre de la Empresa es requerido");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Nombre es un campo requerido.");
            }

        } catch (Exception e) {
            System.out.println("Error en update Empresa");
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("actualiza", updateEmpresa);
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
    }

    public String insertNewEmail() {
        String str = "";

        try {
            System.out.println("inserterNewEmail");

            if (this.emails.contains(this.newEmail)) {
                this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Duplicado", "Dirección de correo electrónico ya está registrada.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                System.out.println("Email Duplicado");
            } else {
                this.emails.add(this.newEmail);
                this.newEmail = new String();
                System.out.println("NewEmail agregado a la lista");
//                this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Dirección de correo electrónico ha sido agradada.");
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
                System.out.println("String con email: " + this.configValor);
            }

        } catch (Exception e) {
            System.out.println("ERROR INSERT NEW EMAIL");
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al agregar la dirección de correo.");
        }
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
        return this.configValor;
    }

    public String removeEmail(String email) {
        String str = "";
        this.emails.remove(email);

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
            System.out.println("String con emial agregado a lista Email " + this.configEmpresa.getValor());
            DaoConfiguracionEmpresa daoConfigEmpresa = new DaoConfiguracionEmpresa();

            addEmail = daoConfigEmpresa.updateConfiguracionEmpresa(this.configEmpresa);

            if (addEmail) {
                System.out.println("Actualizacion de lista de Email");
                this.descripcion = "[CONFIG_EMPRESA] Usuario: " + this.usuario.getUserid() + " Empresa seleccionada: " + this.empresa.getRfc() + " modifico la lista de correo que reciben notificaciones.";
                this.bitacora = registrarBitacora(this.usuario.getIdUsuario(), 2, this.empresa.getIdEmpresa(), this.descripcion, "INFO");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Dirección de correo electrónico ha sido registrada.");
            }

        } catch (Exception e) {
            System.out.println("Error update lista emial");
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al agregar la dirección de correo.");
        }
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
    }

    public String getValor(String propiedad) {
        System.out.println("Propiedad: " + propiedad);

        try {
            DaoConfiguracionEmpresa daoConfigEmpresa = new DaoConfiguracionEmpresa();
            this.configEmpresa = daoConfigEmpresa.getConfiguracionEmpresa(this.empresa.getIdEmpresa(), propiedad);
        } catch (Exception e) {
        }
        return configEmpresa.getValor();
    }

    public void checkConfiguracionEmpresa() {
        boolean updateConfig = false;
        System.out.println("Update check");

        try {
            DaoConfiguracionEmpresa daoConfigEmpresa = new DaoConfiguracionEmpresa();
            daoConfigEmpresa.updateConfiguracionEmpresa(this.validoRemitente);
            daoConfigEmpresa.updateConfiguracionEmpresa(this.invalidoRemitente);
            daoConfigEmpresa.updateConfiguracionEmpresa(this.validoEmail);
            updateConfig = daoConfigEmpresa.updateConfiguracionEmpresa(this.invalidoEmail);

            if (updateConfig) {
                System.out.println("ACTUALIZADO");
                this.descripcion = "[CONFIG_EMPRESA] Usuario: " + this.usuario.getUserid() + " Empresa seleccionada: " + this.empresa.getRfc() + " modifico las notificaciones de XML.";
                this.bitacora = registrarBitacora(this.usuario.getIdUsuario(), 2, this.empresa.getIdEmpresa(), this.descripcion, "INFO");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Los cambios se han guardado.");
            }

        } catch (Exception e) {
            System.out.println("ERROR CHK UPDATE CONFIGEMPRESA");
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", " Ocurrio un error durante la modificación");
        }
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
    }
    
    public void conexionSMTP(){
        boolean con = false;
        String codigo;
        String hash;
        System.out.println("conexionSMTP");
        try {
            if (this.usuarioSMTP.getValor() == null && this.passSMTP == null) {
                //ENVIAR EMAIL SIN AUTENTICARSE
                System.out.println("ENVIAR EMAIL SIN AUTENTICARSE");
            }else{
                codigo = this.servidorSMTP.getValor() + this.puertoSMTP.getValor() + this.nombreSMTP.getValor() + this.emailSMTP.getValor() + this.usuarioSMTP.getValor() 
                        + this.passSMTP.getValor() + this.enviarCodigo + this.SSL_SMTP.getValor();
                System.out.println("codigo "+codigo);
                hash = encodeBase64(codigo);
                System.out.println("hash "+hash);
                String asunto = "Prueba de conexion IQTB";
                String contenido = "Código de confirmacion: "+hash;
                con = sendEmailAuth(this.usuarioSMTP.getValor(), this.passSMTP.getValor(), this.enviarCodigo, asunto, contenido, this.servidorSMTP.getValor(), this.SSL_SMTP.getValor(), this.puertoSMTP.getValor());
                
                if (con) {
                    System.out.println("Prueba de Conexion exitosa.");
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Prueba de conexion exitosa. Por favor, ingresa el código de confirmación.");
                    
                } else {
                    System.out.println("Prueba de Conexion Fallida.");
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexion Fallo. Por favor, revisa los datos de autenticacion.");
                }
            }
            
        } catch (Exception e) {
            System.out.println("ERROR conexion SMTP");
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("conexion", con);
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
    }
    

}

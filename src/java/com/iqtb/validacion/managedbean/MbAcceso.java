package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoEmpresa;
import com.iqtb.validacion.dao.DaoUsuario;
import com.iqtb.validacion.encrypt.Encrypt;
import com.iqtb.validacion.mail.Email;
import com.iqtb.validacion.pojo.Empresas;
import com.iqtb.validacion.pojo.Usuarios;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

/**
 *
 * @author danielromero
 */
@ManagedBean
@SessionScoped
public class MbAcceso implements Serializable {

    private String user;
    private String pass;
    private Usuarios usuario;
    private String empresaSeleccionada;
    private Map<String, String> listaEmpresas;
    private FacesMessage msg;

    public MbAcceso() {
        this.usuario = new Usuarios();
        this.listaEmpresas = new HashMap<String, String>();
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }

    public String getEmpresaSeleccionada() {
        return empresaSeleccionada;
    }

    public void setEmpresaSeleccionada(String empresaSeleccionada) {
        this.empresaSeleccionada = empresaSeleccionada;
    }

    public Map<String, String> getListaEmpresas() {
        return listaEmpresas;
    }

    public void setListaEmpresas(Map<String, String> listaEmpresas) {
        this.listaEmpresas = listaEmpresas;
    }

    public String login() {
        boolean mostarDialog = false;
        try {

            this.usuario = new DaoUsuario().getByUserid(this.user);
            if (this.usuario.getPasskey().equals(Encrypt.getSHA512(this.pass + this.usuario.getSalt()))) {
                List<Empresas> lista = new DaoEmpresa().getEmpresaById(this.usuario.getIdUsuario());
                for (Empresas empresas : lista) {
                    listaEmpresas.put(empresas.getRfc(), empresas.getRfc());
                }
                if (listaEmpresas.size() > 1) {
                    mostarDialog = true;
                } else {
                    this.empresaSeleccionada = lista.get(0).getRfc();
                    return "/principal?faces-redirect=true";
                }
                this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", this.usuario.getUserid() + " ha iniciado sessión");
                empresaSeleccionada = null;

            } else {
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario/Contraseña incorrecto");
            }
        } catch (Exception ex) {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario/Contraseña incorrecto");
        }
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("mostarDialog", mostarDialog);

        return null;
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Session cerrada correctamente", null);
        FacesContext.getCurrentInstance().addMessage(null, this.msg);

        return "/login?faces-redirect=true";
    }

    public String existeSeleccionEmpresa() {
        boolean existeSeleccionEmpresa = false;
        if (!this.empresaSeleccionada.equals("vacio")) {
            existeSeleccionEmpresa = true;
        } else {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor seleccione una empresa");
            FacesContext.getCurrentInstance().addMessage(null, this.msg);
            return null;
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("existeSeleccionEmpresa", existeSeleccionEmpresa);
        return "/principal?faces-redirect=true";
    }

    public String restablecerContrasenia() throws Exception {
        String newPass;
        String newPassKey;
        Usuarios user;
        try {
            user = new DaoUsuario().getByUserid(this.user);

            if (this.user.equals("")) {
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor introdusca usuario para restablecer contraseña.");
                FacesContext.getCurrentInstance().addMessage(null, this.msg);
                return null;
            }
            newPass = Encrypt.getContraseniaAleatoria(8);
            System.out.println("Se ha generado una nueva contraseña para le usuario: " + newPass);
            newPassKey = Encrypt.getSHA512(newPass + user.getSalt());

            Date fReg = new Date();
            long fecha = fReg.getTime();
            Timestamp timestamp = new Timestamp(fecha);
            user.setPasskey(newPassKey);
            user.setLastAction(timestamp);
            boolean update = new DaoUsuario().updateUsuario(user);

            String remitente = "pruebas.email001@gmail.com";
            String contrasenia = "passpruebas";
            String destinatario = user.getEmail();
            String asunto = "IQTB Validación: se ha restablecido su contraseña";
            String contenido = "Contraseña: "+newPass;
            boolean respuestaEmail = Email.envioEmail(remitente, contrasenia, destinatario, asunto, contenido);
            if (update && respuestaEmail) {

                this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La contraseña ha sido enviada al correo electronico " + user.getEmail());
                FacesContext.getCurrentInstance().addMessage(null, this.msg);
                return "/login?faces-redirect=true";

            } else {
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se ha podido restablecer la contraseña, Por favor intente de nuevo.");
                FacesContext.getCurrentInstance().addMessage(null, this.msg);
                return null;
            }
        } catch (Exception e) {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se ha podido restablecer la contraseña, Por favor intente de nuevo.");
            FacesContext.getCurrentInstance().addMessage(null, this.msg);
            return null;
        }
    }

    public String recuperarContrasenia() {
        this.user = "";
        return "/Usuario/restablecer?faces-redirect=true";
    }
}

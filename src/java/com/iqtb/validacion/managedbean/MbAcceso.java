package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoUsuario;
import com.iqtb.validacion.encrypt.Encrypt;
import com.iqtb.validacion.pojo.Usuarios;
import java.io.Serializable;
import java.util.HashMap;
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
public class MbAcceso implements Serializable{

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
        try {
            
            this.usuario = new DaoUsuario().getByUserid(this.user);
            if (this.usuario.getPasskey().equals(Encrypt.getSHA512(this.pass + this.usuario.getSalt()))) {
                return "/principal?faces-redirect=true";
            } else {
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario/Contraseña incorrecto");
            }
        } catch (Exception ex) {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario/Contraseña incorrecto");
        }
        FacesContext.getCurrentInstance().addMessage(null, this.msg);

        return "/login";
    }
    
    public String logout(){
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Session cerrada correctamente", null);
        FacesContext.getCurrentInstance().addMessage(null, msg);

        return "/login?faces-redirect=true";
    }
    
    public String existeSeleccionEmpresa() {
        boolean existeSeleccionEmpresa = false;
        if (!this.empresaSeleccionada.equals("vacio")) {
            existeSeleccionEmpresa = true;
        } else {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor seleccione una empresa");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("existeSeleccionEmpresa", existeSeleccionEmpresa);
        return "/Template/general?faces-redirect=true";
    }
}

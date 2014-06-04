package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoUsuario;
import com.iqtb.validacion.encrypt.Encrypt;
import com.iqtb.validacion.pojo.Usuarios;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
/**
 *
 * @author danielromero
 */
@ManagedBean
@SessionScoped
public class MbAcceso {

 private String user;
    private String pass;
    private Usuarios usuario;
    private FacesMessage msg;

    public MbAcceso() {
        this.usuario = new Usuarios();
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

    public String login() {
        try {
            
            this.usuario = new DaoUsuario().getByUserid(this.user);
            if (this.usuario.getPasskey().equals(Encrypt.getSHA512(this.pass + this.usuario.getSalt()))) {
                return "/principal?faces-redirect=true";
            } else {
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario/Contraseña incorrectos");
            }
        } catch (Exception ex) {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario/Contraseña incorrectos");
        }
        FacesContext.getCurrentInstance().addMessage(null, this.msg);

        return "/login";
    }
}

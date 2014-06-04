package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoUsuario;
import com.iqtb.validacion.encrypt.Encrypt;
import com.iqtb.validacion.pojo.Usuarios;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author danielromero
 */
@ManagedBean
@SessionScoped
public class MbAcceso {

    private String user;
    private String pass;
    private FacesMessage message;
    
    public MbAcceso() {
    }
    public String login(){
        try {
            DaoUsuario daoUsuario = new DaoUsuario();
            Usuarios usuario = daoUsuario.getByUserid(this.user);
            if (usuario != null) {
                if (usuario.getPasskey().equals(Encrypt.getSHA512(this.pass+usuario.getSalt()))) {
                    this.message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Ha iniciado sessión");
                    FacesContext.getCurrentInstance().addMessage(null, this.message);
//                    HttpSession httpSession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
//                    httpSession.setAttribute("correoElectronico", this.user);
                    return "/principal?faces-redirect=true";
                }
            }
            this.message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario/Contraseña incorrecta");
            FacesContext.getCurrentInstance().addMessage(null, this.message);
            return "/login";
            
        } catch (Exception e) {
            
            return null;
        }
        
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
    
    
}

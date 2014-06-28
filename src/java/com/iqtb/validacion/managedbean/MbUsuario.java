package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.pojo.Usuarios;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author danielromero
 */
@ManagedBean
@RequestScoped
public class MbUsuario implements Serializable{

    private Usuarios usuario;
    private String user;
    private String pass;
    private String confirmarPass;
    
    public MbUsuario() {
        this.usuario = new Usuarios();
    }

    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
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

    public String getConfirmarPass() {
        return confirmarPass;
    }

    public void setConfirmarPass(String confirmarPass) {
        this.confirmarPass = confirmarPass;
    }
    
    public void nuevaContrasenia(){
        
    }
    
//    public void hola() throws Exception{
//        MbConfiguracion mbConfiguracion = new MbConfiguracion();
//        mbConfiguracion.bitacora(idUsuario, idServicio, idEmpresa, user, user);
//    }
}

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
    private String confirmarPass;
    private Usuarios usuario;
    private Usuarios usuarioContraseña;
    private String empresaSeleccionada;
    private Map<String, String> listaEmpresas;
    private FacesMessage msg;

    public MbAcceso() {
        this.usuario = new Usuarios();
        this.listaEmpresas = new HashMap<String, String>();
    }

    public Usuarios getUsuarioContraseña() {
        return usuarioContraseña;
    }

    public void setUsuarioContraseña(Usuarios usuarioContraseña) {
        this.usuarioContraseña = usuarioContraseña;
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

    public String getConfirmarPass() {
        return confirmarPass;
    }

    public void setConfirmarPass(String confirmarPass) {
        this.confirmarPass = confirmarPass;
    }

    public String login() {
        boolean mostarDialog = false;
        boolean update = false;
        int fallidos;
        try {

            this.usuario = new DaoUsuario().getByUserid(this.user);
            fallidos = this.usuario.getIntentosFallidos();
            if (this.usuario.getPasskey().equals(Encrypt.getSHA512(this.pass + this.usuario.getSalt()))) {

                if (this.usuario.getEstado().equals("ACTIVO")) {
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

                    this.usuario.setEstado("AUTENTICADO");
                    this.usuario.setIntentosFallidos(0);
                    Date fReg = new Date();
                    long fecha = fReg.getTime();
                    Timestamp timestamp = new Timestamp(fecha);
                    this.usuario.setLastAction(timestamp);
                    update = new DaoUsuario().updateUsuario(this.usuario);
                    if (update) {
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", this.usuario.getUserid() + " ha iniciado sessión");
                    }
                    empresaSeleccionada = null;
                } else {
                    if (this.usuario.getEstado().equals("NUEVO") || this.usuario.getEstado().equals("EXPIRADO")) {
                        this.pass = null;
                        return "/Usuario/cambiar?faces-redirect=true";

                    } else {
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se puede iniciar sesión, el estado es: " + this.usuario.getEstado());
                    }
                }

            } else {
                fallidos++;

                if (fallidos == 3) {
                    Date fReg = new Date();
                    long fecha = fReg.getTime();
                    Timestamp timestamp = new Timestamp(fecha);
                    this.usuario.setIntentosFallidos(fallidos);
                    this.usuario.setLastAction(timestamp);
                    this.usuario.setEstado("BLOQUEADO");
                    update = new DaoUsuario().updateUsuario(this.usuario);
                    if (update) {
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuario bloqueado", "Usuario/Contraseña incorrecto");
                    }
                } else {

                    this.usuario.setIntentosFallidos(fallidos);
                    Date fReg = new Date();
                    long fecha = fReg.getTime();
                    Timestamp timestamp = new Timestamp(fecha);
                    this.usuario.setLastAction(timestamp);
                    update = new DaoUsuario().updateUsuario(this.usuario);
                    if (update) {
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario/Contraseña incorrecto");
                    }
                }

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
        boolean update = false;

        try {
            this.usuario.setEstado("ACTIVO");
            Date fReg = new Date();
            long fecha = fReg.getTime();
            Timestamp timestamp = new Timestamp(fecha);
            this.usuario.setLastAction(timestamp);
            update = new DaoUsuario().updateUsuario(this.usuario);

            if (update) {
                FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
                this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Sesión cerrada correctamente", null);
                FacesContext.getCurrentInstance().addMessage(null, this.msg);

                return "/login?faces-redirect=true";
            }

        } catch (Exception e) {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al cerrar sesión");
        }
        return null;
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

    public void restablecerContrasenia() throws Exception {
        String newPass;
        String newPassKey;
        try {
            this.usuarioContraseña = new DaoUsuario().getByUserid(this.user);

            if (this.usuarioContraseña.equals("")) {
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor introdusca usuario valido para restablecer contraseña.");
                FacesContext.getCurrentInstance().addMessage(null, this.msg);
                return;
            }
            newPass = Encrypt.getContraseniaAleatoria(8);
            System.out.println("Se ha generado una nueva contraseña para le usuario: " + newPass);
            newPassKey = Encrypt.getSHA512(newPass + this.usuarioContraseña.getSalt());

            Date fReg = new Date();
            long fecha = fReg.getTime();
            Timestamp timestamp = new Timestamp(fecha);
            this.usuarioContraseña.setPasskey(newPassKey);
            this.usuarioContraseña.setLastAction(timestamp);
            boolean update = new DaoUsuario().updateUsuario(this.usuarioContraseña);

            String remitente = "pruebas.email001@gmail.com";
            String contrasenia = "passpruebas";
            String destinatario = this.usuarioContraseña.getEmail();
            String asunto = "IQTB Validación: se ha restablecido su contraseña";
            String contenido = "Contraseña: " + newPass;
            boolean respuestaEmail = Email.envioEmail(remitente, contrasenia, destinatario, asunto, contenido);
            if (update && respuestaEmail) {

                this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La contraseña se ha enviado al correo electrónico.");
                FacesContext.getCurrentInstance().addMessage(null, this.msg);
//                return "/login?faces-redirect=true";

            } else {
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo restablecer la contraseña, Por favor intente de nuevo.");
                FacesContext.getCurrentInstance().addMessage(null, this.msg);
                return;
            }
        } catch (Exception e) {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo restablecer la contraseña, Por favor intente de nuevo.");
            FacesContext.getCurrentInstance().addMessage(null, this.msg);
            return;
        }
    }

    public String recuperarContrasenia() {
        this.user = "";
        return "/Usuario/restablecer?faces-redirect=true";
    }

    public String irLogin() {
        this.user = "";
        return "/login?faces-redirect=true";
    }
    
    public String irPrincipal() {
        return "/principal?faces-redirect=true";
    }

    public void nuevaContrasenia() {
        boolean update = false;
        
        try {
            this.usuario = new DaoUsuario().getByUserid(this.user);

            if (this.pass.equals(this.confirmarPass)) {
                
                this.usuario.setSalt(Encrypt.getSALT(20));
                this.usuario.setPasskey(Encrypt.getSHA512(this.pass + this.usuario.getSalt()));
                this.usuario.setEstado("ACTIVO");
                this.usuario.setIntentosFallidos(0);
                Date fReg = new Date();
                long fecha = fReg.getTime();
                Timestamp timestamp = new Timestamp(fecha);
                this.usuario.setLastAction(timestamp);
                update = new DaoUsuario().updateUsuario(this.usuario);
                
                if (update) {
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La contraseña se ha modificado.");
//                    return "/principal";
                }
            } else {
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Las contraseñas no coinciden.");
            }
        } catch (Exception e) {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo guardar la nueva contraseña, Por favor intente de nuevo.");
            FacesContext.getCurrentInstance().addMessage(null, this.msg);
            
        }
        return;
    }
}

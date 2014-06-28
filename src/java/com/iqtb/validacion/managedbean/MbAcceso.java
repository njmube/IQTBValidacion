package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoBitacora;
import com.iqtb.validacion.dao.DaoConfiguracionServicios;
import com.iqtb.validacion.dao.DaoEmpresa;
import com.iqtb.validacion.dao.DaoServicio;
import com.iqtb.validacion.dao.DaoUsuario;
import com.iqtb.validacion.encrypt.Encrypt;
import com.iqtb.validacion.mail.Email;
import com.iqtb.validacion.pojo.Bitacora;
import com.iqtb.validacion.pojo.ConfiguracionesServicios;
import com.iqtb.validacion.pojo.Empresas;
import com.iqtb.validacion.pojo.Servicios;
import com.iqtb.validacion.pojo.Usuarios;
import com.iqtb.validacion.util.DateTime;
import static com.iqtb.validacion.util.DateTime.getTimestamp;
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
import javax.servlet.http.HttpServletRequest;
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
    private Empresas empresa;
    private String empresaSeleccionada;
    private Map<String, String> listaEmpresas;
    private Bitacora bitacora;
    private Servicios servicio;
    private final String nombreServicio = "ADMINISTRACION_ACCESO";
    private String descripcionServicio;
    private ConfiguracionesServicios DIAS_EXPIRAR_CONT;
    private ConfiguracionesServicios DIAS_INACTIVIDAD;
    private ConfiguracionesServicios NUM_INT_FALLIDOS;
    private ConfiguracionesServicios LONGITUD_MIN_CONT;
    private boolean chkBoolean;
    private String newPass;
    private FacesMessage msg;
//    private HttpServletRequest httpServletRequest;
//    private FacesContext faceContext;

    public MbAcceso() {
        this.usuario = new Usuarios();
        this.listaEmpresas = new HashMap<String, String>();
        this.empresa = new Empresas();
        this.bitacora = new Bitacora();
        this.servicio = new Servicios();
        this.DIAS_EXPIRAR_CONT = new ConfiguracionesServicios();

        try {
            this.servicio = new DaoServicio().getServicoByNombre(this.nombreServicio);
            this.DIAS_EXPIRAR_CONT = new DaoConfiguracionServicios().getConfigServicioByIdServicioPropiedad(this.servicio.getIdServicio(), "DIAS_EXPIRAR_CONT");
            this.DIAS_INACTIVIDAD = new DaoConfiguracionServicios().getConfigServicioByIdServicioPropiedad(this.servicio.getIdServicio(), "DIAS_INACTIVIDAD");
            this.NUM_INT_FALLIDOS = new DaoConfiguracionServicios().getConfigServicioByIdServicioPropiedad(this.servicio.getIdServicio(), "NUM_INT_FALLIDOS");
            this.LONGITUD_MIN_CONT = new DaoConfiguracionServicios().getConfigServicioByIdServicioPropiedad(this.servicio.getIdServicio(), "LONGITUD_MIN_CONT");
        } catch (Exception e) {
            System.out.println("Error al obtener el servicio y configuracion de Servicio.");
        }
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

    public boolean isChkBoolean() {
        return chkBoolean;
    }

    public void setChkBoolean(boolean chkBoolean) {
        this.chkBoolean = chkBoolean;
    }

    public String getNewPass() {
        return newPass;
    }

    public void setNewPass(String newPass) {
        this.newPass = newPass;
    }

    public String login() {
        boolean mostarDialog = false;
        boolean update = false;
        int fallidos;
        try {
            System.out.println("consulta para obtener usuario");
            System.out.println("user: " + this.user);
            System.out.println("pass: " + this.pass);
            this.usuario = new DaoUsuario().getByUserid(this.user);

            System.out.println("usuario: " + this.usuario.getNombre());

            fallidos = this.usuario.getIntentosFallidos();
            if (this.usuario.getPasskey().equals(Encrypt.getSHA512(this.pass + this.usuario.getSalt()))) {
                System.out.println("Contraseña Correcta");
                Date test = new Date();
                int diasInactivo = Integer.parseInt(this.DIAS_INACTIVIDAD.getValor());
                Date lastAction = DateTime.sumarRestarDiasFecha(test, -diasInactivo);

                if ((this.usuario.getLastAction().getTime() - (lastAction.getTime())) > 0) {
                    System.out.println("LASTACTION ok");

                    if ((this.usuario.getDateExpirationPass().getTime() - (new Date().getTime())) > 0) {

                        if (this.usuario.getEstado().equals("ACTIVO")) {
                            List<Empresas> lista = new DaoEmpresa().getEmpresaById(this.usuario.getIdUsuario());
                            for (Empresas empresas : lista) {
                                listaEmpresas.put(empresas.getRfc(), empresas.getRfc());
                            }

                            if (listaEmpresas.size() > 1) {
                                mostarDialog = true;
                            } else {
                                this.empresaSeleccionada = lista.get(0).getRfc();

                                this.empresa = new DaoEmpresa().getEmpresaByRFC(this.empresaSeleccionada);
                                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuario", this.usuario);
                                this.usuario.setEstado("AUTENTICADO");
                                this.usuario.setIntentosFallidos(0);
                                this.usuario.setLastAction(getTimestamp());
                                update = new DaoUsuario().updateUsuario(this.usuario);

                                this.descripcionServicio = "[ACCESO] Inicio de sesión: Usuario " + this.usuario.getUserid() + " Empresa Seleccionada " + this.empresa.getRfc() + ".";

                                if (update) {
                                    bitacora(this.usuario.getIdUsuario(), this.servicio.getIdServicio(), this.empresa.getIdEmpresa(), this.descripcionServicio, "INFO");
                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", this.usuario.getUserid() + " ha iniciado sesión.");
//                                    System.out.println("[ACCESO] Inicio de sesión: Usuario " + this.usuario.getUserid() + " Empresa Seleccionada " + this.empresa.getRfc() + ".");
                                }
//                        empresaSeleccionada = null;
                                return "/principal?faces-redirect=true";
                            }

                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuario", this.usuario);
                            this.usuario.setEstado("AUTENTICADO");
                            this.usuario.setIntentosFallidos(0);
                            this.usuario.setLastAction(getTimestamp());
                            update = new DaoUsuario().updateUsuario(this.usuario);

                            if (update) {
                                bitacora(this.usuario.getIdUsuario(), this.servicio.getIdServicio(), this.empresa.getIdEmpresa(), this.descripcionServicio, "INFO");
                                this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", this.usuario.getUserid() + " seleccione una empresa para continuar.");
                            }
                            empresaSeleccionada = null;
                        } else {

                            if (this.usuario.getEstado().equals("NUEVO") || this.usuario.getEstado().equals("EXPIRADO")) {
                                this.descripcionServicio = "[ACCESO] Usuario: " + this.usuario.getUserid() + " Estado: " + this.usuario.getEstado() + " se redigirá al formulario para crear una nueva contraseña.";
                                this.bitacora.setIdUsuario(this.usuario.getIdUsuario());
                                this.bitacora.setIdServicio(this.servicio.getIdServicio());
                                this.bitacora.setDescripcion(this.descripcionServicio);
                                this.bitacora.setFecha(getTimestamp());
                                this.bitacora.setTipo("INFO");
                                new DaoBitacora().registarBitacora(this.bitacora);
                                this.pass = null;
                                System.out.println("ir a usuario cambiar contraseña");
                                return "/Usuario/cambiar?faces-redirect=true";

                            } else {
                                this.descripcionServicio = "[ACCESO] Usuario: " + this.usuario.getUserid() + " No se puede iniciar sesión, el estado es: " + this.usuario.getEstado() + ".";
                                this.bitacora.setIdUsuario(this.usuario.getIdUsuario());
                                this.bitacora.setIdServicio(this.servicio.getIdServicio());
                                this.bitacora.setDescripcion(this.descripcionServicio);
                                this.bitacora.setFecha(getTimestamp());
                                this.bitacora.setTipo("INFO");
                                new DaoBitacora().registarBitacora(this.bitacora);
                                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se puede iniciar sesión, el estado es: " + this.usuario.getEstado());
                            }
                        }
                    } else {

//                        this.usuario.setLastAction(getTimestamp());
                        this.usuario.setEstado("EXPIRADO");

                        this.descripcionServicio = "[ACCESO] Usuario " + this.usuario.getUserid() + " la contraseña ha expirado, el estado es " + this.usuario.getEstado() + ".";

                        new DaoUsuario().updateUsuario(this.usuario);
                        this.bitacora.setIdUsuario(this.usuario.getIdUsuario());
                        this.bitacora.setIdServicio(this.servicio.getIdServicio());
                        this.bitacora.setDescripcion(this.descripcionServicio);
                        this.bitacora.setFecha(getTimestamp());
                        this.bitacora.setTipo("INFO");
                        new DaoBitacora().registarBitacora(this.bitacora);
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario " + this.usuario.getUserid() + " la contraseña ha expirado, el estado es " + this.usuario.getEstado() + ", consulte al Administrador.");

                    }

                } else {
//                    this.usuario.setLastAction(getTimestamp());
                    this.usuario.setEstado("INACTIVO");

                    this.descripcionServicio = "[ACCESO] Usuario " + this.usuario.getUserid() + " han trancurido mas de " + this.DIAS_INACTIVIDAD.getValor() + " dias de inactividad, el estado es " + this.usuario.getEstado() + ".";

                    new DaoUsuario().updateUsuario(this.usuario);
                    this.bitacora.setIdUsuario(this.usuario.getIdUsuario());
                    this.bitacora.setIdServicio(this.servicio.getIdServicio());
                    this.bitacora.setDescripcion(this.descripcionServicio);
                    this.bitacora.setFecha(getTimestamp());
                    this.bitacora.setTipo("INFO");
                    new DaoBitacora().registarBitacora(this.bitacora);
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario " + this.usuario.getUserid() + " han trancurido mas de " + this.DIAS_INACTIVIDAD.getValor() + " dias de inactividad, el estado es " + this.usuario.getEstado() + ", consulte al Administrador.");

                }

            } else {

                fallidos++;
                int intentos = Integer.parseInt(this.NUM_INT_FALLIDOS.getValor());

                if (fallidos >= intentos) {
                    this.usuario.setIntentosFallidos(fallidos);
                    this.usuario.setLastAction(getTimestamp());
                    this.usuario.setEstado("BLOQUEADO");
                    update = new DaoUsuario().updateUsuario(this.usuario);
                    if (update) {
                        this.descripcionServicio = "[ACCESO] Usuario: " + this.usuario.getUserid() + " alcanzo el número de intentos para acceder, el estado es: " + this.usuario.getEstado() + ".";
                        this.bitacora.setIdUsuario(this.usuario.getIdUsuario());
                        this.bitacora.setIdServicio(this.servicio.getIdServicio());
                        this.bitacora.setDescripcion(this.descripcionServicio);
                        this.bitacora.setFecha(getTimestamp());
                        this.bitacora.setTipo("INFO");
                        new DaoBitacora().registarBitacora(this.bitacora);
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuario bloqueado", "Usuario/Contraseña incorrecto");
                    } else {
                        System.out.println("UPDATE " + update);
                    }
                } else {

                    this.usuario.setIntentosFallidos(fallidos);
                    this.usuario.setLastAction(getTimestamp());
                    update = new DaoUsuario().updateUsuario(this.usuario);
                    if (update) {
                        System.out.println("update: " + update + " fallidos: " + fallidos);
                        System.out.println("Usuario: " + this.usuario.getNombre());
                        this.descripcionServicio = "[ACCESO] Usuario: " + this.usuario.getUserid() + " intento accesar, contraseña incorrecta, intentos fallidos: " + this.usuario.getIntentosFallidos() + "/" + intentos + ".";
                        this.bitacora.setIdUsuario(this.usuario.getIdUsuario());
                        this.bitacora.setIdServicio(this.servicio.getIdServicio());
                        this.bitacora.setDescripcion(this.descripcionServicio);
                        this.bitacora.setFecha(getTimestamp());
                        this.bitacora.setTipo("INFO");
                        new DaoBitacora().registarBitacora(this.bitacora);
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario/Contraseña incorrecto");
                    } else {
                        System.out.println("UPDATE " + update);
                    }
                }

            }

        } catch (Exception ex) {
//            this.descripcionServicio = "[ACCESO] ERROR MbAcceso Usuario: " + this.usuario.getUserid() + " Empresa: "+this.empresaSeleccionada;
//            this.bitacora.setIdUsuario(this.usuario.getIdUsuario());
//            this.bitacora.setIdServicio(this.servicio.getIdServicio());
//            this.bitacora.setDescripcion(this.descripcionServicio);
//            this.bitacora.setFecha(getTimestamp());
//            this.bitacora.setTipo("ERROR");
////            new DaoBitacora().registarBitacora(this.bitacora);
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario/Contraseña incorrectoooo");
            System.out.println("EXCEPTION ex ERROR");
        }
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("mostarDialog", mostarDialog);

        return null;
    }

    public String logout() {
        boolean update = false;
        String sesioinUsuario = ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid();
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        try {
            this.usuario.setEstado("ACTIVO");
            this.usuario.setLastAction(getTimestamp());
            update = new DaoUsuario().updateUsuario(this.usuario);

            if (update) {
                FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
                this.descripcionServicio = "[ACCESO] Fin de Sesión: Usuario " + this.usuario.getUserid() + ".";
                bitacora(this.usuario.getIdUsuario(), this.servicio.getIdServicio(), this.empresa.getIdEmpresa(), this.descripcionServicio, "INFO");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Sesión cerrada correctamente", null);
                FacesContext.getCurrentInstance().addMessage(null, this.msg);

                return "/login?faces-redirect=true";
            }

        } catch (Exception e) {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al cerrar sesión");
        }
        return null;
    }

    public boolean getSession() {
        boolean estado;
        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario") == null) {
            estado = false;
        } else {
            estado = true;
        }
        return estado;
    }

    public String existeSeleccionEmpresa() {
        boolean existeSeleccionEmpresa = false;
        if (!this.empresaSeleccionada.equals("vacio")) {
            existeSeleccionEmpresa = true;
        } else {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione una empresa");
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
        boolean respuestaEmail = false;
        int nPass = Integer.parseInt(this.LONGITUD_MIN_CONT.getValor());
        try {

            if (this.user == null || this.user.trim().isEmpty()) {
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor introdusca usuario valido para restablecer contraseña.");
                FacesContext.getCurrentInstance().addMessage(null, this.msg);
                return;
            } else {
                this.usuarioContraseña = new DaoUsuario().getByUserid(this.user);

                if (this.usuarioContraseña == null) {
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor introdusca usuario valido para restablecer contraseña.");
                } else {
                    newPass = Encrypt.getContraseniaAleatoria(nPass);
                    System.out.println("Se ha generado una nueva contraseña para le usuario: " + newPass);
                    newPassKey = Encrypt.getSHA512(newPass + this.usuarioContraseña.getSalt());

                    this.usuarioContraseña.setPasskey(newPassKey);
                    this.usuarioContraseña.setLastAction(getTimestamp());
                    boolean update = new DaoUsuario().updateUsuario(this.usuarioContraseña);

                    String remitente = "pruebas.email001@gmail.com";
                    String contrasenia = "passpruebas";
                    String destinatario = this.usuarioContraseña.getEmail();
                    String asunto = "IQTB Validación: se ha restablecido su contraseña";
                    String contenido = "Contraseña: " + newPass;
                    respuestaEmail = Email.envioEmail(remitente, contrasenia, destinatario, asunto, contenido);

                    if (update && respuestaEmail) {
                        this.descripcionServicio = "[ACCESO] Usuario: " + this.usuarioContraseña.getUserid() + " la contraseña ha sido restablecida y enviada al correo electrónico.";
                        this.bitacora.setIdUsuario(this.usuarioContraseña.getIdUsuario());
                        this.bitacora.setIdServicio(this.servicio.getIdServicio());
                        this.bitacora.setDescripcion(this.descripcionServicio);
                        this.bitacora.setFecha(getTimestamp());
                        this.bitacora.setTipo("INFO");
                        new DaoBitacora().registarBitacora(this.bitacora);
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La contraseña se ha enviado al correo electrónico.");
//                        FacesContext.getCurrentInstance().addMessage(null, this.msg);
//                return "/login?faces-redirect=true";

                    } else {
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo restablecer la contraseña, Por favor intente de nuevo.");
                        FacesContext.getCurrentInstance().addMessage(null, this.msg);
                        return;
                    }
                }

            }

        } catch (Exception e) {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo restablecer la contraseña, Por favor intente de nuevo.");
            FacesContext.getCurrentInstance().addMessage(null, this.msg);
            return;
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("actualiza", respuestaEmail);
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
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
        int nPass = Integer.parseInt(this.LONGITUD_MIN_CONT.getValor());
        System.out.println("Nueva Contraseña");

        try {

            if (this.pass == null || this.pass.trim().isEmpty()) {
                System.out.println("Introdusca un cantraseña valida");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Introdusca una contraseña valida.");
            } else {
                if (this.pass.length() >= nPass) {
                    System.out.println("Contraseña longitud correcta");
                    if (this.pass.equals(this.confirmarPass)) {
                        this.usuario = new DaoUsuario().getByUserid(this.user);
                        System.out.println("Se obtuvo el usuario");

                        this.usuario.setSalt(Encrypt.getSALT(20));
                        this.usuario.setPasskey(Encrypt.getSHA512(this.pass + this.usuario.getSalt()));
                        this.usuario.setEstado("ACTIVO");
                        this.usuario.setIntentosFallidos(0);
                        this.usuario.setLastAction(getTimestamp());
                        update = new DaoUsuario().updateUsuario(this.usuario);
                        System.out.println("Modificaron los datos del Usuario");

                        if (update) {
                            this.descripcionServicio = "[ACCESO] Usuario: " + this.usuario.getUserid() + " ha modificado la contraseña, el estado es " + this.usuario.getEstado();
                            this.bitacora.setIdUsuario(this.usuario.getIdUsuario());
                            this.bitacora.setIdServicio(this.servicio.getIdServicio());
                            this.bitacora.setDescripcion(this.descripcionServicio);
                            this.bitacora.setFecha(getTimestamp());
                            this.bitacora.setTipo("INFO");
                            new DaoBitacora().registarBitacora(this.bitacora);
                            System.out.println("Se agrego a Bitacora");
                            this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La contraseña se ha modificado.");

                        }
                    } else {
                        System.out.println("las contraseñas no son iguales.");
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Las contraseñas no coinciden.");
//                        FacesContext.getCurrentInstance().addMessage(null, this.msg);
                    }

                } else {
                    System.out.println("La contraseña debe contener al menos " + nPass + " caracteres.");
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La contraseña debe contener al menos " + nPass + " caracteres.");
//                    FacesContext.getCurrentInstance().addMessage(null, this.msg);
                }
            }

        } catch (Exception e) {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo guardar la nueva contraseña, Por favor intente de nuevo.");
            FacesContext.getCurrentInstance().addMessage(null, this.msg);
            System.out.println("ERROR NUEVA CONTRASEÑA");

        }
        this.pass = "";
        this.confirmarPass = "";
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("actualiza", update);
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
    }

    public String modContasenia() {
        System.out.println("modContraseña");
        boolean update = false;
        int nPass = Integer.parseInt(this.LONGITUD_MIN_CONT.getValor());
        try {
            if (chkBoolean) {
                System.out.println("boolean "+chkBoolean);
                if (this.usuario.getPasskey().equals(Encrypt.getSHA512(this.pass + this.usuario.getSalt()))) {
                    System.out.println("pass igual a passkey");
                    if (this.newPass.length() >= nPass) {
                        System.out.println("newPass mayor a npass");
                        if (this.newPass.equals(this.confirmarPass)) {
                            DaoUsuario daoUsuario = new DaoUsuario();
                            this.usuario.setSalt(Encrypt.getSALT(20));
                            this.usuario.setPasskey(Encrypt.getSHA512(this.newPass + this.usuario.getSalt()));
                            this.usuario.setLastAction(getTimestamp());
                            update = daoUsuario.updateUsuario(this.usuario);
                            if (update) {
                                this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "los datos se han modificado.");
//                                FacesContext.getCurrentInstance().addMessage(null, this.msg);
                                System.out.println("los datos se han modificado");
                            } else {
                                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al modificar los datos.");
//                                FacesContext.getCurrentInstance().addMessage(null, this.msg);
                                System.out.println("Error al modificar los datos");
                            }

                        } else {
                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Las contraseñas no coinciden.");
//                            FacesContext.getCurrentInstance().addMessage(null, this.msg);
                            System.out.println("Las conyraseñas no coinciden");
                        }

                    } else {
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La contraseña debe contener al menos " + nPass + " caracteres.");
//                        FacesContext.getCurrentInstance().addMessage(null, this.msg);
                        System.out.println("Las conyraseñas no coinciden");
                    }

                } else {
                    System.out.println("Introdusca su contraseña actual");
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, introdusca su contraseña actual.");
//                    FacesContext.getCurrentInstance().addMessage(null, this.msg);
                }

            } else {
                update = new DaoUsuario().updateUsuario(this.usuario);
                if (update) {
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "los datos se han modificado.");
                    System.out.println("los datos se han modificado");
                } else {
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al modificar los datos.");
                    System.out.println("Error al modificar los datos");
                }
            }

        } catch (Exception e) {
            System.out.println("Error ModContraseña");
        }
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
        return "/Usuario/modificarPass?faces-redirect=true";
    }

    public String irCDFIs() {
        FacesContext faceContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        System.out.println("MbAceso Empresa Seleccionada enviada a CFDIs: " + this.empresaSeleccionada);
        httpServletRequest.getSession().setAttribute("empresaSeleccionada", this.empresaSeleccionada);
        return "/CFDI/recibidos?faces-redirect=true";
    }

    public String irConfiguracionEmpresa() {
        FacesContext faceContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        System.out.println("MbAceso Empresa Seleccionada enviada a ConfiguracionEmpresa: " + this.empresaSeleccionada);
        httpServletRequest.getSession().setAttribute("empresaSeleccionada", this.empresaSeleccionada);
        return "/Configuracion/empresa?faces-redirect=true";
    }

    public String irConfiguracionNotificaciones() {
        FacesContext faceContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        System.out.println("MbAceso Empresa Seleccionada enviada a ConfiguracionEmpresa: " + this.empresaSeleccionada);
        httpServletRequest.getSession().setAttribute("empresaSeleccionada", this.empresaSeleccionada);
        return "/Configuracion/notificaciones?faces-redirect=true";
    }

    public String irConfiguracionPlantilla() {
        FacesContext faceContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        System.out.println("MbAceso Empresa Seleccionada enviada a ConfiguracionEmpresa: " + this.empresaSeleccionada);
        httpServletRequest.getSession().setAttribute("empresaSeleccionada", this.empresaSeleccionada);
        return "/Configuracion/plantilla?faces-redirect=true";
    }

    public String irConfiguracionEmail() {
        FacesContext faceContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        System.out.println("MbAceso Empresa Seleccionada enviada a ConfiguracionEmpresa: " + this.empresaSeleccionada);
        httpServletRequest.getSession().setAttribute("empresaSeleccionada", this.empresaSeleccionada);
        return "/Configuracion/email?faces-redirect=true";
    }

    public void bitacora(int idUsuario, int idServicio, int idEmpresa, String descripcion, String tipo) {
        boolean registro = false;

        try {

            this.bitacora.setIdUsuario(idUsuario);
            this.bitacora.setIdServicio(idServicio);
            this.bitacora.setIdEmpresa(idEmpresa);
            this.bitacora.setDescripcion(descripcion);
            this.bitacora.setFecha(getTimestamp());
            this.bitacora.setTipo(tipo);
            registro = new DaoBitacora().registarBitacora(this.bitacora);

            if (registro) {
                System.out.println("Registro insertado en bitacora");
            } else {
                System.out.println("Error al registrar en la Bitacora.");
            }
        } catch (Exception e) {
            System.out.println("Error al registrar en la Bitacora.");
        }

    }

}

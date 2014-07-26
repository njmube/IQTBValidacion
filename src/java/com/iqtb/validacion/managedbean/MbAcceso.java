package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoCfdisRecibidos;
import com.iqtb.validacion.dao.DaoConfiguracionServicios;
import com.iqtb.validacion.dao.DaoEmpresa;
import com.iqtb.validacion.dao.DaoRoles;
import com.iqtb.validacion.dao.DaoRolesOpciones;
import com.iqtb.validacion.dao.DaoServicio;
import com.iqtb.validacion.dao.DaoUsuario;
import com.iqtb.validacion.encrypt.Encrypt;
import static com.iqtb.validacion.encrypt.Encrypt.decrypt;
import com.iqtb.validacion.mail.ConexionSMTP;
import com.iqtb.validacion.pojo.CfdisRecibidos;
import com.iqtb.validacion.pojo.ConfiguracionesServicios;
import com.iqtb.validacion.pojo.Empresas;
import com.iqtb.validacion.pojo.Opciones;
import com.iqtb.validacion.pojo.Roles;
import com.iqtb.validacion.pojo.Servicios;
import com.iqtb.validacion.pojo.Usuarios;
import static com.iqtb.validacion.util.Bitacoras.errorBitacora;
import static com.iqtb.validacion.util.Bitacoras.insertBitacora;
import static com.iqtb.validacion.util.Bitacoras.registrarBitacora;
import com.iqtb.validacion.util.DateTime;
import static com.iqtb.validacion.util.DateTime.getTimestamp;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
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
    private Servicios servicio;
    private final String NOMBRE_SERVICIO = "ADMINISTRACION_ACCESO";
    private String descripcionServicio;
//    private ConfiguracionesServicios DIAS_EXPIRAR_CONT;
    private ConfiguracionesServicios DIAS_INACTIVIDAD;
    private ConfiguracionesServicios NUM_INT_FALLIDOS;
    private ConfiguracionesServicios LONGITUD_MIN_CONT;
    private boolean chkBoolean;
    private boolean intentarLogin;
    private String newPass;
    private List<Opciones> opciones;
    private boolean rolEmpresa;
    private boolean rolServidor;
    private boolean administarCFDIs;
    private boolean modificarEmpresa;
    private boolean configNotificaciones;
    private boolean configPlantillas;
    private boolean configSMTP;
    private boolean configPOP;
    private boolean administrarUsuarios;
    private boolean configServicios;
    private boolean administrarEmpresa;
    private boolean administrarSocios;
    private int cfdisValidos;
    private int cfdisInvalidos;
    private int cfdisDuplicados;
    private int cfdisTotal;
    private int cfdisSinValidar;
    private ConfiguracionesServicios expPass;
    private FacesMessage msg;
    private static final Logger logger = Logger.getLogger(MbAcceso.class);
//    private HttpServletRequest httpServletRequest;
//    private FacesContext faceContext;

    public MbAcceso() {
        this.usuario = new Usuarios();
        this.listaEmpresas = new HashMap<String, String>();
        this.empresa = new Empresas();
        this.servicio = new Servicios();
        opciones = new ArrayList<Opciones>();
        cfdisTotal = 0;
        cfdisDuplicados = 0;
        cfdisInvalidos = 0;
        cfdisValidos = 0;
        cfdisSinValidar = 0;
//        this.DIAS_EXPIRAR_CONT = new ConfiguracionesServicios();

        try {
            this.servicio = new DaoServicio().getServicoByNombre(this.NOMBRE_SERVICIO);
//            this.DIAS_EXPIRAR_CONT = new DaoConfiguracionServicios().getConfigServicioByIdServicioPropiedad(this.servicio.getIdServicio(), "DIAS_EXPIRAR_CONT");
            this.DIAS_INACTIVIDAD = new DaoConfiguracionServicios().getConfigServicioByIdServicioPropiedad(this.servicio.getIdServicio(), "DIAS_INACTIVIDAD");
            this.NUM_INT_FALLIDOS = new DaoConfiguracionServicios().getConfigServicioByIdServicioPropiedad(this.servicio.getIdServicio(), "NUM_INT_FALLIDOS");
            this.LONGITUD_MIN_CONT = new DaoConfiguracionServicios().getConfigServicioByIdServicioPropiedad(this.servicio.getIdServicio(), "LONGITUD_MIN_CONT");
            this.expPass = new DaoConfiguracionServicios().getConfigServicioByIdServicioPropiedad(this.servicio.getIdServicio(), "DIAS_EXPIRAR_CONT");
            intentarLogin = true;
        } catch (Exception e) {
            intentarLogin = false;
            logger.error("Error al obtener el servicio y configuracion de Servicio. ERROR: " + e);
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

    public boolean isRolEmpresa() {
        rolEmpresa = rolEmpresa();
        return rolEmpresa;
    }

    public void setRolEmpresa(boolean rolEmpresa) {
        this.rolEmpresa = rolEmpresa;
    }

    public boolean isRolServidor() {
        rolServidor = rolServidor();
        return rolServidor;
    }

    public void setRolServidor(boolean rolServidor) {
        this.rolServidor = rolServidor;
    }

    public boolean isAdministarCFDIs() {
        administarCFDIs = administarCFDIs();
        return administarCFDIs;
    }

    public void setAdministarCFDIs(boolean administarCFDIs) {
        this.administarCFDIs = administarCFDIs;
    }

    public boolean isModificarEmpresa() {
        modificarEmpresa = modificarEmpresa();
        return modificarEmpresa;
    }

    public void setModificarEmpresa(boolean modificarEmpresa) {
        this.modificarEmpresa = modificarEmpresa;
    }

    public boolean isConfigNotificaciones() {
        configNotificaciones = configNotificaciones();
        return configNotificaciones;
    }

    public void setConfigNotificaciones(boolean configNotificaciones) {
        this.configNotificaciones = configNotificaciones;
    }

    public boolean isConfigPlantillas() {
        configPlantillas = configPlantillas();
        return configPlantillas;
    }

    public void setConfigPlantillas(boolean configPlantillas) {
        this.configPlantillas = configPlantillas;
    }

    public boolean isConfigSMTP() {
        configSMTP = configSMTP();
        return configSMTP;
    }

    public void setConfigSMTP(boolean configSMTP) {
        this.configSMTP = configSMTP;
    }

    public boolean isConfigPOP() {
        configPOP = configPOP();
        return configPOP;
    }

    public void setConfigPOP(boolean configPOP) {
        this.configPOP = configPOP;
    }

    public boolean isAdministrarUsuarios() {
        administrarUsuarios = administrarUsuarios();
        return administrarUsuarios;
    }

    public void setAdministrarUsuarios(boolean administrarUsuarios) {
        this.administrarUsuarios = administrarUsuarios;
    }

    public boolean isConfigServicios() {
        configServicios = configServicios();
        return configServicios;
    }

    public void setConfigServicios(boolean configServicios) {
        this.configServicios = configServicios;
    }

    public boolean isAdministrarEmpresa() {
        administrarEmpresa = administrarEmpresa();
        return administrarEmpresa;
    }

    public void setAdministrarEmpresa(boolean administrarEmpresa) {
        this.administrarEmpresa = administrarEmpresa;
    }

    public int getCfdisValidos() {
        return cfdisValidos;
    }

    public void setCfdisValidos(int cfdisValidos) {
        this.cfdisValidos = cfdisValidos;
    }

    public int getCfdisInvalidos() {
        return cfdisInvalidos;
    }

    public void setCfdisInvalidos(int cfdisInvalidos) {
        this.cfdisInvalidos = cfdisInvalidos;
    }

    public int getCfdisDuplicados() {
        return cfdisDuplicados;
    }

    public void setCfdisDuplicados(int cfdisDuplicados) {
        this.cfdisDuplicados = cfdisDuplicados;
    }

    public int getCfdisTotal() {
        return cfdisTotal;
    }

    public void setCfdisTotal(int cfdisTotal) {
        this.cfdisTotal = cfdisTotal;
    }

    public int getCfdisSinValidar() {
        return cfdisSinValidar;
    }

    public void setCfdisSinValidar(int cfdisSinValidar) {
        this.cfdisSinValidar = cfdisSinValidar;
    }

    public boolean isAdministrarSocios() {
        return administrarSocios = administrarSocios();
    }

    public void setAdministrarSocios(boolean administrarSocios) {
        this.administrarSocios = administrarSocios;
    }

    public String login() {
        logger.info("LOGIN");
        if (!intentarLogin) {
            logger.error("LOGIN No se puede realizar el login");
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se puede realizar el login. Por favor, intentelo mas tarde.");
            FacesContext.getCurrentInstance().addMessage(null, this.msg);
            return null;
        }
        boolean mostarDialog = false;
        boolean mostarConfigSMTP = false;
        boolean update = false;
        int fallidos;
        try {
            this.usuario = new DaoUsuario().getByUserid(this.user);
            logger.info("LOGIN Usuario: " + this.usuario.getUserid());

            fallidos = this.usuario.getIntentosFallidos();
            if (this.user != null && !this.user.isEmpty()) {
                this.usuario = new DaoUsuario().getByUserid(this.user);

                if (this.usuario != null) {
                    if (this.usuario.getPasskey().equals(Encrypt.getSHA512(this.pass + this.usuario.getSalt()))) {
//                        logger.info("Contraseña Correcta");
                        Date test = new Date();
                        int diasInactivo = Integer.parseInt(this.DIAS_INACTIVIDAD.getValor());
                        Date lastAction = DateTime.sumarRestarDiasFecha(test, -diasInactivo);

                        if ((this.usuario.getLastAction().getTime() - (lastAction.getTime())) > 0) {
//                            logger.info("LASTACTION ok");

                            if ((this.usuario.getDateExpirationPass().getTime() - (new Date().getTime())) > 0) {

                                if (this.usuario.getEstado().equals("ACTIVO")) {
                                    Roles rol;
                                    DaoRoles daoRol = new DaoRoles();
                                    rol = daoRol.getRolById(usuario.getIdRol());
                                    if (rol.getTipo().equals("SERVIDOR")) {
                                        System.out.println("SERVIDOR");
                                        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuario", this.usuario);
                                        this.usuario.setEstado("AUTENTICADO");
                                        this.usuario.setIntentosFallidos(0);
                                        this.usuario.setLastAction(getTimestamp());
                                        update = new DaoUsuario().updateUsuario(this.usuario);

                                        if (update) {
                                            mostarConfigSMTP = existeServSMTP();
                                            System.out.println("mostarConfigSMTP "+mostarConfigSMTP);
                                            opciones = new DaoRolesOpciones().getOpcionesByIdRol(usuario.getIdRol());
                                            this.descripcionServicio = "[ACCESO] Inicio de sesión: Usuario " + this.usuario.getUserid() + ".";
                                            insertBitacora(this.usuario.getIdUsuario(), this.servicio.getIdServicio(), this.descripcionServicio, "INFO");
                                            this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", this.usuario.getUserid() + " ha iniciado sesión.");
                                            FacesContext.getCurrentInstance().addMessage(null, this.msg);
                                            logger.info("LOGIN Inicio de sesión: Usuario " + this.usuario.getUserid() + ".");
                                            return "/principal?faces-redirect=true";
                                        } else {
                                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ha ocurrido un error al iniciar sesión. Por favor, intentelo mas tarde.");
                                            FacesContext.getCurrentInstance().addMessage(null, this.msg);
                                            logger.error("LOGIN No se ha podido iniciar sesión");
                                            return null;
                                        }
                                    } else {
                                        System.out.println("EMPRESA");
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

                                            if (update) {
                                                opciones = new DaoRolesOpciones().getOpcionesByIdRol(usuario.getIdRol());
                                                this.descripcionServicio = "[ACCESO] Inicio de sesión: Usuario " + this.usuario.getUserid() + " Empresa Seleccionada " + this.empresa.getRfc() + ".";
                                                registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), empresa.getIdEmpresa(), descripcionServicio, "INFO");
                                                this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", this.usuario.getUserid() + " ha iniciado sesión.");
                                                logger.info("LOGIN Inicio de sesión: Usuario " + this.usuario.getUserid() + " Empresa Seleccionada " + this.empresa.getRfc() + ".");
                                                return "/principal?faces-redirect=true";
                                            } else {
                                                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ha ocurrido un error al iniciar sesión. Por favor, intentelo mas tarde.");
                                                FacesContext.getCurrentInstance().addMessage(null, this.msg);
                                                logger.error("LOGIN No se ha podido iniciar sesión");
                                                return null;
                                            }
                                        }
                                        System.out.println("ENTRO");
                                        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuario", this.usuario);
                                        this.usuario.setEstado("AUTENTICADO");
                                        this.usuario.setIntentosFallidos(0);
                                        this.usuario.setLastAction(getTimestamp());
                                        update = new DaoUsuario().updateUsuario(this.usuario);

                                        if (update) {
                                            opciones = new DaoRolesOpciones().getOpcionesByIdRol(usuario.getIdRol());
                                            System.out.println("ACTUALIZO");
                                            this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", this.usuario.getUserid() + " seleccione una empresa para continuar.");
//                                            FacesContext.getCurrentInstance().addMessage(null, this.msg);

                                        } else {
                                            logger.error("LOGIN Usuario " + this.usuario.getUserid() + " Error al iniciar sesión.");
                                        }
                                    }

                                } else {

                                    if (this.usuario.getEstado().equals("NUEVO") || this.usuario.getEstado().equals("EXPIRADO")) {
                                        this.descripcionServicio = "[ACCESO] Usuario: " + this.usuario.getUserid() + " Estado: " + this.usuario.getEstado() + " se redigirá al formulario para crear una nueva contraseña.";
                                        insertBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), descripcionServicio, "INFO");
                                        this.pass = null;
                                        logger.info("LOGIN Usuario: " + this.usuario.getUserid() + " Estado: " + this.usuario.getEstado() + " se redigirá al formulario para crear una nueva contraseña.");
                                        return "/Usuario/cambiar?faces-redirect=true";

                                    } else {
                                        this.descripcionServicio = "[ACCESO] Usuario: " + this.usuario.getUserid() + " No puede iniciar sesión, el estado es: " + this.usuario.getEstado() + ".";
                                        insertBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), descripcionServicio, "WARNING");
                                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se puede iniciar sesión, el estado es: " + this.usuario.getEstado());
                                        logger.warn("LOGIN Usuario: " + this.usuario.getUserid() + " No puede iniciar sesión, el estado es: " + this.usuario.getEstado());
                                    }
                                }
                            } else {
                                this.usuario.setEstado("EXPIRADO");
                                new DaoUsuario().updateUsuario(this.usuario);
                                this.descripcionServicio = "[ACCESO] Usuario " + this.usuario.getUserid() + " la contraseña ha expirado, el estado es " + this.usuario.getEstado() + ".";
                                insertBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), descripcionServicio, "WARNING");
                                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario " + this.usuario.getUserid() + " la contraseña ha expirado, el estado es " + this.usuario.getEstado() + ".");
                                logger.warn("LOGIN Usuario " + this.usuario.getUserid() + " la contraseña ha expirado, el estado es " + this.usuario.getEstado());
                                return "/Usuario/cambiar?faces-redirect=true";

                            }

                        } else {
                            this.usuario.setEstado("INACTIVO");
                            new DaoUsuario().updateUsuario(this.usuario);
                            this.descripcionServicio = "[ACCESO] Usuario " + this.usuario.getUserid() + " han trancurido mas de " + this.DIAS_INACTIVIDAD.getValor() + " dias de inactividad, el estado es " + this.usuario.getEstado() + ".";
                            insertBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), descripcionServicio, "WARNING");
                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario " + this.usuario.getUserid() + " han trancurido mas de " + this.DIAS_INACTIVIDAD.getValor() + " dias de inactividad, el estado es " + this.usuario.getEstado() + ", consulte al Administrador.");
                            logger.warn("LOGIN Usuario " + this.usuario.getUserid() + " han trancurido mas de " + this.DIAS_INACTIVIDAD.getValor() + " dias de inactividad, el estado es " + this.usuario.getEstado() + ".");

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
                                insertBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), descripcionServicio, "WARNING");
                                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuario bloqueado", "Usuario/Contraseña incorrecto");
                                logger.warn("LOGIN Usuario: " + this.usuario.getUserid() + " alcanzo el número de intentos para acceder, el estado es: " + this.usuario.getEstado());
                            } else {
                                logger.error("LOGIN Error al modificar al usuario: " + usuario.getUserid() + " alcanzo el número de intentos para acceder");
                            }
                        } else {

                            this.usuario.setIntentosFallidos(fallidos);
                            this.usuario.setLastAction(getTimestamp());
                            update = new DaoUsuario().updateUsuario(this.usuario);
                            if (update) {
                                logger.info("Usuario: " + usuario.getUserid() + " Intentos fallidos: " + fallidos + "/" + intentos);
                                this.descripcionServicio = "[ACCESO] Usuario: " + this.usuario.getUserid() + " intento accesar, contraseña incorrecta, intentos fallidos: " + this.usuario.getIntentosFallidos() + "/" + intentos + ".";
                                insertBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), descripcionServicio, "WARNING");
                                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario/Contraseña incorrecto");
                                logger.warn("LOGIN Usuario: " + this.usuario.getUserid() + " intento accesar, contraseña incorrecta, intentos fallidos: " + this.usuario.getIntentosFallidos() + "/" + intentos);
                            } else {
                                logger.error("LOGIN Error al modificar al usuario: " + usuario.getUserid() + " intento accesar, contraseña incorrecta, intentos fallidos: " + this.usuario.getIntentosFallidos() + "/" + intentos);
                            }
                        }
                    }
                } else {
                    logger.warn("LOGIN Usuario: " + user + " no existe en base de datos");
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario/Contraseña incorrecto");
                }

            } else {
                logger.warn("LOGIN No existe un valor para usuario");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario/Contraseña incorrecto");
            }

        } catch (NoSuchAlgorithmException e) {
            this.descripcionServicio = "[ACCESO] LOGIN-getSHA512 ERROR: " + e;
            errorBitacora(servicio.getIdServicio(), descripcionServicio, "ERROR");
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al iniciar sesión");
            logger.error("LOGIN-getSHA512 ERROR " + e);
        } catch (Exception ex) {
            this.descripcionServicio = "[ACCESO] LOGIN ERROR: " + ex;
            errorBitacora(servicio.getIdServicio(), descripcionServicio, "ERROR");
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al iniciar sesión");
            logger.error("LOGIN ERROR " + ex.getMessage());
        }
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("mostarDialog", mostarDialog);
        RequestContext context1 = RequestContext.getCurrentInstance();
        context1.addCallbackParam("mostarConfigSMTP", mostarConfigSMTP);

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
                insertBitacora(this.usuario.getIdUsuario(), this.servicio.getIdServicio(), this.descripcionServicio, "INFO");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Sesión cerrada correctamente", null);
                FacesContext.getCurrentInstance().addMessage(null, this.msg);
                logger.info("Fin de Sesión: Usuario " + this.usuario.getUserid());

                return "/login?faces-redirect=true";
            } else {
                logger.error("Error al modificar los datos del Usuario " + this.usuario.getUserid() + " en el cierre de sesión");
            }

        } catch (Exception e) {
            logger.error("LOGOUT ERROR: " + e);
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
            System.out.println("Entro selleccion de empresa");
            existeSeleccionEmpresa = true;
        } else {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione una empresa");
            FacesContext.getCurrentInstance().addMessage(null, this.msg);
            logger.warn("No existe seleccion de Empresa");
            return null;
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("existeSeleccionEmpresa", existeSeleccionEmpresa);
        System.out.println("Empresa " + empresaSeleccionada);
        try {
            this.empresa = new DaoEmpresa().getEmpresaByRFC(this.empresaSeleccionada);
        } catch (Exception ex) {
            logger.error("existeSeleccionEmpresa Error al obtener la empresa ERROR " + ex);
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuario", this.usuario);
        this.descripcionServicio = "[ACCESO] Inicio de sesión: Usuario " + this.usuario.getUserid() + " Empresa Seleccionada " + this.empresa.getRfc() + ".";
        registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), empresa.getIdEmpresa(), descripcionServicio, "INFO");
        logger.info("LOGIN Inicio de sesión: Usuario " + this.usuario.getUserid() + " Empresa Seleccionada " + this.empresa.getRfc() + ".");
        System.out.println("termina seleccion de empresa");
        return "/principal?faces-redirect=true";
    }

    public void restablecerContrasenia() throws Exception {
        String newPass;
        String newPassKey;
        Servicios servSMTP;
        ConfiguracionesServicios configEmail;
        ConfiguracionesServicios configUser;
        ConfiguracionesServicios configPass;
        ConfiguracionesServicios configHost;
        ConfiguracionesServicios configPort;
        ConfiguracionesServicios configSSL;
        boolean respuestaEmail = false;
        int diasExp = Integer.parseInt(this.expPass.getValor());
        Date test = new Date();
        Date dateExp = DateTime.sumarRestarDiasFecha(test, diasExp);
        int nPass = Integer.parseInt(this.LONGITUD_MIN_CONT.getValor());
        try {

            if (this.user == null || this.user.trim().isEmpty()) {
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor introdusca usuario valido para restablecer contraseña.");
                FacesContext.getCurrentInstance().addMessage(null, this.msg);
//                return;
            } else {
                this.usuarioContraseña = new DaoUsuario().getByUserid(this.user);

                if (this.usuarioContraseña == null) {
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor introdusca usuario valido para restablecer contraseña.");
                    logger.warn("Usuario " + user + " no existe en la base de datos");
                } else {
                    servSMTP = new DaoServicio().getServicoByNombre("SERVIDOR_SMTP");
                    DaoConfiguracionServicios daoConfigServicio = new DaoConfiguracionServicios();
                    configEmail = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), "EMAIL_SMTP");
                    configUser = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), "USER_SMTP");
                    configPass = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), "PASS_SMTP");
                    configHost = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), "HOST_SMTP");
                    configPort = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), "PORT_SMTP");
                    configSSL = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), "SSL_SMTP");
                    if (configEmail.getValor().trim().isEmpty() || configHost.getValor().trim().isEmpty() || configPort.getValor().trim().isEmpty()) {
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por el momento no es posible restablecer la contraseña, no existe un servidor SMTP configurado. Consulte a su administrador.");
                        logger.error("Por el momento no es posible restablecer la contraseña, no existe un servidor SMTP configurado.");
                    } else {
                        newPass = Encrypt.getContraseniaAleatoria(nPass);
                        logger.info("Se ha generado una nueva contraseña para el usuario: " + newPass);
                        newPassKey = Encrypt.getSHA512(newPass + this.usuarioContraseña.getSalt());
                        logger.info("La nueva contraseña ha sido encriptada " + newPassKey);

                        this.usuarioContraseña.setPasskey(newPassKey);
                        this.usuarioContraseña.setLastAction(getTimestamp());
                        this.usuarioContraseña.setDateExpirationPass(dateExp);
                        boolean update = new DaoUsuario().updateUsuario(this.usuarioContraseña);
                        if (update) {
                            logger.info("Usuario " + usuarioContraseña.getUserid() + " actualizado en base de datos.");
                            logger.info("Intentando enviar correo electrónico con la nueva contraseña");
                            String asunto = "[Validación] Restablecer contraseña";
                            String contenido = "Usuario: " + usuarioContraseña.getUserid() + "\nContraseña: " + newPass;
                            boolean ssl = Boolean.valueOf(configSSL.getValor());
                            boolean auth;
                            if (!configUser.getValor().trim().isEmpty() || !configPass.getValor().trim().isEmpty()) {
                                auth = true;
                                String pw = decrypt(configPass.getValor());
                                ConexionSMTP conSMTP = new ConexionSMTP(configHost.getValor(), configPort.getValor(), ssl, auth);
                                conSMTP.setUsername(configUser.getValor());
                                conSMTP.setPassword(pw);
                                conSMTP.getSession();
                                conSMTP.connect();
                                conSMTP.createMessage(usuarioContraseña.getEmail(), asunto, contenido);
                                String conn;
                                conn = conSMTP.sendMessage();
                                if (conn.trim().startsWith("2")) {
                                    respuestaEmail = true;
                                    logger.info("Correo electrónico enviado.");
                                    this.descripcionServicio = "[ACCESO] Usuario: " + usuarioContraseña.getUserid() + " la contraseña ha sido restablecida y enviada al correo electrónico.";
                                    insertBitacora(usuarioContraseña.getIdUsuario(), servicio.getIdServicio(), descripcionServicio, "INFO");
                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La contraseña se ha enviado al correo electrónico.");
                                    logger.info("Usuario: " + usuarioContraseña.getUserid() + " la contraseña ha sido restablecida y enviada al correo electrónico.");
                                } else {
                                    this.descripcionServicio = "[ACCESO] Usuario: " + usuarioContraseña.getUserid() + " Error al envair correo con la nueva contraseña.";
                                    insertBitacora(usuarioContraseña.getIdUsuario(), servicio.getIdServicio(), descripcionServicio, "ERROR");
                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo restablecer la contraseña, Por favor intente de nuevo.");
                                    FacesContext.getCurrentInstance().addMessage(null, this.msg);
                                    logger.error("Error al enviar el correo electrónico con la nueva contraseña");
                                }
                            } else {
                                auth = false;
                                ConexionSMTP conSMTP = new ConexionSMTP(configHost.getValor(), configPort.getValor(), ssl, auth);
                                conSMTP.setUsername(configEmail.getValor());
                                conSMTP.getSession();
                                conSMTP.connect();
                                conSMTP.createMessage(usuarioContraseña.getEmail(), asunto, contenido);
                                String conn;
                                conn = conSMTP.sendMessage();
                                if (conn.trim().startsWith("2")) {
                                    respuestaEmail = true;
                                    logger.info("Correo electrónico enviado.");
                                    this.descripcionServicio = "[ACCESO] Usuario: " + usuarioContraseña.getUserid() + " la contraseña ha sido restablecida y enviada al correo electrónico.";
                                    insertBitacora(usuarioContraseña.getIdUsuario(), servicio.getIdServicio(), descripcionServicio, "INFO");
                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La contraseña se ha enviado al correo electrónico.");
                                    logger.info("Usuario: " + usuarioContraseña.getUserid() + " la contraseña ha sido restablecida y enviada al correo electrónico.");
                                } else {
                                    this.descripcionServicio = "[ACCESO] Usuario: " + usuarioContraseña.getUserid() + " Error al envair correo con la nueva contraseña.";
                                    insertBitacora(usuarioContraseña.getIdUsuario(), servicio.getIdServicio(), descripcionServicio, "ERROR");
                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo restablecer la contraseña, Por favor intente de nuevo.");
                                    FacesContext.getCurrentInstance().addMessage(null, this.msg);
                                    logger.error("Error al enviar el correo electrónico con la nueva contraseña");
                                }
                            }
                        } else {
                            this.descripcionServicio = "[ACCESO] Usuario: " + usuarioContraseña.getUserid() + " Error al modificar usuario en base de datos.";
                            insertBitacora(usuarioContraseña.getIdUsuario(), servicio.getIdServicio(), descripcionServicio, "ERROR");
                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo restablecer la contraseña, Por favor intente de nuevo.");
                            FacesContext.getCurrentInstance().addMessage(null, this.msg);
                            logger.error("Error al modificar usuario en base de datos");
//                            return;
                        }
                    }
                }
            }

        } catch (NoSuchAlgorithmException ex) {
            descripcionServicio = "[ACCESO] RESTABLECER_CONTRASENIA NoSuchAlgorithmException ERROR: " + ex;
            insertBitacora(usuarioContraseña.getIdUsuario(), servicio.getIdServicio(), descripcionServicio, "ERROR");
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo restablecer la contraseña, Por favor intente de nuevo.");
            FacesContext.getCurrentInstance().addMessage(null, this.msg);
            logger.error("RESTABLECER_CONTRASENIA NoSuchAlgorithmException ERROR: " + ex);
//            return;
        } catch (Exception e) {
            descripcionServicio = "[ACCESO] RESTABLECER_CONTRASENIA Exception ERROR: " + e;
            insertBitacora(usuarioContraseña.getIdUsuario(), servicio.getIdServicio(), descripcionServicio, "ERROR");
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo restablecer la contraseña, Por favor intente de nuevo.");
            FacesContext.getCurrentInstance().addMessage(null, this.msg);
            logger.error("RESTABLECER_CONTRASENIA Exception ERROR: " + e);
//            return;
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
        int diasExp = Integer.parseInt(this.expPass.getValor());
        Date test = new Date();
        Date dateExp = DateTime.sumarRestarDiasFecha(test, diasExp);
        logger.info("NUEVA_CONTRASEÑA");

        try {

            if (this.pass == null || this.pass.trim().isEmpty()) {
                logger.warn("Introdusca un contraseña valida");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Introdusca una contraseña valida.");
            } else {
                if (this.pass.length() >= nPass) {
                    logger.info("Contraseña longitud correcta");
                    if (this.pass.equals(this.confirmarPass)) {
                        this.usuario = new DaoUsuario().getByUserid(this.user);
                        logger.info("Se obtuvo el usuario");

                        this.usuario.setSalt(Encrypt.getSALT(20));
                        this.usuario.setPasskey(Encrypt.getSHA512(this.pass + this.usuario.getSalt()));
                        this.usuario.setEstado("ACTIVO");
                        this.usuario.setIntentosFallidos(0);
                        this.usuario.setDateExpirationPass(dateExp);
                        this.usuario.setLastAction(getTimestamp());
                        update = new DaoUsuario().updateUsuario(this.usuario);
                        logger.info("Modificaron los datos del Usuario");

                        if (update) {
                            this.descripcionServicio = "[ACCESO] Usuario: " + this.usuario.getUserid() + " ha modificado la contraseña, el estado es " + this.usuario.getEstado();
                            insertBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), descripcionServicio, "INFO");
                            this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La contraseña se ha modificado.");
                        }
                    } else {
                        logger.warn("las contraseñas no son iguales.");
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Las contraseñas no coinciden.");
//                        FacesContext.getCurrentInstance().addMessage(null, this.msg);
                    }

                } else {
                    logger.warn("La contraseña debe contener al menos " + nPass + " caracteres.");
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La contraseña debe contener al menos " + nPass + " caracteres.");
//                    FacesContext.getCurrentInstance().addMessage(null, this.msg);
                }
            }

        } catch (NoSuchAlgorithmException ex) {
            descripcionServicio = "[ACCESO] NUEVA_CONTRASEÑA Encrypt ERROR: " + ex;
            errorBitacora(servicio.getIdServicio(), descripcionServicio, "ERROR");
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo guardar la nueva contraseña, Por favor intente de nuevo.");
            FacesContext.getCurrentInstance().addMessage(null, this.msg);
            logger.error("NUEVA_CONTRASEÑA Encrypt ERROR: " + ex);
        } catch (Exception e) {
            descripcionServicio = "[ACCESO] NUEVA_CONTRASEÑA ERROR: " + e;
            errorBitacora(servicio.getIdServicio(), descripcionServicio, "ERROR");
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo guardar la nueva contraseña, Por favor intente de nuevo.");
            FacesContext.getCurrentInstance().addMessage(null, this.msg);
            logger.error("NUEVA_CONTRASEÑA ERROR: " + e);
        }
        this.pass = "";
        this.confirmarPass = "";
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("actualiza", update);
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
    }

    public void modContasenia() {
        boolean update = false;
        int nPass = Integer.parseInt(this.LONGITUD_MIN_CONT.getValor());
        try {
            if (chkBoolean) {
                if (this.usuario.getPasskey().equals(Encrypt.getSHA512(this.pass + this.usuario.getSalt()))) {
                    if (this.newPass.length() >= nPass) {
                        if (this.newPass.equals(this.confirmarPass)) {
                            DaoUsuario daoUsuario = new DaoUsuario();
                            this.usuario.setSalt(Encrypt.getSALT(20));
                            this.usuario.setPasskey(Encrypt.getSHA512(this.newPass + this.usuario.getSalt()));
                            this.usuario.setLastAction(getTimestamp());
                            update = daoUsuario.updateUsuario(this.usuario);
                            if (update) {
                                descripcionServicio = "[ACCESO] Usuario: " + usuario.getUserid() + " ha modificado la contraseña.";
                                insertBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), descripcionServicio, "INFO");
                                this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "los datos se han modificado.");
                                logger.info("MOD_CONTRASENIA se ha modificado la contraseña");
                            } else {
                                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al modificar los datos.");
                                logger.error("MOD_CONTRASENIA Error al modificar la contraseña");
                            }
                        } else {
                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Las contraseñas no coinciden.");
//                            logger.warn("Las contraseñas no coinciden");
                        }

                    } else {
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La contraseña debe contener al menos " + nPass + " caracteres.");
//                        logger.warn("Las conyraseñas no coinciden");
                    }
                } else {
//                    logger.warn("Introdusca su contraseña actual");
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, introdusca su contraseña actual.");
                }
            } else {
                update = new DaoUsuario().updateUsuario(this.usuario);
                if (update) {
                    descripcionServicio = "[ACCESO] Usuario: " + usuario.getUserid() + " realizo cambios en los apellidos.";
                    insertBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), descripcionServicio, "INFO");
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "los datos se han modificado.");
                    logger.info("MOD_CONTRASENIA los datos se han modificado");
                } else {
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al modificar los datos.");
                    logger.error("MOD_CONTRASENIA Error al modificar los datos");
                }
            }

        } catch (NoSuchAlgorithmException ex) {
            descripcionServicio = "[ACCESO] MOD_CONTRASENIA Encrypt Error: " + ex;
            errorBitacora(servicio.getIdServicio(), descripcionServicio, "ERROR");
            logger.error("MOD_CONTRASENIA Error: " + ex);
        } catch (Exception e) {
            descripcionServicio = "[ACCESO] MOD_CONTRASENIA Error: " + e;
            errorBitacora(servicio.getIdServicio(), descripcionServicio, "ERROR");
            logger.error("MOD_CONTRASENIA Error: " + e);
        }
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
    }

    public String irCDFIs() {
        FacesContext faceContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        logger.info("MbAceso Empresa Seleccionada enviada a CFDIs: " + this.empresaSeleccionada);
        httpServletRequest.getSession().setAttribute("empresaSeleccionada", this.empresaSeleccionada);
        return "/CFDI/recibidos?faces-redirect=true";
    }

    public String irConfiguracionEmpresa() {
        FacesContext faceContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        logger.info("MbAceso Empresa Seleccionada enviada a ConfiguracionEmpresa: " + this.empresaSeleccionada);
        httpServletRequest.getSession().setAttribute("empresaSeleccionada", this.empresaSeleccionada);
        return "/Configuracion/empresa?faces-redirect=true";
    }

    public String irConfiguracionNotificaciones() {
        FacesContext faceContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        logger.info("MbAceso Empresa Seleccionada enviada a ConfiguracionNotificaciones: " + this.empresaSeleccionada);
        httpServletRequest.getSession().setAttribute("empresaSeleccionada", this.empresaSeleccionada);
        return "/Configuracion/notificaciones?faces-redirect=true";
    }

    public String irConfiguracionPlantilla() {
        FacesContext faceContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        logger.info("MbAceso Empresa Seleccionada enviada a ConfiguracionPlantilla: " + this.empresaSeleccionada);
        httpServletRequest.getSession().setAttribute("empresaSeleccionada", this.empresaSeleccionada);
        return "/Configuracion/plantilla?faces-redirect=true";
    }

    public String irConfiguracionEmail() {
        FacesContext faceContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        logger.info("MbAceso Empresa Seleccionada enviada a ConfiguracionEmail: " + this.empresaSeleccionada);
        httpServletRequest.getSession().setAttribute("empresaSeleccionada", this.empresaSeleccionada);
        return "/Configuracion/email?faces-redirect=true";
    }

    public String irSociosComerciales() {
        FacesContext faceContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        logger.info("MbAceso Empresa Seleccionada enviada a SociosComerciales: " + this.empresaSeleccionada);
        httpServletRequest.getSession().setAttribute("empresaSeleccionada", this.empresaSeleccionada);
        return "/Socios/sociosComerciales?faces-redirect=true";
    }

    public String irModificarPass() {
        return "/Usuario/modificarPass?faces-redirect=true";
    }

    public boolean rolEmpresa() {
        boolean esEmpresa = false;
        Roles rol;
        DaoRoles daoRol = new DaoRoles();
        try {
            rol = daoRol.getRolById(usuario.getIdRol());
            if (rol.getTipo().equals("EMPRESA")) {
                esEmpresa = true;
                cfdisRecibidos();
                cfdisValidos();
                cfdisDuplicados();
                cfdisError();
                cfdisSinValidar();
            }
        } catch (Exception ex) {
            logger.error("rolEmpresa - ERROR " + ex);
        }

        return esEmpresa;
    }

    public boolean rolServidor() {
        boolean esServidor = false;
        Roles rol;
        DaoRoles daoRol = new DaoRoles();
        try {
            rol = daoRol.getRolById(usuario.getIdRol());
            if (rol.getTipo().equals("SERVIDOR")) {
                esServidor = true;
            }
        } catch (Exception ex) {
            logger.error("rolServidor - ERROR " + ex);
        }
        return esServidor;
    }

    public boolean administarCFDIs() {
        if (opciones == null && opciones.size() <= 0) {
            logger.warn("opciones null");
        } else {
            for (Opciones opcion : opciones) {
                if (opcion.getOpcion().equals("Validar_Eliminar_CFDI")) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean modificarEmpresa() {
        if (opciones == null && opciones.size() <= 0) {
            logger.warn("opciones null");
        } else {
            for (Opciones opcion : opciones) {
                if (opcion.getOpcion().equals("Modificar_Empresa")) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean configNotificaciones() {
        if (opciones == null && opciones.size() <= 0) {
            logger.warn("opciones null");
        } else {
            for (Opciones opcion : opciones) {
                if (opcion.getOpcion().equals("Configurar_Notificaciones")) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean configPlantillas() {
        if (opciones == null && opciones.size() <= 0) {
            logger.warn("opciones null");
        } else {
            for (Opciones opcion : opciones) {
                if (opcion.getOpcion().equals("Configurar_Plantillas")) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean configSMTP() {
        if (opciones == null && opciones.size() <= 0) {
            logger.warn("opciones null");
        } else {
            for (Opciones opcion : opciones) {
                if (opcion.getOpcion().equals("Configurar_SMTP")) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean configPOP() {
        if (opciones == null && opciones.size() <= 0) {
            logger.warn("opciones null");
        } else {
            for (Opciones opcion : opciones) {
                if (opcion.getOpcion().equals("Configurar_POP3/IMAP")) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean administrarUsuarios() {
        if (opciones == null && opciones.size() <= 0) {
            logger.warn("opciones null");
        } else {
            for (Opciones opcion : opciones) {
                if (opcion.getOpcion().equals("Administrar_Usuarios")) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean configServicios() {
        if (opciones == null && opciones.size() <= 0) {
            logger.warn("opciones null");
        } else {
            for (Opciones opcion : opciones) {
                if (opcion.getOpcion().equals("Configurar_Servicios")) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean administrarEmpresa() {
        if (opciones == null && opciones.size() <= 0) {
            logger.warn("opciones null");
        } else {
            for (Opciones opcion : opciones) {
                if (opcion.getOpcion().equals("Administrar_Empresas")) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean administrarSocios() {
        if (opciones == null && opciones.size() <= 0) {
            logger.warn("opciones null");
        } else {
            for (Opciones opcion : opciones) {
                if (opcion.getOpcion().equals("Administrar_SociosComerciales")) {
                    return true;
                }
            }
        }
        return false;
    }

    public void cfdisValidos() {
        List<CfdisRecibidos> listaCfdis;

        try {

            DaoCfdisRecibidos daoCfdis = new DaoCfdisRecibidos();
            listaCfdis = daoCfdis.listaCfdisByIdEmpresaEstado(empresa.getIdEmpresa(), "VALIDO");
            cfdisValidos = listaCfdis.size();
        } catch (Exception e) {
            logger.error("estadoCfdis - ERROR " + e);
        }
    }

    public void cfdisDuplicados() {
        List<CfdisRecibidos> listaCfdis;

        try {

            DaoCfdisRecibidos daoCfdis = new DaoCfdisRecibidos();
            listaCfdis = daoCfdis.listaCfdisByIdEmpresaEstado(empresa.getIdEmpresa(), "DUPLICADOS");
            cfdisDuplicados = listaCfdis.size();
        } catch (Exception e) {
            logger.error("cfdisDuplicados - ERROR " + e);
        }
    }

    public void cfdisError() {
        List<CfdisRecibidos> listaCfdis;

        try {
            DaoCfdisRecibidos daoCfdis = new DaoCfdisRecibidos();
            listaCfdis = daoCfdis.listaCfdisErrorByIdEmpresa(empresa.getIdEmpresa());
            cfdisInvalidos = listaCfdis.size();
        } catch (Exception e) {
            logger.error("estadoCfdis - ERROR " + e);
        }
    }

    public void cfdisRecibidos() {
        List<CfdisRecibidos> listaCfdis;

        try {
            DaoCfdisRecibidos daoCfdis = new DaoCfdisRecibidos();
            listaCfdis = daoCfdis.getCfdisByidEmpresa(empresa.getIdEmpresa());
            cfdisTotal = listaCfdis.size();
        } catch (Exception e) {
            logger.error("estadoCfdis - ERROR " + e);
        }
    }

    public void cfdisSinValidar() {
        List<CfdisRecibidos> listaCfdis;
        List<CfdisRecibidos> listaCfdis1;

        try {

            DaoCfdisRecibidos daoCfdis = new DaoCfdisRecibidos();
            listaCfdis = daoCfdis.listaCfdisByIdEmpresaEstado(empresa.getIdEmpresa(), "NUEVO");
            listaCfdis1 = daoCfdis.listaCfdisByIdEmpresaEstado(empresa.getIdEmpresa(), "SIN_VALIDAR");
            cfdisSinValidar = listaCfdis.size() + listaCfdis1.size();
        } catch (Exception e) {
            logger.error("cfdisSinValidar - ERROR " + e);
        }
    }

//    private void statusCFDIs(){
//        List<CfdisRecibidos> listaCfdis;
//        try {
//            DaoCfdisRecibidos daoCfdis = new DaoCfdisRecibidos();
//            empresa = new DaoEmpresa().getEmpresaByRFC(empresaSeleccionada);
//            listaCfdis = daoCfdis.getCfdisByidEmpresa(empresa.getIdEmpresa());
//            for (CfdisRecibidos cfdisRecibidos : listaCfdis) {
//                cfdisTotal++;
//                System.out.println("Entro "+cfdisTotal);
//                if (cfdisRecibidos.getEstado().equals("VALIDO")) {
//                    cfdisValidos++;
//                } else {
//                    if (cfdisRecibidos.getEstado().equals("DUPLICADO")) {
//                        cfdisDuplicados++;
//                    } else {
//                        if (cfdisRecibidos.getEstado().equals("CORRUPTO") || cfdisRecibidos.getEstado().equals("SELLO_INVALIDO") || cfdisRecibidos.getEstado().equals("RECEPTOR_INVALIDO") || cfdisRecibidos.getEstado().equals("SERIE_FOLIO_INVALIDO") || cfdisRecibidos.getEstado().equals("XML_INVALIDO") || cfdisRecibidos.getEstado().equals("CERTIFICADO_INVALIDO")) {
//                            cfdisInvalidos++;
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            logger.error("statusCFDIs - ERROR " + e);
//        }
//        System.out.println("Recibidos: "+cfdisTotal);
//        System.out.println("Validos: "+cfdisValidos);
//        System.out.println("Invalidos: "+cfdisInvalidos);
//        System.out.println("Duplicados: "+cfdisDuplicados);
//    }
    public boolean existeServSMTP() {
        boolean mostrar = false;
        Servicios serv;
        List<ConfiguracionesServicios> listaConfig = new ArrayList<ConfiguracionesServicios>();
        try {
            serv = new DaoServicio().getServicoByNombre("SERVIDOR_SMTP");
            listaConfig = new DaoConfiguracionServicios().listaServiciosByIdServicio(serv.getIdServicio());
            for (ConfiguracionesServicios config : listaConfig) {
                if (config.getPropiedad().equals("HOST_SMTP") && config.getValor().trim().isEmpty()) {
                    mostrar = true;
                    break;
                }
                if (config.getPropiedad().equals("PORT_SMTP") && config.getValor().trim().isEmpty()) {
                    mostrar = true;
                    break;
                }
                if (config.getPropiedad().equals("EMAIL_SMTP") && config.getValor().trim().isEmpty()) {
                    mostrar = true;
                    break;
                }
            }

        } catch (Exception e) {
            logger.error("existeServSMTP - ERROR " + e);
        }
        return mostrar;
    }

    public String idServicoSMTP() {
        return "/Configuracion/servicioSMTP?faces-redirect=true";
    }

}

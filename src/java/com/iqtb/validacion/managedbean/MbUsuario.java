package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoConfiguracionServicios;
import com.iqtb.validacion.dao.DaoEmpresa;
import com.iqtb.validacion.dao.DaoRoles;
import com.iqtb.validacion.dao.DaoServicio;
import com.iqtb.validacion.dao.DaoUsuario;
import com.iqtb.validacion.dao.DaoUsuarioEmpresa;
import com.iqtb.validacion.encrypt.Encrypt;
import com.iqtb.validacion.pojo.ConfiguracionesServicios;
import com.iqtb.validacion.pojo.Empresas;
import com.iqtb.validacion.pojo.Roles;
import com.iqtb.validacion.pojo.Servicios;
import com.iqtb.validacion.pojo.Usuarios;
import com.iqtb.validacion.pojo.UsuariosHasEmpresas;
import com.iqtb.validacion.pojo.UsuariosHasEmpresasId;
import static com.iqtb.validacion.util.Bitacoras.insertBitacora;
import com.iqtb.validacion.util.DateTime;
import static com.iqtb.validacion.util.DateTime.getTimestamp;
import com.iqtb.validacion.util.ImprimirUsuario;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;

/**
 *
 * @author danielromero
 */
@ManagedBean
@ViewScoped
public class MbUsuario implements Serializable {

    private Usuarios usuario;
    private Usuarios user;
    private List<Usuarios> listaUsuarios;
    private List<ImprimirUsuario> usuariosSeleccionados;
    private Roles rol;
    private List<SelectItem> selectOneItemsRol;
    private List<Empresas> empresasSeleccionadas;
    private List<Empresas> empresas;
    private List<String> strEmpresas;
    private List<String> strEmpresasSeleccionadas;
    private DualListModel<String> dualListEmpresas;
    private boolean chkPass;
    private String pass;
    private String newPass;
    private String confirmarPass;
    private Servicios servicio;
    private ConfiguracionesServicios configServicio;
    private ConfiguracionesServicios expPass;
    private List<Roles> listaRoles;
    private List<Roles> rolesSeleccionados;
    private boolean rolEmpresa;
    private final String sessionUsuario;
    private FacesMessage msg;
    private static final Logger logger = Logger.getLogger(MbUsuario.class);

    public MbUsuario() {
        this.usuario = new Usuarios();
        this.usuariosSeleccionados = new ArrayList<ImprimirUsuario>();
        this.rol = new Roles();
        this.rolesSeleccionados = new ArrayList<Roles>();
        this.empresas = new ArrayList<Empresas>();
        this.empresasSeleccionadas = new ArrayList<Empresas>();

        this.sessionUsuario = ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid();
        configServicios();

    }

    @PostConstruct
    public void init() {
        try {
            empresas = new DaoEmpresa().getEmpresas();
            strEmpresas = new ArrayList<String>();
            for (Empresas var : empresas) {
                strEmpresas.add(var.getRfc());
            }

            strEmpresasSeleccionadas = new ArrayList<String>();
            dualListEmpresas = new DualListModel<String>(strEmpresas, strEmpresasSeleccionadas);

        } catch (Exception ex) {
            logger.error("init - ERROR obteniendo lista de empresas " + ex);
        }
    }

    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }

    public List<ImprimirUsuario> getListaUsuarios() {
        List<ImprimirUsuario> listaUser = new ArrayList<ImprimirUsuario>();
        try {
            this.listaUsuarios = new DaoUsuario().getAllUsuarios();
        } catch (Exception e) {
            logger.error("getListaUsuarios - ERROR: " + e);
        }
        if (listaUsuarios != null && listaUsuarios.size() > 0) {
            for (Usuarios usuarios : listaUsuarios) {
                ImprimirUsuario iUsuario = new ImprimirUsuario();
                iUsuario.setIdUsuario(usuarios.getIdUsuario());
                iUsuario.setIdRol(usuarios.getIdRol());
                iUsuario.setEmail(usuarios.getEmail());
                iUsuario.setNombre(usuarios.getNombre());
                iUsuario.setApaterno(usuarios.getApaterno());
                iUsuario.setAmaterno(usuarios.getAmaterno());
                iUsuario.setPasskey(usuarios.getPasskey());
                iUsuario.setSalt(usuarios.getSalt());
                iUsuario.setDateExpirationPass(usuarios.getDateExpirationPass());
                iUsuario.setLastAction(usuarios.getLastAction());
                iUsuario.setEstado(usuarios.getEstado());
                iUsuario.setUserid(usuarios.getUserid());
                iUsuario.setFechaAlta(usuarios.getFechaAlta());
                iUsuario.setIntentosFallidos(usuarios.getIntentosFallidos());
                iUsuario.setTipoRol(tipoRolByid(usuarios.getIdRol()));
                listaUser.add(iUsuario);
            }
        }
        return listaUser;
    }

    public void setListaUsuarios(List<Usuarios> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    public List<ImprimirUsuario> getUsuariosSeleccionados() {
        return usuariosSeleccionados;
    }

    public void setUsuariosSeleccionados(List<ImprimirUsuario> usuariosSeleccionados) {
        this.usuariosSeleccionados = usuariosSeleccionados;
    }

    public List<SelectItem> getSelectOneItemsRol() throws Exception {
        this.selectOneItemsRol = new ArrayList<SelectItem>();
        DaoRoles daoRoles = new DaoRoles();
        List<Roles> roles = daoRoles.getAllRoles();
        for (Roles rol : roles) {
            SelectItem selectItem = new SelectItem(rol.getIdRol(), rol.getNombre());
            this.selectOneItemsRol.add(selectItem);
        }
        return selectOneItemsRol;
    }

    public boolean isChkPass() {
        return chkPass;
    }

    public void setChkPass(boolean chkPass) {
        this.chkPass = chkPass;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getNewPass() {
        return newPass;
    }

    public void setNewPass(String newPass) {
        this.newPass = newPass;
    }

    public String getConfirmarPass() {
        return confirmarPass;
    }

    public void setConfirmarPass(String confirmarPass) {
        this.confirmarPass = confirmarPass;
    }

    public List<Empresas> getEmpresas() {
        return empresas;
    }

    public List<Empresas> getEmpresasSeleccionadas() {
        return empresasSeleccionadas;
    }

    public void setEmpresasSeleccionadas(List<Empresas> empresasSeleccionadas) {
        this.empresasSeleccionadas = empresasSeleccionadas;
    }

    public boolean isRolEmpresa() {
        rolEmpresa = false;
        DaoRoles daoRol = new DaoRoles();
        Roles re;
        try {
            re = daoRol.getRolById(this.usuario.getIdRol());
            if (re.getTipo().equals("EMPRESA")) {
                this.rolEmpresa = true;
            }
        } catch (Exception ex) {
            logger.error("isRolEmpresa - ERROR " + ex);
        }
        return rolEmpresa;
    }

    public void setRolEmpresa(boolean rolEmpresa) {
        this.rolEmpresa = rolEmpresa;
    }

    public String tipoRolByid(int idRol) {
        try {
            DaoRoles daoRoles = new DaoRoles();
            this.rol = daoRoles.getRolById(idRol);
        } catch (Exception e) {
            logger.error("tipoRolByid - ERROR: " + e);
        }
        return this.rol.getTipo();
    }

    public void limpiar() {
        this.usuario = new Usuarios();
        try {
            empresas = new DaoEmpresa().getEmpresas();
            strEmpresas = new ArrayList<String>();
            for (Empresas var : empresas) {
                strEmpresas.add(var.getRfc());
            }

            strEmpresasSeleccionadas = new ArrayList<String>();
            dualListEmpresas = new DualListModel<String>(strEmpresas, strEmpresasSeleccionadas);

        } catch (Exception ex) {
            logger.error("limpiar - ERROR obteniendo lista de empresas " + ex);
        }
    }

    public List<String> getStrEmpresas() {
        return strEmpresas;
    }

    public void setStrEmpresas(List<String> strEmpresas) {
        this.strEmpresas = strEmpresas;
    }

    public DualListModel<String> getDualListEmpresas() {
        return dualListEmpresas;
    }

    public void setDualListEmpresas(DualListModel<String> dualListEmpresas) {
        this.dualListEmpresas = dualListEmpresas;
    }

    public List<Roles> getListaRoles() {
        try {
            this.listaRoles = new DaoRoles().getAllRoles();
        } catch (Exception e) {
            logger.error("getListaRoles - ERROR: " + e);
        }
        return listaRoles;
    }

    public void setListaRoles(List<Roles> listaRoles) {
        this.listaRoles = listaRoles;
    }

    public List<Roles> getRolesSeleccionados() {
        return rolesSeleccionados;
    }

    public void setRolesSeleccionados(List<Roles> rolesSeleccionados) {
        this.rolesSeleccionados = rolesSeleccionados;
    }

    public void updateUsuario() {
        boolean updateUsuario = false;
        int nPass;
        int diasExp = Integer.parseInt(this.expPass.getValor());
        Date test = new Date();
        Date dateExp = DateTime.sumarRestarDiasFecha(test, diasExp);
        empresasSeleccionadas = new ArrayList<Empresas>();
        if (dualListEmpresas.getTarget().size() <= 0) {
            logger.warn("updateUsuario - dualListEmpresasTarget se encuentra vacia");
        } else {
            for (String item : dualListEmpresas.getTarget()) {
                System.out.println("EMPRESA SELECCIONADA " + item);
                for (Empresas emp : empresas) {
                    System.out.println("EMPRESAS " + emp.getRfc());
                    if (item.equals(emp.getRfc())) {
                        empresasSeleccionadas.add(emp);
                        logger.info("updateUsuario - Empresa " + emp.getRfc() + " agredada a empresas selecionadas");
                    }
                }
            }
        }
        try {
            DaoUsuarioEmpresa daoUsuarioEmpresa = new DaoUsuarioEmpresa();
            if (daoUsuarioEmpresa.deleteByIdUsuario(usuario.getIdUsuario())) {
                logger.info("updateUsuario - Se han eliminado todos los accesos a empresas del usuario  " + usuario.getUserid());
            }
            if (this.usuario.getNombre() != null && !this.usuario.getNombre().isEmpty()) {
                if (this.usuario.getApaterno() != null && !this.usuario.getApaterno().isEmpty()) {
                    if (this.usuario.getEmail() != null && !this.usuario.getEmail().isEmpty()) {
                        if (!this.usuario.getEstado().equals("")) {
                            if (this.chkPass) {
                                if (this.pass != null && !this.pass.isEmpty()) {
                                    if (this.newPass != null && !this.newPass.isEmpty()) {
                                        if (this.usuario.getPasskey().equals(Encrypt.getSHA512(this.pass + this.usuario.getSalt()))) {
                                            nPass = Integer.parseInt(this.configServicio.getValor());
                                            if (this.newPass.length() >= nPass) {
                                                System.out.println("newPass mayor a npass");
                                                if (this.newPass.equals(this.confirmarPass)) {
                                                    Roles rol;
                                                    DaoRoles daoRol = new DaoRoles();
                                                    rol = daoRol.getRolById(usuario.getIdRol());
                                                    if (rol.getTipo().equals("EMPRESA")) {
                                                        logger.info("updateUsuario - Tipo de rol " + rol.getTipo());
                                                        if (empresasSeleccionadas.size() <= 0) {
                                                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuarios con rol " + rol.getTipo() + " deben tener al menos una Empresa asignada");
                                                            logger.warn("updateUsuario - Usuarios con rol " + rol.getTipo() + " deben tener al menos una Empresa asignada");
                                                        } else {
                                                            DaoUsuario daoUsuario = new DaoUsuario();
                                                            this.usuario.setSalt(Encrypt.getSALT(20));
                                                            this.usuario.setPasskey(Encrypt.getSHA512(this.newPass + this.usuario.getSalt()));
                                                            this.usuario.setLastAction(getTimestamp());
                                                            this.usuario.setDateExpirationPass(dateExp);
                                                            updateUsuario = daoUsuario.updateUsuario(this.usuario);
                                                            if (updateUsuario) {
                                                                UsuariosHasEmpresasId usuariosEmpresas = new UsuariosHasEmpresasId();
                                                                UsuariosHasEmpresas usuariosHasEmpresas = new UsuariosHasEmpresas();
                                                                for (Empresas emp : empresasSeleccionadas) {
                                                                    usuariosEmpresas.setIdUsuario(usuario.getIdUsuario());
                                                                    usuariosEmpresas.setIdEmpresa(emp.getIdEmpresa());
                                                                    usuariosHasEmpresas.setId(usuariosEmpresas);
                                                                    if (daoUsuarioEmpresa.insertUsuarioEmpresa(usuariosHasEmpresas)) {
                                                                        this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", usuario.getUserid() + " con acceso a la Empresa " + emp.getRfc());
                                                                        FacesContext.getCurrentInstance().addMessage(null, msg);
                                                                        logger.info("updateUsuario - " + usuario.getUserid() + " con acceso a la Empresa " + emp.getRfc());
                                                                    } else {
                                                                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al asignar la empresa " + emp.getRfc() + " con el usuario " + usuario.getUserid());
                                                                        logger.error("updateUsuario - Error al asignar la empresa " + emp.getRfc() + " con el usuario " + usuario.getUserid());
                                                                    }
                                                                }
                                                                String desc = "[USUARIOS] Usuario: " + this.user.getUserid() + " modifico la informacion del usuario " + this.usuario.getUserid() + ".";
                                                                insertBitacora(this.user.getIdUsuario(), servicio.getIdServicio(), desc, "INFO");
                                                                this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se han modificado los datos del Usuario.");
                                                                logger.info("updateUsuario - Se han modificado los datos del Usuario.");
                                                            } else {
                                                                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al modificar los datos.");
                                                                logger.error("updateUsuario - Error al modificar los datos");
                                                            }
                                                        }
                                                    } else {
                                                        logger.info("updateUsuario - Tipo de rol " + rol.getTipo());
                                                        DaoUsuario daoUsuario = new DaoUsuario();
                                                        this.usuario.setSalt(Encrypt.getSALT(20));
                                                        this.usuario.setPasskey(Encrypt.getSHA512(this.newPass + this.usuario.getSalt()));
                                                        this.usuario.setLastAction(getTimestamp());
                                                        this.usuario.setDateExpirationPass(dateExp);
                                                        updateUsuario = daoUsuario.updateUsuario(this.usuario);
                                                        if (updateUsuario) {

                                                            String desc = "[USUARIOS] Usuario: " + this.user.getUserid() + " modifico la informacion del usuario " + this.usuario.getUserid() + ".";
                                                            insertBitacora(this.user.getIdUsuario(), servicio.getIdServicio(), desc, "INFO");
                                                            this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se han modificado los datos del Usuario.");
                                                            logger.info("updateUsuario - Se han modificado los datos del Usuario.");
                                                        } else {
                                                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al modificar los datos del usuario.");
                                                            logger.error("updateUsuario - Error al modificar los datos del usuario");
                                                        }
                                                    }
                                                } else {
                                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Las contraseñas no coinciden.");
                                                    logger.warn("updateUsuario - Las contraseñas no coinciden");
                                                }
                                            } else {
                                                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La contraseña debe contener al menos " + nPass + " caracteres.");
                                                logger.warn("updateUsuario - La contraseña debe contener al menos " + nPass + " caracteres.");
                                            }
                                        } else {
                                            logger.warn("updateUsuario - Introdusca su contraseña actual");
                                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, introdusca su contraseña actual.");
                                        }
                                    } else {
                                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Nueva Contraseña.");
                                        logger.warn("updateUsuario - Error nueva contraseña campo requerido");
                                    }
                                } else {
                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Contraseña Actual.");
                                    logger.warn("updateUsuario - Error contraseña actual campo requerido");
                                }
                            } else {
                                Roles rol;
                                DaoRoles daoRol = new DaoRoles();
                                rol = daoRol.getRolById(usuario.getIdRol());
                                if (rol.getTipo().equals("EMPRESA")) {
                                    logger.info("updateUsuario - Tipo de rol " + rol.getTipo());
                                    if (empresasSeleccionadas.size() <= 0) {
                                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuarios con rol " + rol.getTipo() + " deben tener al menos una Empresa asignada");
                                        logger.warn("updateUsuario - Usuarios con rol " + rol.getTipo() + " deben tener al menos una Empresa asignada");
                                    } else {
                                        DaoUsuario daoUsuario = new DaoUsuario();
                                        updateUsuario = daoUsuario.updateUsuario(this.usuario);
                                        if (updateUsuario) {
                                            UsuariosHasEmpresasId usuariosEmpresas = new UsuariosHasEmpresasId();
                                            UsuariosHasEmpresas usuariosHasEmpresas = new UsuariosHasEmpresas();
                                            for (Empresas emp : empresasSeleccionadas) {
                                                usuariosEmpresas.setIdUsuario(usuario.getIdUsuario());
                                                usuariosEmpresas.setIdEmpresa(emp.getIdEmpresa());
                                                usuariosHasEmpresas.setId(usuariosEmpresas);
                                                if (daoUsuarioEmpresa.insertUsuarioEmpresa(usuariosHasEmpresas)) {
                                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", usuario.getUserid() + " con acceso a la Empresa " + emp.getRfc());
                                                    FacesContext.getCurrentInstance().addMessage(null, msg);
                                                    logger.info("updateUsuario - " + usuario.getUserid() + " con acceso a la Empresa " + emp.getRfc());
                                                } else {
                                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al asignar la empresa " + emp.getRfc() + " con el usuario " + usuario.getUserid());
                                                    logger.error("updateUsuario - Error al asignar la empresa " + emp.getRfc() + " con el usuario " + usuario.getUserid());
                                                }
                                            }
                                            String desc = "[USUARIOS] Usuario: " + this.user.getUserid() + " modifico la informacion del usuario " + this.usuario.getUserid() + ".";
                                            insertBitacora(this.user.getIdUsuario(), servicio.getIdServicio(), desc, "INFO");
                                            this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se han modificado los datos del Usuario.");
                                            logger.info("updateUsuario - Se han modificado los datos del Usuario.");
                                            this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se han modificado los datos del Usuario.");
                                        } else {
                                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al modificar los datos del usuario.");
                                            logger.error("updateUsuario - Error al modificar los datos del usuario");
                                        }
                                    }
                                } else {
                                    logger.info("updateUsuario - Tipo de rol " + rol.getTipo());
                                    DaoUsuario daoUsuario = new DaoUsuario();
                                    updateUsuario = daoUsuario.updateUsuario(this.usuario);
                                    if (updateUsuario) {
                                        String desc = "[USUARIOS] Usuario: " + this.user.getUserid() + " modifico la informacion del usuario " + this.usuario.getUserid() + ".";
                                        insertBitacora(this.user.getIdUsuario(), servicio.getIdServicio(), desc, "INFO");
                                        logger.info("updateUsuario - Se han modificado los datos del Usuario.");
                                        this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se han modificado los datos del Usuario.");
                                    } else {
                                        logger.error("updateUsuario - Error al modificar los datos del usuario");
                                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al modificar los datos del usuario.");
                                    }
                                }
                            }
                        } else {
                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, seleccione un estado.");
                            logger.warn("updateUsuario - Error debe selleccionar un estado");
                        }
                    } else {
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para correo electrónico.");
                        logger.warn("updateUsuario - Error correo electrónico es un campo requerido");
                    }
                } else {
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Apellido Paterno.");
                    logger.warn("updateUsuario - Error Apellido Paterno es un campo requerido");
                }
            } else {
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Nombre.");
                logger.warn("updateUsuario - Error Nombre es un campo requerido");
            }

        } catch (NoSuchAlgorithmException ex) {
            String descripcion = "[USUARIO] updateUsuario - Encrypt ERROR: " + ex;
            insertBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), descripcion, "ERROR");
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al modificar usuario.");
            logger.error("updateUsuario - Encrypt ERROR " + ex);
        } catch (Exception e) {
            String descripcion = "[USUARIO] updateUsuario - ERROR: " + e;
            insertBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), descripcion, "ERROR");
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al modificar usuario.");
            logger.error("updateUsuario - ERROR: " + e);
        }
        this.pass = "";
        this.newPass = "";
        this.confirmarPass = "";
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("updateUsuario", updateUsuario);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void insertUsuario() {
        boolean insertUsuario = false;
        int nPass = Integer.parseInt(this.configServicio.getValor());
        int diasExp = Integer.parseInt(this.expPass.getValor());
        Date test = new Date();
        Date dateExp = DateTime.sumarRestarDiasFecha(test, diasExp);
        DaoUsuario daoUsuario = new DaoUsuario();
        System.out.println("insertUsuario");
        empresasSeleccionadas = new ArrayList<Empresas>();
        if (dualListEmpresas.getTarget().size() <= 0) {
            System.out.println("insertUsuario - daultarget es NULL");
        } else {
            for (String item : dualListEmpresas.getTarget()) {
                System.out.println("EMPRESA SELECCIONADA " + item);
                for (Empresas emp : empresas) {
                    System.out.println("EMPRESAS " + emp.getRfc());
                    if (item.equals(emp.getRfc())) {
                        empresasSeleccionadas.add(emp);
                        logger.info("insertUsuario - Empresa " + emp.getRfc() + " agredada a empresas selecionadas");
                    }
                }
            }
            logger.info("Empresas a almacenar " + dualListEmpresas.getTarget().toString());
            logger.info("No. Empresas seleccionadas " + empresasSeleccionadas.size());
        }

        try {

            if (this.usuario.getNombre() != null && !this.usuario.getNombre().isEmpty()) {
                if (this.usuario.getApaterno() != null && !this.usuario.getApaterno().isEmpty()) {
                    if (this.usuario.getEmail() != null && !this.usuario.getEmail().isEmpty()) {
                        if (daoUsuario.getUsuarioByEmail(this.usuario.getEmail()) == null) {
                            if (this.usuario.getUserid() != null && !this.usuario.getUserid().isEmpty()) {
                                if (daoUsuario.getByUserid(this.usuario.getUserid()) == null) {
                                    if (usuario.getIdRol() != 0) {
                                        if (this.pass != null && !this.pass.trim().isEmpty()) {
                                            if (this.pass.length() >= nPass) {
                                                if (this.pass.equals(this.confirmarPass)) {
                                                    Roles rol;
                                                    DaoRoles daoRol = new DaoRoles();
                                                    rol = daoRol.getRolById(usuario.getIdRol());
                                                    if (rol.getTipo().equals("EMPRESA")) {
                                                        logger.info("insertUsuario - Tipo de rol " + rol.getTipo());
                                                        if (empresasSeleccionadas.size() <= 0) {
                                                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuarios con rol " + rol.getTipo() + " deben tener al menos una Empresa asignada");
                                                            logger.warn("insertUsuario - Usuarios con rol " + rol.getTipo() + " deben tener al menos una Empresa asignada");
//                                                            return;
                                                        } else {
                                                            this.usuario.setEstado("NUEVO");
                                                            this.usuario.setSalt(Encrypt.getSALT(20));
                                                            this.usuario.setPasskey(Encrypt.getSHA512(this.pass + this.usuario.getSalt()));
                                                            this.usuario.setLastAction(getTimestamp());
                                                            this.usuario.setFechaAlta(getTimestamp());
                                                            this.usuario.setDateExpirationPass(dateExp);
                                                            this.usuario.setIntentosFallidos(0);
                                                            insertUsuario = daoUsuario.insertUsuario(this.usuario);
                                                            if (insertUsuario) {
                                                                DaoUsuarioEmpresa daoUsuarioEmpresa = new DaoUsuarioEmpresa();
                                                                UsuariosHasEmpresasId usuariosEmpresas = new UsuariosHasEmpresasId();
                                                                UsuariosHasEmpresas usuariosHasEmpresas = new UsuariosHasEmpresas();
                                                                for (Empresas emp : empresasSeleccionadas) {
                                                                    logger.info("dentro del for IdUsuario " + usuario.getIdUsuario());
                                                                    usuariosEmpresas.setIdUsuario(usuario.getIdUsuario());
                                                                    usuariosEmpresas.setIdEmpresa(emp.getIdEmpresa());
                                                                    usuariosHasEmpresas.setId(usuariosEmpresas);
                                                                    if (daoUsuarioEmpresa.insertUsuarioEmpresa(usuariosHasEmpresas)) {
                                                                        this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", usuario.getUserid() + " con acceso a la Empresa " + emp.getRfc());
                                                                        logger.info(usuario.getUserid() + " con acceso a la Empresa " + emp.getRfc());
                                                                    } else {
                                                                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al asignar la empresa " + emp.getRfc() + " con el usuario " + usuario.getUserid());
                                                                        logger.error("insertUsuario - Error al asignar la empresa " + emp.getRfc() + " con el usuario " + usuario.getUserid());
                                                                    }
                                                                }
                                                            } else {
                                                                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al registrar Usuario.");
                                                                logger.error(" insertUsuario - Error al registrar Usuario");
                                                            }
                                                        }
                                                    } else {
                                                        logger.info("insertUsuario - Tipo de rol " + rol.getTipo());
                                                        this.usuario.setEstado("NUEVO");
                                                        this.usuario.setSalt(Encrypt.getSALT(20));
                                                        this.usuario.setPasskey(Encrypt.getSHA512(this.newPass + this.usuario.getSalt()));
                                                        this.usuario.setLastAction(getTimestamp());
                                                        this.usuario.setFechaAlta(getTimestamp());
                                                        this.usuario.setDateExpirationPass(dateExp);
                                                        this.usuario.setIntentosFallidos(0);
                                                        insertUsuario = daoUsuario.insertUsuario(this.usuario);

                                                        if (insertUsuario) {
                                                            String des = "[USUARIOS] Usuario: " + this.user.getUserid() + " registro un usuario " + this.usuario.getUserid() + ".";
                                                            insertBitacora(this.user.getIdUsuario(), this.servicio.getIdServicio(), des, "INFO");
                                                            this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Usuario registrado.");
                                                            logger.info("insertUsuario - Usuario registrado.");
                                                        }
                                                    }
                                                } else {
                                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Las contraseñas contraseñas no coinciden.");
                                                    logger.warn("insertUsuario - Error Las contraseñas contraseñas no coinciden");
                                                }
                                            } else {
                                                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La contraseña debe contener al menos " + nPass + " caracteres.");
                                                logger.warn("insertUsuario - Error Contraseña debe contener mas caracteres");
                                            }
                                        } else {
                                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Contraseña.");
                                            logger.warn("insertUsuario - Error Contraseña es un campo requerido");
                                        }
                                    } else {
                                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Rol.");
                                        logger.warn("insertUsuario - Por favor, ingrese un valor para Rol");
                                    }
                                } else {
                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Este identificador de usuario ya se ha utilizado.");
                                    logger.warn("insertUsuario - Este identificador de usuario ya se ha utilizado");
                                }
                            } else {
                                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Identificador de Usuario.");
                                logger.warn("insertUsuario - Error Identificador de usuario es un campo requerido");
                            }
                        } else {
                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Este correo electrónico ya se ha utilizado.");
                            logger.warn("insertUsuario - Este correo electrónico ya se ha utilizado");
                        }
                    } else {
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Correo Electrónico.");
                        logger.warn("insertUsuario - Error Email es un campo requerido");
                    }
                } else {
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Apellido Paterno.");
                    logger.warn("insertUsuario - Error Apellido Paterno es un campo requerido");
                }
            } else {
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Nombre.");
                logger.warn("insertUsuario - Error Nombre es un campo requerido");
            }

        } catch (NoSuchAlgorithmException ex) {
            logger.error("insertUsuario - Encrypt ERROR " + ex);
        } catch (Exception e) {
            logger.error("insertUsuario - ERROR insertUsuario");
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("validarUsuario", insertUsuario);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void deleteUsuario() {
        boolean deleteUsuario = false;
        for (ImprimirUsuario impUser : usuariosSeleccionados) {
            Usuarios u = new Usuarios();
            u.setIdUsuario(impUser.getIdUsuario());
            u.setIdRol(impUser.getIdRol());
            u.setEmail(impUser.getEmail());
            u.setNombre(impUser.getNombre());
            u.setApaterno(impUser.getApaterno());
            u.setAmaterno(impUser.getAmaterno());
            u.setPasskey(impUser.getPasskey());
            u.setSalt(impUser.getSalt());
            u.setDateExpirationPass(impUser.getDateExpirationPass());
            u.setLastAction(impUser.getLastAction());
            u.setEstado(impUser.getEstado());
            u.setUserid(impUser.getUserid());
            u.setFechaAlta(impUser.getFechaAlta());
            u.setIntentosFallidos(impUser.getIntentosFallidos());
            this.usuario = u;
            try {
                if (!this.usuario.getEstado().equals("AUTENTICADO")) {
                    deleteUsuario = new DaoUsuario().deleteUsuario(this.usuario);
                    if (deleteUsuario) {
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Corrrecto", "Usuario eliminado.");
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        logger.info("deleteUsuario - Usuario Eliminado");
                    } else {
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar usuario.");
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        logger.error("deleteUsuario - Error al eliminar usuario");
                    }
                } else {
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No es posible eliminar usuarios con sesión activa. Usuario " + this.usuario.getUserid() + " Estado " + this.usuario.getEstado() + ".");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    logger.warn("deleteUsuario - No es posible eliminar usuarios con sesión activa.");
                }
            } catch (Exception e) {
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "ha ocurrido un error al intentar eliminar el usuario " + usuario.getUserid());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                logger.error("deleteUsuario - ERROR" + e);
            }
        }
    }

    public String existeSeleccionUsuario() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();

        String parametro = (String) params.get("nombreParametro");
        System.out.println("parametro: " + parametro);

        boolean estadoUsuario = false;
        if (this.usuariosSeleccionados.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione un usuario.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            if (parametro != null) {
                if (parametro.equals("eliminar")) {
                    estadoUsuario = true;
                }
            } else if (usuariosSeleccionados.size() > 1) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione solo un usuario.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                try {
                    empresas = new DaoEmpresa().getEmpresas();
                    empresasSeleccionadas = new DaoEmpresa().getEmpresaById(usuariosSeleccionados.get(0).getIdUsuario());
                    estadoUsuario = true;
                } catch (Exception ex) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al cargar las empresas asignadas a este usuario.");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    logger.error("existeSeleccionUsuario - Error al cargar las empresas asignadas a este usuario" + ex);
                    estadoUsuario = false;
                }
                strEmpresasSeleccionadas = new ArrayList<String>();
                strEmpresas = new ArrayList<String>();
                for (Empresas var : empresas) {
                    strEmpresas.add(var.getRfc());
                }
                for (Empresas var : empresasSeleccionadas) {
                    strEmpresasSeleccionadas.add(var.getRfc());
                    strEmpresas.remove(var.getRfc());
                }
                empresasSeleccionadas = new ArrayList<Empresas>();
                dualListEmpresas = new DualListModel<String>(strEmpresas, strEmpresasSeleccionadas);
                for (ImprimirUsuario impUser : usuariosSeleccionados) {
                    Usuarios u = new Usuarios();
                    u.setIdUsuario(impUser.getIdUsuario());
                    u.setIdRol(impUser.getIdRol());
                    u.setEmail(impUser.getEmail());
                    u.setNombre(impUser.getNombre());
                    u.setApaterno(impUser.getApaterno());
                    u.setAmaterno(impUser.getAmaterno());
                    u.setPasskey(impUser.getPasskey());
                    u.setSalt(impUser.getSalt());
                    u.setDateExpirationPass(impUser.getDateExpirationPass());
                    u.setLastAction(impUser.getLastAction());
                    u.setEstado(impUser.getEstado());
                    u.setUserid(impUser.getUserid());
                    u.setFechaAlta(impUser.getFechaAlta());
                    u.setIntentosFallidos(impUser.getIntentosFallidos());
                    this.usuario = u;
                    System.out.println("USUARIO " + usuario.getUserid());
                }
            }

        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("estadoUsuario", estadoUsuario);

        return "/Usuario/usuarios?faces-redirect=true";
    }

    public boolean rolEmpresa(int idRol) {
        boolean esEmpresa = false;
        Roles rol;
        DaoRoles daoRol = new DaoRoles();
        try {
            rol = daoRol.getRolById(idRol);
            if (rol.getTipo().equals("EMPRESA")) {
                esEmpresa = true;
            }

        } catch (Exception ex) {
            logger.error("rolEmpresa - ERROR " + ex);
        }
        return esEmpresa;
    }

    private void configServicios() {
        try {
            this.user = new DaoUsuario().getByUserid(this.sessionUsuario);
            this.servicio = new DaoServicio().getServicoByNombre("ADMINISTRACION_ACCESO");
            this.configServicio = new DaoConfiguracionServicios().getConfigServicioByIdServicioPropiedad(this.servicio.getIdServicio(), "LONGITUD_MIN_CONT");
            this.expPass = new DaoConfiguracionServicios().getConfigServicioByIdServicioPropiedad(this.servicio.getIdServicio(), "DIAS_EXPIRAR_CONT");
        } catch (Exception e) {
            logger.error("ERROR configServicios " + e);
        }
    }

}

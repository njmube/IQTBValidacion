/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoEmpresa;
import com.iqtb.validacion.dao.DaoSociosComerciales;
import com.iqtb.validacion.dao.DaoUsuario;
import com.iqtb.validacion.pojo.Empresas;
import com.iqtb.validacion.pojo.SociosComerciales;
import com.iqtb.validacion.pojo.Usuarios;
import static com.iqtb.validacion.util.Bitacoras.insertBitacora;
import static com.iqtb.validacion.util.Bitacoras.registrarBitacora;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

/**
 *
 * @author danielromero
 */
@ManagedBean
@ViewScoped
public class MbSociosComerciales implements Serializable {

    private SociosComerciales socioComercial;
    private List<SociosComerciales> listaSocios;
    private List<SociosComerciales> sociosSeleccionados;
    private String empresaSeleccionada;
    private Empresas empresa;
    private Usuarios usuario;
    private final String sessionUsuario;
    private FacesMessage msg;
    private static final Logger logger = Logger.getLogger(MbSociosComerciales.class);

    public MbSociosComerciales() {
        socioComercial = new SociosComerciales();
        listaSocios = new ArrayList<SociosComerciales>();
        sociosSeleccionados = new ArrayList<SociosComerciales>();
        empresa = new Empresas();
        usuario = new Usuarios();

        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        empresaSeleccionada = (String) httpServletRequest.getSession().getAttribute("empresaSeleccionada");
        this.sessionUsuario = ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid();
        try {
            usuario = new DaoUsuario().getByUserid(sessionUsuario);
            empresa = new DaoEmpresa().getEmpresaByRFC(empresaSeleccionada);
        } catch (Exception e) {
            System.out.println("Error al obtener el USUARIO/EMPRESA ERROR " + e);
        }
    }

//    @PostConstruct
//    public void init() {
//        try {
//            listaSocios = new DaoSociosComerciales().getSociosComercialesByIdEmpresa(empresa.getIdEmpresa());
//        } catch (Exception ex) {
//            logger.error("init - ERROR " + ex);
//        }
//    }

    public void resetSocioComercial() {
        socioComercial = new SociosComerciales();
    }

    public void insertSocioComercial() {
        boolean insert = false;

        try {
            if (socioComercial.getNombre() != null && !socioComercial.getNombre().trim().isEmpty()) {
                if (socioComercial.getRfc() != null && !socioComercial.getRfc().trim().isEmpty()) {
                    if (socioComercial.getCalle() != null && !socioComercial.getCalle().trim().isEmpty()) {
                        DaoSociosComerciales daoSocioComercial = new DaoSociosComerciales();
                        if (daoSocioComercial.getSocioComercialByIdEmpresaRFC(empresa.getIdEmpresa(), socioComercial.getRfc()) == null) {
                            socioComercial.setIdEmpresa(empresa.getIdEmpresa());
                            insert = daoSocioComercial.insertSocioComercial(socioComercial);
                            if (insert) {
                                String descripcion = "[SOCIOCOMERCIAL] Usuario: " + this.usuario.getUserid() + " registro al socio Comercial " + socioComercial.getRfc() + ".";
                                registrarBitacora(this.usuario.getIdUsuario(), 4, this.empresa.getIdEmpresa(), descripcion, "INFO");

                                logger.info("insertSocioComercial - " + this.usuario.getUserid() + " registro al socio comercial " + socioComercial.getRfc());
                                this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Socio Comercial ha sido Registrado.");

                            } else {
                                logger.error("insertSocioComercial - Error al registar los datos del socio comercial " + socioComercial.getRfc());
                                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al registar los datos del socio comercial.");
                            }
                        } else {
                            logger.warn("insertSocioComercial - RFC " + socioComercial.getRfc() + " ya existe registrado para la empresa " + empresa.getRfc());
                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe un socio comercial registrado con este RFC " + socioComercial.getRfc());
                        }
                    } else {
                        logger.warn("insertSocioComercial - Calle de la Empresa es requerido");
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Calle.");
                    }
                } else {
                    logger.warn("insertSocioComercial - RFC de la Empresa es requerido");
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para RFC.");
                }
            } else {
                logger.warn("insertSocioComercial - Nombre de la Empresa es requerido");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Nombre.");
            }
        } catch (Exception e) {
            String descripcion = "[SOCIOCOMERCIAL] insertSocioComercial - ERROR: " + e;
            insertBitacora(usuario.getIdUsuario(), 4, descripcion, "ERROR");
            logger.error("insertSocioComercial - ERROR: " + e);
        }

        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("insert", insert);
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
    }

    public void updateSocioComercial() {
        boolean update = false;

        try {
            if (socioComercial.getNombre() != null && !socioComercial.getNombre().trim().isEmpty()) {
                if (socioComercial.getCalle() != null && !socioComercial.getCalle().trim().isEmpty()) {
                    DaoSociosComerciales daoSocioComercial = new DaoSociosComerciales();
                    socioComercial.setIdEmpresa(empresa.getIdEmpresa());
                    update = daoSocioComercial.updateSocioComercial(socioComercial);
                    if (update) {
                        String descripcion = "[SOCIOCOMERCIAL] Usuario: " + this.usuario.getUserid() + " modifico al socio Comercial " + socioComercial.getRfc() + ".";
                        registrarBitacora(this.usuario.getIdUsuario(), 4, this.empresa.getIdEmpresa(), descripcion, "INFO");

                        logger.info("updateSocioComercial - " + this.usuario.getUserid() + " modifico al socio comercial " + socioComercial.getRfc());
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Socio Comercial ha sido modificado.");

                    } else {
                        logger.error("updateSocioComercial - Error al modificar los datos del socio comercial " + socioComercial.getRfc());
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al modificar los datos del socio comercial.");
                    }
                } else {
                    logger.warn("updateSocioComercial - Calle de la Empresa es requerido");
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Calle.");
                }
            } else {
                logger.warn("updateSocioComercial - Nombre de la Empresa es requerido");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Nombre.");
            }
        } catch (Exception e) {
            String descripcion = "[SOCIOCOMERCIAL] updateSocioComercial - ERROR: " + e;
            insertBitacora(usuario.getIdUsuario(), 4, descripcion, "ERROR");
            logger.error("updateSocioComercial - ERROR: " + e);
        }

        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("update", update);
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
    }

    public void deleteSociosComerciales() {
        boolean delete = false;
        for (SociosComerciales sc : sociosSeleccionados) {
            socioComercial = sc;
            try {
                delete = new DaoSociosComerciales().deleteSocioComercial(socioComercial);
                if (delete) {
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Socio comercial" + socioComercial.getRfc() + " ha sido eliminado");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    logger.info("deleteSociosComerciales - Socio comercial" + socioComercial.getRfc() + " ha sido eliminado");
                } else {
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al intentar eliminar Socio Comercial " + socioComercial.getRfc());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    logger.error("deleteSociosComerciales - Error al intentar eliminar Socio Comercial " + socioComercial.getRfc());
                }
            } catch (Exception e) {
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "ha ocurrido un error al intentar eliminar el usuario " + usuario.getUserid());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                logger.error("deleteSociosComerciales - No se puede eliminar la Empresa " + empresa.getRfc());
            }
        }
    }

    public String existeSeleccionSocio() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();

        String parametro = (String) params.get("nombreParametro");
        System.out.println("parametro: " + parametro);

        boolean estadoSocio = false;
        if (this.sociosSeleccionados.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione un socio.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            if (parametro != null) {
                if (parametro.equals("eliminar")) {
                    estadoSocio = true;
                }
            } else if (sociosSeleccionados.size() > 1) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione solo un socio.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                for (SociosComerciales socio : sociosSeleccionados) {
                    socioComercial = socio;
                }
                estadoSocio = true;
            }

        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("estadoSocio", estadoSocio);

        return "/Socios/sociosComerciales?faces-redirect=true";
    }

    public SociosComerciales getSocioComercial() {
        return socioComercial;
    }

    public void setSocioComercial(SociosComerciales socioComercial) {
        this.socioComercial = socioComercial;
    }

    public List<SociosComerciales> getListaSocios() {
        try {
            listaSocios = new DaoSociosComerciales().getSociosComercialesByIdEmpresa(empresa.getIdEmpresa());
        } catch (Exception ex) {
            logger.error("init - ERROR " + ex);
        }
        return listaSocios;
    }

    public void setListaSocios(List<SociosComerciales> listaSocios) {
        this.listaSocios = listaSocios;
    }

    public List<SociosComerciales> getSociosSeleccionados() {
        return sociosSeleccionados;
    }

    public void setSociosSeleccionados(List<SociosComerciales> sociosSeleccionados) {
        this.sociosSeleccionados = sociosSeleccionados;
    }

}

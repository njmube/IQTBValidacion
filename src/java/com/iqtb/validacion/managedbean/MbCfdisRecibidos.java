/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoCfdisRecibidos;
import com.iqtb.validacion.dao.DaoEmpresa;
import com.iqtb.validacion.pojo.CfdisRecibidos;
import com.iqtb.validacion.pojo.Empresas;
import com.iqtb.validacion.pojo.Usuarios;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.context.RequestContext;

/**
 *
 * @author danielromero
 */
@ManagedBean
@ViewScoped
public class MbCfdisRecibidos implements Serializable {

    private CfdisRecibidos cfdi;
    private List<CfdisRecibidos> cfdisSeleccionados;
    private List<CfdisRecibidos> listaCFDIs;
    private List<CfdisRecibidos> filtroCFDIs;
    private Empresas empresa;
    private FacesContext faceContext;
    private HttpServletRequest httpServletRequest;
    private final String sessionUsuario;
    private String empresaSeleccionada;
    private FacesMessage msg;

    public MbCfdisRecibidos() throws Exception {
        this.cfdi = new CfdisRecibidos();
        this.cfdisSeleccionados = new ArrayList<CfdisRecibidos>();
        this.empresa = new Empresas();
        faceContext = FacesContext.getCurrentInstance();
        httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        this.empresaSeleccionada = (String) httpServletRequest.getSession().getAttribute("empresaSeleccionada");
        this.sessionUsuario = ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid();
        if (httpServletRequest.getSession().getAttribute("empresaSeleccionada") != null) {
            empresa = new DaoEmpresa().getEmpresaByRFC(this.empresaSeleccionada);
        }

    }

    public CfdisRecibidos getCfdi() {
        return cfdi;
    }

    public void setCfdi(CfdisRecibidos cfdi) {
        this.cfdi = cfdi;
    }

    public List<CfdisRecibidos> getListaCFDIs() {
        return listaCFDIs;
    }

    public void setListaCFDIs(List<CfdisRecibidos> listaCFDIs) {
        this.listaCFDIs = listaCFDIs;
    }

    public List<CfdisRecibidos> getFiltroCFDIs() {
        return filtroCFDIs;
    }

    public void setFiltroCFDIs(List<CfdisRecibidos> filtroCFDIs) {
        this.filtroCFDIs = filtroCFDIs;
    }

    public Empresas getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresas empresa) {
        this.empresa = empresa;
    }

    public List<CfdisRecibidos> getCfdisSeleccionados() {
        return cfdisSeleccionados;
    }

    public void setCfdisSeleccionados(List<CfdisRecibidos> cfdisSeleccionados) {
        this.cfdisSeleccionados = cfdisSeleccionados;
    }

    public List<CfdisRecibidos> getCFDIs() {

        try {
            DaoCfdisRecibidos daoCFDIs = new DaoCfdisRecibidos();
            this.listaCFDIs = daoCFDIs.getCfdisByidEmpresa(this.empresa.getIdEmpresa());

            return listaCFDIs;

        } catch (Exception e) {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario/Contraseña incorrecto");
            FacesContext.getCurrentInstance().addMessage(null, this.msg);
            return null;
        }

    }

    public String deleteCFDIs() {
        boolean delete = false;
        System.out.println("Eliminar CFDIS MB");

        if (this.cfdisSeleccionados == null || this.cfdisSeleccionados.isEmpty()) {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Selecciona un CFDI");
        } else {

            for (CfdisRecibidos cfdisRecibidos : cfdisSeleccionados) {
                try {
                    delete = new DaoCfdisRecibidos().delete(cfdisRecibidos);
                    if (delete) {
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "CFDIs eliminados");
                    } else {
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar los CFDIs");
                    }
                } catch (Exception e) {
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar los CFDIs");
                }
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
        return "/CFDI/recibidos";
    }

    public String existeSeleccionCFDI() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();

        String parametro = (String) params.get("nombreParametro");
        System.out.println("parametro: " + parametro);

        boolean estadoCFDI = false;
        if (this.cfdisSeleccionados.isEmpty()) {
            System.out.println("cfdisSeleccionados esta vacio");
            this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Debe seleccionar un CFDI");
            FacesContext.getCurrentInstance().addMessage(null, this.msg);
        } else {
            System.out.println("cfdisSeleccionados no esta vacio");
            if (parametro != null && parametro.equals("eliminarCFDI")) {
                System.out.println("parametro no es nulo y es igual a eliminarCFDI");
                estadoCFDI = true;
            }
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("estadoCFDI", estadoCFDI);
        return "/CFDI/recibidos";
    }
}

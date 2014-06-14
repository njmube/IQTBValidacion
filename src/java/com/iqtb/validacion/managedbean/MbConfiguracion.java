/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoEmpresa;
import com.iqtb.validacion.pojo.Empresas;
import com.iqtb.validacion.pojo.Usuarios;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author danielromero
 */
@ManagedBean
@ViewScoped
public class MbConfiguracion implements Serializable{

    private Empresas empresa;
    private final String sessionUsuario;
    private FacesMessage msg;

    public MbConfiguracion() throws Exception {
        this.empresa = new Empresas();

        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        String empresaSeleccionada = (String) httpServletRequest.getSession().getAttribute("empresaSeleccionada");
        this.sessionUsuario = ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid();
        if (httpServletRequest.getSession().getAttribute("empresaSeleccionada") != null) {
            empresa = new DaoEmpresa().getEmpresaByRFC(empresaSeleccionada);
        }
    }

    public Empresas getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresas empresa) {
        this.empresa = empresa;
    }

    public void updateEmpresa() {
        boolean empresaUpdate = false;

        this.empresa.setNombre(this.empresa.getNombre());
        this.empresa.setCalle(this.empresa.getCalle());

        try {
            empresaUpdate = new DaoEmpresa().updateEmpresa(this.empresa);
            if (empresaUpdate) {
                this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", this.empresa.getRfc() + " se modifico corectamente.");
            }
        } catch (Exception e) {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", this.empresa.getRfc() + " no se pudo Modificar.");
        }
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
        
    }
}

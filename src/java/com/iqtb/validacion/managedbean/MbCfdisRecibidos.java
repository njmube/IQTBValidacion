/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoBitacora;
import com.iqtb.validacion.dao.DaoCfdisRecibidos;
import com.iqtb.validacion.dao.DaoEmpresa;
import com.iqtb.validacion.dao.DaoSociosComerciales;
import com.iqtb.validacion.dao.DaoUsuario;
import com.iqtb.validacion.pojo.Bitacora;
import com.iqtb.validacion.pojo.CfdisRecibidos;
import com.iqtb.validacion.pojo.Empresas;
import com.iqtb.validacion.pojo.Servicios;
import com.iqtb.validacion.pojo.SociosComerciales;
import com.iqtb.validacion.pojo.Usuarios;
import static com.iqtb.validacion.util.DateTime.getTimestamp;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author danielromero
 */
@ManagedBean
@SessionScoped
public class MbCfdisRecibidos implements Serializable {

    private CfdisRecibidos cfdi;
    private List<CfdisRecibidos> cfdisSeleccionados;
    private List<CfdisRecibidos> listaCFDIs;
    private List<CfdisRecibidos> filtroCFDIs;
    private Empresas empresa;
    private SociosComerciales socioComercial;
    private StreamedContent file;
    private String strNombreXML;
    private Usuarios usuario;
    private Bitacora bitacora;
    private Servicios servicio;
    private String descripcion;

    private FacesContext faceContext;
    private HttpServletRequest httpServletRequest;
    private final String sessionUsuario;
    private String empresaSeleccionada;
    private FacesMessage msg;

    public MbCfdisRecibidos() throws Exception {
        this.cfdi = new CfdisRecibidos();
        this.cfdisSeleccionados = new ArrayList<CfdisRecibidos>();
        this.empresa = new Empresas();
        this.socioComercial = new SociosComerciales();
        this.usuario = new Usuarios();
        this.bitacora = new Bitacora();
        this.servicio = new Servicios();

        faceContext = FacesContext.getCurrentInstance();
        httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        this.empresaSeleccionada = (String) httpServletRequest.getSession().getAttribute("empresaSeleccionada");
        this.sessionUsuario = ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid();
        this.usuario = new DaoUsuario().getByUserid(this.sessionUsuario);
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

    public SociosComerciales getSocioComercial() {
        return socioComercial;
    }

    public void setSocioComercial(SociosComerciales socioComercial) {
        this.socioComercial = socioComercial;
    }

    public StreamedContent getFile() {
        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }

    public List<CfdisRecibidos> getCFDIs() {
        this.listaCFDIs = null;
        System.out.println("sessionUsuario " + this.sessionUsuario);
        System.out.println("USUARIO " + this.usuario.getUserid());
        System.out.println("EMPRESA " + this.empresa.getRfc());
        try {
            DaoCfdisRecibidos daoCFDIs = new DaoCfdisRecibidos();

            this.listaCFDIs = daoCFDIs.getCfdisByidEmpresa(this.empresa.getIdEmpresa());
            if (this.listaCFDIs == null || this.listaCFDIs.size() <= 0) {
                System.out.println("NO hay CFDIS");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CFDIs", "No existen CFDIs recibidos para mostrar.");
                FacesContext.getCurrentInstance().addMessage(null, this.msg);
            }

//            return listaCFDIs;
        } catch (Exception e) {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al obtener los CFDIs recibidos");
            FacesContext.getCurrentInstance().addMessage(null, this.msg);
            System.out.println("Error MbCfdisRecibidos getCFDIs");
        }

        return listaCFDIs;
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
                        this.descripcion = "[CFDIs] Usuario: " + this.sessionUsuario + " Empresa seleccionada: " + this.empresa.getRfc() + ", elimino el CFDI Serie " + cfdisRecibidos.getSerie()+" Folio "+cfdisRecibidos.getFolio();
                        bitacora(this.usuario.getIdUsuario(), 2, this.empresa.getIdEmpresa(), this.descripcion, "INFO");
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "CFDIs eliminados");
                    } else {
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar los CFDIs");
                    }
                } catch (Exception e) {
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar los CFDIs");
                    System.out.println("Eroor MbCfdisRecibidos delete CFDIs");
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

    public String getSocioComercialByID(int id) {

        try {
            DaoSociosComerciales daoSociosComerciales = new DaoSociosComerciales();
            this.socioComercial = daoSociosComerciales.getSocioComercialByID(id);
        } catch (Exception e) {
            System.out.println("Error al obtener el socio comercial.");
        }
        return this.socioComercial.getRfc();
    }

    public void downloadXML(int idCfdi) {
        SociosComerciales sc;
        CfdisRecibidos cr;
        try {
            System.out.println("Descargar XML");
            DaoCfdisRecibidos daoCfdis = new DaoCfdisRecibidos();
            cr = daoCfdis.getCfdiByID(idCfdi);

            DaoSociosComerciales daoSocCom = new DaoSociosComerciales();
            sc = daoSocCom.getSocioComercialByID(cr.getIdSocioComercial());
            String rfc = sc.getRfc();
            strNombreXML = rfc + "_" + cr.getUuid() + ".xml";

            System.out.println("colocando nombre al achivo: " + strNombreXML);
            FacesContext ctx = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
            response.setContentType("application/force-download");
            String disposition = "attachment; fileName=" + strNombreXML;
            response.setHeader("Content-Disposition", disposition);
            ServletOutputStream out = response.getOutputStream();
            InputStream ascii;
//            out.print(cr.getXmlSat());

            String texto = cr.getXmlSat();
            System.out.println("xml " + texto);

            ascii = new ByteArrayInputStream(texto.getBytes("UTF-8"));

            byte[] buf = new byte[5 * 1000 * 1024]; // 5000K buffer
            //byte[] buf = new byte[]; // 5000K buffer
            int bytesRead;
            while ((bytesRead = ascii.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
            }

            out.flush();
            System.out.println("se escribio en el archivo");
            out.close();
            System.out.println("close out");
            ctx.responseComplete();
            System.out.println("response complet");
            this.descripcion = "[CFDIs] Usuario: " + this.sessionUsuario + " Empresa seleccionada: " + this.empresa.getRfc() + ", descargó XML de CFDI Serie "+cr.getSerie()+" Folio "+cr.getFolio();
            bitacora(this.usuario.getIdUsuario(), 2, this.empresa.getIdEmpresa(), this.descripcion, "INFO");

        } catch (Exception e) {
            System.out.println("Error al obtener el CFDI");
        }
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoCfdisRecibidos;
import com.iqtb.validacion.dao.DaoEmpresa;
import com.iqtb.validacion.dao.DaoPlantilla;
import com.iqtb.validacion.dao.DaoServicio;
import com.iqtb.validacion.dao.DaoSociosComerciales;
import com.iqtb.validacion.dao.DaoUsuario;
import com.iqtb.validacion.jasper.JasperUtils;
import com.iqtb.validacion.pojo.Bitacora;
import com.iqtb.validacion.pojo.CfdisRecibidos;
import com.iqtb.validacion.pojo.Empresas;
import com.iqtb.validacion.pojo.Plantillas;
import com.iqtb.validacion.pojo.Servicios;
import com.iqtb.validacion.pojo.SociosComerciales;
import com.iqtb.validacion.pojo.Usuarios;
import static com.iqtb.validacion.util.Bitacoras.errorBitacora;
import static com.iqtb.validacion.util.Bitacoras.registrarBitacora;
import com.iqtb.validacion.util.ImprimirCFDI;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author danielromero
 */
@ManagedBean
@ViewScoped
public class MbCfdisRecibidos implements Serializable {

    private ImprimirCFDI cfdi;
    private CfdisRecibidos verCfdi;
    private List<ImprimirCFDI> cfdisSeleccionados;
    private List<CfdisRecibidos> listaCFDIs;
    private List<CfdisRecibidos> filtroCFDIs;
    private Empresas empresa;
    private SociosComerciales socioComercial;
    private StreamedContent file;
    private Usuarios usuario;
    private Bitacora bitacora;
    private Servicios servicio;
    private String descripcion;

    private FacesContext faceContext;
    private HttpServletRequest httpServletRequest;
    private final String sessionUsuario;
    private String empresaSeleccionada;
    private Integer idSocioComercial;
    private boolean enableValidar;
    private FacesMessage msg;
    private static final Logger logger = Logger.getLogger(MbCfdisRecibidos.class);

    public MbCfdisRecibidos() throws Exception {
        this.cfdi = new ImprimirCFDI();
        verCfdi = new CfdisRecibidos();
        this.cfdisSeleccionados = new ArrayList<ImprimirCFDI>();
        this.empresa = new Empresas();
        this.socioComercial = new SociosComerciales();
        this.usuario = new Usuarios();
        this.bitacora = new Bitacora();
        this.servicio = new Servicios();
        enableValidar = false;

        faceContext = FacesContext.getCurrentInstance();
        httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        this.empresaSeleccionada = (String) httpServletRequest.getSession().getAttribute("empresaSeleccionada");
        this.sessionUsuario = ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid();
        try {
            this.usuario = new DaoUsuario().getByUserid(this.sessionUsuario);
            if (httpServletRequest.getSession().getAttribute("empresaSeleccionada") != null) {
                empresa = new DaoEmpresa().getEmpresaByRFC(this.empresaSeleccionada);
            }
            servicio = new DaoServicio().getServicoByNombre("VALIDADOR");
        } catch (Exception e) {
            logger.error("Error al obtener USUARIO EMPRESA SERVICIO. ERROR: " + e);
        }

    }

    public ImprimirCFDI getCfdi() {
        return cfdi;
    }

    public void setCfdi(ImprimirCFDI cfdi) {
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

    public List<ImprimirCFDI> getCfdisSeleccionados() {
        return cfdisSeleccionados;
    }

    public void setCfdisSeleccionados(List<ImprimirCFDI> cfdisSeleccionados) {
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

    public Integer getIdSocioComercial() {
        return idSocioComercial;
    }

    public void setIdSocioComercial(Integer idSocioComercial) {
        this.idSocioComercial = idSocioComercial;
    }

    public boolean isEnableValidar() {
        return enableValidar;
    }

    public void setEnableValidar(boolean enableValidar) {
        this.enableValidar = enableValidar;
    }

    public CfdisRecibidos getVerCfdi() {
        return verCfdi;
    }

    public void setVerCfdi(CfdisRecibidos verCfdi) {
        this.verCfdi = verCfdi;
    }

    public List<ImprimirCFDI> getCFDIs() {
        List<ImprimirCFDI> imprimirCfdi = new ArrayList<ImprimirCFDI>();
        this.listaCFDIs = null;
        try {
            DaoCfdisRecibidos daoCFDIs = new DaoCfdisRecibidos();

            this.listaCFDIs = daoCFDIs.getCfdisByidEmpresa(this.empresa.getIdEmpresa());
            if (this.listaCFDIs == null || this.listaCFDIs.size() <= 0) {
//                this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CFDIs", "No existen CFDIs recibidos para mostrar.");
//                FacesContext.getCurrentInstance().addMessage(null, this.msg);
                logger.info("getCFDIs - No existen CFDIs recibidos para mostrar");
                return imprimirCfdi;
            }

//            return listaCFDIs;
        } catch (Exception e) {
            descripcion = "[CFDIs] getCFDIS ERROR: " + e;
            errorBitacora(servicio.getIdServicio(), descripcion, "ERROR");
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al obtener los CFDIs");
            FacesContext.getCurrentInstance().addMessage(null, this.msg);
            logger.error("getCFDIs - ERROR: " + e);
        }
        for (CfdisRecibidos cfdi : listaCFDIs) {
            ImprimirCFDI imCfdi = new ImprimirCFDI();
            imCfdi.setIdCfdiRecibido(cfdi.getIdCfdiRecibido());
            imCfdi.setIdEmpresa(cfdi.getIdEmpresa());
            imCfdi.setIdSocioComercial(cfdi.getIdSocioComercial());
            imCfdi.setSerie(cfdi.getSerie());
            imCfdi.setFolio(cfdi.getFolio());
            imCfdi.setUuid(cfdi.getUuid());
            imCfdi.setFecha(cfdi.getFecha());
            imCfdi.setXmlSat(cfdi.getXmlSat());
            imCfdi.setFechaRecepcion(cfdi.getFechaRecepcion());
            imCfdi.setEstado(cfdi.getEstado());
            imCfdi.setError(cfdi.getError());
            imCfdi.setReportado(cfdi.getReportado());
            imCfdi.setTotal(cfdi.getTotal());
            try {
                DaoSociosComerciales daoSociosComerciales = new DaoSociosComerciales();
                this.socioComercial = daoSociosComerciales.getSocioComercialByID(cfdi.getIdSocioComercial());
            } catch (Exception e) {
                descripcion = "[CFDIs] getSocioComercialbyID ERROR " + e;
                errorBitacora(servicio.getIdServicio(), descripcion, "ERROR");
                logger.error("getSocioComercialByID ERROR: " + e);
            }
            imCfdi.setRfcSocioComercial(socioComercial.getRfc());
            imprimirCfdi.add(imCfdi);
        }
        return imprimirCfdi;
    }

    public String deleteCFDIs() {
        boolean delete = false;

        if (this.cfdisSeleccionados == null || this.cfdisSeleccionados.isEmpty()) {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Selecciona un CFDI");
            logger.warn("deleteCFDIs - no existen CFDIs seleccionados");
        } else {

            for (ImprimirCFDI imprimirCFDI : cfdisSeleccionados) {
                CfdisRecibidos cfdisRecibidos = new CfdisRecibidos();
                cfdisRecibidos.setIdCfdiRecibido(imprimirCFDI.getIdCfdiRecibido());
                cfdisRecibidos.setIdEmpresa(imprimirCFDI.getIdEmpresa());
                cfdisRecibidos.setIdSocioComercial(imprimirCFDI.getIdSocioComercial());
                cfdisRecibidos.setSerie(imprimirCFDI.getSerie());
                cfdisRecibidos.setFolio(imprimirCFDI.getFolio());
                cfdisRecibidos.setUuid(imprimirCFDI.getUuid());
                cfdisRecibidos.setFecha(imprimirCFDI.getFecha());
                cfdisRecibidos.setXmlSat(imprimirCFDI.getXmlSat());
                cfdisRecibidos.setFechaRecepcion(imprimirCFDI.getFechaRecepcion());
                cfdisRecibidos.setEstado(imprimirCFDI.getEstado());
                cfdisRecibidos.setError(imprimirCFDI.getError());
                cfdisRecibidos.setReportado(imprimirCFDI.getReportado());
                cfdisRecibidos.setTotal(imprimirCFDI.getTotal());
                cfdisRecibidos.setEstadoNotificacion(imprimirCFDI.getEstadoNotificacion());
                try {
                    delete = new DaoCfdisRecibidos().delete(cfdisRecibidos);
                    if (delete) {
                        descripcion = "[CFDIs] Usuario: " + this.sessionUsuario + " elimino el CFDI Serie " + cfdisRecibidos.getSerie() + " Folio " + cfdisRecibidos.getFolio();
                        registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), empresa.getIdEmpresa(), descripcion, "INFO");
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "CFDIs eliminados");
                        logger.info("deleteCFDIs - Usuario: " + this.sessionUsuario + " elimino el CFDI con Serie " + cfdisRecibidos.getSerie() + " Folio " + cfdisRecibidos.getFolio());
                    } else {
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar los CFDIs");
                        logger.info("deleteCFDIs - Error al elimnar el CFDI con Serie " + cfdisRecibidos.getSerie() + " Folio " + cfdisRecibidos.getFolio());
                    }
                } catch (Exception e) {
                    descripcion = "[CFDIs] deleteCFDIs ERROR " + e;
                    errorBitacora(servicio.getIdServicio(), descripcion, "ERROR");
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar los CFDIs");
                    logger.error("deleteCFDIs ERROR: " + e);
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

        boolean estadoCFDI = false;
        if (this.cfdisSeleccionados.isEmpty()) {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Debe seleccionar un CFDI");
            FacesContext.getCurrentInstance().addMessage(null, this.msg);
        } else {
            if (parametro != null && parametro.equals("eliminarCFDI")) {
                estadoCFDI = true;
            }
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("estadoCFDI", estadoCFDI);
        return "/CFDI/recibidos";
    }

    public void viewCFDI() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();

        String parametro = (String) params.get("nombreParametro");

        boolean estadoCFDI = false;
        if (cfdisSeleccionados.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione un CFDI");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            if (cfdisSeleccionados.size() > 1) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione solo un CFDI");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                estadoCFDI = true;
                for (ImprimirCFDI cfdiR : cfdisSeleccionados) {
                    this.cfdi = cfdiR;
                }

            }
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("estadoCFDI", estadoCFDI);
//        return "/CFDI/recibidos";
    }

    public void verCFDI() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();

        int id = Integer.valueOf((String) params.get("idCfdi"));
        boolean estadoCFDI = false;
        try {
            verCfdi = new DaoCfdisRecibidos().getCfdiByID(id);
            estadoCFDI = true;
        } catch (Exception e) {
            logger.error("verCFDI ERROR "+e);
        }
        
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("estadoCFDI", estadoCFDI);

    }

    public String SocioComercialByID(int id) {
//        FacesContext facesContext = FacesContext.getCurrentInstance();
//        Map params = facesContext.getExternalContext().getRequestParameterMap();
//        System.out.println("parametro "+(String)params.get("idSocioComercial"));
//        Integer idSocio = Integer.valueOf((String)params.get("idSocioComercial"));
//        if (idSocio == null) {
//            System.out.println("idSocio null");
//        }else{
//            System.out.println("id "+idSocio);
//        }
        try {
            DaoSociosComerciales daoSociosComerciales = new DaoSociosComerciales();
            this.socioComercial = daoSociosComerciales.getSocioComercialByID(id);
        } catch (Exception e) {
            descripcion = "[CFDIs] getSocioComercialbyID ERROR " + e;
            errorBitacora(servicio.getIdServicio(), descripcion, "ERROR");
            logger.error("getSocioComercialByID ERROR: " + e);
        }
        return this.socioComercial.getRfc();
    }

    public void downloadXML() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();

        int id = Integer.valueOf((String) params.get("idCfdi"));
        SociosComerciales sc;
        CfdisRecibidos cr;
        String strNombreXML;
        try {
            logger.info("downloadXML- inicia descarga de XML");
            DaoCfdisRecibidos daoCfdis = new DaoCfdisRecibidos();
            cr = daoCfdis.getCfdiByID(id);

            DaoSociosComerciales daoSocCom = new DaoSociosComerciales();
            sc = daoSocCom.getSocioComercialByID(cr.getIdSocioComercial());
            String rfc = sc.getRfc();
            strNombreXML = rfc + "_" + cr.getUuid() + ".xml";

            logger.info("downloadXML- Nombre del XML: " + strNombreXML);
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
            out.close();
            ctx.responseComplete();
            descripcion = "[CFDIs] Usuario: " + usuario.getIdUsuario() + ", descargó XML de CFDI Serie " + cr.getSerie() + " Folio " + cr.getFolio();
            registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), empresa.getIdEmpresa(), descripcion, "INFO");
            logger.info("downloadXML- XML: " + strNombreXML + " Generado.");
        } catch (Exception e) {
            descripcion = "[CFDIs] downloadXML ERROR: " + e;
            errorBitacora(servicio.getIdServicio(), descripcion, "ERROR");
            logger.error("downloadXML - ERROR: " + e);
        }
    }

    public void downloadPDF() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();

        int id = Integer.valueOf((String) params.get("idCfdi"));
        SociosComerciales sc;
        CfdisRecibidos cr;
        String strNombrePDF;
        List<Plantillas> plantillas;
        try {
            logger.info("downloadPDF - inicia descarga de PDF");
            DaoCfdisRecibidos daoCfdis = new DaoCfdisRecibidos();
            cr = daoCfdis.getCfdiByID(id);
//            if (cr.getEstado().equals("XML_INVALIDO")) {
//                this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "No es posible visualizar en formato PFD este CFDI su estado es: " + cr.getEstado());
//                FacesContext.getCurrentInstance().addMessage(null, this.msg);
//                logger.warn("downloadPDF - No es posible visualizar en formato PFD este CFDI su estado es: " + cr.getEstado());
//                return;
//            }
            DaoSociosComerciales daoSocCom = new DaoSociosComerciales();
            sc = daoSocCom.getSocioComercialByID(cr.getIdSocioComercial());
            String rfc = sc.getRfc();
            strNombrePDF = rfc + "_" + cr.getUuid() + ".pdf";
            plantillas = new DaoPlantilla().listaPlantillas();
            if (plantillas.size() <= 0) {
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No existen Plantillas almcenadas.");
                FacesContext.getCurrentInstance().addMessage(null, this.msg);
                logger.warn("downloadPDF - No existen Plantillas almcenadas");
                return;
            }
            JasperUtils jasperUtils = new JasperUtils();
            JasperPrint jasperPrint = jasperUtils.fill(cr.getXmlSat(), plantillas.get(0).getRuta());
            byte[] jas = jasperUtils.getPdfAsBytesArray(jasperPrint);
            System.out.println("colocando nombre al achivo: " + strNombrePDF);
            FacesContext ctx = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
            response.setContentType("application/force-download");
            String disposition = "attachment; fileName=" + strNombrePDF;
            response.setHeader("Content-Disposition", disposition);
            ServletOutputStream out = response.getOutputStream();
            InputStream ascii;

            ascii = new ByteArrayInputStream(jas);

            byte[] buf = new byte[5 * 1000 * 1024]; // 5000K buffer
            //byte[] buf = new byte[]; // 5000K buffer
            int bytesRead;
            while ((bytesRead = ascii.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
            }

            out.flush();
            out.close();
            ctx.responseComplete();
            descripcion = "[CFDIs] Usuario: " + this.sessionUsuario + " descargó PDF de CFDI Serie " + cr.getSerie() + " Folio " + cr.getFolio();
            registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), empresa.getIdEmpresa(), descripcion, "INFO");
            logger.info("downloadPDF- PDF: " + strNombrePDF + " Generado.");
        } catch (JRException ex) {
            descripcion = "[CFDIs] downloadPDF- ERROR JRE: " + ex;
            errorBitacora(servicio.getIdServicio(), descripcion, "ERROR");
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No es posible generar el PDF.");
            FacesContext.getCurrentInstance().addMessage(null, this.msg);
            logger.error("downloadPDF- ERROR JRE: " + ex);
        } catch (Exception e) {
            descripcion = "[CFDIs] downloadPDF- ERROR: " + e;
            errorBitacora(servicio.getIdServicio(), descripcion, "ERROR");
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No es posible generar el PDF.");
            FacesContext.getCurrentInstance().addMessage(null, this.msg);
            logger.error("downloadPDF- ERROR: " + e);
        }
    }

    public void validar() {
        logger.info("Inicia Validar CFDIs");
        boolean btnValidar = true;
        int numCfdis = 0;
        if (cfdisSeleccionados.isEmpty()) {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione un CFDI");
            FacesContext.getCurrentInstance().addMessage(null, this.msg);
            return;
        }
        for (ImprimirCFDI imprimirCFDI : cfdisSeleccionados) {
            CfdisRecibidos cRecibido = new CfdisRecibidos();
            cRecibido.setIdCfdiRecibido(imprimirCFDI.getIdCfdiRecibido());
            cRecibido.setIdSocioComercial(imprimirCFDI.getIdSocioComercial());
            cRecibido.setIdEmpresa(imprimirCFDI.getIdEmpresa());
            cRecibido.setSerie(imprimirCFDI.getSerie());
            cRecibido.setFolio(imprimirCFDI.getFolio());
            cRecibido.setUuid(imprimirCFDI.getUuid());
            cRecibido.setFecha(imprimirCFDI.getFecha());
            cRecibido.setTotal(imprimirCFDI.getTotal());
            cRecibido.setXmlSat(imprimirCFDI.getXmlSat());
            cRecibido.setFechaRecepcion(imprimirCFDI.getFechaRecepcion());
            cRecibido.setEstado("NUEVO");
            cRecibido.setError("");
            cRecibido.setReportado(imprimirCFDI.getReportado());
            cRecibido.setEstadoNotificacion("SIN_NOTIFICAR");
            if (new DaoCfdisRecibidos().actualizarCfdi(cRecibido)) {
                numCfdis++;
                logger.info("CFDIs con id: " + cRecibido.getIdCfdiRecibido() + " se le ha colocado estado: " + cRecibido.getEstado());
            } else {
                logger.error("Error al modificar el campo estado y error del CFDI con id: " + cRecibido.getIdCfdiRecibido());
            }
        }
        this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Validación", numCfdis + " CFDIs se han colocado en estado NUEVO, para ser validados nuevamente.");
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
    }

}

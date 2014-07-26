/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author danielromero
 */
public class ImprimirCFDI implements Serializable{
    private Integer idCfdiRecibido;
     private int idSocioComercial;
     private int idEmpresa;
     private String serie;
     private Integer folio;
     private String uuid;
     private Date fecha;
     private BigDecimal total;
     private String xmlSat;
     private Date fechaRecepcion;
     private String estado;
     private String error;
     private Byte reportado;
     private String estadoNotificacion;
     private String rfcSocioComercial;

    public Integer getIdCfdiRecibido() {
        return idCfdiRecibido;
    }

    public void setIdCfdiRecibido(Integer idCfdiRecibido) {
        this.idCfdiRecibido = idCfdiRecibido;
    }

    public int getIdSocioComercial() {
        return idSocioComercial;
    }

    public void setIdSocioComercial(int idSocioComercial) {
        this.idSocioComercial = idSocioComercial;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public Integer getFolio() {
        return folio;
    }

    public void setFolio(Integer folio) {
        this.folio = folio;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getXmlSat() {
        return xmlSat;
    }

    public void setXmlSat(String xmlSat) {
        this.xmlSat = xmlSat;
    }

    public Date getFechaRecepcion() {
        return fechaRecepcion;
    }

    public void setFechaRecepcion(Date fechaRecepcion) {
        this.fechaRecepcion = fechaRecepcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Byte getReportado() {
        return reportado;
    }

    public void setReportado(Byte reportado) {
        this.reportado = reportado;
    }

    public String getRfcSocioComercial() {
        return rfcSocioComercial;
    }

    public void setRfcSocioComercial(String rfcSocioComercial) {
        this.rfcSocioComercial = rfcSocioComercial;
    }

    public String getEstadoNotificacion() {
        return estadoNotificacion;
    }

    public void setEstadoNotificacion(String estadoNotificacion) {
        this.estadoNotificacion = estadoNotificacion;
    }
     
     
}

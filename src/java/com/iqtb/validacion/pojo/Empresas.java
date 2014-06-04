package com.iqtb.validacion.pojo;
// Generated 04-jun-2014 13:10:04 by Hibernate Tools 3.6.0



/**
 * Empresas generated by hbm2java
 */
public class Empresas  implements java.io.Serializable {


     private Integer idEmpresa;
     private String nombre;
     private String rfc;
     private String calle;
     private String numExterior;
     private String numInterior;
     private String colonia;
     private String localidad;
     private String referencia;
     private String municipio;
     private String estado;
     private String pais;
     private String cp;
     private String codigogln;
     private String email;
     private String telefono;
     private String curp;

    public Empresas() {
    }

	
    public Empresas(String nombre, String rfc, String calle) {
        this.nombre = nombre;
        this.rfc = rfc;
        this.calle = calle;
    }
    public Empresas(String nombre, String rfc, String calle, String numExterior, String numInterior, String colonia, String localidad, String referencia, String municipio, String estado, String pais, String cp, String codigogln, String email, String telefono, String curp) {
       this.nombre = nombre;
       this.rfc = rfc;
       this.calle = calle;
       this.numExterior = numExterior;
       this.numInterior = numInterior;
       this.colonia = colonia;
       this.localidad = localidad;
       this.referencia = referencia;
       this.municipio = municipio;
       this.estado = estado;
       this.pais = pais;
       this.cp = cp;
       this.codigogln = codigogln;
       this.email = email;
       this.telefono = telefono;
       this.curp = curp;
    }
   
    public Integer getIdEmpresa() {
        return this.idEmpresa;
    }
    
    public void setIdEmpresa(Integer idEmpresa) {
        this.idEmpresa = idEmpresa;
    }
    public String getNombre() {
        return this.nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getRfc() {
        return this.rfc;
    }
    
    public void setRfc(String rfc) {
        this.rfc = rfc;
    }
    public String getCalle() {
        return this.calle;
    }
    
    public void setCalle(String calle) {
        this.calle = calle;
    }
    public String getNumExterior() {
        return this.numExterior;
    }
    
    public void setNumExterior(String numExterior) {
        this.numExterior = numExterior;
    }
    public String getNumInterior() {
        return this.numInterior;
    }
    
    public void setNumInterior(String numInterior) {
        this.numInterior = numInterior;
    }
    public String getColonia() {
        return this.colonia;
    }
    
    public void setColonia(String colonia) {
        this.colonia = colonia;
    }
    public String getLocalidad() {
        return this.localidad;
    }
    
    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }
    public String getReferencia() {
        return this.referencia;
    }
    
    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
    public String getMunicipio() {
        return this.municipio;
    }
    
    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }
    public String getEstado() {
        return this.estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public String getPais() {
        return this.pais;
    }
    
    public void setPais(String pais) {
        this.pais = pais;
    }
    public String getCp() {
        return this.cp;
    }
    
    public void setCp(String cp) {
        this.cp = cp;
    }
    public String getCodigogln() {
        return this.codigogln;
    }
    
    public void setCodigogln(String codigogln) {
        this.codigogln = codigogln;
    }
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    public String getTelefono() {
        return this.telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public String getCurp() {
        return this.curp;
    }
    
    public void setCurp(String curp) {
        this.curp = curp;
    }




}


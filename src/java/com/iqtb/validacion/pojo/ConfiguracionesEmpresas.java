package com.iqtb.validacion.pojo;
// Generated 04-jun-2014 13:10:04 by Hibernate Tools 3.6.0



/**
 * ConfiguracionesEmpresas generated by hbm2java
 */
public class ConfiguracionesEmpresas  implements java.io.Serializable {


     private ConfiguracionesEmpresasId id;
     private String propiedad;
     private String valor;
     private String descripcion;
     private String tipo;

    public ConfiguracionesEmpresas() {
    }

    public ConfiguracionesEmpresas(ConfiguracionesEmpresasId id, String propiedad, String valor, String descripcion, String tipo) {
       this.id = id;
       this.propiedad = propiedad;
       this.valor = valor;
       this.descripcion = descripcion;
       this.tipo = tipo;
    }
   
    public ConfiguracionesEmpresasId getId() {
        return this.id;
    }
    
    public void setId(ConfiguracionesEmpresasId id) {
        this.id = id;
    }
    public String getPropiedad() {
        return this.propiedad;
    }
    
    public void setPropiedad(String propiedad) {
        this.propiedad = propiedad;
    }
    public String getValor() {
        return this.valor;
    }
    
    public void setValor(String valor) {
        this.valor = valor;
    }
    public String getDescripcion() {
        return this.descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public String getTipo() {
        return this.tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }




}



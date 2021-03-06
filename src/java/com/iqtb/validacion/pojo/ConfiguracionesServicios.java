package com.iqtb.validacion.pojo;
// Generated 18-jul-2014 19:02:45 by Hibernate Tools 3.6.0



/**
 * ConfiguracionesServicios generated by hbm2java
 */
public class ConfiguracionesServicios  implements java.io.Serializable {


     private Integer idConfiguracionServicio;
     private int idServicio;
     private String propiedad;
     private String valor;
     private String descripcion;
     private String tipo;

    public ConfiguracionesServicios() {
    }

    public ConfiguracionesServicios(int idServicio, String propiedad, String valor, String descripcion, String tipo) {
       this.idServicio = idServicio;
       this.propiedad = propiedad;
       this.valor = valor;
       this.descripcion = descripcion;
       this.tipo = tipo;
    }
   
    public Integer getIdConfiguracionServicio() {
        return this.idConfiguracionServicio;
    }
    
    public void setIdConfiguracionServicio(Integer idConfiguracionServicio) {
        this.idConfiguracionServicio = idConfiguracionServicio;
    }
    public int getIdServicio() {
        return this.idServicio;
    }
    
    public void setIdServicio(int idServicio) {
        this.idServicio = idServicio;
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



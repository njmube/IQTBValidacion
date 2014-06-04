package com.iqtb.validacion.pojo;
// Generated 04-jun-2014 13:10:04 by Hibernate Tools 3.6.0



/**
 * SociosComercialesId generated by hbm2java
 */
public class SociosComercialesId  implements java.io.Serializable {


     private int idSocioComercial;
     private int idEmpresa;

    public SociosComercialesId() {
    }

    public SociosComercialesId(int idSocioComercial, int idEmpresa) {
       this.idSocioComercial = idSocioComercial;
       this.idEmpresa = idEmpresa;
    }
   
    public int getIdSocioComercial() {
        return this.idSocioComercial;
    }
    
    public void setIdSocioComercial(int idSocioComercial) {
        this.idSocioComercial = idSocioComercial;
    }
    public int getIdEmpresa() {
        return this.idEmpresa;
    }
    
    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }


   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof SociosComercialesId) ) return false;
		 SociosComercialesId castOther = ( SociosComercialesId ) other; 
         
		 return (this.getIdSocioComercial()==castOther.getIdSocioComercial())
 && (this.getIdEmpresa()==castOther.getIdEmpresa());
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + this.getIdSocioComercial();
         result = 37 * result + this.getIdEmpresa();
         return result;
   }   


}


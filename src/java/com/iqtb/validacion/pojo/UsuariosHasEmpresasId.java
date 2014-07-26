package com.iqtb.validacion.pojo;
// Generated 18-jul-2014 19:02:45 by Hibernate Tools 3.6.0



/**
 * UsuariosHasEmpresasId generated by hbm2java
 */
public class UsuariosHasEmpresasId  implements java.io.Serializable {


     private int idUsuario;
     private int idEmpresa;

    public UsuariosHasEmpresasId() {
    }

    public UsuariosHasEmpresasId(int idUsuario, int idEmpresa) {
       this.idUsuario = idUsuario;
       this.idEmpresa = idEmpresa;
    }
   
    public int getIdUsuario() {
        return this.idUsuario;
    }
    
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
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
		 if ( !(other instanceof UsuariosHasEmpresasId) ) return false;
		 UsuariosHasEmpresasId castOther = ( UsuariosHasEmpresasId ) other; 
         
		 return (this.getIdUsuario()==castOther.getIdUsuario())
 && (this.getIdEmpresa()==castOther.getIdEmpresa());
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + this.getIdUsuario();
         result = 37 * result + this.getIdEmpresa();
         return result;
   }   


}



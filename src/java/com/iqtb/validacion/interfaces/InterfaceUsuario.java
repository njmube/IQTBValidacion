package com.iqtb.validacion.interfaces;

import com.iqtb.validacion.pojo.Usuarios;
import org.hibernate.Session;

/**
 *
 * @author danielromero
 */
public interface InterfaceUsuario {
    public Usuarios getByUserid(String userid) throws Exception;
    public boolean updateUsuario(Usuarios usuario) throws Exception;
    
}

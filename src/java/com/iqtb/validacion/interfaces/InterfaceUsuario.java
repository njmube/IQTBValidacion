package com.iqtb.validacion.interfaces;

import com.iqtb.validacion.pojo.Usuarios;
import java.util.List;
import org.hibernate.Session;

/**
 *
 * @author danielromero
 */
public interface InterfaceUsuario {
    public List<Usuarios> getUsuariosAutenticados() throws Exception;
    public Usuarios getByUserid(String userid) throws Exception;
    public boolean updateUsuario(Usuarios usuario) throws Exception;
    
    
}

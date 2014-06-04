/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.interfaces;

import com.iqtb.validacion.pojo.UsuariosHasEmpresasId;
import java.util.List;

/**
 *
 * @author danielromero
 */
public interface InterfaceUsuarioEmpresa {
    public List<UsuariosHasEmpresasId> getEmpresasByIdUsuario(Integer idUsuario) throws Exception;
    
}

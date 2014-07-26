/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.interfaces;

import com.iqtb.validacion.pojo.SociosComerciales;
import java.util.List;

/**
 *
 * @author danielromero
 */
public interface InterfaceSociosComerciales {
    public SociosComerciales getSocioComercialByID(Integer idSocioComercial) throws Exception;
    public List<SociosComerciales> filtroSocioComercialByRFC(String rfc) throws Exception;
    public List<SociosComerciales> getSociosComercialesByIdEmpresa(Integer idEmpresa) throws Exception;
    public boolean insertSocioComercial(SociosComerciales socioComercial) throws Exception;
    public boolean updateSocioComercial(SociosComerciales socioComercial) throws Exception;
    public boolean deleteSocioComercial(SociosComerciales socioComercial) throws Exception;
    public SociosComerciales getSocioComercialByIdEmpresaRFC(Integer idEmpresa, String rfc) throws Exception;
    
}

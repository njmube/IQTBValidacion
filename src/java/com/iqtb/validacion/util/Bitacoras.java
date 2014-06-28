/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.util;

import com.iqtb.validacion.dao.DaoBitacora;
import com.iqtb.validacion.pojo.Bitacora;
import static com.iqtb.validacion.util.DateTime.getTimestamp;

/**
 *
 * @author danielromero
 */
public class Bitacoras {
    
    public static Bitacora registrarBitacora(int idUsuario, int idServicio, int idEmpresa, String descripcion, String tipo){
        boolean registro = false;
        Bitacora bitacora = new Bitacora();
        DaoBitacora daoBitacora = new DaoBitacora();
        
        try {

            bitacora.setIdUsuario(idUsuario);
            bitacora.setIdServicio(idServicio);
            bitacora.setIdEmpresa(idEmpresa);
            bitacora.setDescripcion(descripcion);
            bitacora.setFecha(getTimestamp());
            bitacora.setTipo(tipo);
            registro = daoBitacora.registarBitacora(bitacora);

            if (registro) {
                System.out.println("Registro insertado en bitacora");
            } else {
                System.out.println("Error al registrar en la Bitacora.");
            }
        } catch (Exception e) {
            System.out.println("Error al registrar en la Bitacora.");
        }
        return bitacora;
    }
    
}

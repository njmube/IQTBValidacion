/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.util;

import com.iqtb.validacion.dao.DaoBitacora;
import com.iqtb.validacion.pojo.Bitacora;
import static com.iqtb.validacion.util.DateTime.getTimestamp;
import org.apache.log4j.Logger;

/**
 *
 * @author danielromero
 */
public class Bitacoras {
    private static Logger logger = Logger.getLogger(Bitacoras.class);
    
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
                logger.info("BITACORA_REG Registro insertado en Bitacora");
            } else {
                logger.error("BITACORA_REG Error al registrar en la Bitacora");
            }
        } catch (Exception e) {
            logger.error("BITACORA_REG Error al registrar en la Bitacora ERROR: " +e);
        }
        return bitacora;
    }
    
    public static Bitacora insertBitacora(int idUsuario, int idServicio, String descripcion, String tipo){
        boolean registro = false;
        Bitacora bitacora = new Bitacora();
        DaoBitacora daoBitacora = new DaoBitacora();
        
        try {

            bitacora.setIdUsuario(idUsuario);
            bitacora.setIdServicio(idServicio);
            bitacora.setDescripcion(descripcion);
            bitacora.setFecha(getTimestamp());
            bitacora.setTipo(tipo);
            registro = daoBitacora.registarBitacora(bitacora);

            if (registro) {
                logger.info("BITACORA_IN Registro insertado en Bitacora");
            } else {
                logger.error("BITACORA_IN Error al registrar en la Bitacora");
            }
        } catch (Exception e) {
            logger.error("BITACORA_IN Error al registrar en la Bitacora ERROR: " +e);
        }
        return bitacora;
    }
    
    public static Bitacora errorBitacora(int idServicio, String descripcion, String tipo){
        boolean registro = false;
        Bitacora bitacora = new Bitacora();
        DaoBitacora daoBitacora = new DaoBitacora();
        
        try {

            bitacora.setIdServicio(idServicio);
            bitacora.setDescripcion(descripcion);
            bitacora.setFecha(getTimestamp());
            bitacora.setTipo(tipo);
            registro = daoBitacora.registarBitacora(bitacora);

            if (registro) {
                logger.info("BITACORA_ER Registro insertado en Bitacora");
            } else {
                logger.error("BITACORA_ER Error al registrar en la Bitacora");
            }
        } catch (Exception e) {
            logger.error("BITACORA_ER Error al registrar en la Bitacora ERROR: " +e);
        }
        return bitacora;
    }
    
}

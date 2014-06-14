/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.servlet;

import com.iqtb.validacion.dao.DaoUsuario;
import com.iqtb.validacion.pojo.Usuarios;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 *
 * @author danielromero
 */
public class SomeHttpSessionListener implements HttpSessionListener{

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        System.err.println("Sesión Creada: "+se.getSession().getId());
        //        ... Cualquier otra cosa ...
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        Usuarios usuario = (Usuarios) se.getSession().getAttribute("usuario");
        
        if (usuario != null) {
            Date fReg = new Date();
            long fecha = fReg.getTime();
            Timestamp timestamp = new Timestamp(fecha);
            usuario.setLastAction(timestamp);
            usuario.setEstado("ACTIVO");
            
            
            try {
                new DaoUsuario().updateUsuario(usuario);
            } catch (Exception e) {
                Logger.getLogger(SomeHttpSessionListener.class.getName()).log(Level.SEVERE, null, e);
            }
            
        }else{
            System.out.println("Usuario Nulo"); 
        }
        System.err.println("Sesión Destruida: "+se.getSession().getId());
//        ... Cualquier otra cosa ...
    }
    
}

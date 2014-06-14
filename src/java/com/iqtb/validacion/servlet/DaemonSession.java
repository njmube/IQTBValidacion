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
import java.util.List;
import java.util.TimerTask;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author danielromero
 */
public class DaemonSession extends TimerTask implements ServletContextListener{
    int cont;

    @Override
    public void run() {
        
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        boolean update = false;
        List<Usuarios> listaUsuarios;
        System.out.println("run "+cont);
        try {
            listaUsuarios = new DaoUsuario().getUsuariosAutenticados();
            if (!listaUsuarios.isEmpty()) {
                for (Usuarios usuarios : listaUsuarios) {
                    Date fReg = new Date();
                    long fecha = fReg.getTime();
                    Timestamp timestamp = new Timestamp(fecha);
                    usuarios.setLastAction(timestamp);
                    usuarios.setEstado("ACTIVO");
                    update = new DaoUsuario().updateUsuario(usuarios);
                    
                    if (update) {
                        System.out.println("DaemonSession Actualizo el estado de los usuarios AUTENTICADOS "+usuarios.getUserid());
                    }else{
                        System.out.println("DaemonSession Error al actualizar el estado de los usuarios AUTENTICADOS "+usuarios.getUserid());
                    }
                }
                
            } else {
                System.out.println("DaemonSession No existen Usuarios AUTENTICADOS");
            }
        } catch (Exception e) {
            System.out.println("DaemonSession Error al obtener los usuarios AUTENTICADOS");
        }
        
        
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        
    }
    
}

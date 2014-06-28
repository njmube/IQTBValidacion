/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.dao;

import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.interfaces.InterfaceConfiguracionServicio;
import com.iqtb.validacion.pojo.ConfiguracionesServicios;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author danielromero
 */
public class DaoConfiguracionServicios implements InterfaceConfiguracionServicio{

    @Override
    public ConfiguracionesServicios getConfigServicioByIdServicioPropiedad(int idServicio, String propiedad) throws Exception {
        ConfiguracionesServicios configServicio = null;
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            String hql = "from ConfiguracionesServicios where idServicio = :IDSERVICIO and propiedad = :PROPIEDAD";
            Query query = session.createQuery(hql);
            query.setParameter("IDSERVICIO", idServicio);
            query.setParameter("PROPIEDAD", propiedad);
            configServicio = (ConfiguracionesServicios) query.uniqueResult();
        } catch (HibernateException he) {
            session.getTransaction().rollback();
        }finally{
            tx.commit();
            if (session.isOpen()) {
                session.close();
            }
        }
        return configServicio;
    }
    
}

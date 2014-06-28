/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.dao;

import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.interfaces.InterfaceConfiguracionEmpresa;
import com.iqtb.validacion.pojo.ConfiguracionesEmpresas;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author danielromero
 */
public class DaoConfiguracionEmpresa implements InterfaceConfiguracionEmpresa{

    @Override
    public ConfiguracionesEmpresas getConfiguracionEmpresa(int idEmpresa, String propiedad) throws Exception {
        ConfiguracionesEmpresas configEmpresa = null;
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            String hql = "from ConfiguracionesEmpresas where idEmpresa = :ID and propiedad = :PROPIEDAD";
            Query query = session.createQuery(hql);
            query.setParameter("ID", idEmpresa);
            query.setParameter("PROPIEDAD", propiedad);
            
            configEmpresa = (ConfiguracionesEmpresas) query.uniqueResult();
            
        } catch (HibernateException he) {
            session.getTransaction().rollback();
        }finally{
            tx.commit();
            if (session.isOpen()) {
                session.close();
            }
        }
        return configEmpresa;
    }

    @Override
    public boolean updateConfiguracionEmpresa(ConfiguracionesEmpresas configEmpresa) throws Exception {
        boolean update = false;
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.update(configEmpresa);
            update = true;
        } catch (HibernateException he) {
            session.getTransaction().rollback();
        }finally{
            tx.commit();
            if (session.isOpen()) {
                session.close();
            }
        }
        return update;
    }

    
}

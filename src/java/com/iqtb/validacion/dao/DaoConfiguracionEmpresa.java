/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.dao;

import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.interfaces.InterfaceConfiguracionEmpresa;
import com.iqtb.validacion.pojo.ConfiguracionesEmpresas;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author danielromero
 */
public class DaoConfiguracionEmpresa implements InterfaceConfiguracionEmpresa{
    private Logger logger = Logger.getLogger(DaoConfiguracionEmpresa.class);

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
            tx.commit();
        } catch (HibernateException he) {
            logger.error("getConfiguracionEmpresa ERROR: "+he);
            session.getTransaction().rollback();
        }finally{
            
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
            tx.commit();
            update = true;
        } catch (HibernateException he) {
            logger.error("updateConfiguracionEmpresa ERROR: "+he);
            session.getTransaction().rollback();
        }finally{
            
            if (session.isOpen()) {
                session.close();
            }
        }
        return update;
    }

    @Override
    public boolean insertConfiguracionEmpresa(ConfiguracionesEmpresas configEmpresa) throws Exception {
        boolean insert = false;
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.save(configEmpresa);
            tx.commit();
            insert = true;
        } catch (HibernateException he) {
            logger.error("insertConfiguracionEmpresa - ERROR: "+he);
            session.getTransaction().rollback();
        }finally{
            
            if (session.isOpen()) {
                session.close();
            }
        }
        return insert;
    }

    
}

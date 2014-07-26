/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.dao;

import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.interfaces.InterfaceSociosComerciales;
import com.iqtb.validacion.pojo.SociosComerciales;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author danielromero
 */
public class DaoSociosComerciales implements InterfaceSociosComerciales{
private Logger logger = Logger.getLogger(DaoSociosComerciales.class);
    @Override
    public SociosComerciales getSocioComercialByID(Integer idSocioComercial) throws Exception {
        SociosComerciales socioComercial = null;
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            String hql = "from SociosComerciales where idSocioComercial = :ID";
            Query query = session.createQuery(hql);
            query.setParameter("ID", idSocioComercial);
            socioComercial = (SociosComerciales) query.uniqueResult();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("getSocioComercialByID ERROR: "+he);
            session.getTransaction().rollback();
        }finally{
            if (session.isOpen()) {
                session.close();
            }
        }
        return socioComercial;
    }

    @Override
    public List<SociosComerciales> filtroSocioComercialByRFC(String rfc) throws Exception {
        List<SociosComerciales> sc = null;
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            String hql = "from SociosComerciales where rfc like '%:RFC%'";
            Query query = session.createQuery(hql);
            query.setParameter("RFC", rfc);
            sc = query.list();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("filtroSocioComercialByRFC ERROR: "+he);
            session.getTransaction().rollback();
        }finally{
            if (session.isOpen()) {
                session.close();
            }
        }
        return sc;
    }

    @Override
    public List<SociosComerciales> getSociosComercialesByIdEmpresa(Integer idEmpresa) throws Exception {
        List<SociosComerciales> listaSC = null;
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            String hql = "from SociosComerciales where idEmpresa = :IDEMPRESA";
            Query query = session.createQuery(hql);
            query.setParameter("IDEMPRESA", idEmpresa);
            listaSC = query.list();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("getSociosComercialesByIdEmpresa - ERROR: "+he);
            session.getTransaction().rollback();
        }finally{
            if (session.isOpen()) {
                session.close();
            }
        }
        return listaSC;
    }

    @Override
    public boolean insertSocioComercial(SociosComerciales socioComercial) throws Exception {
        boolean insert = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            session.save(socioComercial);
            tx.commit();
            insert = true;
        } catch (HibernateException he) {
            logger.error("insertSocioComercial - ERROR: "+he);
            session.getTransaction().rollback();
        }finally{
            if (session.isOpen()) {
                session.close();
            }
        }
        return insert;
    }

    @Override
    public boolean updateSocioComercial(SociosComerciales socioComercial) throws Exception {
        boolean update = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            session.update(socioComercial);
            tx.commit();
            update = true;
        } catch (HibernateException he) {
            logger.error("updateSocioComercial - ERROR: "+he);
            session.getTransaction().rollback();
        }finally{
            if (session.isOpen()) {
                session.close();
            }
        }
        return update;
    }

    @Override
    public boolean deleteSocioComercial(SociosComerciales socioComercial) throws Exception {
        boolean delete = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            session.delete(socioComercial);
            tx.commit();
            delete = true;
        } catch (HibernateException he) {
            logger.error("deleteSocioComercial - ERROR: "+he);
            session.getTransaction().rollback();
        }finally{
            if (session.isOpen()) {
                session.close();
            }
        }
        return delete;
    }

    @Override
    public SociosComerciales getSocioComercialByIdEmpresaRFC(Integer idEmpresa, String rfc) throws Exception {
        SociosComerciales socioComercial = null;
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            String hql = "from SociosComerciales where idEmpresa = :IDEMPRESA and rfc = :RFC";
            Query query = session.createQuery(hql);
            query.setParameter("IDEMPRESA", idEmpresa);
            query.setParameter("RFC", rfc);
            socioComercial = (SociosComerciales) query.uniqueResult();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("getSocioComercialByIdEmpresaRFC ERROR: "+he);
            session.getTransaction().rollback();
        }finally{
            if (session.isOpen()) {
                session.close();
            }
        }
        return socioComercial;
    }
    
}

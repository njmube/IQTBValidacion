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
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author danielromero
 */
public class DaoSociosComerciales implements InterfaceSociosComerciales{

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
            
        } catch (HibernateException he) {
            session.getTransaction().rollback();
        }finally{
            tx.commit();
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
            
        } catch (HibernateException he) {
            session.getTransaction().rollback();
        }finally{
            tx.commit();
            if (session.isOpen()) {
                session.close();
            }
        }
        return sc;
    }
    
}

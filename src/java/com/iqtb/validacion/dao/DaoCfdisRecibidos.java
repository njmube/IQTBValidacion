/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.dao;

import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.interfaces.InterfaceCfdisRecibidos;
import com.iqtb.validacion.pojo.CfdisRecibidos;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author danielromero
 */
public class DaoCfdisRecibidos implements InterfaceCfdisRecibidos {

    @Override
    public List<CfdisRecibidos> getCfdisByidEmpresa(int idEmpresa) throws Exception {
        List<CfdisRecibidos> listaCFDIs = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            String hql = "from CfdisRecibidos where idEmpresa = :IDEMPRESA";
            Query query = session.createQuery(hql);
            query.setParameter("IDEMPRESA", idEmpresa);
            listaCFDIs = query.list();

            return listaCFDIs;
        } catch (HibernateException he) {
            session.getTransaction().rollback();
        } finally {
            tx.commit();
            if (session.isOpen()) {
                session.close();
            }
        }
        return listaCFDIs;
    }

    @Override
    public boolean delete(CfdisRecibidos cfdi) throws Exception {
        boolean del = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            System.out.println("try Eliminar CFDI");
            session.delete(cfdi);
            del = true;
            System.out.println("delete: "+del);
        } catch (HibernateException he) {
            System.out.println("rollback cfdi");
            session.getTransaction().rollback();
        } finally {
            System.out.println("finally eliminar cfdi");
            tx.commit();
            if (session.isOpen()) {
                session.close();
            }
        }
        return del;
    }

    @Override
    public CfdisRecibidos getCfdiByID(int idCfdi) throws Exception {
        CfdisRecibidos cfdi = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            String hql = "from CfdisRecibidos where idCfdiRecibido = :ID";
            Query query = session.createQuery(hql);
            query.setParameter("ID", idCfdi);
            cfdi = (CfdisRecibidos) query.uniqueResult();
        } catch (HibernateException he) {
            session.getTransaction().rollback();
        }finally{
            tx.commit();
            if (session.isOpen()) {
                session.close();
            }
        }
        return cfdi;
    }

}

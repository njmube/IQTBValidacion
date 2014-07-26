/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.dao;

import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.interfaces.InterfaceRoles;
import com.iqtb.validacion.pojo.Roles;
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
public class DaoRoles implements InterfaceRoles {

    private Logger logger = Logger.getLogger(DaoRoles.class);

    @Override
    public Roles getRolByNombre(String nombre) throws Exception {
        Roles rol = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        try {
            String hql = "from Roles where nombre = :NOMBRE";
            Query query = session.createQuery(hql);
            query.setParameter("NOMBRE", nombre);
            rol = (Roles) query.uniqueResult();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("getRolByNombre ERROR: " + he);
            session.getTransaction().rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return rol;
    }

    @Override
    public Roles getRolById(int idRol) throws Exception {
        Roles rol = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        try {
            String hql = "from Roles where idRol = :IDROL";
            Query query = session.createQuery(hql);
            query.setParameter("IDROL", idRol);
            rol = (Roles) query.uniqueResult();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("getRolById ERROR: " + he);
            session.getTransaction().rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return rol;
    }

    @Override
    public List<Roles> getAllRoles() throws Exception {
        List<Roles> listaRoles = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        try {
            String hql = "from Roles";
            Query query = session.createQuery(hql);
            listaRoles = query.list();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("getAllRoles ERROR: " + he);
            session.getTransaction().rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return listaRoles;
    }

    @Override
    public boolean updateRol(Roles rol) throws Exception {
        boolean update = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            session.update(rol);
            tx.commit();
            update = true;
        } catch (HibernateException he) {
            session.getTransaction().rollback();
            logger.error("updateRol - ERROR " + he);
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return update;
    }

    @Override
    public boolean deleteRol(Roles rol) throws Exception {
        boolean delete = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            session.delete(rol);
            tx.commit();
            delete = true;
        } catch (HibernateException he) {
            session.getTransaction().rollback();
            logger.error("deleteRol - ERROR " + he);
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return delete;
    }

    @Override
    public boolean insertRol(Roles rol) throws Exception {
        boolean insert = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            session.save(rol);
            tx.commit();
            insert = true;
        } catch (HibernateException he) {
            session.getTransaction().rollback();
            logger.error("insertRol - ERROR " + he);
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return insert;
    }

}

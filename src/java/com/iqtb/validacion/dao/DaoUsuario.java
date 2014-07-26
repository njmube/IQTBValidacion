package com.iqtb.validacion.dao;

import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.interfaces.InterfaceUsuario;
import com.iqtb.validacion.pojo.Usuarios;
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
public class DaoUsuario implements InterfaceUsuario {
    private Logger logger = Logger.getLogger(DaoUsuario.class);

    @Override
    public Usuarios getByUserid(String userId) throws Exception {
        Usuarios usuario = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            
            String hql = "from Usuarios where userid = :USUARIO";

            Query query = session.createQuery(hql);
            query.setParameter("USUARIO", userId);
            usuario = (Usuarios) query.uniqueResult();
        } catch (HibernateException he) {
            logger.error("Error getByUserid. ERROR: "+he);
            session.getTransaction().rollback();
        }finally{
            tx.commit();
            if (session.isOpen()) {
                session.close();
            } 
        }
        return usuario;
    }

    @Override
    public boolean updateUsuario(Usuarios usuario) throws Exception {
        boolean update = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = session.beginTransaction();
        
        try {
            session.saveOrUpdate(usuario);
            update = true;
        } catch (HibernateException he) {
            logger.error("Error updateUsuario. ERROR: "+he);
            session.getTransaction().rollback();
        }finally{
            tr.commit();
            if (session.isOpen()) {
                session.close();
            }
        }
        return update;
    }

    @Override
    public List<Usuarios> getUsuariosAutenticados() throws Exception {
        List<Usuarios> listaUsuarios = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            String hql = "from Usuarios where estado = 'AUTENTICADO'";
            Query query = session.createQuery(hql);
            listaUsuarios = query.list();
        } catch (HibernateException he) {
            logger.error("Error getUsuariosAutenticados(). ERROR: "+he);
            session.getTransaction().rollback();
        }finally{
            tx.commit();
            if (session.isOpen()) {
                session.close();
            }
        }
        return listaUsuarios;
    }

    @Override
    public List<Usuarios> getAllUsuarios() throws Exception {
        List<Usuarios> listaUsuarios = null;
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
         try {
            String hql = "from Usuarios";
            Query query = session.createQuery(hql);
            listaUsuarios = query.list();
        } catch (HibernateException he) {
            logger.error("Error getAllUsuarios(). ERROR: "+he);
            session.getTransaction().rollback();
        }finally{
             tx.commit();
             if (session.isOpen()) {
                 session.close();
             }
         }
         return listaUsuarios;
    }

    @Override
    public boolean insertUsuario(Usuarios usuario) throws Exception {
        boolean insert = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = session.beginTransaction();
        
        try {
            logger.info("Antes del save idUsuario "+usuario.getIdUsuario());
            session.save(usuario);
            logger.info("despues del save idUsuario "+usuario.getIdUsuario());
            tr.commit();
            insert = true;
        } catch (HibernateException he) {
            logger.error("insertUsuario. ERROR: "+he);
            session.getTransaction().rollback();
        }finally{
            if (session.isOpen()) {
                session.close();
            }
        }
        return insert;
    }

    @Override
    public boolean deleteUsuario(Usuarios usuario) throws Exception {
        boolean delete = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = session.beginTransaction();
        
        try {
            session.delete(usuario);
            delete = true;
        } catch (HibernateException he) {
            logger.error("Error deleteUsuario. ERROR: "+he);
            session.getTransaction().rollback();
        }finally{
            tr.commit();
            if (session.isOpen()) {
                session.close();
            }
        }
        return delete;
    }

    @Override
    public Usuarios getUsuarioByEmail(String email) throws Exception {
        Usuarios usuario = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            
            String hql = "from Usuarios where email = :EMAIL";

            Query query = session.createQuery(hql);
            query.setParameter("EMAIL", email);
            usuario = (Usuarios) query.uniqueResult();

        } catch (HibernateException he) {
            logger.error("Error getUsuarioByEmail. ERROR: "+he);
            session.getTransaction().rollback();
            throw he;
        }finally{
            tx.commit();
            if (session.isOpen()) {
                session.close();
            } 
        }
        return usuario;
    }

    @Override
    public List<Usuarios> getUsuariosByIdRol(int idRol) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

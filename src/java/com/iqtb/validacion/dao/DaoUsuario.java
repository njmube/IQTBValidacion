package com.iqtb.validacion.dao;

import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.interfaces.InterfaceUsuario;
import com.iqtb.validacion.pojo.Usuarios;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author danielromero
 */
public class DaoUsuario implements InterfaceUsuario {

    @Override
    public Usuarios getByUserid(String userId) throws Exception {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            
            String hql = "from Usuarios where userid = :USUARIO";

            Query query = session.createQuery(hql);
            query.setParameter("USUARIO", userId);
            Usuarios usuario = (Usuarios) query.uniqueResult();

            return usuario;
        } catch (HibernateException he) {
            session.getTransaction().rollback();
            throw he;
        }finally{
            tx.commit();
            if (session.isOpen()) {
                session.close();
            } 
        }

        
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
            session.getTransaction().rollback();;
        }finally{
            tx.commit();
            if (session.isOpen()) {
                session.close();
            }
        }
        return listaUsuarios;
    }

}

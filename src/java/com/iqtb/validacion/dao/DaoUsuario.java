package com.iqtb.validacion.dao;

import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.interfaces.InterfaceUsuario;
import com.iqtb.validacion.pojo.Usuarios;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author danielromero
 */
public class DaoUsuario implements InterfaceUsuario {

    private Session session;
    private Transaction tx;

    @Override
    public Usuarios getByUserid(String userId) throws Exception {

        session = HibernateUtil.getSessionFactory().openSession();
        tx = session.beginTransaction();

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
        //session = HibernateUtil.getSessionFactory().openSession();
        
        Session se = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = se.beginTransaction();
        //tx = session.beginTransaction();
        System.out.println("idusuario "+usuario.getIdUsuario());
        System.out.println("Nombre "+usuario.getNombre());
        System.out.println("passkey "+usuario.getPasskey());
        
        try {
            System.out.println("entro tyr");
            se.saveOrUpdate(usuario);
            System.out.println("session save");
//            se.flush();
            System.out.println("TRUE");
            return true;
        } catch (HibernateException he) {
            se.getTransaction().rollback();
            return false;
        }finally{
            System.out.println("FINALLY");
            tr.commit();
            if (se.isOpen()) {
                se.close();
            }
        }
    }

}

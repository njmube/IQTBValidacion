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
    private Transaction transaction;

    @Override
    public Usuarios getByUserid(String userId) throws Exception {

        session = HibernateUtil.getSessionFactory().openSession();
        transaction = session.beginTransaction();

        try {
            
            String hql = "from Usuarios where userid = :USUARIO";

            Query query = session.createQuery(hql);
            query.setParameter("USUARIO", userId);
            Usuarios usuario = (Usuarios) query.uniqueResult();

            return usuario;
        } catch (HibernateException he) {
            transaction.rollback();
            throw he;
        }finally{
            transaction.commit();
            session.close();
        }

        
    }

}

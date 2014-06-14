/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.dao;

import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.interfaces.InterfaceEmpresa;
import com.iqtb.validacion.pojo.Empresas;
import com.iqtb.validacion.pojo.UsuariosHasEmpresasId;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author danielromero
 */
public class DaoEmpresa implements InterfaceEmpresa {

    @Override
    public List<Empresas> getEmpresaById(Integer idUsuario) throws Exception {
        List<Empresas> listaEmpresas = new ArrayList<Empresas>();
        Empresas empresa;
        List<UsuariosHasEmpresasId> lista = new DaoUsuarioEmpresa().getEmpresasByIdUsuario(idUsuario);

        for (UsuariosHasEmpresasId usuariosHasEmpresasId : lista) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();
            try {
                String hql = "from Empresas where idEmpresa = :IDEMPRESA";
                Query query = session.createQuery(hql);
                query.setParameter("IDEMPRESA", usuariosHasEmpresasId.getIdEmpresa());
                empresa = (Empresas) query.uniqueResult();
                listaEmpresas.add(empresa);
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
        return listaEmpresas;
    }

    @Override
    public Empresas getEmpresaByRFC(String rfc) throws Exception {
        Empresas empresa = null;
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        try {
            String hql = "from Empresas where rfc = :RFC";
            Query query = session.createQuery(hql);
            query.setParameter("RFC", rfc);
            empresa = (Empresas) query.uniqueResult();
        } catch (HibernateException he) {
            session.getTransaction().rollback();
            throw he;
        }finally{
            tx.commit();
            if (session.isOpen()) {
                session.close();
            }
        }
        return empresa;
    }

    @Override
    public boolean updateEmpresa(Empresas empresa) throws Exception {
        boolean update = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            session.saveOrUpdate(empresa);
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

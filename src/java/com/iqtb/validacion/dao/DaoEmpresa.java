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

    private Session session;
    private Transaction tx;

    @Override
    public List<Empresas> getEmpresaById(Integer idUsuario) throws Exception {
        List<Empresas> listaEmpresas = new ArrayList<Empresas>();
        Empresas empresa;
        List<UsuariosHasEmpresasId> lista = new DaoUsuarioEmpresa().getEmpresasByIdUsuario(idUsuario);

        for (UsuariosHasEmpresasId usuariosHasEmpresasId : lista) {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
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
    
//    public static void main(String[] args) throws Exception {
//        List<Empresas> list = new DaoEmpresa().getEmpresaById(1);
//        
//        for (Empresas empresas : list) {
//            System.out.println("Empresa: " + empresas.getRfc());
//        }
//    }

}

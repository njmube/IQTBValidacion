/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.dao;

import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.interfaces.InterfaceUsuarioEmpresa;
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
public class DaoUsuarioEmpresa implements InterfaceUsuarioEmpresa{
    private Session session;
    private Transaction tx;
    
    @Override
    public List<UsuariosHasEmpresasId> getEmpresasByIdUsuario(Integer idUsuario) {
        List<UsuariosHasEmpresasId> uei = null;
        
        session = HibernateUtil.getSessionFactory().openSession();
        tx = session.beginTransaction();
        List<UsuariosHasEmpresasId> listaUsuariosEmpresas = new ArrayList<UsuariosHasEmpresasId>();
        try {
            String hql = "select id from UsuariosHasEmpresas";
            Query query = session.createQuery(hql);
            uei =  query.list();
            
            for (UsuariosHasEmpresasId usuariosHasEmpresas : uei) {
                if (usuariosHasEmpresas.getIdUsuario() == idUsuario) {
                    listaUsuariosEmpresas.add(usuariosHasEmpresas);
                }
            }
            
            for (UsuariosHasEmpresasId usuariosEmpresasId : listaUsuariosEmpresas) {
                System.out.println("Empresa: "+usuariosEmpresasId.getIdEmpresa());
            }
        } catch (HibernateException he) {
            session.getTransaction().rollback();
            throw he;
        }finally{
            tx.commit();
            if (session.isOpen()) {
                session.close();
            }
        }
        return listaUsuariosEmpresas;
    }
    
}

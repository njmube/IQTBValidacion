/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.dao;

import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.interfaces.InterfaceServicio;
import com.iqtb.validacion.pojo.Servicios;
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
public class DaoServicio implements InterfaceServicio{
    private Logger logger = Logger.getLogger(DaoServicio.class);

    @Override
    public Servicios getServicoByNombre(String nombre) throws Exception{
        Servicios servicio = null;
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            String hql = "from Servicios where nombre = :NOMBRE";
            Query query = session.createQuery(hql);
            query.setParameter("NOMBRE", nombre);
            servicio = (Servicios) query.uniqueResult();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("Error al obtener el servicio. ERROR: "+he);
            session.getTransaction().rollback();
        }finally{
            if (session.isOpen()) {
               session.close();
            }
        }
        return servicio;
    }
    
    public Integer verIdServicio(){ 
        List<Integer> listConfig;
        Integer valor = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        try{
            //Realiza la consulta en la base de datos con la tabla de CONFIGURACIONES_EMPRESAS 
            String hql = "SELECT idServicio FROM Servicios where nombre= 'VALIDADOR'";
            Query q = session.createQuery(hql);
            listConfig = q.list();
            if(listConfig!=null&&listConfig.size()>0)
                valor = listConfig.get(0);
            logger.info("Se obtuvo el id del servicio ");
        }catch(HibernateException e){
            logger.error("No se puede obtener el id del servicio "+e.getMessage());
        }finally{
            tx.commit();
            session.close();
        }
        return valor;
    }

    @Override
    public boolean updateServicio(Servicios servicio) throws Exception {
        boolean update = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            session.saveOrUpdate(servicio);
            tx.commit();
            update = true;
        } catch (HibernateException he) {
            logger.error("updateServicio - ERROR: "+he);
            session.getTransaction().rollback();
        }finally{
            if (session.isOpen()) {
               session.close();
            }
        }
        return update;
    }
    
}

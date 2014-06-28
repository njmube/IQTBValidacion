/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoPlantilla;
import com.iqtb.validacion.pojo.Empresas;
import com.iqtb.validacion.pojo.Plantillas;
import com.iqtb.validacion.pojo.Usuarios;
import com.iqtb.validacion.util.Imagen;
import com.iqtb.validacion.util.ManejoArchivos;
import com.iqtb.validacion.util.Subreporte;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;

/**
 *
 * @author danielromero
 */
@ManagedBean
@RequestScoped
public class MbPlantilla {

    private Plantillas plantilla;
    private List<Plantillas> listaPlantillas;
    private List<Plantillas> plantillasSeleccionadas;
    private String destino = "/Users/danielromero/NetBeansProjects/IQTBValidacion/Plantillas/tmp/";
    private String carpeta = "";
    private String principal = "";
    private List<Imagen> listaImagenes;
    private Subreporte subreporteSeleccionado;
    private List<Subreporte> listaSubreportes;
    private Empresas empresa;
    private final String sessionUsuario;
    private FacesMessage msg;
    
    public MbPlantilla() {
        this.plantilla = new Plantillas();
        this.listaPlantillas = new ArrayList<Plantillas>();
        this.plantillasSeleccionadas = new ArrayList<Plantillas>();
        this.listaImagenes = new  ArrayList<Imagen>();
        this.subreporteSeleccionado = new Subreporte();
        this.sessionUsuario = ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid();
    }

    public Plantillas getPlantilla() {
        return plantilla;
    }

    public void setPlantilla(Plantillas plantilla) {
        this.plantilla = plantilla;
    }

    public List<Plantillas> getListaPlantillas() throws Exception {
        this.listaPlantillas = new DaoPlantilla().listaPlantillas();
        return listaPlantillas;
    }

    public void setListaPlantillas(List<Plantillas> listaPlantillas) {
        this.listaPlantillas = listaPlantillas;
    }

    public List<Plantillas> getPlantillasSeleccionadas() {
        return plantillasSeleccionadas;
    }

    public void setPlantillasSeleccionadas(List<Plantillas> plantillasSeleccionadas) {
        this.plantillasSeleccionadas = plantillasSeleccionadas;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public List<Subreporte> getListaSubreportes() {
        return listaSubreportes;
    }

    public void setListaSubreportes(List<Subreporte> listaSubreportes) {
        this.listaSubreportes = listaSubreportes;
    }

    public Subreporte getSubreporteSeleccionado() {
        return subreporteSeleccionado;
    }

    public void setSubreporteSeleccionado(Subreporte subreporteSeleccionado) {
        this.subreporteSeleccionado = subreporteSeleccionado;
    }

    public List<Imagen> getListaImagenes() {
        return listaImagenes;
    }

    public void setListaImagenes(List<Imagen> listaImagenes) {
        this.listaImagenes = listaImagenes;
    }
    
    public void resetPlantilla(){
        this.carpeta = "";
        this.principal = "";
        this.listaImagenes = new ArrayList<Imagen>();
        this.listaSubreportes = new ArrayList<Subreporte>();
        
    }
    
    public String onFlowProcess(FlowEvent event) {
        if ("Subreportes".equals(event.getNewStep())) {
            if ((this.principal == null) || (this.principal.isEmpty()) || (principal.length() <= 0)) {
                this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaucion", "Selecciona un reporte Principal");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return event.getOldStep();
            } 
        }
        if ("updateSubreportes".equals(event.getNewStep())) {
            if ((principal == null) || (principal.isEmpty()) || (principal.length() <= 0)) {
                this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaucion", "Selecciona un reporte Principal");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return event.getOldStep();
            }
        }
        if ("confirmacion".equals(event.getNewStep())) {
            for (Imagen ima : listaImagenes) {
                if (ima.getEstado().equals("Sin subir")) {
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaucion", "Aun no ha seleccionado todas la imagenes");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    return event.getOldStep();
                }
            }
            
            this.listaSubreportes = new ArrayList<Subreporte>();
            ManejoArchivos ma = new ManejoArchivos();
            List<String> listaSub = ma.getListaSubreportes(destino + sessionUsuario + "/" + carpeta);

            for (String string : listaSub) {
                Subreporte subreprote = new Subreporte();
                subreprote.setNombre(string);
                listaSubreportes.add(subreprote);
            }
        }
        if ("updateConfirmacion".equals(event.getNewStep())) {
            for (Imagen ima : listaImagenes) {
                if (ima.getEstado().equals("Sin subir")) {
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaucion", "Aun no han cargado todas la imagenes");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    return event.getOldStep();
                }
            }
            
            this.listaSubreportes = new ArrayList<Subreporte>();
            ManejoArchivos ma = new ManejoArchivos();
            List<String> listaSub = ma.getListaSubreportes(destino + sessionUsuario + "/" + carpeta);

            for (String string : listaSub) {
                Subreporte subr = new Subreporte();
                subr.setNombre(string);
                listaSubreportes.add(subr);
            }
        }
        return event.getNewStep();
    }
    
    public void uploadPrincipal(FileUploadEvent event) {
        ManejoArchivos ma = new ManejoArchivos();
        ma.borrarDirectorio(new File(destino + sessionUsuario));

        this.carpeta = ma.quirtarExtension(event.getFile().getFileName()); //asignamos el nombre de la carpeta con el nombre de la plantilla

        File direccion = new File(destino + sessionUsuario + "/" + carpeta);
        boolean ban = direccion.mkdirs(); // creamos la ruta temporal de la plantilla
        if (ban) {
            System.out.println(" - metodo_uploadPrincipal MBPlantillas ] Se creo correctamente el directorio de la plantilla: " + direccion.getAbsolutePath());
//            this.log.info("[ " + ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUsuario() + " - metodo_uploadPrincipal MBPlantillas ] Se creo correctamente el directorio de la plantilla: " + direccion.getAbsolutePath());
            InputStream in = null;
            try {
                in = event.getFile().getInputstream();
            } catch (IOException ex) {
                System.out.println(" - metodo_uploadPrincipal MBPlantillas ] Error al cargar la plantilla " + ex.getMessage());
//                this.log.error("[ " + ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUsuario() + " - metodo_uploadPrincipal MBPlantillas ] Error al cargar la plantilla " + ex.getMessage());
            }
            try {
                OutputStream out = new FileOutputStream(destino + sessionUsuario + "/" + carpeta + "/main.xprint");
                int reader = 0;
                byte[] bytes = new byte[(int) event.getFile().getSize()];
                while ((reader = in.read(bytes)) != -1) {
                    out.write(bytes, 0, reader);
                }
                in.close();
                out.flush();
                out.close();
                File f2 = new File(destino + sessionUsuario + "/" + carpeta + "/main.xprint");
                if (f2.isFile()) {
                    System.out.println(sessionUsuario+" - metodo_uploadPrincipal MBPlantillas ] Ruta del reporte Principal: " + f2.getAbsolutePath());
//                    this.log.info("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Ruta del reporte Principal: " + f2.getAbsolutePath());
                    boolean compila = ma.compilarPlantilla(f2.getAbsolutePath());
                    if (compila) {
                        listaSubreportes.clear();// Se limpia la lista de subreportes
                        System.out.println(sessionUsuario+" - metodo_uploadPrincipal MBPlantillas ] Se compilo corractamente el reporte principal: " + f2.getName());
//                        this.log.info("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Se compilo corractamente el reporte principal: " + f2.getName());
                        this.principal = event.getFile().getFileName(); //asignamos el nombre de la planilla
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "la plantilla: " + event.getFile().getFileName() + " a sido subida y compilada");
                        try {
                            this.listaImagenes = ma.ListaImagenes(destino + sessionUsuario + "/" + carpeta + "/main.xprint");
//                            this.listaImagenes = ma.listaImagenes(destino + sessionUsuario + "/" + carpeta + "/main.xprint");
                            for (Imagen ima : listaImagenes) {
                                System.out.println(sessionUsuario+" - metodo_uploadPrincipal MBPlantillas ] Imagen de reporte principal: " + ima.getNombre());
//                                this.log.info("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Imagen de reporte principal: " + ima.getNombre());
                            }
                        } catch (Exception e) {
                            System.out.println(sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al obtener la imagenes del reporte principal");
//                            this.log.error("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al obtener la imagenes del reporte principal");
                        }
                    } else {
                        System.out.println(sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al compilar la plantillas: " + f2.getName());
//                        this.log.error("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al compilar la plantillas: " + f2.getName());
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "la plantilla: " + event.getFile().getFileName() + " no ha sido compilada");
                    }
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            } catch (Exception e) {
                System.out.println(sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al cargar la plantilla " + e.getMessage());
//                this.log.error("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al cargar la plantilla " + e.getMessage());
            }
        } else {
            System.out.println(sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al crear el directorio de la plantilla: " + direccion.getAbsolutePath());
//            this.log.error("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al crear el directorio de la plantilla: " + direccion.getAbsolutePath());
        }
    }
    
    public void asignarReportePrincipalPNulo() {
        if (this.principal.length() > 0) {
            this.principal = "";
            this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se elimino satisfactoriamente el Reporte Principal");
        } else {
        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Primero seleccione un Reporte Principal");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public String eliminarPlantilla() {
        boolean bandera;

        for (Plantillas plantillas : this.plantillasSeleccionadas) {
            this.plantilla = plantillas;


            try {
                DaoPlantilla daoPlantillas = new DaoPlantilla();
                bandera = daoPlantillas.deletePlantilla(this.plantilla);
                if (bandera) {
//                    this.log.info("[ " + sessionUsuario + " - metodo_eliminarPlantilla MBPlantillas ] Se elimino correctamente el registro de plantilla en la BD: " + this.plantilla.getNombre());
                    String rutaPlantilla = this.plantilla.getRuta();
                    File rutaZip = new File(rutaPlantilla);
                    boolean eliminaZip = rutaZip.delete();
                    if (eliminaZip) {
                        System.out.println("- metodo_eliminarPlantilla MBPlantillas ] Se elimino correctamente el archivo de plantilla, ruta: " + rutaZip.getAbsolutePath());
//                        this.log.info("[ " + sessionUsuario + " - metodo_eliminarPlantilla MBPlantillas ] Se elimino correctamente el archivo de plantilla, ruta: " + rutaZip.getAbsolutePath());
                    } else {
                        System.out.println(" - metodo_eliminarPlantilla MBPlantillas ] Erro al eliminar el archivo de la plantilla, ruta: " + rutaZip.getAbsolutePath());
//                        this.log.error("[ " + sessionUsuario + " - metodo_eliminarPlantilla MBPlantillas ] Erro al eliminar el archivo de la plantilla, ruta: " + rutaZip.getAbsolutePath());
                    }
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se elimino la plantilla: " + this.plantilla.getNombre());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                } else {
                    System.out.println(" - metodo_eliminarPlantilla MBPlantillas ] Error al eliminar registro de plantilla en la BD: " + this.plantilla.getNombre());
//                    this.log.error("[ " + sessionUsuario + " - metodo_eliminarPlantilla MBPlantillas ] Error al eliminar registro de plantilla en la BD: " + this.plantilla.getNombre());
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "no se pudo eliminar la plantilla: " + this.plantilla.getNombre());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            } catch (Exception ex) {
                System.out.println(" - metodo_eliminarPlantilla MBPlantillas ] Error al eliminar registro de plantilla en la BD: " + this.plantilla.getNombre());
//                this.log.error("[ " + sessionUsuario + " - metodo_eliminarPlantilla MBPlantillas ] Error al eliminar registro de plantilla en la BD: " + this.plantilla.getNombre());
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "no se pudo eliminar la plantilla: " + this.plantilla.getNombre());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }

//        this.plantilla.setIdPlantillas(null);
//        this.plantilla.setNombre(null);
//        this.plantilla.setRuta(null);
//        this.plantilla.setSha1(null);
        this.plantilla = new Plantillas();
        return "/Configuracion/plantilla?faces-redirect=true";
    }
    
    public void uploadSubreportes(FileUploadEvent event) {
        InputStream in = null;

        if (!event.getFile().getFileName().equals(principal)) {
            try {
                in = event.getFile().getInputstream();
            } catch (IOException ex) {
                System.out.println("sessionUsuario + \" - metodo_uploadSubreportes MBPlantillas ] Error al cargar el subreporte \" + ex.getMessage()");
//                this.log.error("[ " + sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Error al cargar el subreporte " + ex.getMessage());
            }
            try {
                OutputStream out = new FileOutputStream(new File(destino + sessionUsuario + "/" + carpeta + "/" + event.getFile().getFileName()));
                int reader = 0;
                byte[] bytes = new byte[(int) event.getFile().getSize()];
                while ((reader = in.read(bytes)) != -1) {
                    out.write(bytes, 0, reader);
                }
                in.close();
                out.flush();
                out.close();
                ManejoArchivos ma = new ManejoArchivos();
                File subRepor = new File(destino + sessionUsuario + "/" + carpeta + "/" + event.getFile().getFileName());
                System.out.println(sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Ruta del subreporte " + subRepor.getAbsolutePath());
//                this.log.info("[ " + sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Ruta del subreporte " + subRepor.getAbsolutePath());
                if (subRepor.isFile()) {

                    boolean compila = ma.compilarPlantilla(subRepor.getAbsolutePath());
                    if (compila) {
                        System.out.println(sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Se compilo corractamente el subreporte: " + subRepor.getName());
//                        this.log.info("[ " + sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Se compilo corractamente el subreporte: " + subRepor.getName());
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", event.getFile().getFileName() + " a sido subido y compilado");

                        listarSubreportes(); //metodo para obtener los subreporte

                        try {
                            List<Imagen> listaImaSub = ma.ListaImagenes(destino + sessionUsuario + "/" + carpeta + "/" + event.getFile().getFileName());
//                            List<Imagen> listaImaSub = ma.listaImagenes(destino + sessionUsuario + "/" + carpeta + "/" + event.getFile().getFileName());
                            for (Imagen imagen1 : listaImaSub) {
                                System.out.println(sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Imagen de subreporte: " + imagen1.getNombre());
//                                this.log.info("[ " + sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Imagen de subreporte: " + imagen1.getNombre());
                                this.listaImagenes.add(imagen1);
                            }
                        } catch (IOException ex) {
                            System.out.println(sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Error al obtener las imagenes del subreporte ");
//                            this.log.error("[ " + sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Error al obtener las imagenes del subreporte ");
                        }
                    } else {
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Subreporte: ", event.getFile().getFileName() + " no ha sido cargado correctamente y no pudo compilar");
                    }
                }
            } catch (Exception e) {
                System.out.println(sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Error al cargar el subreporte ");
//                this.log.error("[ " + sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Error al cargar el subreporte ");
            }
        } else {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precausion ", "Estas tratando de subir un subreporte con el mismo nombre del reporte principal: " + event.getFile().getFileName());
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public void listarSubreportes() {
        ManejoArchivos ma = new ManejoArchivos();
        List<String> lista = ma.getListaSubreportes(destino + sessionUsuario + "/" + carpeta + "/");
        listaSubreportes.clear();
        for (String string : lista) {
            Subreporte sub = new Subreporte();
            sub.setNombre(string);
            listaSubreportes.add(sub);
        }
    }
    
    public void borrarSubreporte() {
        ManejoArchivos ma = new ManejoArchivos();
        System.out.println("subreporte a borrar: " + subreporteSeleccionado.getNombre());

        List<Imagen> listIma = null;
        try {
            listIma = ma.ListaImagenes(destino + sessionUsuario + "/" + carpeta + "/" + subreporteSeleccionado.getNombre());
//            listIma = ma.listaImagenes(destino + sessionUsuario + "/" + carpeta + "/" + subreporteSeleccionado.getNombre());
            for (Imagen imagen1 : listIma) {
                System.out.println("Imagen a borrar: " + imagen1.getNombre());
            }
        } catch (IOException ex) {
            ex.getMessage();
        }

        boolean correcto = ma.eliminaArchivo(destino + sessionUsuario + "/" + carpeta + "/" + subreporteSeleccionado.getNombre());

        if (correcto) {
            for (Imagen imagen1 : listIma) {
                boolean ban = listaImagenes.remove(imagen1);
                System.out.println("Se elimino coprrectamente la imagen: " + imagen1.getNombre());
            }

            this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se elimino stisfactoriamente la sucursal");
            listarSubreportes();
        } else {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar la sucursal");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
//        return "/Plantillas/plantilla";
    }
    
    public void uploadImagenes(FileUploadEvent event) {
        Boolean ban = false;
        Integer pos = null;
        int totalImagenes = listaImagenes.size();
        int contador = 0;
        for (Imagen ima1 : listaImagenes) {
            if (ima1.getNombre().equals(event.getFile().getFileName())) {
                pos = listaImagenes.indexOf(ima1);
                ban = true;
            }
            if (ima1.getEstado().equals("Subido")) {
                contador++;
            }
        }
        if (contador >= totalImagenes) {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Se han cargador todas la imagenes en la tabla", null);
        } else {
            if (!ban) {
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Imagen: " + event.getFile().getFileName() + " no se encuentra en la tabla iamagenes");
            } else {
                Imagen ima1 = listaImagenes.get(pos);
                String folder = ima1.getCarpeta();

                File f1 = new File(destino + sessionUsuario + "/" + carpeta + "/" + folder);
                f1.mkdirs();
                InputStream in = null;
                try {
                    in = event.getFile().getInputstream();
                } catch (IOException ex) {
                    System.out.println(sessionUsuario + " - metodo_uploadImagenes MBPlantillas ] Error al cargar la imagen: " + ex.getMessage());
//                    this.log.error("[ " + sessionUsuario + " - metodo_uploadImagenes MBPlantillas ] Error al cargar la imagen: " + ex.getMessage());
                }
                try {
                    OutputStream out = new FileOutputStream(new File(destino + sessionUsuario + "/" + carpeta + "/" + folder + "/" + event.getFile().getFileName()));
                    int reader = 0;
                    byte[] bytes = new byte[(int) event.getFile().getSize()];
                    while ((reader = in.read(bytes)) != -1) {
                        out.write(bytes, 0, reader);
                    }
                    in.close();
                    out.flush();
                    out.close();
                    ima1.setEstado("Subido");
                    this.msg = new FacesMessage("Imagen: ", event.getFile().getFileName() + " a sido cargado correctamente.");
                } catch (Exception e) {
                    System.out.println(sessionUsuario + " - metodo_uploadImagenes MBPlantillas ] Error al cargar la imagen: " + e.getMessage());
//                    this.log.error("[ " + sessionUsuario + " - metodo_uploadImagenes MBPlantillas ] Error al cargar la imagen: " + e.getMessage());
                }
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public String guardarPlantilla() {
        ManejoArchivos ma = new ManejoArchivos();
        Plantillas p = new Plantillas();
        File rutaZip = new File("/Users/danielromero/NetBeansProjects/IQTBValidacion/Plantillas/zips/");
        rutaZip.mkdirs();
        try {
            
//            this.log.info("[ " + sessionUsuario + " - metodo_guardarPlantilla MBPlantillas ] Series seleccionadas: " + serie);
            File archivoZip = new File("/Users/danielromero/NetBeansProjects/IQTBValidacion/Plantillas/zips/" + sessionUsuario+ ".zip");
            ManejoArchivos.makeZip(new File(destino + sessionUsuario + "/" + carpeta), archivoZip.getAbsolutePath(), carpeta.length() + 2);

            String sumaSha1 = ma.calculaSHA1(archivoZip.getAbsolutePath());

            p.setIdEmpresa(1);
            p.setNombre(sessionUsuario + ".zip");
            p.setRuta(archivoZip.getAbsolutePath());
            p.setSha1(sumaSha1);

            System.out.println(sessionUsuario + " - metodo_guardarPlantilla MBPlantillas ] Plantilla a insertar, nombre: " + p.getNombre() + ", ruta: " + p.getRuta() + ", sha1: " + p.getSha1());
//            this.log.info("[ " + sessionUsuario + " - metodo_guardarPlantilla MBPlantillas ] Plantilla a insertar, nombre: " + p.getNombre() + ", ruta: " + p.getRuta() + ", sha1: " + p.getSha1());
            boolean ban = new DaoPlantilla().insertPlantilla(p);
            Plantillas plantilla1;
            if (ban) {
                System.out.println(sessionUsuario + " - metodo_guardarPlantilla MBPlantillas ] Se inserto correctamente la plantilla en la BD: " + archivoZip.getAbsolutePath());
//                
            } else {
                System.out.println(sessionUsuario + " - metodo_guardaPlantilla MBPlantillas ] No se inserto la plantilla en la BD: " + p.getNombre());
//                this.log.info("[ " + sessionUsuario + " - metodo_guardaPlantilla MBPlantillas ] No se inserto la plantilla en la BD: " + p.getNombre());
            }
        } catch (IOException ex) {
            System.out.println(" - metodo_guardarPlantilla MBPlantillas ] Error al crear el zip: " + ex.getMessage());
//            this.log.error("[ " + ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUsuario() + " - metodo_guardarPlantilla MBPlantillas ] Error al crear el zip: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println(" - metodo_guardarPlantilla MBPlantillas ] Error al crear el zip: " + ex.getMessage());
//            this.log.error("[ " + ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUsuario() + " - metodo_guardarPlantilla MBPlantillas ] Error al crear el zip: " + ex.getMessage());
        }

        this.msg = new FacesMessage("Succefull ", " Se ha subido correctamento la plantilla: " + p.getNombre());
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return "plantilla";
    }
    
    public String existeSeleccionPlantilla() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();

        String parametro = (String) params.get("nombreParametro");
        System.out.println("parametro: " + parametro);

        boolean estadoPlantilla = false;
        if (this.plantillasSeleccionadas.isEmpty()) {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaucion", "Seleccione una platilla");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            if (parametro != null) {
                if (parametro.equals("eliminar")) {
                    estadoPlantilla = true;
                }
            } else if (this.plantillasSeleccionadas.size() > 1) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaucion", "Seleccione solo una plantilla");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                estadoPlantilla = true;
                for (Plantillas plantillas : plantillasSeleccionadas) {
                    this.plantilla = plantillas;
                }
            }
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("estadoPlantilla", estadoPlantilla);
        return "/Configuracion/plantilla?faces-redirect=true";
    }
    
}

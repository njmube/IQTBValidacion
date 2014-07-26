/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoEmpresa;
import com.iqtb.validacion.dao.DaoPlantilla;
import com.iqtb.validacion.dao.DaoUsuario;
import com.iqtb.validacion.pojo.Empresas;
import com.iqtb.validacion.pojo.Plantillas;
import com.iqtb.validacion.pojo.Usuarios;
import com.iqtb.validacion.util.Imagen;
import com.iqtb.validacion.util.ImprimirPlantilla;
import com.iqtb.validacion.util.ManejoArchivos;
import com.iqtb.validacion.util.Subreporte;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author danielromero
 */
@ManagedBean
@SessionScoped
public class MbPlantilla implements Serializable {

    private UploadedFile file;
    private UploadedFile subReporte;
    private UploadedFile fileImagen;
    private String fileNombre;
    private boolean enableSubreporte;
    private boolean enableImagenes;
    private boolean enableSave;
//    private FileUploadEvent event;
    private Plantillas plantilla;
    private List<Plantillas> listaPlantillas;
    private List<ImprimirPlantilla> plantillasSeleccionadas; // /xsa/tomcat/webapps/ROOT/iqtb/Plantillas/tmp/
    private final String destino = "/xsa/tomcat/webapps/ROOT/iqtb/Plantillas/tmp/"; // /Users/danielromero/NetBeansProjects/Validacion/Plantillas/tmp/
    private String carpeta = "";
    private String principal = "";
    private String nombrePlantilla;
    private List<Imagen> listaImagenes;
    private Subreporte subreporteSeleccionado;
    private List<Subreporte> listaSubreportes;
    private Usuarios usuario;
    private Empresas empresa;
    private boolean existePlantilla;
    private final String sessionUsuario;
    private FacesMessage msg;
    private static final Logger logger = Logger.getLogger(MbPlantilla.class);

    public MbPlantilla() {
        this.plantilla = new Plantillas();
        this.listaPlantillas = new ArrayList<Plantillas>();
        this.plantillasSeleccionadas = new ArrayList<ImprimirPlantilla>();
        this.listaImagenes = new ArrayList<Imagen>();
        this.subreporteSeleccionado = new Subreporte();
        this.listaSubreportes = new ArrayList<Subreporte>();
        enableSubreporte = false;
        enableImagenes = false;
        enableSave = false;
        File f1 = new File(destino);
        if (!f1.isDirectory()) {
            f1.mkdirs();
        }

        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        String empresaSeleccionada = (String) httpServletRequest.getSession().getAttribute("empresaSeleccionada");
        this.sessionUsuario = ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid();
        try {
            usuario = new DaoUsuario().getByUserid(this.sessionUsuario);
            empresa = new DaoEmpresa().getEmpresaByRFC(empresaSeleccionada);
        } catch (Exception e) {
            logger.error("Error al obtener USUARIO y EMPRESA. ERROR: " + e);
        }

    }

    public Plantillas getPlantilla() {
        return plantilla;
    }

    public void setPlantilla(Plantillas plantilla) {
        this.plantilla = plantilla;
    }

    public List<ImprimirPlantilla> getListaPlantillas() {
        List<ImprimirPlantilla> imprimirPlantilla = new ArrayList<ImprimirPlantilla>();
        try {
            this.listaPlantillas = new DaoPlantilla().listaPlantillasByIdEmpresa(empresa.getIdEmpresa());
            if (this.listaPlantillas == null || this.listaPlantillas.size() <= 0) {
                logger.info("getListaPlantillas - No existen Plantillas para mostrar");
                return imprimirPlantilla;
            }
        } catch (Exception ex) {
            System.out.println("getListaPlantillas - ERROR " + ex);
        }
        for (Plantillas plantilla : listaPlantillas) {
            ImprimirPlantilla iPlantilla = new ImprimirPlantilla();
            iPlantilla.setIdPlantillas(plantilla.getIdPlantillas());
            iPlantilla.setIdEmpresa(plantilla.getIdEmpresa());
            iPlantilla.setNombre(plantilla.getNombre());
            iPlantilla.setRuta(plantilla.getRuta());
            iPlantilla.setSha1(plantilla.getSha1());
            iPlantilla.setRfcEmpresa(empresaById(plantilla.getIdEmpresa()));
            imprimirPlantilla.add(iPlantilla);
        }
        return imprimirPlantilla;
    }

    public void setListaPlantillas(List<Plantillas> listaPlantillas) {
        this.listaPlantillas = listaPlantillas;
    }

    public List<ImprimirPlantilla> getPlantillasSeleccionadas() {
        return plantillasSeleccionadas;
    }

    public void setPlantillasSeleccionadas(List<ImprimirPlantilla> plantillasSeleccionadas) {
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

    public String getNombrePlantilla() {
        return nombrePlantilla;
    }

    public void setNombrePlantilla(String nombrePlantilla) {
        this.nombrePlantilla = nombrePlantilla;
    }

    public boolean isExistePlantilla() {
        existePlantilla = existePlantilla();
        return existePlantilla;
    }

    public void setExistePlantilla(boolean existePlantilla) {
        this.existePlantilla = existePlantilla;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        FacesMessage message = new FacesMessage("setFile", " Se coloco un archivo a la variale File "+file.getFileName());
        FacesContext.getCurrentInstance().addMessage(null, message);
        this.file = file;
    }

    public String getFileNombre() {
        return fileNombre;
    }

    public void setFileNombre(String fileNombre) {
        this.fileNombre = fileNombre;
    }

    public boolean isEnableSubreporte() {
        return enableSubreporte;
    }

    public void setEnableSubreporte(boolean enableSubreporte) {
        this.enableSubreporte = enableSubreporte;
    }

    public UploadedFile getSubReporte() {
        return subReporte;
    }

    public void setSubReporte(UploadedFile subReporte) {
        this.subReporte = subReporte;
    }

    public boolean isEnableImagenes() {
        return enableImagenes;
    }

    public void setEnableImagenes(boolean enableImagenes) {
        this.enableImagenes = enableImagenes;
    }

    public UploadedFile getFileImagen() {
        return fileImagen;
    }

    public void setFileImagen(UploadedFile fileImagen) {
        this.fileImagen = fileImagen;
    }

    public boolean isEnableSave() {
        return enableSave;
    }

    public void setEnableSave(boolean enableSave) {
        this.enableSave = enableSave;
    }

    public void resetPlantilla() {
        this.carpeta = "";
        this.principal = "";
        this.listaImagenes = new ArrayList<Imagen>();
        this.listaSubreportes = new ArrayList<Subreporte>();
        ManejoArchivos managed = new ManejoArchivos();
        managed.eliminar(destino);

    }

    public String irCargarPlantilla() {
        return "/principal?faces-redirect=true";
    }

//    public void probando() {
//        logger.info("Inicia Probando");
//        new ManejoArchivos().eliminar(destino + empresa.getRfc());
//        logger.info("Se elimino la ruta " + destino + empresa.getRfc());
//        File carpetas = new File(destino + empresa.getRfc() + "/Pruebas/");
//        if (carpetas.mkdirs()) {
//            File archivo = new File(destino + empresa.getRfc() + "/Pruebas/archivo.txt");
//            try {
//                FileWriter escribir = new FileWriter(archivo);
//                escribir.write("ESTE texto es de prueba 123...");
//                escribir.close();
//                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Archivo creado correctamente");
//            } catch (IOException ex) {
//                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "probadno - ERROR " + ex);
//                logger.error("probadno - ERROR " + ex);
//            }
//        } else {
//            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al crear la carpeta");
//            logger.error("Error al crear la carpeta " + carpetas.getAbsolutePath());
//        }
//        FacesContext.getCurrentInstance().addMessage(null, msg);
//    }

    public void upload() {
        logger.info("Inicia carga de Reporte Principal");
        logger.info("Empresa " + empresa.getRfc());
        ManejoArchivos manager = new ManejoArchivos();

        if (file != null) {
            fileNombre = file.getFileName();
            logger.info("Nombre archivo " + file.getFileName());
            manager.eliminar(destino);

            this.carpeta = manager.quirtarExtension(file.getFileName()); //asignamos el nombre de la carpeta con el nombre de la plantilla
            logger.info("carpeta " + carpeta);
            logger.info("destino " + destino);
            logger.info("Nombre carpeta " + destino + empresa.getRfc() + "/" + carpeta + "/");
            File direccion = new File(destino + empresa.getRfc() + "/" + carpeta + "/");

            boolean ban = direccion.mkdirs(); // creamos la ruta temporal de la plantilla
            if (ban) {
                logger.info("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Se creo correctamente el directorio de la plantilla: " + direccion.getAbsolutePath());
                InputStream in = null;
                try {
                    in = file.getInputstream();
                } catch (IOException ex) {
                    logger.error("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al cargar la plantilla " + ex);
                }
                try {
                    OutputStream out = new FileOutputStream(destino + empresa.getRfc() + "/" + carpeta + "/main.xprint");
                    int reader = 0;
                    byte[] bytes = new byte[(int) file.getSize()];
                    while ((reader = in.read(bytes)) != -1) {
                        out.write(bytes, 0, reader);
                    }
                    in.close();
                    out.flush();
                    out.close();
                    File f2 = new File(destino + empresa.getRfc() + "/" + carpeta + "/main.xprint");
                    if (f2.isFile()) {
                        logger.info("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Ruta del reporte Principal: " + f2.getAbsolutePath());
                        boolean compila = manager.compilarPlantilla(f2.getAbsolutePath());
                        if (compila) {
                            listaSubreportes.clear();// Se limpia la lista de subreportes
                            logger.info("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Se compilo corractamente el reporte principal: " + f2.getName());
                            this.principal = file.getFileName(); //asignamos el nombre de la planilla
                            this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La plantilla: " + file.getFileName() + " a sido cargada y compilada");
                            enableSubreporte = true;
                            try {
                                this.listaImagenes = manager.ListaImagenes(destino + empresa.getRfc() + "/" + carpeta + "/main.xprint");
//                            this.listaImagenes = ma.listaImagenes(destino + sessionUsuario + "/" + carpeta + "/main.xprint");
                                for (Imagen ima : listaImagenes) {
                                    logger.info("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Imagen de reporte principal: " + ima.getNombre());
                                }
                            } catch (IOException e) {
                                System.out.println(sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al obtener la imagenes del reporte principal");
                                logger.error("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al obtener la imagenes del reporte principal");
                            }
                        } else {
                            System.out.println(sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al compilar la plantillas: " + f2.getName());
                            logger.error("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al compilar la plantillas: " + f2.getName());
                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La plantilla: " + file.getFileName() + " no ha sido compilada");
                        }
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                    }
                } catch (IOException e) {
                    logger.error("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al cargar la plantilla " + e);
                }
            } else {
                System.out.println(sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al crear el directorio de la plantilla: " + direccion.getAbsolutePath());
                logger.error("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al crear el directorio de la plantilla: " + direccion.getAbsolutePath());
            }
        } else {
            FacesMessage message = new FacesMessage("Error", " Ha ocurrido un error al cargar el archivo");
            FacesContext.getCurrentInstance().addMessage(null, message);
            logger.error("upload - Error al cargar el archvio File es null");
        }
    }

    public void uploadSubreporte() {
        logger.info("Inicia carga de Subreporte");
        logger.info("Empresa " + empresa.getRfc());

        if (subReporte != null) {
            InputStream in = null;
            System.out.println("Inicia uploadSubreportes");

            if (!subReporte.getFileName().equals(file.getFileName())) {
                try {
                    in = subReporte.getInputstream();
                } catch (IOException ex) {
                    logger.error("[ " + sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Error al cargar el subreporte " + ex);
                }
                try {
                    OutputStream out = new FileOutputStream(new File(destino + empresa.getRfc() + "/" + carpeta + "/" + subReporte.getFileName()));
                    int reader = 0;
                    byte[] bytes = new byte[(int) subReporte.getSize()];
                    while ((reader = in.read(bytes)) != -1) {
                        out.write(bytes, 0, reader);
                    }
                    in.close();
                    out.flush();
                    out.close();
                    ManejoArchivos ma = new ManejoArchivos();
                    File subRepor = new File(destino + empresa.getRfc() + "/" + carpeta + "/" + subReporte.getFileName());
                    System.out.println(sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Ruta del subreporte " + subRepor.getAbsolutePath());
                    logger.info("[ " + sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Ruta del subreporte " + subRepor.getAbsolutePath());
                    if (subRepor.isFile()) {

                        boolean compila = ma.compilarPlantilla(subRepor.getAbsolutePath());
                        if (compila) {
                            System.out.println(sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Se compilo corractamente el subreporte: " + subRepor.getName());
                            logger.info("[ " + sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Se compilo corractamente el subreporte: " + subRepor.getName());
                            this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", subReporte.getFileName() + " a sido subido y compilado");

                            listarSubreportes(); //metodo para obtener los subreporte

                            try {
                                List<Imagen> listaImaSub = ma.ListaImagenes(destino + empresa.getRfc() + "/" + carpeta + "/" + subReporte.getFileName());
//                            List<Imagen> listaImaSub = ma.listaImagenes(destino + sessionUsuario + "/" + carpeta + "/" + event.getFile().getFileName());
                                for (Imagen imagen1 : listaImaSub) {
                                    System.out.println(sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Imagen de subreporte: " + imagen1.getNombre());
                                    logger.info("[ " + sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Imagen de subreporte: " + imagen1.getNombre());
                                    this.listaImagenes.add(imagen1);
                                }
                            } catch (IOException ex) {
                                System.out.println(sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Error al obtener las imagenes del subreporte ");
                                logger.error("[ " + sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Error al obtener las imagenes del subreporte ");
                            }
                            enableImagenes = true;
                        } else {
                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Subreporte: ", subReporte.getFileName() + " no ha sido cargado correctamente y no pudo compilar");
                        }
                    }
                } catch (IOException e) {
                    logger.error("[ " + sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Error al cargar el subreporte " + e);
                }
            } else {
                this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución ", "Estas tratando de subir un subreporte con el mismo nombre del reporte principal: " + subReporte.getFileName());
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ha ocurrido un eror al cargar el archivo");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void uploadImagen() {
        if (fileImagen != null) {
            Boolean ban = false;
            Integer pos = null;
            int totalImagenes = listaImagenes.size();
            int contador = 0;
            for (Imagen ima1 : listaImagenes) {
                if (ima1.getNombre().equals(fileImagen.getFileName())) {
                    pos = listaImagenes.indexOf(ima1);
                    ban = true;

                }
                if (ima1.getEstado().equals("Cargado")) {
                    contador++;
                }
            }
            if (contador >= totalImagenes) {
                this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Se han cargador todas la imagenes en la tabla", null);
            } else {
                if (!ban) {
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Imagen: " + fileImagen.getFileName() + " no se encuentra en la tabla iamagenes");
                } else {
                    Imagen ima1 = listaImagenes.get(pos);
                    String folder = ima1.getCarpeta();

                    File f1 = new File(destino + empresa.getRfc() + "/" + carpeta + "/" + folder);
                    f1.mkdirs();
                    InputStream in = null;
                    try {
                        in = fileImagen.getInputstream();
                    } catch (IOException ex) {
                        System.out.println(sessionUsuario + " - metodo_uploadImagenes MBPlantillas ] Error al cargar la imagen: " + ex.getMessage());
                        logger.error("[ " + sessionUsuario + " - metodo_uploadImagenes MBPlantillas ] Error al cargar la imagen: " + ex.getMessage());
                    }
                    try {
                        OutputStream out = new FileOutputStream(new File(destino + empresa.getRfc() + "/" + carpeta + "/" + folder + "/" + fileImagen.getFileName()));
                        int reader = 0;
                        byte[] bytes = new byte[(int) fileImagen.getSize()];
                        while ((reader = in.read(bytes)) != -1) {
                            out.write(bytes, 0, reader);
                        }
                        in.close();
                        out.flush();
                        out.close();
                        ima1.setEstado("Cargado");
                        this.msg = new FacesMessage("Imagen: ", fileImagen.getFileName() + " a sido cargado correctamente.");
                    } catch (Exception e) {
                        System.out.println(sessionUsuario + " - metodo_uploadImagenes MBPlantillas ] Error al cargar la imagen: " + e.getMessage());
                        logger.error("[ " + sessionUsuario + " - metodo_uploadImagenes MBPlantillas ] Error al cargar la imagen: " + e.getMessage());
                    }
                    boolean bandera = true;
                    for (Imagen imagen : listaImagenes) {
                        bandera = true;
                        if (!imagen.getEstado().equals("Cargado")) {
                            bandera = false;
                        }
                    }
                    enableSave = bandera;
                }
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ha ocurrido un eror al cargar el archivo");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
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
                if (ima.getEstado().equals("Sin seleccionar")) {
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaucion", "Aún no ha seleccionado todas la imagenes");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    return event.getOldStep();
                }
            }

            this.listaSubreportes = new ArrayList<Subreporte>();
            ManejoArchivos ma = new ManejoArchivos();
            List<String> listaSub = ma.getListaSubreportes(destino + empresa.getRfc() + "/" + carpeta);

            for (String string : listaSub) {
                Subreporte subreprote = new Subreporte();
                subreprote.setNombre(string);
                listaSubreportes.add(subreprote);
            }
        }
        if ("updateConfirmacion".equals(event.getNewStep())) {
            for (Imagen ima : listaImagenes) {
                if (ima.getEstado().equals("Sin seleccionar")) {
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaucion", "Aún no han cargado todas la imagenes");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    return event.getOldStep();
                }
            }

            this.listaSubreportes = new ArrayList<Subreporte>();
            ManejoArchivos ma = new ManejoArchivos();
            List<String> listaSub = ma.getListaSubreportes(destino + empresa.getRfc() + "/" + carpeta);

            for (String string : listaSub) {
                Subreporte subr = new Subreporte();
                subr.setNombre(string);
                listaSubreportes.add(subr);
            }
        }
        return event.getNewStep();
    }

    public void uploadPrincipal(FileUploadEvent event) {
        logger.info("Inicia carga de Reporte Principal");
        logger.info("Nombre event " + event.getFile().getFileName());
        logger.info("Empresa " + empresa.getRfc());
        ManejoArchivos ma = new ManejoArchivos();

        ma.eliminar(destino);

        this.carpeta = ma.quirtarExtension(event.getFile().getFileName()); //asignamos el nombre de la carpeta con el nombre de la plantilla
        logger.info("carpeta " + carpeta);
        logger.info("destino " + destino);
        logger.info("Nombre carpeta " + destino + empresa.getRfc() + "/" + carpeta + "/");
        File direccion = new File(destino + empresa.getRfc() + "/" + carpeta + "/");

        boolean ban = direccion.mkdirs(); // creamos la ruta temporal de la plantilla
        if (ban) {
            logger.info("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Se creo correctamente el directorio de la plantilla: " + direccion.getAbsolutePath());
            InputStream in = null;
            try {
                in = event.getFile().getInputstream();
            } catch (IOException ex) {
                logger.error("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al cargar la plantilla " + ex.getMessage());
            }
            try {
                OutputStream out = new FileOutputStream(destino + empresa.getRfc() + "/" + carpeta + "/main.xprint");
                int reader = 0;
                byte[] bytes = new byte[(int) event.getFile().getSize()];
                while ((reader = in.read(bytes)) != -1) {
                    out.write(bytes, 0, reader);
                }
                in.close();
                out.flush();
                out.close();
                File f2 = new File(destino + empresa.getRfc() + "/" + carpeta + "/main.xprint");
                if (f2.isFile()) {
                    logger.info("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Ruta del reporte Principal: " + f2.getAbsolutePath());
                    boolean compila = ma.compilarPlantilla(f2.getAbsolutePath());
                    if (compila) {
                        listaSubreportes.clear();// Se limpia la lista de subreportes
                        logger.info("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Se compilo corractamente el reporte principal: " + f2.getName());
                        this.principal = event.getFile().getFileName(); //asignamos el nombre de la planilla
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La plantilla: " + event.getFile().getFileName() + " a sido cargada y compilada");
                        try {
                            this.listaImagenes = ma.ListaImagenes(destino + empresa.getRfc() + "/" + carpeta + "/main.xprint");
//                            this.listaImagenes = ma.listaImagenes(destino + sessionUsuario + "/" + carpeta + "/main.xprint");
                            for (Imagen ima : listaImagenes) {
                                logger.info("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Imagen de reporte principal: " + ima.getNombre());
                            }
                        } catch (IOException e) {
                            System.out.println(sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al obtener la imagenes del reporte principal");
                            logger.error("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al obtener la imagenes del reporte principal");
                        }
                    } else {
                        System.out.println(sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al compilar la plantillas: " + f2.getName());
                        logger.error("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al compilar la plantillas: " + f2.getName());
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La plantilla: " + event.getFile().getFileName() + " no ha sido compilada");
                    }
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            } catch (IOException e) {
                System.out.println(sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al cargar la plantilla " + e);
                logger.error("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al cargar la plantilla " + e.getMessage());
            }
        } else {
            System.out.println(sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al crear el directorio de la plantilla: " + direccion.getAbsolutePath());
            logger.error("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al crear el directorio de la plantilla: " + direccion.getAbsolutePath());
        }
    }

    public void asignarReportePrincipalPNulo() {
        if (this.principal.length() > 0) {
            new ManejoArchivos().eliminar(destino);
            this.principal = "";
            this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se elimino satisfactoriamente el Reporte Principal");
        } else {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, seleccione un Reporte Principal");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public String eliminarPlantilla() {
        boolean bandera;

        for (ImprimirPlantilla iPlantillas : plantillasSeleccionadas) {
            Plantillas plantilla = new Plantillas();
            plantilla.setIdPlantillas(iPlantillas.getIdPlantillas());
            plantilla.setIdEmpresa(iPlantillas.getIdEmpresa());
            plantilla.setNombre(iPlantillas.getNombre());
            plantilla.setRuta(iPlantillas.getRuta());
            plantilla.setSha1(iPlantillas.getSha1());
            this.plantilla = plantilla;

            try {
                DaoPlantilla daoPlantillas = new DaoPlantilla();
                bandera = daoPlantillas.deletePlantilla(this.plantilla);
                if (bandera) {
                    logger.info("[ " + sessionUsuario + " - metodo_eliminarPlantilla MBPlantillas ] Se elimino correctamente el registro de plantilla en la BD: " + this.plantilla.getNombre());
                    String rutaPlantilla = "/xsa/tomcat/webapps/ROOT/iqtb/Plantillas/" + empresa.getRfc(); // /xsa/tomcat/webapps/ROOT/iqtb/Plantillas/

                    boolean eliminaZip = new ManejoArchivos().eliminar(rutaPlantilla);
                    if (eliminaZip) {
                        System.out.println("- metodo_eliminarPlantilla MBPlantillas ] Se elimino correctamente el archivo de plantilla, ruta: " + rutaPlantilla);
                        logger.info("[ " + sessionUsuario + " - metodo_eliminarPlantilla MBPlantillas ] Se elimino correctamente el archivo de plantilla, ruta: " + rutaPlantilla);
                    } else {
                        System.out.println(" - metodo_eliminarPlantilla MBPlantillas ] Erro al eliminar el archivo de la plantilla, ruta: " + rutaPlantilla);
                        logger.error("[ " + sessionUsuario + " - metodo_eliminarPlantilla MBPlantillas ] Erro al eliminar el archivo de la plantilla, ruta: " + rutaPlantilla);
                    }
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se elimino la plantilla: " + this.plantilla.getNombre());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                } else {
                    System.out.println(" - metodo_eliminarPlantilla MBPlantillas ] Error al eliminar registro de plantilla en la BD: " + this.plantilla.getNombre());
                    logger.error("[ " + sessionUsuario + " - metodo_eliminarPlantilla MBPlantillas ] Error al eliminar registro de plantilla en la BD: " + this.plantilla.getNombre());
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "no se pudo eliminar la plantilla: " + this.plantilla.getNombre());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            } catch (Exception ex) {
                System.out.println(" - metodo_eliminarPlantilla MBPlantillas ] Error al eliminar registro de plantilla en la BD: " + this.plantilla.getNombre());
                logger.error("[ " + sessionUsuario + " - metodo_eliminarPlantilla MBPlantillas ] Error al eliminar registro de plantilla en la BD: " + this.plantilla.getNombre());
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
        System.out.println("Inicia uploadSubreportes");

        if (!event.getFile().getFileName().equals(principal)) {
            try {
                in = event.getFile().getInputstream();
            } catch (IOException ex) {
                logger.error("[ " + sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Error al cargar el subreporte " + ex);
            }
            try {
                OutputStream out = new FileOutputStream(new File(destino + empresa.getRfc() + "/" + carpeta + "/" + event.getFile().getFileName()));
                int reader = 0;
                byte[] bytes = new byte[(int) event.getFile().getSize()];
                while ((reader = in.read(bytes)) != -1) {
                    out.write(bytes, 0, reader);
                }
                in.close();
                out.flush();
                out.close();
                ManejoArchivos ma = new ManejoArchivos();
                File subRepor = new File(destino + empresa.getRfc() + "/" + carpeta + "/" + event.getFile().getFileName());
                System.out.println(sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Ruta del subreporte " + subRepor.getAbsolutePath());
                logger.info("[ " + sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Ruta del subreporte " + subRepor.getAbsolutePath());
                if (subRepor.isFile()) {

                    boolean compila = ma.compilarPlantilla(subRepor.getAbsolutePath());
                    if (compila) {
                        System.out.println(sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Se compilo corractamente el subreporte: " + subRepor.getName());
                        logger.info("[ " + sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Se compilo corractamente el subreporte: " + subRepor.getName());
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", event.getFile().getFileName() + " a sido subido y compilado");

                        listarSubreportes(); //metodo para obtener los subreporte

                        try {
                            List<Imagen> listaImaSub = ma.ListaImagenes(destino + empresa.getRfc() + "/" + carpeta + "/" + event.getFile().getFileName());
//                            List<Imagen> listaImaSub = ma.listaImagenes(destino + sessionUsuario + "/" + carpeta + "/" + event.getFile().getFileName());
                            for (Imagen imagen1 : listaImaSub) {
                                System.out.println(sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Imagen de subreporte: " + imagen1.getNombre());
                                logger.info("[ " + sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Imagen de subreporte: " + imagen1.getNombre());
                                this.listaImagenes.add(imagen1);
                            }
                        } catch (IOException ex) {
                            System.out.println(sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Error al obtener las imagenes del subreporte ");
                            logger.error("[ " + sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Error al obtener las imagenes del subreporte ");
                        }
                    } else {
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Subreporte: ", event.getFile().getFileName() + " no ha sido cargado correctamente y no pudo compilar");
                    }
                }
            } catch (Exception e) {
                System.out.println(sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Error al cargar el subreporte ");
                logger.error("[ " + sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Error al cargar el subreporte ");
            }
        } else {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución ", "Estas tratando de subir un subreporte con el mismo nombre del reporte principal: " + event.getFile().getFileName());
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void listarSubreportes() {
        ManejoArchivos ma = new ManejoArchivos();
        List<String> lista = ma.getListaSubreportes(destino + empresa.getRfc() + "/" + carpeta + "/");
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
            listIma = ma.ListaImagenes(destino + empresa.getRfc() + "/" + carpeta + "/" + subreporteSeleccionado.getNombre());
//            listIma = ma.listaImagenes(destino + sessionUsuario + "/" + carpeta + "/" + subreporteSeleccionado.getNombre());
            for (Imagen imagen1 : listIma) {
                System.out.println("Imagen a borrar: " + imagen1.getNombre());
            }
        } catch (IOException ex) {
            ex.getMessage();
        }

        boolean correcto = ma.eliminaArchivo(destino + empresa.getRfc() + "/" + carpeta + "/" + subreporteSeleccionado.getNombre());

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
            if (ima1.getEstado().equals("Cargado")) {
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

                File f1 = new File(destino + empresa.getRfc() + "/" + carpeta + "/" + folder);
                f1.mkdirs();
                InputStream in = null;
                try {
                    in = event.getFile().getInputstream();
                } catch (IOException ex) {
                    System.out.println(sessionUsuario + " - metodo_uploadImagenes MBPlantillas ] Error al cargar la imagen: " + ex.getMessage());
                    logger.error("[ " + sessionUsuario + " - metodo_uploadImagenes MBPlantillas ] Error al cargar la imagen: " + ex.getMessage());
                }
                try {
                    OutputStream out = new FileOutputStream(new File(destino + empresa.getRfc() + "/" + carpeta + "/" + folder + "/" + event.getFile().getFileName()));
                    int reader = 0;
                    byte[] bytes = new byte[(int) event.getFile().getSize()];
                    while ((reader = in.read(bytes)) != -1) {
                        out.write(bytes, 0, reader);
                    }
                    in.close();
                    out.flush();
                    out.close();
                    ima1.setEstado("Cargado");
                    this.msg = new FacesMessage("Imagen: ", event.getFile().getFileName() + " a sido cargado correctamente.");
                } catch (Exception e) {
                    System.out.println(sessionUsuario + " - metodo_uploadImagenes MBPlantillas ] Error al cargar la imagen: " + e.getMessage());
                    logger.error("[ " + sessionUsuario + " - metodo_uploadImagenes MBPlantillas ] Error al cargar la imagen: " + e.getMessage());
                }
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void guardarPlantilla() {
        ManejoArchivos ma = new ManejoArchivos();
        Plantillas p = new Plantillas();
        //File rutaZip = new File("/Users/danielromero/NetBeansProjects/IQTBValidacion/Plantillas/zips/");
        //rutaZip.mkdirs();
        try {

//            this.log.info("[ " + sessionUsuario + " - metodo_guardarPlantilla MBPlantillas ] Series seleccionadas: " + serie);
            //File archivoZip = new File("/Users/danielromero/NetBeansProjects/IQTBValidacion/Plantillas/zips/" + sessionUsuario+ ".zip");
            //ManejoArchivos.makeZip(new File(destino + sessionUsuario + "/" + carpeta), archivoZip.getAbsolutePath(), carpeta.length() + 2);
            //String sumaSha1 = ma.calculaSHA1(archivoZip.getAbsolutePath());
            String rutaFinal = "/xsa/tomcat/webapps/ROOT/iqtb/Plantillas/"; //  /xsa/tomcat/webapps/ROOT/iqtb/Plantillas/
            ma.mover(destino + empresa.getRfc(), rutaFinal + empresa.getRfc());
            p.setIdEmpresa(empresa.getIdEmpresa());
            if (nombrePlantilla != null && !nombrePlantilla.trim().isEmpty()) {
                p.setNombre(nombrePlantilla);
            } else {
                p.setNombre("Plantilla " + empresa.getRfc());
            }
            p.setRuta(rutaFinal + empresa.getRfc() + "/" + carpeta + "/" + "main" + ".jasper");
            p.setSha1("");

            System.out.println(sessionUsuario + " - metodo_guardarPlantilla MBPlantillas ] Plantilla a insertar, nombre: " + p.getNombre() + ", ruta: " + p.getRuta() + ", sha1: " + p.getSha1());
            logger.info("[ " + sessionUsuario + " - metodo_guardarPlantilla MBPlantillas ] Plantilla a insertar, nombre: " + p.getNombre() + ", ruta: " + p.getRuta());
            boolean ban = new DaoPlantilla().insertPlantilla(p);
            Plantillas plantilla1;
            if (ban) {

                System.out.println(sessionUsuario + " - metodo_guardarPlantilla MBPlantillas ] Se inserto correctamente la plantilla en la BD: " + p.getNombre());
//                
            } else {
                System.out.println(sessionUsuario + " - metodo_guardaPlantilla MBPlantillas ] No se inserto la plantilla en la BD: " + p.getNombre());
                logger.info("[ " + sessionUsuario + " - metodo_guardaPlantilla MBPlantillas ] No se inserto la plantilla en la BD: " + p.getNombre());
            }
        } catch (IOException ex) {
            System.out.println(" - metodo_guardarPlantilla MBPlantillas ] Error IOException: " + ex);
            logger.error("[ " + sessionUsuario + " - metodo_guardarPlantilla MBPlantillas ] Error al crear el zip: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println(" - metodo_guardarPlantilla MBPlantillas ] Error Exception: " + ex);
            logger.error("[ " + sessionUsuario + " - metodo_guardarPlantilla MBPlantillas ] Error al crear el zip: " + ex.getMessage());
        }

        this.msg = new FacesMessage("Correcto", " Se ha cargado correctamente la plantilla: " + p.getNombre());
        FacesContext.getCurrentInstance().addMessage(null, msg);
//        return "plantilla";
    }

    public String existeSeleccionPlantilla() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();

        String parametro = (String) params.get("nombreParametro");
        System.out.println("parametro: " + parametro);

        boolean estadoPlantilla = false;
        if (this.plantillasSeleccionadas.isEmpty()) {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Seleccione una platilla");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            if (parametro != null) {
                if (parametro.equals("eliminar")) {
                    estadoPlantilla = true;
                }
            } else if (this.plantillasSeleccionadas.size() > 1) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Seleccione solo una plantilla");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                estadoPlantilla = true;
                for (ImprimirPlantilla iPlantillas : plantillasSeleccionadas) {
                    Plantillas plantilla = new Plantillas();
                    plantilla.setIdPlantillas(iPlantillas.getIdPlantillas());
                    plantilla.setIdEmpresa(iPlantillas.getIdEmpresa());
                    plantilla.setNombre(iPlantillas.getNombre());
                    plantilla.setRuta(iPlantillas.getRuta());
                    plantilla.setSha1(iPlantillas.getSha1());
                    this.plantilla = plantilla;
                }
            }
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("estadoPlantilla", estadoPlantilla);
        return "/Configuracion/plantilla?faces-redirect=true";
    }

    public String empresaById(int idEmpresa) {
        Empresas empresa = null;
        try {
            empresa = new DaoEmpresa().getEmpresaByidEmpresa(idEmpresa);
        } catch (Exception ex) {
            System.out.println("empresaById - ERROR " + ex);
        }
        if (empresa == null) {
            return "";
        }
        return empresa.getRfc();
    }

    public boolean existePlantilla() {
        if (listaPlantillas.size() <= 0) {
            return true;
        } else {
            return false;
        }
    }

}

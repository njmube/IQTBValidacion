/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.dto;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpSession;

/**
 *
 * @author danielromero
 */
public class AuthorizationListener implements PhaseListener{

    @Override
    public void afterPhase(PhaseEvent event) {
        FacesContext facesContext = event.getFacesContext();
        String currentPage = facesContext.getViewRoot().getViewId();
        boolean islogginPage = (currentPage.lastIndexOf("login.xhtml") > -1) ? true : false;
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
        Object usuario = session.getAttribute("usuario");
        
        if (!islogginPage && usuario == null) {
            NavigationHandler nh = facesContext.getApplication().getNavigationHandler();
            nh.handleNavigation(facesContext, null, "/login.xhtml?faces-redirect=true");
        }
    }

    @Override
    public void beforePhase(PhaseEvent event) {
        
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }
    
}

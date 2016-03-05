package com.web.jsf.beans.handlers;


import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.ResourceBundle;
import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

public class AuthenticationPhaseListener implements PhaseListener {

    private static final long serialVersionUID = 1L;
    private static HashMap<String, String> pagePermissionMapping = null;

    private void pagePermissionMapping() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (pagePermissionMapping == null) {
            pagePermissionMapping = new HashMap();
            try {
                ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "accessProp");
                if (bundle != null) {
                    Enumeration e = bundle.getKeys();
                    while (e.hasMoreElements()) {
                        String key = (String) e.nextElement();
                        String value = bundle.getString(key);
                        pagePermissionMapping.put(key, value);
                    }
                }
            } catch (Exception e) {               
            }
        }
    }

    @Override
    public void afterPhase(PhaseEvent event) {
    }

    @Override
    public synchronized void beforePhase(PhaseEvent event) {
        pagePermissionMapping();
        FacesContext context = event.getFacesContext();
        ExternalContext ex = context.getExternalContext();
        String viewId = "/index.xhtml";
        if (context.getViewRoot() != null && context.getViewRoot().getViewId() != null) {
            viewId = context.getViewRoot().getViewId();
       
        }
        String permissions = pagePermissionMapping.get(viewId);       
      //  UserContext uc = (UserContext) ex.getSessionMap().get("userContext");
        if (permissions != null && permissions.contains("PUBLIC")) {
            return;
        }
     //   if (permissions != null && uc == null && permissions.contains("LOGGED") && !viewId.contains("index.xhtml")) {
        //    redirect(context, "../../index.jsf?illegalAccess");
     //   }
     
    }

    private static void redirect(FacesContext facesContext, String url) {
        try {
            facesContext.getExternalContext().redirect(url);
        } catch (IOException e) {
            throw new FacesException("Cannot redirect to " + url + " due to IO exception.", e);
        }
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }
}

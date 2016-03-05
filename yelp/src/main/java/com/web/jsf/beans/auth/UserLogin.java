package com.web.jsf.beans.auth;

import com.progress.backend.entities.UserEntity;
import com.progress.backend.services.user.UserService;
import com.web.jsf.beans.handlers.ApplicationManager;
import com.web.jsf.beans.handlers.SessionController;
import java.io.Serializable;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;


/**
 *
 * @author armena
 */
@ManagedBean
@RequestScoped
public class UserLogin implements Serializable {

    private static final long serialVersionUID = 1L;
    @ManagedProperty("#{applicationManager}")
    private ApplicationManager applicationManager;
    @ManagedProperty("#{sessionController}")
    private SessionController sessionController = null;
    @ManagedProperty("#{i18n}")
    private ResourceBundle bundle = null;
    private String email;
    private String password;
    private Boolean rememberMe;

    public UserLogin() {
    }

    public String loginUser() {
        ExternalContext facesContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = facesContext.getSessionMap();
        sessionMap.remove("expired");
        String navigation = "index";
        try {
            UserEntity user = applicationManager.getUserService().userLogin(email, password);

            if (user == null) {
                FacesMessage msg = new FacesMessage(bundle.getString("notuser"), bundle.getString("notuser"));
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return null;
            }
            
            if(user != null){
                sessionController.setUser(user);
                navigation = "dashboard";
            }
          
        } catch (Exception e) {
        }
        return navigation;
    }

    private void facesError(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }

  

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public void setApplicationManager(ApplicationManager applicationManager) {
        this.applicationManager = applicationManager;
    }

  
    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }
    
    

}

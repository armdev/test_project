package com.web.jsf.beans.auth;

import com.web.jsf.beans.handlers.SessionController;
import com.web.jsf.beans.profile.UserProfile;
import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Armen Arzumanyan
 */
@ManagedBean
@RequestScoped
public class UserLogout implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private FacesContext context = null;
    private ExternalContext externalContext = null;
  

    public UserLogout() {
    }
    
    public String doLogout() {
        context = FacesContext.getCurrentInstance();
        externalContext = context.getExternalContext();
        externalContext.getSessionMap().remove("sessionController");
        // boolean manager  = externalContext.getSessionMap().containsKey("sessionController");
        //System.out.println("manager " + manager);
        externalContext.getSessionMap().clear();
        externalContext.invalidateSession();
        HttpSession session = (HttpSession) externalContext.getSession(true);
        session.invalidate();
        System.out.println("Log out");
      
        HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
        Cookie cookie = new Cookie("JSESSIONID", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return "/index?faces-redirect=true";
    }
}

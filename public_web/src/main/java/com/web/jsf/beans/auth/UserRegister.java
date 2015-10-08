package com.web.jsf.beans.auth;

import com.progress.backend.entities.UserEntity;
import com.progress.backend.services.file.FileService;
import com.progress.backend.services.user.UserService;
import org.apache.log4j.Logger;
import java.io.Serializable;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author armena
 */
@ManagedBean
@ViewScoped
public class UserRegister implements Serializable {

    private static Logger log = Logger.getLogger(UserRegister.class);
    private static final long serialVersionUID = 1L;
    @ManagedProperty("#{userService}")
    private UserService userService = null;
    @ManagedProperty("#{fileService}")
    private FileService fileService = null;
    @ManagedProperty("#{i18n}")
    private ResourceBundle bundle = null;
    private Boolean agree;

    private UserEntity user = new UserEntity();

    public UserRegister() {
        user = new UserEntity();
    }

    public String registerUser() {
        boolean check = userService.checkEmail(user.getEmail());
        if (check) {
            facesError(bundle.getString("emailError"));
            return null;
        }
        boolean checkRegister = userService.save(user);
        if (checkRegister) {
            return "success";
        }

        return "error";

    }

    private void facesError(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    public Boolean getAgree() {
        return agree;
    }

    public void setAgree(Boolean agree) {
        this.agree = agree;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}

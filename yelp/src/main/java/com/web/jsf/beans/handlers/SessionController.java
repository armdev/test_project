package com.web.jsf.beans.handlers;

import com.progress.backend.entities.UserEntity;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;


/**
 *
 * @author Armen Arzumanyan
 */
@ManagedBean
@SessionScoped
public class SessionController implements Serializable {
    private static final long serialVersionUID = 1L;

    private UserEntity user = new UserEntity();

    public SessionController() {

    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
    
    

}

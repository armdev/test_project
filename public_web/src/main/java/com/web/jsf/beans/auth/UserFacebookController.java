package com.web.jsf.beans.auth;


import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.apache.log4j.Logger;

@ApplicationScoped
@ManagedBean(name = "userFacebookController")
public class UserFacebookController implements Serializable {

    private static final Logger log = Logger.getLogger(UserFacebookController.class);
   
    @PostConstruct
    public void init() {
       
    }

    public void saveUserFacebookCredentials(UserFacebookEntity userFacebookEntity) {
      
    }

 
}

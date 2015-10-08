package com.web.jsf.beans.auth;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.apache.log4j.Logger;
import org.brickred.socialauth.SocialAuthManager;


@ManagedBean(name = "facebookAuthManagerBean")
@SessionScoped
public class FacebookAuthManagerBean implements Serializable {

    private static final Logger log = Logger.getLogger(FacebookAuthManagerBean.class);

    private String socialId;
    private SocialAuthManager manager = new SocialAuthManager();
    private SocialAuthManager googleManager = new SocialAuthManager();
    private String avatar;
    private String userStatus;

    public FacebookAuthManagerBean() {

    }

    @PostConstruct
    public void init() {

    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSocialId() {
        return socialId;
    }

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }

    public SocialAuthManager getManager() {
        return manager;
    }

    public void setManager(SocialAuthManager manager) {
        this.manager = manager;
    }

    public SocialAuthManager getGoogleManager() {
        return googleManager;
    }

    public void setGoogleManager(SocialAuthManager googleManager) {
        this.googleManager = googleManager;
    }
    
    

}

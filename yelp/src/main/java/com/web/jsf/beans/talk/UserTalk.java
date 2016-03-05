package com.web.jsf.beans.talk;

import com.web.jsf.beans.profile.*;
import com.progress.backend.entities.FileEntity;
import com.progress.backend.entities.TalkCategoryEntity;
import com.progress.backend.entities.TalkEntity;
import com.progress.backend.entities.TalkFlowEntity;

import com.progress.backend.entities.UserEntity;
import com.progress.backend.entities.UserFacebookEntity;
import com.progress.backend.services.file.FileService;
import com.progress.backend.services.user.UserFacebookService;

import com.progress.backend.services.user.UserService;
import com.web.jsf.beans.handlers.ApplicationManager;
import com.web.jsf.beans.handlers.SessionController;
import java.io.InputStream;
import org.apache.log4j.Logger;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author armena
 */
@ManagedBean
@ViewScoped
public class UserTalk implements Serializable {

    private static Logger log = Logger.getLogger(UserTalk.class);
    private static final long serialVersionUID = 1L;

    @ManagedProperty("#{applicationManager}")
    private ApplicationManager applicationManager;
    @ManagedProperty("#{sessionController}")
    private SessionController sessionController = null;
    @ManagedProperty("#{i18n}")
    private ResourceBundle bundle = null;
    private TalkCategoryEntity talkCategoryEntity;
    private TalkEntity talkEntity;
    private TalkFlowEntity talkFlowEntity;

    public UserTalk() {
        log.info("Constructor called : UserTalk ");
         talkEntity = new TalkEntity();
    }

    @PostConstruct
    public void init() {
       

    }

    public String makeTalk() {
        try {
            System.out.println("sessionController.getUser().getId() " + sessionController.getUser().getId());
             System.out.println("TitleFrontend::::: " + talkEntity.getTitle());
            System.out.println("MessageFrontend::::: " + talkEntity.getMessage());
            talkEntity.setUserId(sessionController.getUser().getId());
            applicationManager.getTalkService().save(talkEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "index";
    }

    public List<TalkEntity> getTalkList() {
        return applicationManager.getTalkService().findAll();
    }

    @PreDestroy
    public void destroy() {
        log.info("destroy : UserTalk ");

    }

    public TalkCategoryEntity getTalkCategoryEntity() {
        return talkCategoryEntity;
    }

    public void setTalkCategoryEntity(TalkCategoryEntity talkCategoryEntity) {
        this.talkCategoryEntity = talkCategoryEntity;
    }

    public TalkEntity getTalkEntity() {
        return talkEntity;
    }

    public void setTalkEntity(TalkEntity talkEntity) {
        this.talkEntity = talkEntity;
    }

    public TalkFlowEntity getTalkFlowEntity() {
        return talkFlowEntity;
    }

    public void setTalkFlowEntity(TalkFlowEntity talkFlowEntity) {
        this.talkFlowEntity = talkFlowEntity;
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

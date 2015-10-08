package com.web.jsf.beans.profile;

import com.progress.backend.entities.FileEntity;
import com.progress.backend.entities.PortfolioEntity;
import com.progress.backend.entities.Tag;
import com.progress.backend.entities.UserEntity;
import com.progress.backend.services.file.FileService;
import com.progress.backend.services.file.PortfolioFileService;
import com.progress.backend.services.tag.TagService;
import com.progress.backend.services.user.PortfolioService;
import com.progress.backend.services.user.UserService;
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
public class UserPortfolio implements Serializable {

    private static Logger log = Logger.getLogger(UserPortfolio.class);
    private static final long serialVersionUID = 1L;

    @ManagedProperty("#{userService}")
    private UserService userService = null;
    @ManagedProperty("#{portfolioService}")
    private PortfolioService portfolioService = null;
    @ManagedProperty("#{portfolioFileService}")
    private PortfolioFileService portfolioFileService = null;
    @ManagedProperty("#{sessionController}")
    private SessionController sessionController = null;
    @ManagedProperty("#{i18n}")
    private ResourceBundle bundle = null;

    private Part uploadedFile = null;
    private UserEntity user = new UserEntity();
    private PortfolioEntity portfolioEntity;

    public UserPortfolio() {
        System.out.println("Constructor called :UserPortfolio ");
    }

    @PostConstruct
    public void init() {
        portfolioEntity = new PortfolioEntity();
        if (sessionController.getUser() != null && sessionController.getUser().getId() != null) {
            user = userService.findById(sessionController.getUser().getId());
        }
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Constructor called : destory ");

    }

    public String update() {
        FileEntity file = new FileEntity();
        String imageId = null;
        try {
            long size = 0;
            if (uploadedFile != null) {
                size = uploadedFile.getSize();
                long maxSize = 5 * 1000000;
                if (size > maxSize) {
                    facesError(bundle.getString("fileistoobig"));
                    return null;
                }
                String content = uploadedFile.getContentType();
                System.out.println("Content type " + content);
                InputStream stream = uploadedFile.getInputStream();
                byte[] contentBytes = new byte[(int) size];
                stream.read(contentBytes);
                file.setMimetype(content);
                file.setFilesize(size);
                file.setTitle(uploadedFile.getName());
                file.setContent(contentBytes);

                if (content.equalsIgnoreCase("image/jpeg") || content.equalsIgnoreCase("image/pjpeg")
                        || content.equalsIgnoreCase("image/jpg") || content.equalsIgnoreCase("image/gif")
                        || content.equalsIgnoreCase("image/x-png") || content.equalsIgnoreCase("image/png")) {
                    try {
                        imageId = portfolioFileService.addFile(file);

                        System.out.println("Saving file  " + imageId);
                    } catch (Exception e) {
                    }
                } else {

                }

            }
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }

        if (imageId != null) {
            portfolioEntity.setImageId(imageId);
        }
        try {
            portfolioEntity.setUserId(sessionController.getUser().getId());
            portfolioEntity.setProfileId(sessionController.getUser().getProfileId());
            portfolioService.save(portfolioEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        facesSuccess(bundle.getString("success"));
        return null;

    }

    public List<PortfolioEntity> getPortfolioList() {
        List<PortfolioEntity> list = portfolioService.findAllByUserId(sessionController.getUser().getId());
        return list;
    }

    public PortfolioEntity getPortfolioEntity() {
        return portfolioEntity;
    }

    public void setPortfolioEntity(PortfolioEntity portfolioEntity) {
        this.portfolioEntity = portfolioEntity;
    }

    public Part getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(Part uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    private void facesError(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }

    private void facesSuccess(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void setPortfolioService(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    public void setPortfolioFileService(PortfolioFileService portfolioFileService) {
        this.portfolioFileService = portfolioFileService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

}

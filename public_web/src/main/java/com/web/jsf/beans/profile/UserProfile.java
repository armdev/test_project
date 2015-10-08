package com.web.jsf.beans.profile;

import com.progress.backend.entities.FileEntity;
import com.progress.backend.entities.Tag;
import com.progress.backend.entities.UserEntity;
import com.progress.backend.services.file.FileService;
import com.progress.backend.services.tag.TagService;
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
public class UserProfile implements Serializable {

    private static Logger log = Logger.getLogger(UserProfile.class);
    private static final long serialVersionUID = 1L;
    //@Inject
    @ManagedProperty("#{userService}")
    private UserService userService = null;
    @ManagedProperty("#{fileService}")
    private FileService fileService = null;
    @ManagedProperty("#{sessionController}")
    private SessionController sessionController = null;
    @ManagedProperty("#{i18n}")
    private ResourceBundle bundle = null;
    @ManagedProperty("#{tagService}")
    private TagService tagService = null;

    private Part uploadedFile = null;
    private UserEntity user = new UserEntity();

    public UserProfile() {
        System.out.println("Constructor called : user profile ");
    }

    @PostConstruct
    public void init() {
        System.out.println("called : init: ");

        if (sessionController.getUser() != null && sessionController.getUser().getId() != null) {
            user = userService.findById(sessionController.getUser().getId());
            System.out.println("image id!!!!  " + user.getImageId());
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
            System.out.println("uploadedFile " + uploadedFile);
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
                // file.setUserId(userId);
                // file.setType(StatusTypes.POST_PHOTO);
                if (content.equalsIgnoreCase("image/jpeg") || content.equalsIgnoreCase("image/pjpeg")
                        || content.equalsIgnoreCase("image/jpg") || content.equalsIgnoreCase("image/gif")
                        || content.equalsIgnoreCase("image/x-png") || content.equalsIgnoreCase("image/png")) {
                    try {
                        imageId = fileService.addFile(file);

                        System.out.println("Saving file  " + imageId);
                    } catch (Exception e) {
                    }
                } else {
                    //facesSuccess(bundle.getString("fileformatwrong"));
                }

            }
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }

        if (imageId != null) {
            user.setImageId(imageId);
        }
        try {
            //System.out.println("Skiil tags size frontend " + user.getSkillTags().size());
            userService.updateProfile(sessionController.getUser().getId(), user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        facesSuccess(bundle.getString("success"));
        return null;

    }

    public List<String> completeTags(String query) {
        List<Tag> fountTags = new ArrayList<Tag>();
        List<String> suggestions = new ArrayList<String>();
        try {
            fountTags = tagService.getTagList(query);
            for (Tag tag : fountTags) {
                suggestions.add(tag.getTag());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("suggestions list size " + suggestions.size());
        return suggestions;
    }

    public List<String> complete(String input) {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            System.out.print("input + i " + input + i);
            result.add(input + i);
        }
        return result;
    }

    public void handleSelect(SelectEvent event) {
        Object selectedObject = event.getObject();
        //   MessageUtil.addInfoMessage("selected.object", selectedObject);
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

//    public PropertyResourceBundle getBundle() {
//        FacesContext context = FacesContext.getCurrentInstance();
//        return context.getApplication().evaluateExpressionGet(context, "#{i18n}", PropertyResourceBundle.class);
//    }
    public void setTagService(TagService tagService) {
        this.tagService = tagService;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

}
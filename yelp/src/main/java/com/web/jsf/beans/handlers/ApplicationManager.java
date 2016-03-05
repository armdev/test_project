package com.web.jsf.beans.handlers;

import com.progress.backend.services.file.FileService;
import com.progress.backend.services.talk.TalkCategoryService;
import com.progress.backend.services.talk.TalkFlowService;
import com.progress.backend.services.talk.TalkService;
import com.progress.backend.services.user.PostService;
import com.progress.backend.services.user.UserFacebookService;
import com.progress.backend.services.user.UserService;
import java.io.Serializable;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

/**
 *
 * @author Home
 */
@ManagedBean(name = "applicationManager")
@ApplicationScoped
public class ApplicationManager implements Serializable {

    private static final long serialVersionUID = 1L;
    @ManagedProperty("#{userService}")
    private UserService userService;
    @ManagedProperty("#{userFacebookService}")
    private UserFacebookService userFacebookService;
    @ManagedProperty("#{postService}")
    private PostService postService;
    @ManagedProperty("#{fileService}")
    private FileService fileService;
    @ManagedProperty("#{talkCategoryService}")
    private TalkCategoryService talkCategoryService;
    @ManagedProperty("#{talkFlowService}")
    private TalkFlowService talkFlowService;
    @ManagedProperty("#{talkService}")
    private TalkService talkService;

    public ApplicationManager() {
    }

    public UserService getUserService() {
        return userService;
    }

    public UserFacebookService getUserFacebookService() {
        return userFacebookService;
    }

    public PostService getPostService() {
        return postService;
    }

    public FileService getFileService() {
        return fileService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setUserFacebookService(UserFacebookService userFacebookService) {
        this.userFacebookService = userFacebookService;
    }

    public void setPostService(PostService postService) {
        this.postService = postService;
    }

    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    public TalkCategoryService getTalkCategoryService() {
        return talkCategoryService;
    }

    public void setTalkCategoryService(TalkCategoryService talkCategoryService) {
        this.talkCategoryService = talkCategoryService;
    }

    public TalkFlowService getTalkFlowService() {
        return talkFlowService;
    }

    public void setTalkFlowService(TalkFlowService talkFlowService) {
        this.talkFlowService = talkFlowService;
    }

    public TalkService getTalkService() {
        return talkService;
    }

    public void setTalkService(TalkService talkService) {
        this.talkService = talkService;
    }
    
    

}

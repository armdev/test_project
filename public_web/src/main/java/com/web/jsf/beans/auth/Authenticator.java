package com.web.jsf.beans.auth;


import com.progress.backend.entities.UserEntity;
import com.web.jsf.utils.HttpJSFUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.apache.log4j.Logger;
import org.brickred.socialauth.AuthProvider;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.SocialAuthConfig;
import org.brickred.socialauth.SocialAuthManager;
import org.brickred.socialauth.util.SocialAuthUtil;


@ManagedBean(name = "socialauthenticator")
@SessionScoped
public class Authenticator implements Serializable {

    private static final Logger log = Logger.getLogger(Authenticator.class);
  
    @ManagedProperty("#{facebookAuthManagerBean}")
    private FacebookAuthManagerBean facebookAuthManagerBean;
    @ManagedProperty("#{mailProps}")
    private ResourceBundle mailBundle = null;
    @ManagedProperty("#{i18n}")
    private ResourceBundle bundle = null;
    @ManagedProperty("#{userFacebookController}")
    private UserFacebookController userFacebookController = null;


    private UserEntity user;
    private boolean checkEmail = false;
    private boolean checkFileSize = false;
    

   
    private FacesContext context = null;
    private ExternalContext ex = null;
    private Map<String, Object> sessionMap;
  private UserFacebookEntity userFacebook;
    private Part uploadedFile;
    private UIComponent componentAvatar;
    private UIComponent componentEmail;
    private String avatarStyle = "alert-danger";

    public Authenticator() {

    }

    @PostConstruct
    public void init() {
        log.info("Authenticator init called");
        context = FacesContext.getCurrentInstance();
        ex = context.getExternalContext();
        user = new UserEntity();
    }
    
     public void protectAvatar() {
     if(facebookAuthManagerBean.getAvatar() == null){
         try {
             FacesContext.getCurrentInstance().getExternalContext().redirect("index.jsf?avatar=notset");
         } catch (IOException ex) {
             java.util.logging.Logger.getLogger(Authenticator.class.getName()).log(Level.SEVERE, null, ex);
         }
     }

    }

    public void clearUser() {
        user = new UserEntity();
        user.setEmail(null);
        
       // checkAvatar = false;
        checkEmail = false;

    }

    public String initAvatar() {
    
        return "index";
    }

    public String facebookLogin() {
        log.info("Facebook login called");
        try {
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            SocialAuthConfig config = SocialAuthConfig.getDefault();
            config.load("oauth_consumer.properties");
            SocialAuthManager manager = new SocialAuthManager();
            manager.setSocialAuthConfig(config);
            String successUrl = mailBundle.getString("fbCallbackUrl");
            log.info("successUrl " + successUrl);
            String url = manager.getAuthenticationUrl(org.brickred.socialauth.util.Constants.FACEBOOK, successUrl);

            log.info("put authManager " + manager);
            facebookAuthManagerBean.setManager(manager);

            log.info("put socialId facebook");
            log.info("url " + url);
           // log.info("set avatar " + avatar);
            //facebookAuthManagerBean.setAvatar(avatar);
            facebookAuthManagerBean.setSocialId("facebook");
            log.info("FB LOGIN, GO TO FB.com ");
            FacesContext.getCurrentInstance().responseComplete();
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
            //  return "back";
        } catch (Exception e) {
            log.info("Error during FB login " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return null;
    }

 

   

    public String initFacebook() {
        if (user == null) {
            log.info("User is null");
            user = new UserEntity();
        }
        if (userFacebook == null) {
            userFacebook = new UserFacebookEntity();
        }
        log.info("FacebookBean init called");
        log.info("init facebook called");
        try {
            log.info("@socialId " + facebookAuthManagerBean.getSocialId());
            if (facebookAuthManagerBean == null || facebookAuthManagerBean.getSocialId() == null) {
                log.info("@facebook login again  ");
                this.facebookLogin();
                return null;
            }
            if (facebookAuthManagerBean.getSocialId() != null && facebookAuthManagerBean.getSocialId().equals("facebook")) {
                HttpServletRequest request = HttpJSFUtil.getRequest();
                log.info("@facebook manager " + facebookAuthManagerBean.getManager());
                if (facebookAuthManagerBean.getManager() == null) {
                    log.info("@facebook manager is null return " + facebookAuthManagerBean.getManager());
                    return null;
                }
                Map<String, String> paramsMap = SocialAuthUtil.getRequestParametersMap(request);
                log.info("@facebook paramsMap size " + paramsMap.size());
//                if (paramsMap == null) {
//                    log.info("@paramsMap is null return " + paramsMap);
//                    return null;
//                }
                log.info("@@@call provider ");
                AuthProvider provider = facebookAuthManagerBean.getManager().connect(paramsMap);
                if (provider == null) {
                    return "failalert";
                }
                log.info("@provider " + provider);
                Profile p = provider.getUserProfile();
                log.info("@p profile  " + p);
                log.info("@paramsMap " + paramsMap);

                if (facebookAuthManagerBean.getAvatar() != null) {
                    log.info("@GET AVATAR@@@@@@@@@@@ " + facebookAuthManagerBean.getAvatar());
                  //  user.setAvatar(facebookAuthManagerBean.getAvatar());
                }
                if (p.getEmail() != null) {
                    user.setEmail(p.getEmail());
                }
                if (p.getFirstName() != null) {
                    user.setFirstname(p.getFirstName());
                }
                if (p.getLastName() != null) {
                    user.setLastname(p.getLastName());
                }
                if (p.getLanguage() != null) {
                    user.setLanguage(p.getLanguage());
                }
                if (p.getDob() != null) {
                    userFacebook.setBirthday(p.getDob().toString());
                }
                if (p.getCountry() != null) {
                    userFacebook.setCountry(p.getCountry());
                }
                if (p.getFirstName() != null) {
                    userFacebook.setFirstname(p.getFirstName());
                }
                if (p.getGender() != null) {
                    userFacebook.setGender(p.getGender());
                }
                if (p.getLanguage() != null) {
                    userFacebook.setLanguage(p.getLanguage());
                }
                if (p.getLastName() != null) {
                    userFacebook.setLastname(p.getLastName());
                }
                if (p.getLocation() != null) {
                    userFacebook.setLocation(p.getLocation());
                }
                if (p.getProfileImageURL() != null) {
                    userFacebook.setProfileimageurl(p.getProfileImageURL());
                }
                
                 // FacesContext.getCurrentInstance().getExternalContext().redirect("pages/user/dashboard.jsf?success");

//                UserEntity u = controller.getAccountByAvatarAndByEmail(user.getEmail(), user.getAvatar());
//                if (u != null) {
//                    checkAvatar = false;
//                    log.info("FB login, Existing user , making context, LINE 359 ");
//                    this.makeContext(u);
//                    FacesContext.getCurrentInstance().getExternalContext().redirect("pages/user/dashboard.jsf?success");
//                    return "userpanel";
//                } else {
//                    checkAvatar = controller.checkUserAvatar(user.getAvatar().trim());
//                    log.info("FB checkAvatar avatar " + checkAvatar);
//                    if (!checkAvatar) {
//                        //user.setSocialMediaProvider(CommonConstants.FACEBOOK_PROVIDER);
//                        UserEntity retUser = null;
//                        if (facebookAuthManagerBean.getSocialId().equals("facebook")) {
//                            retUser = controller.registerNewUser(user, userFacebook, null);
//                            log.info("FB checkAvatar end " + checkAvatar);
//                            log.info("FB login, New user , making context, LINE 372 ");
//                            this.makeContext(retUser);
//                            FacesContext.getCurrentInstance().getExternalContext().redirect("pages/user/dashboard.jsf?success");
//                            return "userpanel";
//                        }
//                    }
//                }

            }
        } catch (Exception e) {
            log.info("Error during facebook init " + e.getLocalizedMessage());
            return "failalert";
        }
        return null;
    }

//    public String registerUserAfterFacebookCallBack() {
//        log.info("@@@@@Facebook registration started!!!!");
//        String navigation = null;
//        try {
//            User u = controller.getAccountByAvatarAndByEmail(user.getEmail(), user.getAvatar());
//            log.info("returned user " + u);
//            if (u != null) {
//                if (u.getStatus() == CommonConstants.ACTIVATED) {
//                    log.info("@@@@@@@@User already registered, do login");
//
//                    ExternalContext facesContext = FacesContext.getCurrentInstance().getExternalContext();
//                    Map<String, Object> sessionMap = facesContext.getSessionMap();
//                    sessionMap.remove("expired");
//                    UserContext userContext = (UserContext) facesContext.getSessionMap().get("userContext");
//                    if (userContext == null) {
//                        userContext = new UserContext();
//                        sessionMap.put("userContext", userContext);
//                    }
//                    SessionController sessionController = (SessionController) sessionMap.get("sessionController");
//                    if (sessionController == null) {
//                        sessionController = new SessionController();
//                        sessionMap.put("sessionController", sessionController);
//                    }
//                    userContext.setUser(u);
//                    sessionController.setUser(u);
//                    sessionController.setUserContext(userContext);
//                    try {
//                        log.info("Update user fb data");
//                        if (facebookAuthManagerBean != null && facebookAuthManagerBean.getSocialId().equals("facebook")) {
//                            UserFacebookEntity fb = controller.getAccountFacebookData(u.getId());
//                            if (fb == null) {
//                                if (userFacebook != null && userFacebook.getFirstname() != null) {
//                                    userFacebook.setUserId(u.getId());
//                                    userFacebookController.saveUserFacebookCredentials(userFacebook);
//                                }
//                            }
//                        }
//                        if (facebookAuthManagerBean != null && facebookAuthManagerBean.getSocialId().equals("google")) {
//                            UserGoogleEntity ug = controller.getAccountGoogleData(u.getId());
//                            if (ug == null) {
//                                if (userGoogleEntity != null && userGoogleEntity.getFirstname() != null) {
//                                    userGoogleEntity.setUserId(u.getId());
//                                    userGoogleController.saveUserGoogleCredentials(userGoogleEntity);
//                                }
//                            }
//                        }
//                        return "userpanel";
//                    } catch (Exception ex) {
//                        java.util.logging.Logger.getLogger(Authenticator.class.getName()).log(Level.SEVERE, null, ex);
//                        return null;
//                    }
//                } else if (u.getStatus() == CommonConstants.NOT_ACTIVATED) {
//                    FacesContext context = FacesContext.getCurrentInstance();
//                    context.addMessage(null, new FacesMessage(bundle.getString("notactivated")));
//                    return null;
//                }
//            }
//
//            if (user == null || user.getEmail() == null) {
//                return null;
//            }
//            log.info("User not existing, new user ");
////            checkEmail = controller.checkUserByEmail(user.getEmail());
////            if (checkEmail) {
////                FacesContext context = FacesContext.getCurrentInstance();
////                context.addMessage(null, new FacesMessage(bundle.getString("emailAlert")));
////                return null;
////            }
//            log.info("user with email " + user.getEmail() + " want to register");
//            checkAvatar = controller.checkUserAvatar(user.getAvatar());
//            log.info("checkAvatar " + user.getAvatar() + " already in db???  " + checkAvatar);
//
//            if (checkAvatar) {
//                log.info("Avatar already in use");
//                FacesContext context = FacesContext.getCurrentInstance();
//                context.addMessage(null, new FacesMessage(bundle.getString("avataralert")));
//                return null;
//            }
//
//            log.info("Start new user registration");
//            if (facebookAuthManagerBean.getSocialId().equals("facebook")) {
//                user.setSocialMediaProvider(CommonConstants.FACEBOOK_PROVIDER);
//            } else {
//                user.setSocialMediaProvider(CommonConstants.GOOGLE_PROVIDER);
//            }
//            User retUser = null;
//            if (facebookAuthManagerBean.getSocialId().equals("facebook")) {
//                retUser = controller.registerNewUser(user, userFacebook, null);
//            } else if (facebookAuthManagerBean.getSocialId().equals("google")) {
//                retUser = controller.registerNewUser(user, null, userGoogleEntity);
//            } else {
//                retUser = controller.registerNewUser(user, null, null);
//            }
//
//            log.info("Finish new user registration");
//            if (retUser != null) {
//                if (retUser.getStatus() == CommonConstants.ACTIVATED) {
//                    log.info("@@@@@@@@User already registered, do login");
//                    ExternalContext facesContext = FacesContext.getCurrentInstance().getExternalContext();
//                    Map<String, Object> sessionMap = facesContext.getSessionMap();
//                    sessionMap.remove("expired");
//                    UserContext userContext = (UserContext) facesContext.getSessionMap().get("userContext");
//                    if (userContext == null) {
//                        userContext = new UserContext();
//                        sessionMap.put("userContext", userContext);
//                    }
//                    SessionController sessionController = (SessionController) sessionMap.get("sessionController");
//                    if (sessionController == null) {
//                        sessionController = new SessionController();
//                        sessionMap.put("sessionController", sessionController);
//                    }
//                    userContext.setUser(retUser);
//                    sessionController.setUser(retUser);
//                    sessionController.setUserContext(userContext);
//                    try {
//
//                        return "userpanel";
//                    } catch (Exception ex) {
//                        java.util.logging.Logger.getLogger(Authenticator.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
//
//            } else {
//                navigation = "failalert";
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return navigation;
//    }

//    public void handleKeyEvent() {
//        if (user.getAvatar() != null && user.getAvatar().length() > 3) {
//            //S/ystem.out.println("avatar call " + user.getAvatar());
//            checkAvatar = controller.checkUserAvatar(user.getAvatar());
//            //  System.out.println("checkAvatar " + checkAvatar);
//            if (checkAvatar) {
//                avatarStyle = "alert-danger";
//                FacesContext context = FacesContext.getCurrentInstance();
//                context.addMessage(componentAvatar.getClientId(), new FacesMessage(bundle.getString("avataralert")));
//            } else {
//                //acceptAvatar = true;
//                avatarStyle = "alert-success";
//                checkAvatar = false;//alert
//                FacesContext context = FacesContext.getCurrentInstance();
//                context.addMessage(componentAvatar.getClientId(), new FacesMessage(bundle.getString("avatarsuccess")));
//                checkAvatar = false;//alert
//
//            }
//        }
//    }

//    public String registerUser() {
//        String navigation = null;
//        if (user == null || user.getEmail() == null) {
//            checkEmail = true;
//            return null;
//        }
//
//        User checkUser = controller.getAccountByAvatarAndByEmail(user.getEmail(), user.getAvatar());
//        if (checkUser != null) {
//            FacesContext context = FacesContext.getCurrentInstance();
//            context.addMessage(null, new FacesMessage(bundle.getString("emailAlert")));
//            return null;
//        }
//        checkAvatar = controller.checkUserAvatar(user.getAvatar());
//        if (checkAvatar) {
//            FacesContext context = FacesContext.getCurrentInstance();
//            context.addMessage(null, new FacesMessage(bundle.getString("avataralert")));
//            return null;
//        }
//
//        try {
//            long size = 0;
//
//            if (uploadedFile != null) {
//                size = uploadedFile.getSize();
//
//                long maxSize = 655350;
//
//                if (size > maxSize) {
//                    log.info("File size is too big ");
//                    FacesMessage msg = new FacesMessage(bundle.getString("fileistobig"), bundle.getString("fileistobig"));
//                    FacesContext.getCurrentInstance().addMessage(null, msg);
//                    checkFileSize = true;
//                    return null;
//                }
//
//                String content = uploadedFile.getContentType();
//                InputStream stream = uploadedFile.getInputStream();
//                byte[] contentBytes = new byte[(int) size];
//                stream.read(contentBytes);
//
//                if (!content.equalsIgnoreCase("image/jpeg") && !content.equalsIgnoreCase("image/pjpeg")
//                        && !content.equalsIgnoreCase("image/jpg") && !content.equalsIgnoreCase("image/gif")
//                        && !content.equalsIgnoreCase("image/x-png") && !content.equalsIgnoreCase("image/png")) {
//                    try {
//
//                        return null;
//                    } catch (Exception e) {
//                    }
//                }
//                if (size <= maxSize) {
//                    user.setAvatarPhoto(contentBytes);
//                }
//
//            }
//        } catch (Exception ioe) {
//            ioe.printStackTrace();
//        }
//        user.setSocialMediaProvider(0);
//        User retUser = controller.registerNewUser(user, null, null);
//
//        if (retUser != null) {
//            navigation = "successalert";
//        } else {
//            navigation = "failalert";
//        }
//        return navigation;
//    }

    public String makeContext(UserEntity u) {
//        ExternalContext facesContext = FacesContext.getCurrentInstance().getExternalContext();
//        Map<String, Object> sessionMap = facesContext.getSessionMap();
//        sessionMap.remove("expired");
//        UserContext userContext = (UserContext) facesContext.getSessionMap().get("userContext");
//        if (userContext == null) {
//            userContext = new UserContext();
//            sessionMap.put("userContext", userContext);
//        }
//        SessionController sessionController = (SessionController) sessionMap.get("sessionController");
//        if (sessionController == null) {
//            sessionController = new SessionController();
//            sessionMap.put("sessionController", sessionController);
//        }
//        userContext.setUser(u);
//        sessionController.setUser(u);
//        sessionController.setUserContext(userContext);
        return null;
    }

    public boolean isCheckFileSize() {
        return checkFileSize;
    }

    public void setCheckFileSize(boolean checkFileSize) {
        this.checkFileSize = checkFileSize;
    }

    public Part getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(Part uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public UserFacebookEntity getUserFacebook() {
        return userFacebook;
    }

    public void setUserFacebook(UserFacebookEntity userFacebook) {
        this.userFacebook = userFacebook;
    }

    public void setUserFacebookController(UserFacebookController userFacebookController) {
        this.userFacebookController = userFacebookController;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public void setMailBundle(ResourceBundle mailBundle) {
        this.mailBundle = mailBundle;
    }

    public boolean isCheckEmail() {
        return checkEmail;
    }

    public void setCheckEmail(boolean checkEmail) {
        this.checkEmail = checkEmail;
    }

 

    public void setFacebookAuthManagerBean(FacebookAuthManagerBean facebookAuthManagerBean) {
        this.facebookAuthManagerBean = facebookAuthManagerBean;
    }


    public UIComponent getComponentAvatar() {
        return componentAvatar;
    }

    public void setComponentAvatar(UIComponent componentAvatar) {
        this.componentAvatar = componentAvatar;
    }

    public String getAvatarStyle() {
        return avatarStyle;
    }

    public void setAvatarStyle(String avatarStyle) {
        this.avatarStyle = avatarStyle;
    }

    public UIComponent getComponentEmail() {
        return componentEmail;
    }

    public void setComponentEmail(UIComponent componentEmail) {
        this.componentEmail = componentEmail;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Map<String, Object> getSessionMap() {
        return sessionMap;
    }

    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }

  

}

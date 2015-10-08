package com.progress.backend.services.user;

import com.mongodb.*;
import com.progress.backend.connections.DbInitBean;

import com.progress.backend.entities.UserEntity;
import com.progress.backend.entities.UserFacebookEntity;
import com.progress.backend.utils.CommonUtils;
import com.progress.backend.utils.StatusTypeConstants;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import net.karmafiles.ff.core.tool.dbutil.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 *
 * @author armen.arzumanyan@gmail.com
 */
@Service("userService")
@Component
public class UserService implements Serializable {
    private static final long serialVersionUID = 1L;
    @Autowired
    private DbInitBean initDatabase;
    
    @Autowired
    private UserFacebookService userFacebookService;

    public UserEntity save(UserEntity userEntity, UserFacebookEntity userFacebookEntity) {
        try {
            UUID profileId = UUID.randomUUID();
            userEntity.setId(CommonUtils.longValue(DbInitBean.getNextId(initDatabase.getDatabase(), "userSeqGen")));
            userEntity.setRegisteredDate(new Date(System.currentTimeMillis()));
            //userEntity.setPasswd(CommonUtils.hashPassword(userEntity.getPasswd().trim()));
            userEntity.setStatus(StatusTypeConstants.ACTIVE);
            userEntity.setProfileId(profileId.toString());
            DBObject dbObject = Converter.toDBObject(userEntity);
            WriteResult result = initDatabase.getUserCollection().save(dbObject, WriteConcern.SAFE);
           userFacebookService.save(userFacebookEntity, userEntity.getId());
            System.out.println("Saving Facebook data from user Service");
//            userFacebookEntity.setId(CommonUtils.longValue(DbInitBean.getNextId(initDatabase.getDatabase(), "fbSeqGen")));
//            userFacebookEntity.setUserId(userEntity.getId());
//            userFacebookEntity.setRegisteredDate(new Date(System.currentTimeMillis()));
//            DBObject dbObjectnew = Converter.toDBObject(userFacebookEntity);
//            WriteResult resultNew = initDatabase.getFacebookCollection().save(dbObjectnew, WriteConcern.SAFE);
          
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userEntity;
    }

    public boolean updateProfile(Long userId, UserEntity entity) {
        try {
            BasicDBObject document = new BasicDBObject();
           
            System.out.println("Update user, image id is " + entity.getImageId());
            document.append("$set",
                    new BasicDBObject()
                    .append("email", entity.getEmail())
                    .append("firstname", entity.getFirstname())
                    .append("lastname", entity.getLastname())
                    .append("currentJob", entity.getCurrentJob())
                    .append("country", entity.getCountry())
                    .append("city", entity.getCity())
                    .append("dateBirth", entity.getDateBirth())
                    .append("gender", entity.getGender())
                    .append("skillTags", entity.getSkillTags())
                    .append("skype", entity.getSkype())
                    .append("imageId", entity.getImageId())
                    .append("about", entity.getAbout()));
            initDatabase.getUserCollection().update(new BasicDBObject().append("id", userId), document);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public List<UserEntity> findAll() {
        List<UserEntity> userList = new ArrayList<UserEntity>();
        String sort = "registeredDate";
        String order = "desc";
        DBObject sortCriteria = new BasicDBObject(sort, "desc".equals(order) ? -1 : 1);
        BasicDBObject query = new BasicDBObject();
        DBCursor cursor = initDatabase.getUserCollection().find(query).sort(sortCriteria);
        try {
            while (cursor.hasNext()) {
                DBObject document = cursor.next();
                UserEntity entity = new UserEntity();
                entity = Converter.toObject(UserEntity.class, document);
                userList.add(entity);
            }
        } finally {
            cursor.close();
        }
        return userList;
    }

    public UserEntity findById(Long id) {
        UserEntity entity = null;
        BasicDBObject query = new BasicDBObject();
        query.put("id", id);
        DBCursor cursor = initDatabase.getUserCollection().find(query);
        try {
            if (cursor.count() > 0) {
                DBObject document = cursor.next();
                entity = new UserEntity();
                entity = Converter.toObject(UserEntity.class, document);
            }
        } finally {
            cursor.close();
        }
        return entity;
    }

    public UserEntity userLogin(String email, String passwd) {
        if (email == null || passwd == null) {
            return null;
        }
        UserEntity entity = null;
        BasicDBObject query = new BasicDBObject();
        query.put("email", email.trim());
        query.put("status", StatusTypeConstants.ACTIVE);
        query.put("passwd", CommonUtils.hashPassword(passwd.trim()));
        DBCursor cursor = initDatabase.getUserCollection().find(query);
        try {
            if (cursor.count() > 0) {
                DBObject document = cursor.next();
                entity = new UserEntity();
                entity = Converter.toObject(UserEntity.class, document);

            } else {
                return null;
            }
        } finally {
            cursor.close();
        }
        return entity;
    }

    public Integer getUsersCount() {
        Integer listCount = 0;
        try {
            BasicDBObject query = new BasicDBObject();
            DBCursor cursor = initDatabase.getUserCollection().find(query);
            listCount = cursor.count();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listCount;
    }

    public boolean updateImageId(Long userId, String imageId) {
        try {
            BasicDBObject document = new BasicDBObject();
            document.append("$set", new BasicDBObject().append("imageId", imageId));
            initDatabase.getUserCollection().update(new BasicDBObject().append("id", userId), document);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public void updatePassword(Long id, String password) {
        BasicDBObject document = new BasicDBObject();
        document.append("$set", new BasicDBObject()
                .append("passwd", CommonUtils.hashPassword(password)));
        initDatabase.getUserCollection().update(new BasicDBObject().append("id", id), document);
    }

    public UserEntity getUserByEmail(String email) {
        UserEntity entity = null;
        BasicDBObject query = new BasicDBObject();
        query.put("email", email);
        DBCursor cursor = initDatabase.getUserCollection().find(query);
        try {
            if (cursor.count() > 0) {
                DBObject document = cursor.next();
                entity = new UserEntity();
                entity = Converter.toObject(UserEntity.class, document);
            }
        } finally {
            cursor.close();
        }
        return entity;
    }

    public UserEntity getUserByProfileId(String profileId) {
        UserEntity entity = null;
        BasicDBObject query = new BasicDBObject();
        query.put("profileId", profileId);
        DBCursor cursor = initDatabase.getUserCollection().find(query);
        try {
            if (cursor.count() > 0) {
                DBObject document = cursor.next();
                entity = new UserEntity();
                entity = Converter.toObject(UserEntity.class, document);
            }
        } finally {
            cursor.close();
        }
        return entity;
    }

    public boolean checkEmail(String email) {
        boolean retValue = false;
        BasicDBObject query = new BasicDBObject();
        query.put("email", email);
        DBCursor cursor = initDatabase.getUserCollection().find(query);
        try {
            if (cursor == null) {
                return false;
            }
            if (cursor.count() > 0) {
                retValue = true;
            } else {
                return false;
            }
        } finally {
            cursor.close();
        }
        return retValue;
    }
}

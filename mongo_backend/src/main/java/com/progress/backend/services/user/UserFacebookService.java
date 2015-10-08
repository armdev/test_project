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
@Service("userFacebookService")
@Component
public class UserFacebookService implements Serializable {

    private static final long serialVersionUID = 1L;
    @Autowired
    private DbInitBean initDatabase;

    public UserFacebookEntity save(UserFacebookEntity entity, Long userId) {
        try {
            System.out.println("Save or update facebook data");
            entity.setId(CommonUtils.longValue(DbInitBean.getNextId(initDatabase.getDatabase(), "fbSeqGen")));
            entity.setUserId(userId);
            entity.setRegisteredDate(new Date(System.currentTimeMillis()));
            DBObject dbObject = Converter.toDBObject(entity);
            WriteResult result = initDatabase.getFacebookCollection().save(dbObject, WriteConcern.SAFE);
           
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }

    public List<UserFacebookEntity> findAll() {
        List<UserFacebookEntity> userList = new ArrayList<UserFacebookEntity>();
        String sort = "registeredDate";
        String order = "desc";
        DBObject sortCriteria = new BasicDBObject(sort, "desc".equals(order) ? -1 : 1);
        BasicDBObject query = new BasicDBObject();
        DBCursor cursor = initDatabase.getFacebookCollection().find(query).sort(sortCriteria);
        try {
            while (cursor.hasNext()) {
                DBObject document = cursor.next();
                UserFacebookEntity entity = new UserFacebookEntity();
                entity = Converter.toObject(UserFacebookEntity.class, document);
                userList.add(entity);
            }
        } finally {
            cursor.close();
        }
        return userList;
    }

    public UserFacebookEntity findByUserId(Long userId) {
        UserFacebookEntity entity = null;
        BasicDBObject query = new BasicDBObject();
        query.put("userId", userId);
        DBCursor cursor = initDatabase.getFacebookCollection().find(query);
        try {
            if (cursor.count() > 0) {
                DBObject document = cursor.next();
                entity = new UserFacebookEntity();
                entity = Converter.toObject(UserFacebookEntity.class, document);
            }
        } finally {
            cursor.close();
        }
        return entity;
    }

   

    public Integer getCount() {
        Integer listCount = 0;
        try {
            BasicDBObject query = new BasicDBObject();
            DBCursor cursor = initDatabase.getFacebookCollection().find(query);
            listCount = cursor.count();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listCount;
    }

   
    public UserFacebookEntity getFacebookByEmail(String email) {
        UserFacebookEntity entity = null;
        BasicDBObject query = new BasicDBObject();
        query.put("email", email);
        DBCursor cursor = initDatabase.getFacebookCollection().find(query);
        try {
            if (cursor.count() > 0) {
                DBObject document = cursor.next();
                entity = new UserFacebookEntity();
                entity = Converter.toObject(UserFacebookEntity.class, document);
            }
        } finally {
            cursor.close();
        }
        return entity;
    }

  
    
}

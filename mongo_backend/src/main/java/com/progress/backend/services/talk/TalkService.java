package com.progress.backend.services.talk;

import com.mongodb.*;
import com.progress.backend.connections.MongoCoreService;

import com.progress.backend.entities.TalkEntity;

import com.progress.backend.entities.UserEntity;
import com.progress.backend.entities.UserFacebookEntity;
import com.progress.backend.services.user.UserFacebookService;
import com.progress.backend.services.user.UserService;
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
@Service("talkService")
@Component
public class TalkService implements Serializable {

    @Autowired
    private MongoCoreService mongoCoreService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserFacebookService userFacebookService;

    public void save(TalkEntity entity) {
        try {

            BasicDBObject document = new BasicDBObject();
            entity.setId(CommonUtils.longValue(MongoCoreService.getNextId(mongoCoreService.getDatabase(), "talkSeqGen")));
            entity.setDateCreated(new Date(System.currentTimeMillis()));
            DBObject dbObject = Converter.toDBObject(entity);
            System.out.println("Title::::: " + entity.getTitle());
            System.out.println("Message::::: " + entity.getMessage());
            mongoCoreService.getTalkCollection().save(dbObject, WriteConcern.SAFE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean update(Long id, TalkEntity entity) {
        try {
            DBObject document = Converter.toDBObject(entity);
            mongoCoreService.getTalkCollection().update(new BasicDBObject().append("id", id), document);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public Integer getCount() {
        Integer listCount = 0;
        try {
            BasicDBObject query = new BasicDBObject();
            DBCursor cursor = mongoCoreService.getTalkCollection().find(query);
            listCount = cursor.count();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listCount;
    }

    public List<TalkEntity> findAll() {
        List<TalkEntity> list = new ArrayList<TalkEntity>();
        String sort = "dateCreated";
        String order = "desc";
        DBObject sortCriteria = new BasicDBObject(sort, "desc".equals(order) ? -1 : 1);
        BasicDBObject query = new BasicDBObject();
        DBCursor cursor = mongoCoreService.getTalkCollection().find(query).sort(sortCriteria);
        try {
            while (cursor.hasNext()) {
                DBObject document = cursor.next();
                TalkEntity entity = new TalkEntity();
                entity = Converter.toObject(TalkEntity.class, document);
                UserEntity user = userService.findById(entity.getUserId());
                UserFacebookEntity userFacebookEntity = userFacebookService.findByUserId(entity.getUserId());
                entity.setUser(user);
                entity.setUserFacebookEntity(userFacebookEntity);
                list.add(entity);
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    public List<TalkEntity> findAllByCategory(Long categoryId) {
        List<TalkEntity> list = new ArrayList<TalkEntity>();
        String sort = "dateCreated";
        String order = "desc";
        DBObject sortCriteria = new BasicDBObject(sort, "desc".equals(order) ? -1 : 1);
        BasicDBObject query = new BasicDBObject();
        query.put("categoryId", categoryId);
        DBCursor cursor = mongoCoreService.getTalkCollection().find(query).sort(sortCriteria);
        try {
            while (cursor.hasNext()) {
                DBObject document = cursor.next();
                TalkEntity entity = new TalkEntity();
                entity = Converter.toObject(TalkEntity.class, document);
                list.add(entity);
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    public Integer getCountByCategory(Long categoryId) {
        Integer listCount = 0;
        try {
            BasicDBObject query = new BasicDBObject();
            query.put("categoryId", categoryId);
            DBCursor cursor = mongoCoreService.getTalkCollection().find(query);
            listCount = cursor.count();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listCount;
    }

    public List<TalkEntity> findAllByCategoryAndUserId(Long categoryId, Long userId) {
        List<TalkEntity> list = new ArrayList<TalkEntity>();
        String sort = "dateCreated";
        String order = "desc";
        DBObject sortCriteria = new BasicDBObject(sort, "desc".equals(order) ? -1 : 1);
        BasicDBObject query = new BasicDBObject();
        query.put("categoryId", categoryId);
        query.put("userId", userId);
        DBCursor cursor = mongoCoreService.getTalkCollection().find(query).sort(sortCriteria);
        try {
            while (cursor.hasNext()) {
                DBObject document = cursor.next();
                TalkEntity entity = new TalkEntity();
                entity = Converter.toObject(TalkEntity.class, document);
                list.add(entity);
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    public Integer getCountByCategoryAndUserId(Long categoryId, Long userId) {
        Integer listCount = 0;
        try {
            BasicDBObject query = new BasicDBObject();
            query.put("categoryId", categoryId);
            query.put("userId", userId);
            DBCursor cursor = mongoCoreService.getTalkCollection().find(query);
            listCount = cursor.count();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listCount;
    }

    public TalkEntity findById(Long id) {
        TalkEntity entity = null;
        BasicDBObject query = new BasicDBObject();
        query.put("id", id);
        DBCursor cursor = mongoCoreService.getTalkCollection().find(query);
        try {
            if (cursor.count() > 0) {
                DBObject document = cursor.next();
                entity = new TalkEntity();
                entity = Converter.toObject(TalkEntity.class, document);
                UserEntity user = userService.findById(entity.getUserId());
                UserFacebookEntity userFacebookEntity = userFacebookService.findByUserId(entity.getUserId());
                entity.setUser(user);
                entity.setUserFacebookEntity(userFacebookEntity);
            }
        } finally {
            cursor.close();
        }
        return entity;
    }

    public void remove(Long id) {
        try {
            BasicDBObject document = new BasicDBObject();
            document.put("id", id);
            mongoCoreService.getTalkCollection().remove(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

package com.progress.backend.services.user;

import com.mongodb.*;
import com.progress.backend.connections.DbInitBean;
import com.progress.backend.entities.PortfolioEntity;

import com.progress.backend.entities.UserEntity;
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
@Service("portfolioService")
@Component
public class PortfolioService implements Serializable {

    private static final long serialVersionUID = 1L;
    @Autowired
    private DbInitBean initDatabase;

    public boolean save(PortfolioEntity entity) {
        try {
            entity.setId(CommonUtils.longValue(DbInitBean.getNextId(initDatabase.getDatabase(), "portfolioSeqGen")));
            entity.setRegisteredDate(new Date(System.currentTimeMillis()));
            DBObject dbObject = Converter.toDBObject(entity);
            WriteResult result = initDatabase.getPortfolioCollection().save(dbObject, WriteConcern.SAFE);
            if (result.getError() == null) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateProfile(Long userId, PortfolioEntity entity) {
        try {
            BasicDBObject document = new BasicDBObject();

            document.append("$set",
                    new BasicDBObject()
                    .append("title", entity.getTitle())
                    .append("description", entity.getDescription())
                    .append("link", entity.getLink())
                    .append("categoryId", entity.getCategoryId())
                    .append("price", entity.getPrice())
                    .append("imageId", entity.getImageId())
                    .append("status", entity.getStatus()));
            initDatabase.getPortfolioCollection().update(new BasicDBObject().append("id", userId), document);
        } catch (Exception e) {
        }
        return true;
    }

    public List<PortfolioEntity> findAll() {
        List<PortfolioEntity> list = new ArrayList<PortfolioEntity>();
        String sort = "registeredDate";
        String order = "desc";
        DBObject sortCriteria = new BasicDBObject(sort, "desc".equals(order) ? -1 : 1);
        BasicDBObject query = new BasicDBObject();
        DBCursor cursor = initDatabase.getPortfolioCollection().find(query).sort(sortCriteria);
        try {
            while (cursor.hasNext()) {
                DBObject document = cursor.next();
                PortfolioEntity entity = new PortfolioEntity();
                entity = Converter.toObject(PortfolioEntity.class, document);
                list.add(entity);
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    public List<PortfolioEntity> findAllByCategoryIdAndUserId(Long userId, Long categoryId) {
        List<PortfolioEntity> list = new ArrayList<PortfolioEntity>();
        String sort = "registeredDate";
        String order = "desc";
        DBObject sortCriteria = new BasicDBObject(sort, "desc".equals(order) ? -1 : 1);
        BasicDBObject query = new BasicDBObject();
        query.put("userId", userId);
        query.put("categoryId", categoryId);
        DBCursor cursor = initDatabase.getPortfolioCollection().find(query).sort(sortCriteria);
        try {
            while (cursor.hasNext()) {
                DBObject document = cursor.next();
                PortfolioEntity entity = new PortfolioEntity();
                entity = Converter.toObject(PortfolioEntity.class, document);
                list.add(entity);
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    public List<PortfolioEntity> findAllByUserId(Long userId) {
        List<PortfolioEntity> list = new ArrayList<PortfolioEntity>();
        String sort = "registeredDate";
        String order = "desc";
        DBObject sortCriteria = new BasicDBObject(sort, "desc".equals(order) ? -1 : 1);
        BasicDBObject query = new BasicDBObject();
        query.put("userId", userId);

        DBCursor cursor = initDatabase.getPortfolioCollection().find(query).sort(sortCriteria);
        try {
            while (cursor.hasNext()) {
                DBObject document = cursor.next();
                PortfolioEntity entity = new PortfolioEntity();
                entity = Converter.toObject(PortfolioEntity.class, document);
                list.add(entity);
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    public PortfolioEntity findById(Long id, Long userId) {
        PortfolioEntity entity = null;
        BasicDBObject query = new BasicDBObject();
        query.put("id", id);
        query.put("userId", userId);
        DBCursor cursor = initDatabase.getPortfolioCollection().find(query);
        try {
            if (cursor.count() > 0) {
                DBObject document = cursor.next();
                entity = new PortfolioEntity();
                entity = Converter.toObject(PortfolioEntity.class, document);
            }
        } finally {
            cursor.close();
        }
        return entity;
    }

    public PortfolioEntity findByProfileId(Long id, Long profileId) {
        PortfolioEntity entity = null;
        BasicDBObject query = new BasicDBObject();
        query.put("id", id);
        query.put("profileId", profileId);
        DBCursor cursor = initDatabase.getPortfolioCollection().find(query);
        try {
            if (cursor.count() > 0) {
                DBObject document = cursor.next();
                entity = new PortfolioEntity();
                entity = Converter.toObject(PortfolioEntity.class, document);
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
            DBCursor cursor = initDatabase.getPortfolioCollection().find(query);
            listCount = cursor.count();
        } catch (Exception e) {
        }
        return listCount;
    }

    public boolean updateImageId(Long id, String imageId) {
        try {
            BasicDBObject document = new BasicDBObject();
            document.append("$set", new BasicDBObject().append("imageId", imageId));
            initDatabase.getPortfolioCollection().update(new BasicDBObject().append("id", id), document);
        } catch (Exception e) {
        }
        return true;
    }

}

package com.progress.backend.services.talk;

import com.mongodb.*;
import com.progress.backend.connections.MongoCoreService;
import com.progress.backend.entities.TalkFlowEntity;

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
@Service("talkFlowService")
@Component
public class TalkFlowService implements Serializable {

    @Autowired
    private MongoCoreService mongoCoreService;

    public void save(TalkFlowEntity entity) {
        try {

            BasicDBObject document = new BasicDBObject();
            entity.setId(CommonUtils.longValue(MongoCoreService.getNextId(mongoCoreService.getDatabase(), "talkflowSeqGen")));
            entity.setDateCreated(new Date(System.currentTimeMillis()));
            DBObject dbObject = Converter.toDBObject(entity);
            mongoCoreService.getTalkFlowCollection().save(dbObject, WriteConcern.SAFE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean update(Long id, TalkFlowEntity entity) {
        try {
            DBObject document = Converter.toDBObject(entity);
            mongoCoreService.getTalkFlowCollection().update(new BasicDBObject().append("id", id), document);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public Integer getCount() {
        Integer listCount = 0;
        try {
            BasicDBObject query = new BasicDBObject();
            DBCursor cursor = mongoCoreService.getTalkFlowCollection().find(query);
            listCount = cursor.count();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listCount;
    }

    public List<TalkFlowEntity> findAll() {
        List<TalkFlowEntity> list = new ArrayList<TalkFlowEntity>();
        String sort = "dateCreated";
        String order = "desc";
        DBObject sortCriteria = new BasicDBObject(sort, "desc".equals(order) ? -1 : 1);
        BasicDBObject query = new BasicDBObject();
        DBCursor cursor = mongoCoreService.getTalkFlowCollection().find(query).sort(sortCriteria);
        try {
            while (cursor.hasNext()) {
                DBObject document = cursor.next();
                TalkFlowEntity entity = new TalkFlowEntity();
                entity = Converter.toObject(TalkFlowEntity.class, document);
                list.add(entity);
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    public List<TalkFlowEntity> findAllByTalkId(Long talkId) {
        List<TalkFlowEntity> list = new ArrayList<TalkFlowEntity>();
        String sort = "dateCreated";
        String order = "asc";
        DBObject sortCriteria = new BasicDBObject(sort, "asc".equals(order) ? 1 : -1);
        BasicDBObject query = new BasicDBObject();
        query.put("talkId", talkId);
        DBCursor cursor = mongoCoreService.getTalkFlowCollection().find(query).sort(sortCriteria);
        try {
            while (cursor.hasNext()) {
                DBObject document = cursor.next();
                TalkFlowEntity entity = new TalkFlowEntity();
                entity = Converter.toObject(TalkFlowEntity.class, document);
                list.add(entity);
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    public Integer getCountByTalkId(Long talkId) {
        Integer listCount = 0;
        try {
            BasicDBObject query = new BasicDBObject();
            query.put("talkId", talkId);
            DBCursor cursor = mongoCoreService.getTalkFlowCollection().find(query);
            listCount = cursor.count();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listCount;
    }

    public TalkFlowEntity findById(Long id) {
        TalkFlowEntity entity = null;
        BasicDBObject query = new BasicDBObject();
        query.put("id", id);
        DBCursor cursor = mongoCoreService.getTalkFlowCollection().find(query);
        try {
            if (cursor.count() > 0) {
                DBObject document = cursor.next();
                entity = new TalkFlowEntity();
                entity = Converter.toObject(TalkFlowEntity.class, document);

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
            mongoCoreService.getTalkFlowCollection().remove(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

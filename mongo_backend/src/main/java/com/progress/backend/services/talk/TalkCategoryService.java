package com.progress.backend.services.talk;

import com.mongodb.*;
import com.progress.backend.connections.MongoCoreService;
import com.progress.backend.entities.TalkCategoryEntity;

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
@Service("talkCategoryService")
@Component
public class TalkCategoryService implements Serializable {

    @Autowired
    private MongoCoreService mongoCoreService;

    public void save(TalkCategoryEntity entity) {
        try {
            BasicDBObject document = new BasicDBObject();
            entity.setId(CommonUtils.longValue(MongoCoreService.getNextId(mongoCoreService.getDatabase(), "categorySeqGen")));
            entity.setDateCreated(new Date(System.currentTimeMillis()));
            DBObject dbObject = Converter.toDBObject(entity);
            mongoCoreService.getTalkCategoryCollection().save(dbObject, WriteConcern.SAFE);
        } catch (Exception e) {
        }
    }

    public boolean update(Long id, TalkCategoryEntity entity) {
        try {
            DBObject document = Converter.toDBObject(entity);
            mongoCoreService.getTalkCategoryCollection().update(new BasicDBObject().append("id", id), document);
        } catch (Exception e) {
        }
        return true;
    }

    public Integer getCount() {
        Integer listCount = 0;
        try {
            BasicDBObject query = new BasicDBObject();
            DBCursor cursor = mongoCoreService.getTalkCategoryCollection().find(query);
            listCount = cursor.count();
        } catch (Exception e) {
        }
        return listCount;
    }

    public List<TalkCategoryEntity> findAll() {
        List<TalkCategoryEntity> list = new ArrayList<>();
        String sort = "dateCreated";
        String order = "asc";
        DBObject sortCriteria = new BasicDBObject(sort, "asc".equals(order) ? 1 : -1);
        BasicDBObject query = new BasicDBObject();
        try (DBCursor cursor = mongoCoreService.getTalkCategoryCollection().find(query).sort(sortCriteria)) {
            while (cursor.hasNext()) {
                DBObject document = cursor.next();
                TalkCategoryEntity entity = new TalkCategoryEntity();
                entity = Converter.toObject(TalkCategoryEntity.class, document);
                list.add(entity);
            }
        }
        return list;
    }

    public TalkCategoryEntity findById(Long id) {
        TalkCategoryEntity entity = null;
        BasicDBObject query = new BasicDBObject();
        query.put("id", id);
        try (DBCursor cursor = mongoCoreService.getTalkCategoryCollection().find(query)) {
            if (cursor.count() > 0) {
                DBObject document = cursor.next();
                entity = new TalkCategoryEntity();
                entity = Converter.toObject(TalkCategoryEntity.class, document);

            }
        }
        return entity;
    }

    public void remove(Long id) {
        try {
            BasicDBObject document = new BasicDBObject();
            document.put("id", id);
            mongoCoreService.getTalkCategoryCollection().remove(document);
        } catch (Exception e) {
        }
    }

}

package com.progress.backend.services.user;

import com.mongodb.*;
import com.progress.backend.connections.DbInitBean;
import com.progress.backend.entities.PostBody;

import com.progress.backend.entities.UserEntity;
import com.progress.backend.utils.CommonUtils;
import com.progress.backend.utils.StatusTypeConstants;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.karmafiles.ff.core.tool.dbutil.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 *
 * @author armen.arzumanyan@gmail.com
 */
@Service("postService")
@Component
public class PostService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    private DbInitBean initDatabase;

    public PostBody save(PostBody entity) {
        try {

            entity.setId(CommonUtils.longValue(DbInitBean.getNextId(initDatabase.getDatabase(), "postSeqGen")));
            entity.setPostDate(new Date(System.currentTimeMillis()));

            DBObject dbObject = Converter.toDBObject(entity);
            WriteResult result = initDatabase.getPostCollection().save(dbObject, WriteConcern.SAFE);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }

    public List<PostBody> findAll() {
        List<PostBody> list = new ArrayList<PostBody>();
        String sort = "postDate";
        String order = "desc";
        DBObject sortCriteria = new BasicDBObject(sort, "desc".equals(order) ? -1 : 1);
        BasicDBObject query = new BasicDBObject();
        DBCursor cursor = initDatabase.getPostCollection().find(query).sort(sortCriteria);
        try {
            while (cursor.hasNext()) {
                DBObject document = cursor.next();
                PostBody entity = new PostBody();
                entity = Converter.toObject(PostBody.class, document);
                list.add(entity);
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    public List<PostBody> findByUserId(Long userId) {
        List<PostBody> list = new ArrayList<PostBody>();
        String sort = "postDate";
        String order = "desc";
        DBObject sortCriteria = new BasicDBObject(sort, "desc".equals(order) ? -1 : 1);
        BasicDBObject query = new BasicDBObject();
        query.put("userId", userId);
        DBCursor cursor = initDatabase.getPostCollection().find(query).sort(sortCriteria);
        try {
            while (cursor.hasNext()) {
                DBObject document = cursor.next();
                PostBody entity = new PostBody();
                entity = Converter.toObject(PostBody.class, document);
                list.add(entity);
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    public boolean remove(Long id, Long userId) {//companyId
        try {
            BasicDBObject document = new BasicDBObject();
            document.put("id", id);
            document.put("userId", userId);
            initDatabase.getPostCollection().remove(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public Integer getCount() {
        Integer listCount = 0;
        try {
            BasicDBObject query = new BasicDBObject();
            DBCursor cursor = initDatabase.getPostCollection().find(query);
            listCount = cursor.count();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listCount;
    }

}

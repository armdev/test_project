package com.progress.backend.services.tag;

import com.mongodb.*;
import com.progress.backend.connections.DbInitBean;
import com.progress.backend.entities.Tag;
import com.progress.backend.utils.CommonUtils;
import java.util.ArrayList;
import java.util.List;
import net.karmafiles.ff.core.tool.dbutil.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 *
 * @author armen.arzumanyan@gmail.com
 */
@Service("tagService")
@Component
public class TagService {

    @Autowired
    private DbInitBean initDatabase;

    public void save(Tag entity) {
        try {
            boolean check = checkTag(entity.getTag());
            if (check) {
                return;
            }
            entity.setId(CommonUtils.longValue(DbInitBean.getNextId(initDatabase.getDatabase(), "tagSeqGen")));
            DBObject dbObject = Converter.toDBObject(entity);
            initDatabase.getTagCollection().ensureIndex(new BasicDBObject("tag", "text"), new BasicDBObject("key", "text"));
            initDatabase.getTagCollection().createIndex(new BasicDBObject("id", 1));
            initDatabase.getTagCollection().save(dbObject, WriteConcern.SAFE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Tag> getTagList(String tag) {
        List<Tag> list = new ArrayList<Tag>();
        String sort = "key";
        String order = "asc";
        DBObject sortCriteria = new BasicDBObject(sort, "asc".equals(order) ? 1 : -1);
        BasicDBObject query = new BasicDBObject();
        if (tag != null) {
            query.put("tag", java.util.regex.Pattern.compile(tag));//like
        }
        DBCursor cursor = initDatabase.getTagCollection().find(query).sort(sortCriteria).limit(50);
        try {
            while (cursor.hasNext()) {
                DBObject document = cursor.next();
                Tag entity = new Tag();
                entity = Converter.toObject(Tag.class, document);
                list.add(entity);
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    public boolean checkTag(String tag) {
        boolean retValue = false;
        BasicDBObject query = new BasicDBObject();

        query.put("tag", tag);
        DBCursor cursor = initDatabase.getTagCollection().find(query);
        try {
            if (cursor == null) {
                return false;
            }
            if (cursor != null && cursor.count() > 0) {
                retValue = true;
            } else {
                return false;
            }
        } finally {
            cursor.close();
        }
        return retValue;
    }

    public Integer getTagCount(String key) {
        Integer listCount = 0;
        try {
            BasicDBObject query = new BasicDBObject();
            if (key != null) {
                query.put("key", key);
            }
            DBCursor cursor = initDatabase.getTagCollection().find(query);
            listCount = cursor.count();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listCount;
    }

}

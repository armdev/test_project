package com.progress.backend.connections;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import java.io.Serializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author armen.arzumanyan@gmail.com
 */
@Service("mongoCoreService")
public class MongoCoreService implements Serializable {

    private static final long serialVersionUID = 1L;
    @Autowired
    private MongoConnectionFactory mongoFactory;
    private transient DB database = null;
    private transient DBCollection userCollection = null;
    private transient DBCollection facebookCollection = null;
    private transient DBCollection fileCollection = null;
    private transient DBCollection postCollection = null;
    private transient DBCollection talkCategoryCollection = null;
    private transient DBCollection talkCollection = null;
    private transient DBCollection talkFlowCollection = null;

    public MongoCoreService() {
        Mongo mongo;
        try {
            mongoFactory = new MongoConnectionFactory();
            mongo = mongoFactory.createMongoInstance();
            database = mongo.getDB("younetdb");
            userCollection = database.getCollection("user");
            facebookCollection = database.getCollection("facebook");
            fileCollection = database.getCollection("filestorage");
            postCollection = database.getCollection("posts");
            talkCategoryCollection = database.getCollection("talkcategory");
            talkCollection = database.getCollection("talk");
            talkFlowCollection = database.getCollection("talkflow");
        } catch (MongoException ex) {
            ex.printStackTrace();
        }
    }

    public DBCollection getTalkFlowCollection() {
        return talkFlowCollection;
    }

    
    
    public DBCollection getTalkCollection() {
        return talkCollection;
    }

    public void setTalkCollection(DBCollection talkCollection) {
        this.talkCollection = talkCollection;
    }

    public DBCollection getTalkCategoryCollection() {
        return talkCategoryCollection;
    }

    public DBCollection getPostCollection() {
        return postCollection;
    }

    public DBCollection getFacebookCollection() {
        return facebookCollection;
    }

    public DBCollection getUserCollection() {
        return userCollection;
    }

    public DBCollection getFileCollection() {
        return fileCollection;
    }

    public DB getDatabase() {
        return database;
    }

    public static String getNextId(DB db, String seq_name) {
        String sequence_collection = "seq"; // the name of the sequence collection
        String sequence_field = "seq"; // the name of the field which holds the sequence

        DBCollection seq = db.getCollection(sequence_collection); // get the collection (this will create it if needed)               

        if (seq == null) {
            seq = db.createCollection(sequence_collection, null);
        }

        // this object represents your "query", its analogous to a WHERE clause in SQL
        DBObject query = new BasicDBObject();
        query.put("id", seq_name); // where id = the input sequence name

        // this object represents the "update" or the SET blah=blah in SQL
        DBObject change = new BasicDBObject(sequence_field, 1);
        DBObject update = new BasicDBObject("$inc", change); // the $inc here is a mongodb command for increment

        // Atomically updates the sequence field and returns the value for you
        DBObject res = seq.findAndModify(query, new BasicDBObject(), new BasicDBObject(), false, update, true, true);
        return res.get(sequence_field).toString();
    }
}

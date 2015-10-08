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
@Service("initDatabase")
public class DbInitBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Autowired
    private MongoConnectionFactory mongoFactory;
    private transient DB database = null;
    private transient DBCollection userCollection;
    private transient DBCollection jobCollection;
    private transient DBCollection fileCollection;
    private transient DBCollection portfolioFileCollection;
    private transient DBCollection portfolioCollection;
    private transient DBCollection tagCollection;
    private transient DBCollection resumeCollection;
    private transient DBCollection projectCollection;
    private transient DBCollection messagesCollection;

    public DbInitBean() {
        Mongo mongo;
        try {
            mongoFactory = new MongoConnectionFactory();
            mongo = mongoFactory.createMongoInstance();
            database = mongo.getDB("painters");
            userCollection = database.getCollection("users");
            jobCollection = database.getCollection("job");
            fileCollection = database.getCollection("filestorage");
            portfolioFileCollection = database.getCollection("portfoliofiles");
            tagCollection = database.getCollection("tag");
            resumeCollection = database.getCollection("resume");
            projectCollection = database.getCollection("project");
            messagesCollection = database.getCollection("messages");
            portfolioCollection = database.getCollection("portfolio");
        } catch (MongoException ex) {
            ex.printStackTrace();
        }
    }

    public DBCollection getPortfolioCollection() {
        return portfolioCollection;
    }

    
    
    public DBCollection getMessagesCollection() {
        return messagesCollection;
    }

    public DBCollection getProjectCollection() {
        return projectCollection;
    }

    public DBCollection getResumeCollection() {
        return resumeCollection;
    }

    public void setResumeCollection(DBCollection resumeCollection) {
        this.resumeCollection = resumeCollection;
    }

    public DBCollection getUserCollection() {
        return userCollection;
    }

    public DBCollection getJobCollection() {
        return jobCollection;
    }

    public DBCollection getFileCollection() {
        return fileCollection;
    }

    public DBCollection getTagCollection() {
        return tagCollection;
    }

    public DB getDatabase() {
        return database;
    }

    public DBCollection getPortfolioFileCollection() {
        return portfolioFileCollection;
    }

    public void setPortfolioFileCollection(DBCollection portfolioFileCollection) {
        this.portfolioFileCollection = portfolioFileCollection;
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

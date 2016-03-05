package com.progress.backend.connections;

import com.mongodb.Mongo;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import java.io.Serializable;
import java.net.UnknownHostException;
import org.springframework.stereotype.Service;

@Service("mongoConnectionFactory")
public class MongoConnectionFactory implements Serializable {

    private static final long serialVersionUID = 1L;
    private transient MongoOptions mongoOptions = null;
    private transient MongoOptionsFactory factory = null;

    public MongoConnectionFactory() {
        
        mongoOptions = new MongoOptions();
        factory = new MongoOptionsFactory();
        factory.setAutoConnectRetry(true);
        factory.setConnectionsPerHost(10000);
        factory.setConnectionTimeout(11);
     
        mongoOptions = factory.createMongoOptions();

    }

    public Mongo createMongoInstance() {
        Mongo mongo = null;
        try {
            ServerAddress serverAddress = new ServerAddress("localhost", 27017);
            mongo = new Mongo(serverAddress, mongoOptions);
            mongo.setWriteConcern(WriteConcern.SAFE);
        } catch (UnknownHostException e) {
            throw new MongoInitializationException("Could not create the default Mongo instance", e);
        }
        return mongo;
    }
}

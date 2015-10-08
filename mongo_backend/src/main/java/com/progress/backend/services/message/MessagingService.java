package com.progress.backend.services.message;

import com.progress.backend.services.user.UserService;
import com.mongodb.*;
import com.progress.backend.connections.DbInitBean;

import com.progress.backend.entities.MessagesEntity;
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
@Service("messagingService")
@Component
public class MessagingService implements Serializable {

    @Autowired
    private DbInitBean initDatabase;
    @Autowired
    private UserService userService;

    public static final Integer INBOX = 0;
    public static final Integer OUTBOX = 1;
    public static final Integer READ = 1;
    public static final Integer UNREAD = 0;

    public Integer getNewMessageCount(Long userId) {
        Integer listCount = 0;
        try {
            BasicDBObject query = new BasicDBObject();
            query.put("isRead", UNREAD);
            query.put("recipientId", userId);
            query.put("location", INBOX);
            DBCursor cursor = initDatabase.getMessagesCollection().find(query);
            listCount = cursor.count();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listCount;
    }

    public boolean checkSentMessageOwner(Long id, Long userId) {
        boolean retValue = false;
        BasicDBObject query = new BasicDBObject();
        query.put("messageId", id);
        query.put("userId", userId);
        query.put("location", OUTBOX);
        DBCursor cursor = initDatabase.getMessagesCollection().find(query);
        try {
            if (cursor != null && cursor.count() > 0) {
                retValue = true;
            }
        } finally {
            cursor.close();
        }
        return retValue;
    }

    public Integer getOutgoingMessageListCount(Long userId) {
        Integer listCount = 0;
        try {
            BasicDBObject query = new BasicDBObject();
            query.put("senderId", userId);
            query.put("location", OUTBOX);
            DBCursor cursor = initDatabase.getMessagesCollection().find(query);
            listCount = cursor.count();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listCount;
    }

    public List<MessagesEntity> getOutgoingMessageList(Long userId, Integer skip, Integer limit) {
        List<MessagesEntity> messagesList = new ArrayList<MessagesEntity>();
        String sort = "sentDatetime";
        String order = "desc";
        DBObject sortCriteria = new BasicDBObject(sort, "desc".equals(order) ? -1 : 1);
        BasicDBObject query = new BasicDBObject();
        query.put("senderId", userId);
        query.put("location", OUTBOX);
        DBCursor cursor = initDatabase.getMessagesCollection().find(query).sort(sortCriteria).skip(skip).limit(limit);
        try {
            while (cursor.hasNext()) {
                DBObject document = cursor.next();
                MessagesEntity entity = new MessagesEntity();
                entity = Converter.toObject(MessagesEntity.class, document);
                if (entity.getRecipientId() != StatusTypeConstants.ADMIN_USER) {
                    UserEntity user = userService.findById(entity.getRecipientId());
                    entity.setSenderName(user.getFirstname() + " " + user.getLastname());
                    entity.setProfileId(user.getProfileId());
                }
                messagesList.add(entity);
            }
        } finally {
            cursor.close();
        }
        return messagesList;
    }

    public void setMessageUnread(Long messageId) {
        BasicDBObject document = new BasicDBObject();
        document.append("$set", new BasicDBObject()
                .append("isRead", READ));
        initDatabase.getMessagesCollection().update(new BasicDBObject().append("id", messageId), document);
    }

    public boolean checkMessageOwner(Long senderId, Long userId) {
        boolean retValue = false;
        BasicDBObject query = new BasicDBObject();
        query.put("senderId", senderId);
        query.put("recipientId", userId);
        DBCursor cursor = initDatabase.getMessagesCollection().find(query);
        try {
            if (cursor != null && cursor.count() > 0) {
                retValue = true;
            }
        } finally {
            cursor.close();
        }
        return retValue;
    }

    public Integer getIncomingMessageListCount(Long userId) {
        Integer listCount = 0;
        try {
            BasicDBObject query = new BasicDBObject();
            query.put("recipientId", userId);
            query.put("location", INBOX);
            DBCursor cursor = initDatabase.getMessagesCollection().find(query);
            listCount = cursor.count();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listCount;
    }

    public List<MessagesEntity> getIncomingMessageList(Long userId, Integer skip, Integer limit) {
        List<MessagesEntity> messagesList = new ArrayList<MessagesEntity>();
        String sort = "sentDatetime";
        String order = "desc";
        DBObject sortCriteria = new BasicDBObject(sort, "desc".equals(order) ? -1 : 1);
        BasicDBObject query = new BasicDBObject();
        query.put("recipientId", userId);
        query.put("location", INBOX);
        DBCursor cursor = initDatabase.getMessagesCollection().find(query).sort(sortCriteria).skip(skip).limit(limit);
        try {
            while (cursor.hasNext()) {
                DBObject document = cursor.next();
                MessagesEntity entity = new MessagesEntity();
                entity = Converter.toObject(MessagesEntity.class, document);
                if (entity.getSenderId() != StatusTypeConstants.ADMIN_USER) {
                    UserEntity user = userService.findById(entity.getSenderId());
                    entity.setSenderName(user.getFirstname() + " " + user.getLastname());
                    entity.setProfileId(user.getProfileId());
                }
                messagesList.add(entity);
            }
        } finally {
            cursor.close();
        }
        return messagesList;
    }

    public void sendSystemMessage(Long senderId, Long recipientId, String subject, String body, Integer type) {
        if (senderId == null || senderId <= 0L) {
            return;
        }
        if (recipientId == null || recipientId <= 0L) {
            return;
        }
        try {
            BasicDBObject document = new BasicDBObject();
            Long id = (CommonUtils.longValue(DbInitBean.getNextId(initDatabase.getDatabase(), "msgsSeqGen")));
            document.put("id", id);
            document.put("senderId", senderId);
            document.put("recipientId", recipientId);
            document.put("subject", subject);
            document.put("body", body);
            document.put("sentDatetime", new Date(System.currentTimeMillis()));
            document.put("type", type);
            document.put("isRead", UNREAD);
            document.put("location", INBOX);
            initDatabase.getMessagesCollection().insert(document, WriteConcern.SAFE);

        } catch (Exception ex) {
            //  log.error(ex + (ex.getMessage() != null ? ex.getMessage() : ""));
        }
    }

    public void sendMessage(Long senderId, Long recipientId, String subject, String body, Integer type) {
        if (senderId == null || senderId <= 0L) {
            return;
        }
        if (recipientId == null || recipientId <= 0L) {
            return;
        }
        try {
            BasicDBObject document = new BasicDBObject();
            Long id = (CommonUtils.longValue(DbInitBean.getNextId(initDatabase.getDatabase(), "msgsSeqGen")));
            document.put("id", id);
            document.put("senderId", senderId);
            document.put("recipientId", recipientId);
            document.put("subject", subject);
            document.put("body", body);
            document.put("sentDatetime", new Date(System.currentTimeMillis()));
            document.put("type", type);
            document.put("isRead", UNREAD);
            document.put("location", OUTBOX);
            initDatabase.getMessagesCollection().insert(document, WriteConcern.SAFE);

            BasicDBObject inboxDpcument = new BasicDBObject();
            Long id1 = (CommonUtils.longValue(DbInitBean.getNextId(initDatabase.getDatabase(), "msgsSeqGen")));
            inboxDpcument.put("id", id1);
            inboxDpcument.put("senderId", senderId);
            inboxDpcument.put("recipientId", recipientId);
            inboxDpcument.put("subject", subject);
            inboxDpcument.put("body", body);
            inboxDpcument.put("sentDatetime", new Date(System.currentTimeMillis()));
            inboxDpcument.put("type", type);
            inboxDpcument.put("isRead", UNREAD);
            inboxDpcument.put("location", INBOX);
            initDatabase.getMessagesCollection().insert(inboxDpcument, WriteConcern.SAFE);

        } catch (Exception ex) {
            //  log.error(ex + (ex.getMessage() != null ? ex.getMessage() : ""));
        }
    }

    public void deleteMessage(Long senderId, Long recipientId) {
        try {
            BasicDBObject document = new BasicDBObject();
            document.put("senderId", senderId);
            document.put("recipientId", recipientId);
            initDatabase.getMessagesCollection().remove(document);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void save(MessagesEntity entity) {
        BasicDBObject document = new BasicDBObject();
        entity.setId(CommonUtils.longValue(DbInitBean.getNextId(initDatabase.getDatabase(), "msgsSeqGen")));
        entity.setSentDatetime(new Date(System.currentTimeMillis()));
        DBObject dbObject = Converter.toDBObject(entity);
        initDatabase.getMessagesCollection().insert(dbObject, WriteConcern.SAFE);
    }

    public MessagesEntity findById(Long id) {
        MessagesEntity entity = null;
        BasicDBObject query = new BasicDBObject();
        query.put("id", id);
        DBCursor cursor = initDatabase.getMessagesCollection().find(query);
        try {
            if (cursor.count() > 0) {
                DBObject document = cursor.next();
                entity = new MessagesEntity();
                entity = Converter.toObject(MessagesEntity.class, document);
            }
        } finally {
            cursor.close();
        }
        return entity;
    }

}

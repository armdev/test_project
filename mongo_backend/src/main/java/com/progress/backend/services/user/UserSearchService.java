package com.progress.backend.services.user;

import com.mongodb.*;
import com.progress.backend.connections.DbInitBean;
import com.progress.backend.entities.UserEntity;
import com.progress.backend.utils.StatusTypeConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 *
 * @author armen.arzumanyan@gmail.com
 */
@Service("userSearchService")
@Component
public class UserSearchService {

    @Autowired
    private DbInitBean initDatabase;

    public List<UserEntity> doAdvancedSearch(String searchString) {
        CommandResult result = executeFullTextSearch(searchString);
        //System.out.println("Result " + result);
        return extractSearchResultIds(result);
    }

    private CommandResult executeFullTextSearch(String searchString) {
        final DBObject textSearch = new BasicDBObject();
        textSearch.put("text", "users");
        textSearch.put("search", searchString);
        textSearch.put("limit", 100); // override default of 100
        //textSearch.put("project", new BasicDBObject("_id", 1));
        return initDatabase.getDatabase().command(textSearch);
    }

    private List<UserEntity> extractSearchResultIds(CommandResult result) {
        List<UserEntity> objectIds = new ArrayList<UserEntity>();
        try {
            BasicDBList resultList = (BasicDBList) result.get("results");
            if (resultList != null) {

                Iterator<Object> it = resultList.iterator();
                while (it.hasNext()) {
                    BasicDBObject resultContainer = (BasicDBObject) it.next();
                    UserEntity entity = new UserEntity();
                    BasicDBObject document = (BasicDBObject) resultContainer.get("obj");
                    Integer userType = (Integer) document.get("userType");

                    if (userType != null && userType == StatusTypeConstants.REGULAR_USER) {
                        entity.setId((Long) document.get("id"));
                        entity.setProfileId((String) document.get("profileId"));
                        entity.setEmail((String) document.get("email"));
                        entity.setFirstname((String) document.get("firstname"));
                        entity.setLastname((String) document.get("lastname"));
                        entity.setPasswd((String) document.get("passwd"));
                        entity.setUserType((Integer) document.get("userType"));

                        entity.setGender((String) document.get("gender"));
                        entity.setCurrentJob((String) document.get("currentJob"));
                        entity.setCountry((String) document.get("country"));
                        entity.setCity((String) document.get("city"));

                        entity.setImageId((String) document.get("imageId"));

                        entity.setSkillTags((List) document.get("skillTags"));
                        entity.setAbout((String) document.get("about"));

                        entity.setStatus((Integer) document.get("status"));
                        entity.setRegisteredDate((Date) document.get("registeredDate"));
                        objectIds.add(entity);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objectIds;
    }
}

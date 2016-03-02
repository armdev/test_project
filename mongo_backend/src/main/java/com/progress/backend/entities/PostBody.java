
package com.progress.backend.entities;

import java.io.Serializable;
import java.util.Date;
import org.bson.types.ObjectId;

/**
 *
 * @author IEUser
 */
public class PostBody implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private ObjectId _id;
    private Long id;
    private Long userId;
    
    private String title;
    private String url;
    private String image;
    private String keywords;
    private String description;
    private Date postDate;
    private Integer rate;
    
    public PostBody() {
    }
    
    
    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }   
    

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }
    
    
    

}

package com.progress.backend.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;


public class TalkEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    private ObjectId _id;
    private Long id;
    private Long categoryId;
    private Long userId;
    private String title;
    private String message;
    private String location;
    private Integer status;
    private Date dateCreated;
    private UserEntity user;
    private UserFacebookEntity userFacebookEntity;

    public TalkEntity() {
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }  

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public UserFacebookEntity getUserFacebookEntity() {
        return userFacebookEntity;
    }

    public void setUserFacebookEntity(UserFacebookEntity userFacebookEntity) {
        this.userFacebookEntity = userFacebookEntity;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 53 * hash + (this.categoryId != null ? this.categoryId.hashCode() : 0);
        hash = 53 * hash + (this.userId != null ? this.userId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TalkEntity other = (TalkEntity) obj;
        if (this._id != other._id && (this._id == null || !this._id.equals(other._id))) {
            return false;
        }
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.categoryId != other.categoryId && (this.categoryId == null || !this.categoryId.equals(other.categoryId))) {
            return false;
        }
        if (this.userId != other.userId && (this.userId == null || !this.userId.equals(other.userId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TalkEntity{" + "_id=" + _id + ", id=" + id + ", categoryId=" + categoryId + ", userId=" + userId + ", title=" + title + ", message=" + message + ", location=" + location + ", status=" + status + ", dateCreated=" + dateCreated + '}';
    }

}

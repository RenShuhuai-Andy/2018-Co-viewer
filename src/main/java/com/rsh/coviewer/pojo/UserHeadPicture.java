package com.rsh.coviewer.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户头像的序列化对象
 * Created by rsh on 2018/7/4.
 */
public class UserHeadPicture implements Serializable {
    private Integer id;

    private String headpicture;

    private Date modified;

    private Short allow;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHeadpicture() {
        return headpicture;
    }

    public void setHeadpicture(String headpicture) {
        this.headpicture = headpicture == null ? null : headpicture.trim();
    }

    public Date getModified() {
        return modified == null ? null : (Date) modified.clone();
    }

    public void setModified(Date modified) {
        this.modified = modified == null ? null : (Date) modified.clone();
    }

    public Short getAllow() {
        return allow;
    }

    public void setAllow(Short allow) {
        this.allow = allow;
    }
}
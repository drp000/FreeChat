package com.drp.freechat.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * @author durui
 * @date 2020/6/1
 * @description
 */
@Entity
public class SmsConversations {
    @Id(autoincrement = true)
    private Long id;

    private int type;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private int status;
    private long date;

    private String body;

    @Generated(hash = 1612460864)
    public SmsConversations(Long id, int type, int status, long date, String body) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.date = date;
        this.body = body;
    }

    @Generated(hash = 1968924955)
    public SmsConversations() {
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getDate() {
        return this.date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

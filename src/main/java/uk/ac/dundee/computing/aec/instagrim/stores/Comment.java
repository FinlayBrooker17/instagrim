/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.stores;

import java.util.Date;
import java.util.List;



/**
 *
 * @author Finlay
 */

public class Comment {
    String user;
    Date time;
    String comment;
    List<String> replys;
    List<String> replyUser;
    List<Date> replyTime;

    public List<String> getReplys() {
        return replys;
    }

    public void setReplys(List<String> replys) {
        this.replys = replys;
    }

    public List<String> getReplyUser() {
        return replyUser;
    }

    public void setReplyUser(List<String> replyUser) {
        this.replyUser = replyUser;
    }

    public List<Date> getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(List<Date> replyTime) {
        this.replyTime = replyTime;
    }
   
    
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}

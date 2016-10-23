/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.models;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.dundee.computing.aec.instagrim.stores.Comment;

/**
 *
 * @author Finlay
 */
public class CommentModel {
    
    Cluster cluster;

    public void CommentModel() {

    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }
    
    public java.util.LinkedList<Comment> getPicComments(String picid){
        java.util.LinkedList<Comment> comments = new java.util.LinkedList<>();
        List<String> replys = new ArrayList<>();
        List<String> replyUser = new ArrayList<>();
        List<Date> replyTime = new ArrayList<>();
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("select * from comments where picid =?");
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        UUID.fromString(picid)));
        if (rs.isExhausted()) {
            System.out.println("No Images returned");
            session.close();
            return null;
        } else {
            for (Row row : rs) {
                Comment c = new Comment();
                c.setUser(row.getString("user"));
                //System.out.println(row.getString("user"));
                c.setComment(row.getString("comment"));
                //System.out.println(row.getString("comment"));
                c.setTime(row.getDate("time"));
                //System.out.println(row.getDate("time"));
                
                replys = row.getList("replys",String.class);
                replyUser = row.getList("replyuser",String.class);
                replyTime = row.getList("replytime",Date.class);
                
                c.setReplys(replys);
                c.setReplyUser(replyUser);
                c.setReplyTime(replyTime);
                
                comments.add(c);
                //System.out.println(""+add);
                
            }
            session.close();
            return comments;
        }
        //return comments;
    } 
    
    public void addComment(String picid, String postUser, String comment){
        Session session = cluster.connect("instagrim");
        
            PreparedStatement ps = session.prepare("Insert into comments (picid,user,comment,time) values(?,?,?,?)");
            BoundStatement bs = new BoundStatement(ps);
            Date time = new Date();
        try {
            time = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(time.toString());
        } catch (ParseException ex) {
            Logger.getLogger(CommentModel.class.getName()).log(Level.SEVERE, null, ex);
        }
            UUID picUUID = UUID.fromString(picid);
            session.execute(bs.bind(picUUID, postUser, comment, time));
            session.close();
    }
    
    public void addReply(String picid, String cTime, String reply, String userReply){
        Session session = cluster.connect("instagrim");
        List<String> replys = new ArrayList<>();
        List<String> replyUser = new ArrayList<>();
        List<Date> replyTime = new ArrayList<>();
        Date commentTime = null;
        try {
            commentTime = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(cTime);
        } catch (ParseException ex) {
            Logger.getLogger(CommentModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        PreparedStatement p = session.prepare("select replys,replyuser,replytime from comments where picid = ? AND time = ?");
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(p);
        rs = session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        UUID.fromString(picid),commentTime));
        if (rs.isExhausted()) {
            System.out.println("No Replies");
            session.close();
            return;
        }
        
        for (Row row : rs) {
            
            replys.addAll(row.getList("replys",String.class)) ;
            replyUser.addAll(row.getList("replyuser",String.class));
            replyTime.addAll(row.getList("replytime",Date.class));
        }    
        System.out.println(commentTime);
        Date date = new Date();
        replys.add(reply);
        replyUser.add(userReply);
        replyTime.add(date);
            
        PreparedStatement ps = session.prepare("INSERT INTO comments (picid,time,replys,replyuser,replytime) VALUES(?,?,?,?,?)");
        BoundStatement bs = new BoundStatement(ps);
        UUID picUUID = UUID.fromString(picid);
        session.execute(bs.bind(picUUID,commentTime,replys,replyUser,replyTime));
        session.close();
        
    }
}


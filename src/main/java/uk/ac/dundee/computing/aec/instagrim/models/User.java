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
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.dundee.computing.aec.instagrim.lib.AeSimpleSHA1;

/**
 *
 * @author Administrator
 */
public class User {
    Cluster cluster;
    public User(){
        
    }
    
    public boolean RegisterUser(String username, String Password){
        AeSimpleSHA1 sha1handler=  new AeSimpleSHA1();
        String EncodedPassword=null;
        try {
            EncodedPassword= sha1handler.SHA1(Password);
        }catch (UnsupportedEncodingException | NoSuchAlgorithmException et){
            System.out.println("Can't check your password");
            return false;
        }
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("insert into userprofiles (login,password) Values(?,?)");
       
        BoundStatement boundStatement = new BoundStatement(ps);
        session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        username,EncodedPassword));
        //We are assuming this always works.  Also a transaction would be good here !
        session.close();
        return true;
    }
    
    // this method is my code
    public boolean UpdateUser(String username,String firstname,String lastname,String email){
        // update a users profile with new information
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("insert into userprofiles (login,first_name,last_name,email) Values(?,?,?,?)");
        BoundStatement bs = new BoundStatement(ps);
        session.execute(bs.bind(username,firstname,lastname,email));
        session.close();
        return true;
    }
    
    public boolean UpdateFirstName(String username, String firstname){
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("insert into userprofiles (login,first_name) Values(?,?)");
        BoundStatement bs = new BoundStatement(ps);
        session.execute(bs.bind(username,firstname));
        session.close();
        return true;
    }
    
    public boolean UpdateLastName(String username, String lastname){
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("insert into userprofiles (login,last_name) Values(?,?)");
        BoundStatement bs = new BoundStatement(ps);
        session.execute(bs.bind(username,lastname));
        session.close();
        return true;
    }
    
    public boolean UpdateEmail(String username, String email){
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("insert into userprofiles (login,email) Values(?,?)");
        BoundStatement bs = new BoundStatement(ps);
        session.execute(bs.bind(username,email));
        session.close();
        return true;
    }
    
    public boolean UpdateProfilePic(String username, String picid){
        Session session = cluster.connect("instagrim");
        UUID pic = UUID.fromString(picid);
        
        PreparedStatement ps = session.prepare("insert into userprofiles (login,profile_pic) Values(?,?)");
        BoundStatement bs = new BoundStatement(ps);
        session.execute(bs.bind(username,pic));
        session.close();
        return true;
    }
    
    // this method is also mine
    public String[] getUserInfo(String user){
        String[] userInfo = new String[6];
        Session session = cluster.connect("instagrim");
        ResultSet rs = null;
        PreparedStatement ps = null;
        ps = session.prepare("select login,password,first_name,last_name,email,profile_pic from userprofiles where login =?");
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute(boundStatement.bind(user));
        for(Row row: rs){
            userInfo[0] = row.getString("login");
            userInfo[1] = row.getString("password");
            if(row.getString("first_name") == null){
                userInfo[2] = "null";
            }
            else{
                userInfo[2] = row.getString("first_name");
            };
            if(row.getString("last_name") == null){
                userInfo[3] = "null";
            }
            else{
                userInfo[3] = row.getString("last_name");
            };
            //userInfo[4] = row.getString("email");
            if(row.getString("email") == null){
                userInfo[4] = "null";
            }
            else{
                userInfo[4] = row.getString("email");
            };
            //userInfo[5] = row.getUUID("profile_pic").toString();
            if(row.getUUID("profile_pic") == null){
                userInfo[5] = "null";
            }
            else{
                userInfo[5] = row.getUUID("profile_pic").toString();
            };
            
            
        }
        session.close();
        return userInfo;
    }
    
    public boolean changePassword(String user,String oldPass,String newPass){
        String encPass = null;
        AeSimpleSHA1 sha1handler=  new AeSimpleSHA1();
        Session session = cluster.connect("instagrim");
        ResultSet rs = null;
        PreparedStatement ps = null;
        ps = session.prepare("select password from userprofiles where login =?");
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute(boundStatement.bind(user));
        for(Row row: rs){
            encPass = row.getString("password");
        }
        try {
            if(encPass.equals(sha1handler.SHA1(oldPass))){
                System.out.println("Passwords match");
                PreparedStatement p = session.prepare("insert into userprofiles (login,password) Values(?,?)");
                BoundStatement bs = new BoundStatement(p);
                session.execute(bs.bind(user,sha1handler.SHA1(newPass)));
                session.close();
                return true;
            }
                
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Can't change your password");
            session.close();
            return false;
        }
        session.close();
        return false;
    }
    
    public String changeUsername(String oldUsername,String newUsername){
        Session session = cluster.connect("instagrim");
        String password = null;
        String fname = null;
        String lname = null;
        String email = null;
        UUID propic = null; 
        UUID picid = null;
        Date picAdded = null;
        Date time = null;
        List<String> replyUser = new ArrayList<>();
        ////
        ResultSet rs = null;
        PreparedStatement ps = null;
        ps = session.prepare("select * from userprofiles where login =?");
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute(boundStatement.bind(oldUsername));
        for(Row row: rs){
            password = row.getString("password");
            fname = row.getString("first_name");
            lname = row.getString("last_name");
            email = row.getString("email");
            propic = row.getUUID("profile_pic");
        }
        ////
        ps = session.prepare("select * from userpiclist where user =?");
        BoundStatement b = new BoundStatement(ps);
        rs = session.execute(b.bind(oldUsername));
        for(Row row: rs){
            picid = row.getUUID("picid");
            picAdded = row.getDate("pic_added");
        }
        
        ////
        ps = session.prepare("insert into userprofiles (login,password,first_name,last_name,email,profile_pic) VALUES (?,?,?,?,?,?)");
        BoundStatement boundState = new BoundStatement(ps);
        session.execute(boundState.bind(newUsername,password,fname,lname,email,propic));
        ////
        ps = session.prepare("insert into userpiclist (picid,user,pic_added) VALUES (?,?,?)");
        BoundStatement bState = new BoundStatement(ps);
        session.execute(bState.bind(picid,newUsername,picAdded));
        ////
        ps = session.prepare("delete from userprofiles where login =?");
        BoundStatement bs = new BoundStatement(ps);
        session.execute(bs.bind(oldUsername));
        ////
        ps = session.prepare("delete from userpiclist where user =?");
        BoundStatement bounds = new BoundStatement(ps);
        session.execute(bounds.bind(oldUsername));
        
        ////
        ps = session.prepare("select picid from userpiclist where user =?");
        BoundStatement bt = new BoundStatement(ps);
        rs = session.execute(bt.bind(newUsername));
        for(Row row: rs){
            picid = row.getUUID("picid");
            ResultSet r = null;
            ps = session.prepare("insert into Pics (picid,user) VALUES (?,?)");
            BoundStatement bn = new BoundStatement(ps);
            r = session.execute(bn.bind(picid,newUsername));
        }
        ////
        ps = session.prepare("select * from comments");
        BoundStatement bos = new BoundStatement(ps);
        rs = session.execute(bos.bind());
        for(Row row: rs){
            picid = row.getUUID("picid");
            time = row.getDate("time");
            if(oldUsername.equals(row.getString("user"))){
                ResultSet r = null;
                ps = session.prepare("insert into comments (picid,user,time) VALUES (?,?,?)");
                BoundStatement bn = new BoundStatement(ps);
                r = session.execute(bn.bind(picid,newUsername,time));
            }
            replyUser.addAll(row.getList("replyuser", String.class));
            if(replyUser.contains(oldUsername)){
                for(int i = 0;i<replyUser.size();i++){
                    if(replyUser.get(i).equals(oldUsername)){
                        replyUser.remove(i);
                        replyUser.add(i, newUsername);
                    }    
                }
                ResultSet r = null;
                ps = session.prepare("insert into comments (picid,time,replyuser) VALUES (?,?,?)");
                BoundStatement bn = new BoundStatement(ps);
                r = session.execute(bn.bind(picid,time,replyUser));
            }
        }
        session.close();     
        return newUsername;
    }
    ////
    public boolean checkUsername(String username){
        Session session = cluster.connect("instagrim");
        ResultSet rs = null;
        PreparedStatement ps = null;
        ps = session.prepare("select * from userprofiles");
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute(boundStatement.bind());
        for(Row row: rs){
            if(username.equals(row.getString("login"))){
                session.close();
                return false;
            }
        }
        session.close();
        return true;
    }
    ////
    
    public boolean IsValidUser(String username, String Password){
        AeSimpleSHA1 sha1handler=  new AeSimpleSHA1();
        String EncodedPassword=null;
        try {
            EncodedPassword= sha1handler.SHA1(Password);
        }catch (UnsupportedEncodingException | NoSuchAlgorithmException et){
            System.out.println("Can't check your password");
            return false;
        }
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("select password from userprofiles where login =?");
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        username));
        if (rs.isExhausted()) {
            System.out.println("No Images returned");
            session.close();
            return false;
        } else {
            for (Row row : rs) {
               
                String StoredPass = row.getString("password");
                if (StoredPass.compareTo(EncodedPassword) == 0)
                    session.close();
                    return true;
            }
        }
   
    session.close();
    return false;  
    }
       public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    
}

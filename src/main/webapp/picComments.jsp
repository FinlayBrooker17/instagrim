<%-- 
    Document   : picComments
    Created on : 10-Oct-2016, 09:52:39
    Author     : Finlay
--%>
<%@page import="java.util.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>
<%@page import= "uk.ac.dundee.computing.aec.instagrim.models.User" %>
<%@page import= "uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Instagrim</title>
        <link rel="stylesheet" type="text/css" href="/Instagrim/Styles.css" />
    </head>
    <body>
        
        <header>
        
        <h1>InstaGrim ! </h1>
        <h2>Your world in Black and White</h2>
        </header>
        <%
            LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
            String picid = (String) request.getAttribute("picid");
            java.util.LinkedList<Comment> comments = (java.util.LinkedList<Comment>) request.getAttribute("comments");
            String userPic = (String) request.getAttribute("userPic");
            String dateAdded = (String) request.getAttribute("DateAdded");
        %>
        <nav>
            <ul>

               
                <li><a href="upload.jsp">Upload</a></li>
                    <%
                        
                        //LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
                        if (lg != null) {
                            String UserName = lg.getUsername();
                            if (lg.getlogedin()) {
                    %>

                <li><a href="/Instagrim/Images/<%=lg.getUsername()%>">Your Images</a></li>
                <li><a href="/Instagrim/Profile/<%=lg.getUsername()%>">Your Profile</a></li>
                <li><a href="/Instagrim/Images/*">Browse Images</a></li>
                <li><a href="/Instagrim/Logout">Logout</a></li>
                    <%}
                            }else{
                                %>
                 <li><a href="register.jsp">Register</a></li>
                <li><a href="login.jsp">Login</a></li>
                <%
                                        
                            
                    }%>
            </ul>
        </nav>
                <div id="imagebox">
                    <a href="/Instagrim/Image/<%=picid%>"><img src="/Instagrim/Image/<%=picid%>"></a>
                    <p>This image was uploaded by <%=userPic%> on <%=dateAdded%></p>
                </div>
                <div id="commentInput">
                    <form method="POST" action="Comments">
                        <h4>Comment on this image!</h4>
                        <input id="commentText" type="text" name="comment">
                        <input type="hidden" name="picid" value="<%=picid%>">
                        <input type="submit" value="Submit">
                    </form>
                </div>
                        <br/>
                <div id="Comments">
                    <%if (comments == null) {%>
                    <p>No Comments found</p>
                    <%
                        } else {%>
                        
                    <%
                            Iterator<Comment> iterator;
                            iterator = comments.iterator();
                            while (iterator.hasNext()) {
                            Comment c = (Comment) iterator.next();%>                           
                            <div id="comment"><h6><%=c.getUser()%> commented on <%=c.getTime()%> </h6><p><%=c.getComment()%></p>
                                
                                <form id="replyForm" method="POST" name="Comments">
                                    <input type="input" name="reply">
                                    <input type="hidden" name="picid" value="<%=picid%>">
                                    <input type="hidden" name="commentTime" value="<%=c.getTime()%>">
                                    <input type="submit" value="Submit">
                                </form>
                                    <%
                                        
                                        if(c.getReplys() == null){
                                            
                                        }else{
                                            for(int i = 0;i<c.getReplys().size();i++){
                                                %>
                                                <div id="reply">
                                                    <h6><%=c.getReplyUser().get(i)%> replied to <%=c.getUser()%>'s comment on <%=c.getReplyTime().get(i)%></h6>
                                                    <p><%=c.getReplys().get(i)%></p>
                                                </div>
                                
                                <%
                                            }
                                        }
                                    %>
                            </div>
                                                    <%}
                                }%>
                        
                </div>
        <footer>
            <ul>
                <li class="footer"><a href="/Instagrim">Home</a></li>
                <li>&COPY; Andy C</li>
            </ul>
        </footer>
    </body>
</html>

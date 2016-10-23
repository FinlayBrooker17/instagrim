<%-- 
    Document   : profile
    Created on : 26-Sep-2016, 09:02:41
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
            //String[] information = (String[]) request.getAttribute("info");
            String fname = (String) request.getAttribute("fname");
            String lname = (String) request.getAttribute("lname");
            String email = (String) request.getAttribute("email");
            String picid = (String) request.getAttribute("picid");
            
        %>
        <nav>
            <ul>

               
                <li><a href="/Instagrim/upload.jsp">Upload</a></li>
                
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
        
            <h1> Hello <%=lg.getUsername()%> </h1>
            <div id="container">
            <div id="profilepic">
                <img src="/Instagrim/Thumb/<%=picid%>">
            </div>
            <div id="profileinfo">
                <p>User Details</p>
                    <ul>
                        <li>First Name <%=fname%></li>
                        <li>Last Name <%=lname%></li>
                        <li>Email <%=email%></li>
                    </ul>
            </div>
            </div>        
            
            <form method="POST" action="Profile">
                <ul class="tab">
                    <li><a href="#" class="tablinks" onclick="show(event,'update')">Update Profile Information</a></li>
                    <li><a href="#" class="tablinks" onclick="show(event,'propic')">Update Profile Picture</a></li>
                    <li><a href="#" class="tablinks" onclick="show(event,'user')">Change Username</a></li>
                    <li><a href="#" class="tablinks" onclick="show(event,'pass')">Change Password</a></li>
                </ul>
                <div id="update" class="tabcontent">
                <ul>
                    <li>First Name <input type="text" name="fname"></li>
                    <li>Last Name <input type="text" name="lname"></li>
                    <li>E-mail <input type="text" name="email"></li>
                    <!--<li>Address <input type="text" name="address"></li>-->
                </ul>
                <br/>
                <input type="submit" value="Sumbit"> 
                </div>
                
            
                <div id="propic" class="tabcontent">
                    <h2>Profile Picture</h2>
                    <br/>
                    <%
            java.util.LinkedList<Pic> lsPics = (java.util.LinkedList<Pic>) request.getAttribute("images");
            if (lsPics == null) {
        %>
        <p>No Pictures found</p>
        <%
        } else {
            Iterator<Pic> iterator;
            iterator = lsPics.iterator();
            while (iterator.hasNext()) {
                Pic p = (Pic) iterator.next();

        %>
        <div id="imagelistbox">
        <img src="/Instagrim/Thumb/<%=p.getSUUID()%>"><br/>
        <button type="submit" value="<%=p.getSUUID()%>" name="picid" > Select </button>
        
        <br/>
        </div><%

            }
            }
        %>
                </div>
                <div id="user" class="tabcontent">
                    <ul>
                        <li>Change Username <input type="text" name="username"></li>
                    </ul>
                </div>
                <div id="pass" class="tabcontent">
                    <ul>
                        <li>Old Password <input type="password" name="oldpass"></li>
                        <li>New Password <input type="password" name="newpass"></li>
                        <li>Confirm New Password <input type="password" name="conpass"></li>
                    </ul>
                </div>
            </form>
            
            <!-- this script is not my code modified from w3 schools website -->
            <script>
                function show(evt, cityName) {
    var i, tabcontent, tablinks;
    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }
    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }
    document.getElementById(cityName).style.display = "block";
    evt.currentTarget.className += " active";
}
            </script>
            
            
            <footer>
            <ul>
                <li class="footer"><a href="/Instagrim">Home</a></li>
                <li>&COPY; Andy C</li>
            </ul>
        </footer>
    </body>
</html>

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.lib.Convertors;
import uk.ac.dundee.computing.aec.instagrim.models.PicModel;
import uk.ac.dundee.computing.aec.instagrim.models.User;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;

/**
 *
 * @author Finlay
 */
@WebServlet(urlPatterns = {"/Profile","/Profile/*"})

public class Profile extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Cluster cluster;
    private HashMap CommandsMap = new HashMap();
    
    public Profile(){
        super();
        CommandsMap.put("Profile",1);
    }
    
    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        cluster = CassandraHosts.getCluster();
    }
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    /*protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. *//*
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Profile</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet Profile at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }*/

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String args[] = Convertors.SplitRequestPath(request);
        int command;
        try {
            command = (Integer) CommandsMap.get(args[1]);
        } catch (Exception et) {
            error("Bad Operator", response);
            return;
        }
        switch (command) {
            case 1:
                if(args[2].contains("#")){
                    args[2] = args[2].replace('#', ' ');
                    args[2] = args[2].trim();
                }
                DisplayUserInfo(args[2],request, response);
                break;
            default:
                error("Bad Operator", response);
        }
        
        //processRequest(request, response);
    }

    private void DisplayUserInfo(String User, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User us = new User();
        us.setCluster(cluster);
        String[] info = us.getUserInfo(User);
        PicModel tm = new PicModel();
        tm.setCluster(cluster);
        java.util.LinkedList<Pic> lsPics = tm.getPicsForUser(User);
        //request.setAttribute("info", info);
        request.setAttribute("fname", info[2]);
        request.setAttribute("lname", info[3]);
        request.setAttribute("email", info[4]);
        request.setAttribute("picid", info[5]);
        request.setAttribute("images", lsPics);
        RequestDispatcher rd = request.getRequestDispatcher("/profile.jsp");
        try{
            rd.forward(request, response);
        }catch(NullPointerException np){
            
        }

    }
    
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //
        String loginName = request.getParameter("username");
        //
        String oldPass = request.getParameter("oldpass");
        String newPass = request.getParameter("newpass");
        String conPass = request.getParameter("conpass");
        //
        String firstname = request.getParameter("fname");
        String lastname = request.getParameter("lname");
        String email = request.getParameter("email");
        String picid = request.getParameter("picid");
        //Set<String> eMail = new HashSet<>();
        //eMail.add(email);
        HttpSession session=request.getSession();
        
        User us = new User();
        us.setCluster(cluster);
        LoggedIn lg= (LoggedIn)session.getAttribute("LoggedIn");
            String username="majed";
            //System.out.println(lg.getlogedin());
            if (lg.getlogedin()){
                username=lg.getUsername();
            }
        //us.UpdateUser(username, firstname, lastname, email);
        // delete afterwards
        System.out.println(firstname);
        System.out.println(lastname);
        System.out.println(email);
        System.out.println(picid);
        //
        if(!"".equals(firstname)){
            us.UpdateFirstName(username, firstname);
        }
        if(!"".equals(lastname)){
            us.UpdateLastName(username, lastname);
        }
        if(!"".equals(email)){
            us.UpdateEmail(username, email);
        }
        if(picid != null){
            us.UpdateProfilePic(username, picid);
        }
        //
        if(!"".equals(oldPass) && !"".equals(newPass) && !"".equals(conPass)){
            if(!newPass.equals(conPass)){
                
            }else{
                boolean check = us.changePassword(username, oldPass, newPass);
                if(check == false){
                    error("Bad Operator", response);
                }
            }
        }
        if(!"".equals(loginName)){
            if(us.checkUsername(loginName)){
                if(!loginName.equals(username)){
                    username = us.changeUsername(username, loginName);
                    lg.setUsername(username);
                }  
            }else{
                error("Username taken",response);
            }
            
        }
        //
        
        RequestDispatcher rd = request.getRequestDispatcher("/Instagrim/Profile/" + username);
             try{
            //rd.forward(request, response);
            response.sendRedirect("/Instagrim/Profile/" + username);
        }catch(Exception np){
            System.out.println(np);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
    private void error(String mess, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter out = null;
        out = new PrintWriter(response.getOutputStream());
        out.println("<h1>You have an error in your input</h1>");
        out.println("<h2>" + mess + "</h2>");
        out.close();
        return;
    }
}

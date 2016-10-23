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
import java.util.LinkedList;
import java.util.UUID;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.lib.Convertors;
import uk.ac.dundee.computing.aec.instagrim.models.CommentModel;
import uk.ac.dundee.computing.aec.instagrim.models.PicModel;
import uk.ac.dundee.computing.aec.instagrim.stores.Comment;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;

/**
 *
 * @author Finlay
 */
@WebServlet(urlPatterns = {"/Comments","/Comments/*"})@MultipartConfig
public class Comments extends HttpServlet {

    
    private static final long serialVersionUID = 1L;
    private Cluster cluster;
    private HashMap CommandsMap = new HashMap();
    
    public Comments(){
        super();
        CommandsMap.put("Comments",1);
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
            /* TODO output your page here. You may use following sample code. 
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Comment</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet Comment at " + request.getContextPath() + "</h1>");
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
        ////
        String args[] = Convertors.SplitRequestPath(request);
        int command;
        try {
            command = (Integer) CommandsMap.get(args[1]);
        } catch (Exception et) {
            error("Bad Operator", response);
            return;
        }
        switch(command){
            case 1:
                /// get comments
                getPicComments(args[2],request,response);
                break;
            default:
                error("Bad Operator",response);
        }
        
        //processRequest(request, response);
    }

    
    public void getPicComments(String picid,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CommentModel cm = new CommentModel();
        cm.setCluster(cluster);
        PicModel pm = new PicModel();
        pm.setCluster(cluster);
        LinkedList<Comment> comments;
        comments = cm.getPicComments(picid);
        String[] info = pm.getPicInfo(UUID.fromString(picid));
        request.setAttribute("userPic",info[0]);
        request.setAttribute("DateAdded",info[1]);
        request.setAttribute("comments",comments);
        request.setAttribute("picid",picid);
        RequestDispatcher rd = request.getRequestDispatcher("/picComments.jsp");
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
        String comment = request.getParameter("comment");
        HttpSession session=request.getSession();
        LoggedIn lg = (LoggedIn)session.getAttribute("LoggedIn");
        String postUser = lg.getUsername();
        String args[] = Convertors.SplitRequestPath(request);
        String picid = request.getParameter("picid");
        String reply = request.getParameter("reply");
        String cTime = request.getParameter("commentTime");
        
        
            
            
            System.out.println("Worked");
            CommentModel cm = new CommentModel();
        cm.setCluster(cluster);
        if(comment==null && reply!=null){
            cm.addReply(picid,cTime,reply,postUser);
            System.out.println("Reached point 1");
        }
        else if(comment!=null && reply==null){
            
        cm.addComment(picid,postUser,comment);
        
        }
        try{
            //rd.forward(request, response);
            response.sendRedirect("/Instagrim/Comments/" + picid);
        }catch(Exception np){
            System.out.println(np);
        }
        
        
        
        //processRequest(request, response);
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

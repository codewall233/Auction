/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import connection.MysqlConnection;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.Naming;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pack.Constants;


public class SignupServlet extends HttpServlet implements Constants {
   
   Connection con;
   PreparedStatement pst;
   ResultSet rst;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try 
        {
            String name=request.getParameter("name");
            String username=request.getParameter("username");
            String password=request.getParameter("password");

            Random random=new Random(100);


            con=MysqlConnection.getMysqlConnection();
            pst=con.prepareStatement("select * from bidders where username=?");
            pst.setString(1,username);
            rst= pst.executeQuery();
            if(rst.next())
            {
                   rst.close();
               pst.close();
               con.close();
               response.sendRedirect("signup.jsp?msg=Username Already Exists");
            }
            else
            {

            String rmiUrl="rmi://"+CERTIFICATE_AUTHORITY_ADDRESS+"/server";
            RemoteMethods methods=(RemoteMethods)Naming.lookup(rmiUrl);
            String certificate=methods.addUser(username+"agent"+random.nextInt(10000));


            con=MysqlConnection.getMysqlConnection();
            pst=con.prepareStatement("insert into bidders (name,username,password,agentid) values (?,?,?,?)");
            pst.setString(1,name);
            pst.setString(2,username);
            pst.setString(3,password);
            pst.setString(4,certificate);
            pst.executeUpdate();

            pst.close();
            con.close();

               response.sendRedirect("signup.jsp?msg=Signup Succesful Use this Username to Agent Application");
            }
           
        }
        catch(Exception e)
        {
          out.println("Error:"+e.getLocalizedMessage());
        }
        finally {
            out.close();
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

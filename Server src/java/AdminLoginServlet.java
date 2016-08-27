/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import connection.MysqlConnection;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class AdminLoginServlet extends HttpServlet {

   Connection con;
   PreparedStatement pst;
   ResultSet rst;
  
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try
        {
            String username=request.getParameter("username");
            String password=request.getParameter("password");
            
           
            con=MysqlConnection.getMysqlConnection();
            pst=con.prepareStatement("select * from userlogin where username=? and password=?");
            pst.setString(1,username);
            pst.setString(2,password);
            rst= pst.executeQuery();
            if(rst.next())
            {
              
               HttpSession session=request.getSession(true);
               session.setAttribute("seller",rst.getString("seller"));
                rst.close();
               pst.close();
               response.sendRedirect("addproduct.jsp");
            }
            else
            {
               rst.close();
               pst.close();
               response.sendRedirect("index.jsp?msg=Wrong Username or Password");
            }
           con.close();
            
           
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

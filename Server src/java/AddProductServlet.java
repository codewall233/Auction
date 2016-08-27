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

public class AddProductServlet extends HttpServlet {

   Connection con;
   PreparedStatement pst;
   ResultSet rst;
   
   
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try
        {
            HttpSession session=request.getSession();
            String seller=(String)session.getAttribute("seller");
            String productid=request.getParameter("productid");
            String productname=request.getParameter("productname");
            String startdate=request.getParameter("startdate");
            String enddate=request.getParameter("enddate");
            String iniprice=request.getParameter("iniprice");

            con=MysqlConnection.getMysqlConnection();
            pst=con.prepareStatement("select * from products where productid=?");
            pst.setString(1,productid);
            rst= pst.executeQuery();
            if(!rst.next())
            {
            rst.close();
            pst.close();
            pst=con.prepareStatement("insert into products (seller,productid,productname,startdate,enddate,iniprice) values (?,?,?,?,?,?)");
            pst.setString(1,seller);
            pst.setString(2,productid);
            pst.setString(3,productname);
            pst.setString(4,startdate);
            pst.setString(5,enddate);
            pst.setFloat(6,Float.parseFloat(iniprice));
            pst.executeUpdate();
            pst.close();
             response.sendRedirect("addproduct.jsp?msg=Product Added Successfully");
            }
            else
            {
             rst.close();
             pst.close();
              response.sendRedirect("addproduct.jsp?msg=Product Id Already Exists");
            }


            con.close();


          
        }
        catch(Exception e)
        {

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

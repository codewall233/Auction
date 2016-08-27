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
import java.util.ArrayList;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pack.BiddingDetails;


public class LogoutServlet extends HttpServlet {

   Connection con;
   PreparedStatement pst;
   ResultSet rst;
   
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try 
        {
            String seller=request.getParameter("seller");
            String agentid=request.getParameter("agentid");
            String agentname=request.getParameter("agentname");
            String productid=request.getParameter("productid");
            String productname=request.getParameter("productname");
            String price=request.getParameter("price");
            float iprice=Float.parseFloat(price);

            con=MysqlConnection.getMysqlConnection();
            pst=con.prepareStatement("insert into purchases (seller,agentid,agentname,productid,productname,price,buyed) values (?,?,?,?,?,?,?)");
            pst.setString(1,seller);
            pst.setString(2,agentid);
            pst.setString(3,agentname);
            pst.setString(4,productid);
            pst.setString(5,productname);
            pst.setFloat(6,iprice);
            pst.setString(7,"no");
            pst.executeUpdate();

           pst.close();
           con.close();

           ArrayList<BiddingDetails> details= (ArrayList<BiddingDetails>)getServletContext().getAttribute(seller);
           Iterator<BiddingDetails> itr=details.iterator();
           while(itr.hasNext())
           {
               BiddingDetails data=itr.next();
               if(data.productid.equals(productid))
               {
                   itr.remove();
                   break;
               }

           }
           
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

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
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pack.BiddingDetails;

public class FetchProductServlet extends HttpServlet {

   Connection con;
   PreparedStatement pst;
   ResultSet rst;
   
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try
        {
            ServletContext context=getServletContext();
            String seller=request.getParameter("seller");
            ArrayList<BiddingDetails> products=(ArrayList<BiddingDetails>)context.getAttribute(seller);
            if(products==null)
            {
            con=MysqlConnection.getMysqlConnection();
            pst=con.prepareStatement("select * from products where seller=?");
            pst.setString(1,seller);
            rst= pst.executeQuery();
            products=new ArrayList<BiddingDetails>();
            while(rst.next())
            {
                BiddingDetails dat=new BiddingDetails(seller,rst.getString("productid"), rst.getString("productname"), rst.getString("startdate"),rst.getString("enddate"), rst.getFloat("iniprice"),rst.getFloat("iniprice"));
                dat.agentid="Not Yet Started Bidding";
                products.add(dat);
            }
            context.setAttribute(seller,products);
            rst.close();
            pst.close();

            }

            String data="No Products Available";
            int i=0;
            Iterator<BiddingDetails> itr=products.iterator();
            while(itr.hasNext())
            {
                BiddingDetails details=itr.next();
                if(i==0)
                {
                    i=1;
                    data=seller+"^"+details.agentid+"^"+details.productid+"^"+details.productname+"^"+details.startdate+"^"+details.enddate+"^"+details.iniprice+"^"+details.finalprice;

                }
                else
                {
                  data=data+"~"+seller+"^"+details.agentid+"^"+details.productid+"^"+details.productname+"^"+details.startdate+"^"+details.enddate+"^"+details.iniprice+"^"+details.finalprice;
                }

            }
             out.println(data);
        

           


          
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

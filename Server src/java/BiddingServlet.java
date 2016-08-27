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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pack.BehaviourDetails;
import pack.BiddingDetails;
import pack.Constants;


public class BiddingServlet extends HttpServlet implements Constants {

   Connection con;
   PreparedStatement pst;
   ResultSet rst;

   float threshold=70;
   int shllingCount=3;
   
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try 
        {
            String agentid=request.getParameter("agentid");
            String agentname=request.getParameter("agentname");
            String productid=request.getParameter("productid");
            String seller=request.getParameter("seller");
            String increment=request.getParameter("increment");
            float fincrement=Float.parseFloat(increment);

            con=MysqlConnection.getMysqlConnection();
            pst=con.prepareStatement("select * from roles where agentid=?");
            pst.setString(1,agentid);
            rst= pst.executeQuery();
            if(!rst.next())
            {
               
                
                    if(fincrement>=threshold)
                    {
                       BehaviourDetails details=(BehaviourDetails)getServletContext().getAttribute(agentid);
                        if(details==null)
                        {
                            getServletContext().setAttribute(agentid,new BehaviourDetails(agentid,1));
                             updateBid(getServletContext(), seller, productid, fincrement, agentid,agentname);
                                out.print("Shilling Behaviour Detected for Product ID:"+productid);

                        }
                        else
                        {
                            details.count=details.count+1;
                           if(details.count>=shllingCount)
                           {
                                getServletContext().removeAttribute(agentid);
                               rst.close();
                               pst.close();
                               pst=con.prepareStatement("insert into roles (agentid,blocked) values (?,?)");
                               pst.setString(1,agentid);
                               pst.setString(2,"yes");
                               pst.executeUpdate();
                               pst.close();

                                String rmiUrl="rmi://"+CERTIFICATE_AUTHORITY_ADDRESS+"/server";
            RemoteMethods methods=(RemoteMethods)Naming.lookup(rmiUrl);
            methods.blockUser(agentid);

                               out.print("Your are Blocked for Shlling Behaviour for Product ID:"+productid);
                           }
                           else
                           {
                                getServletContext().setAttribute(agentid,details);
                                updateBid(getServletContext(), seller, productid, fincrement, agentid,agentname);
                                out.print("Shlling Behaviour Detected for Product ID:"+productid);



                           }


                        }
                    }
                    else
                    {
                         updateBid(getServletContext(), seller, productid, fincrement, agentid,agentname);
                         out.print("Normal Bidding Behaviour Data Updated for product id:"+productid);

                    }


                


            }
             else
            {
                 String blocked=rst.getString("blocked");
                if(blocked.equals("yes"))
                {
                      String rmiUrl="rmi://"+CERTIFICATE_AUTHORITY_ADDRESS+"/server";
            RemoteMethods methods=(RemoteMethods)Naming.lookup(rmiUrl);
            methods.blockUser(agentid);
                    out.println("Agent id Blocked Since We Have found a lot Shiiling Behaviour for product id "+productid);
                }
             
            }
           

          
        }
        catch(Exception e)
        {
            out.println("Error"+e.getLocalizedMessage());
        }
        finally {
            out.close();
        }
    }

    public void updateBid(ServletContext context,String seller,String productid,float incremment,String agentid,String agentname)
    {
         ArrayList<BiddingDetails> products=(ArrayList<BiddingDetails>)getServletContext().getAttribute(seller);
         if(products!=null)
         {
            Iterator<BiddingDetails> itr=  products.iterator();
            while(itr.hasNext())
            {
                BiddingDetails details=itr.next();
                if(details.productid.equals(productid))
                {

                    details.agentid=agentid;
                    details.seller=agentname;
                    details.finalprice=details.finalprice+incremment;
                    break;
                }

            }
            getServletContext().setAttribute(seller,products);
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

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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pack.Constants;


public class LoginServlet extends HttpServlet implements Constants {

   Connection con;
   PreparedStatement pst;
   ResultSet rst;

   int thresholdPurchsedCount=3;
   int shillingCount=2;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try
        {
            String username=request.getParameter("username");
            String password=request.getParameter("password");

          

            con=MysqlConnection.getMysqlConnection();
            pst=con.prepareStatement("select * from bidders where username=? and password=?");
            pst.setString(1,username);
            pst.setString(2,password);
            rst= pst.executeQuery();
            if(rst.next())
            {
                String agentname=rst.getString("name");
                String agentid=rst.getString("agentid");

                String role="Complete Trusted";
                rst.close();
                pst.close();
            pst=con.prepareStatement("select * from roles where agentid=? and blocked=?");
            pst.setString(1,agentid);
            pst.setString(2,"yes");
            rst= pst.executeQuery();
            if(rst.next())
            {
                out.println("blocked");

                 String rmiUrl="rmi://"+CERTIFICATE_AUTHORITY_ADDRESS+"/server";
            RemoteMethods methods=(RemoteMethods)Naming.lookup(rmiUrl);
            methods.blockUser(agentid);
            }
            else
            {
                 rst.close();
                pst.close();
            pst=con.prepareStatement("select count(agentid) as cnt from previousbids where agentid=?");
            pst.setString(1,agentid);
            rst= pst.executeQuery();
            if(rst.next())
            {
                int count=rst.getInt("cnt");
                if(count>=shillingCount)
                {
                    role="Shilling Behaviour";
                }

            }
           
               int noCount=0,yesCount=0;
                 rst.close();
                pst.close();
            pst=con.prepareStatement("select count(agentid) as cnt from purchases where agentid=? and buyed=?");
            pst.setString(1,agentid);
            pst.setString(2,"no");
            rst= pst.executeQuery();
            if(rst.next())
            {
                noCount=rst.getInt("cnt");

            }
            rst.close();
            pst.close();

             pst=con.prepareStatement("select count(agentid) as cnt from purchases where agentid=? and buyed=?");
            pst.setString(1,agentid);
            pst.setString(2,"yes");
            rst= pst.executeQuery();
            if(rst.next())
            {
                yesCount=rst.getInt("cnt");

            }
            rst.close();
            pst.close();

            if(noCount>yesCount)
            {
                role="Semi Trusted";
            }



            
            out.print(agentid+"^"+agentname+"^"+role);

            }

                
                
            }
            else
            {
             out.println("Login Failed");
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

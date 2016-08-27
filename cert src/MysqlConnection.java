




import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


public class MysqlConnection
{


 
    static String url = "jdbc:mysql://localhost:3306/";
    static String db = "cert_authority";
    static String driver = "com.mysql.jdbc.Driver";
    static String username="root",password="mysql";
    public Connection connection;
    
    public Connection establishConnection()
    {
        try
        {
         Class.forName(driver).newInstance();
         connection=DriverManager.getConnection(url+db,username,password);
         return connection;
        }
        catch(Exception e)
        {
            System.out.println("Error in Mysql Connection Class"+e.getLocalizedMessage());
            return null;
        }
    }

    public static Connection getMysqlConnection() throws Exception
    {

       
      
        Connection connection=new MysqlConnection().establishConnection();

        return connection;
    }

}

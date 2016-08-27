
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

public interface RemoteMethods extends Remote
{

   
    public String addUser(String data) throws RemoteException;
    public String verifvyUser(String data) throws RemoteException;
    public String blockUser(String data) throws RemoteException;
    
   

}

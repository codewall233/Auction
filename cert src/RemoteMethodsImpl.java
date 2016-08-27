
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;
import javax.swing.JOptionPane;
import sun.misc.BASE64Encoder;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

public class RemoteMethodsImpl extends UnicastRemoteObject implements RemoteMethods {

    PublicKey publicKey;
    PrivateKey privateKey;

    String ALGORITHM="RSA";

    Connection connection;

    MainFrame frame;

    public RemoteMethodsImpl(MainFrame _frame) throws RemoteException
    {
        this.frame=_frame;
           try
        {
         File fpublic=new File("public.der");
         File fprivate=new File("private.der");

         if(!fpublic.exists()||!fprivate.exists())
         {

         generateRSAKeyPair();
         byte[] privateKeyBytes = privateKey.getEncoded();
         byte[] publicKeyBytes = publicKey.getEncoded();

         FileOutputStream fos=new FileOutputStream(fpublic);
         fos.write(publicKeyBytes);
         fos.close();

         fos=new FileOutputStream(fprivate);
         fos.write(privateKeyBytes);
         fos.close();

         }
         else
         {

          publicKey=readPublicKey(fpublic);
          privateKey=readPrivateKey(fprivate);
         }

        }
        catch(Exception e1)
        {
            System.out.println(e1.getLocalizedMessage());
        }

    }

    public String blockUser(String certificate)
    {
           try
        {

            System.out.println("Entered Blocke User:"+certificate);
            connection = MysqlConnection.getMysqlConnection();
            PreparedStatement pst = connection.prepareStatement("update certificates set status=? where certificate=?");
            pst.setString(1,"allow");
            pst.setString(2,certificate);
            pst.executeUpdate();
            pst.close();
            connection.close();
            frame.fetchCertificates();

            return "blocked";


        }
        catch(Exception e)
        {
             System.out.println("Error Database Check:"+e.getLocalizedMessage());
        }

           return "failed";


    }

    public String verifvyUser(String data) throws RemoteException
    {
        String storedCertificate,generatedCertificate;
        String message="certificate verification failed";
          try
        {
            PreparedStatement pst = connection.prepareStatement("select * from certificates where userdata=?");
            pst.setString(1,data);
            ResultSet rst=pst.executeQuery();
            if(rst.next())
            {
                String status=rst.getString("status");
                if(status.equals("blocked"))
                {
                    return "blocked";
                }
                rst.close();
                pst.close();
            }

            else
            {
            rst.close();
            pst.close();

            connection = MysqlConnection.getMysqlConnection();
            pst = connection.prepareStatement("select * from certificates where userdata=?");
            pst.setString(1,data);
            rst=pst.executeQuery();
            if(rst.next())
            {
                storedCertificate=rst.getString("certificate");
                generatedCertificate=new BASE64Encoder().encode(generateSignature("RSA",data.getBytes(), privateKey));
                if(storedCertificate.equals(generatedCertificate))
                {
                    message="certificate verification successful";
                }


            }

            rst.close();
            pst.close();
          }
            connection.close();


        }
        catch(Exception e)
        {
             System.out.println("Error Database Check:"+e.getLocalizedMessage());
        }

        return message;

    }

  


    public String addUser(String data) throws RemoteException
    {
        
          String message="User Added Successfully";
         
          try
        {

          
            connection = MysqlConnection.getMysqlConnection();
            PreparedStatement pst = connection.prepareStatement("select * from certificates where userdata=?");
            pst.setString(1,data);
            ResultSet rst=pst.executeQuery();
            if(!rst.next())
            {
            rst.close();
            pst.close();
            String certificate=new BASE64Encoder().encode(generateSignature("RSA",(data).getBytes(), privateKey));
            pst = connection.prepareStatement("insert into certificates (userdata,certificate,status) values (?,?,?)");
            pst.setString(1,data);
            pst.setString(2, certificate);
            pst.setString(3,"allowed");
            pst.executeUpdate();
            MainFrame.modelUsers.insertRow(MainFrame.modelUsers.getRowCount(), new String[]{data,certificate,"allowed"});
            message=certificate;
                
            }
            else
            {
                message="User Already Exists";
            }           

            rst.close();
            pst.close();
            connection.close();

           

        }
        catch(Exception e)
        {
             System.out.println("Error Database Check:"+e.getLocalizedMessage());
        }

          return message;
    }

  

     public void generateRSAKeyPair()
     {
        try
        {

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
	SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
	keyGen.initialize(1024, random);
	KeyPair key = keyGen.generateKeyPair();
	privateKey=key.getPrivate();
	publicKey=key.getPublic();
        }
        catch(Exception e)
        {
          System.out.println("Error in RSA Key Generation:"+e.getLocalizedMessage());
        }
    }

    public PublicKey readPublicKey(File publicKeyFile)
     {
         try
         {
    byte[] keyBytes = new byte[(int)publicKeyFile.length()];
    FileInputStream fis = new FileInputStream(publicKeyFile);
    fis.read(keyBytes);
    X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(keyBytes);
    KeyFactory factory = KeyFactory.getInstance(ALGORITHM);
    PublicKey pubKey = (PublicKey)factory.generatePublic(publicKeySpec);
    return pubKey;
         }
         catch(Exception e)
         {
         System.out.println(e.getLocalizedMessage());
         }
     return null;
}

     public PrivateKey readPrivateKey(File privateKeyFile)
     {
     try
     {
    byte[] keyBytes = new byte[(int)privateKeyFile.length()];
    FileInputStream fis = new FileInputStream(privateKeyFile);
    fis.read(keyBytes);
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
    RSAPrivateKey privKey = (RSAPrivateKey) keyFactory.generatePrivate(spec);
    return privKey;
         }
     catch(Exception e)
     {
         System.out.println(e.getLocalizedMessage());
     }
     return null;
}

   

     



   

    public byte[] generateSignature(String algorithm,byte[] data,PrivateKey privateKey) throws Exception
{
 if(algorithm.equals("DSA"))
    {
    Signature sign = Signature.getInstance(algorithm);
    sign.initSign(privateKey);
    sign.update(data);
    return sign.sign();
    }
 else
    {
    Signature sig = Signature.getInstance("MD5WithRSA");
    sig.initSign(privateKey);
    sig.update(data);
    byte[] signatureBytes = sig.sign();

    return signatureBytes;
    }
  }



  public boolean verifySignature(String algorithm,byte[] data,byte[] signature, PublicKey publicKey)throws Exception {
if(algorithm.equals("DSA"))
      {
    Signature sign = Signature.getInstance(algorithm);
    sign.initVerify(publicKey);
    sign.update(data);
    return sign.verify(signature);
      }
 else
      {



          Signature sig = Signature.getInstance("MD5WithRSA");
         sig.initVerify(publicKey);
         sig.update(data);
        return sig.verify(signature);



      }
  }

    

}

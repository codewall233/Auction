


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

public class ServerCommunicator
{

    public static String sendHttpRequestWithoutFile(String url,List <NameValuePair> params)
    {
         CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httppost = new HttpPost(url);
         /*  List <NameValuePair> params = new ArrayList <NameValuePair>();
           params.add(new BasicNameValuePair("group_name", group_name));*/


            httppost.setEntity(new UrlEncodedFormEntity(params));

            System.out.println("executing request " + httppost.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httppost);

                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null)
                {

                    System.out.println("Response content length: " + resEntity.getContentLength());
                }


                BufferedReader in = new BufferedReader(new InputStreamReader(resEntity.getContent()));

		String inputLine;
		StringBuffer response_data = new StringBuffer();

		while ((inputLine = in.readLine()) != null)
                {
			response_data.append(inputLine);
                        System.out.println("Input Line"+inputLine);
		}
		in.close();

                String receivedData=response_data.toString();
                System.out.println(receivedData);



            httpclient.close();
            return receivedData;
        }
        catch(Exception e)
        {
            System.out.println("HTTP RESPONSE FAILED:"+e.getLocalizedMessage());
        }

         return null;

    }

    public static String sendHttpRequestWithFile(String url,HttpEntity reqEntity)
    {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httppost = new HttpPost(url);

          /*  FileBody bin = new FileBody(new File(filename));
            StringBody group = new StringBody(group_name, ContentType.TEXT_PLAIN);
            StringBody user = new StringBody(username, ContentType.TEXT_PLAIN);
            StringBody srand = new StringBody(srandom, ContentType.TEXT_PLAIN);

            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("file", bin)
                    .addPart("group_name", group)
                    .addPart("user",user)
                    .addPart("srandom",srand)
                    .build();*/


            httppost.setEntity(reqEntity);

            System.out.println("executing request " + httppost.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httppost);

                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    System.out.println("Response content length: " + resEntity.getContentLength());
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(resEntity.getContent()));

		String inputLine;
		StringBuffer response_data = new StringBuffer();

		while ((inputLine = in.readLine()) != null)
                {
			response_data.append(inputLine);

		}
		in.close();

                String dataReceived=response_data.toString();
              
                response.close();

            httpclient.close();

            return dataReceived;
        }
        catch(Exception e)
        {
            System.out.println("HTTP RESPONSE FAILED:"+e.getLocalizedMessage());
        }

        return null;

    }

}



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.table.DefaultTableModel;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import pack.BiddingItems;
import pack.Constants;
import pack.WonProducts;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


public class MainFrame extends javax.swing.JFrame implements Constants {

    ArrayList<BiddingItems> biddingItemList;
    ArrayList<WonProducts> wonProductsList;

    String agentId,agentName,role;

    DefaultTableModel modelProducts;
    String[] productColumns=new String[]{"Seller","Agent ID","Product Id","Product Name","Start Date","End Date","Initial Price","Latest Bid Price"};
    Object[][] productData;

    DefaultTableModel modelBidSettings;
    String[] bidColumns=new String[]{"Seller","Product Id","Agent ID","Increment Amount","Final Prize","Current Bid Amount"};
    Object[][] bidData;

     DefaultTableModel modelWon;
    String[] wonColumns=new String[]{"Seller","Product Id","Agent ID","Agent Name","Final Prize","Purchased"};
    Object[][] wonData;

    Timer timer;

    JPopupMenu menuProducts;
    JMenuItem itemProducts;
    productMenuHandler handlerProducts;

    JPopupMenu menuBids;
    JMenuItem itemBidsBuy,itemBidsDelete;
    productMenuHandlerBids handlerBids;


    /** Creates new form MainFrame */
    public MainFrame(String _agentid,String _agentname,String _role) {
        this.agentId=_agentid;
        this.role=_role;
        this.agentName=_agentname;
        initComponents();
        
        modelProducts=new DefaultTableModel(productData, productColumns);
        Mainframe_Table_Products.setModel(modelProducts);

        modelBidSettings=new DefaultTableModel(bidData, bidColumns);
        Mainframe_Table_Bid_Settings.setModel(modelBidSettings);

         modelWon=new DefaultTableModel(wonData,wonColumns);
        Mainframe_Table_Won.setModel(modelWon);

         handlerProducts=new productMenuHandler();
         menuProducts=new JPopupMenu();
         itemProducts=new JMenuItem("Bid For Product");
         itemProducts.addActionListener(handlerProducts);
         menuProducts.add(itemProducts); 
         Mainframe_Table_Products.setComponentPopupMenu(menuProducts);

         handlerBids=new productMenuHandlerBids();
         menuBids=new JPopupMenu();
         itemBidsBuy=new JMenuItem("Buy Product");
         itemBidsDelete=new JMenuItem("Delete");
         itemBidsBuy.addActionListener(handlerBids);
         itemBidsDelete.addActionListener(handlerBids);
         menuBids.add(itemBidsBuy);
         menuBids.add(itemBidsDelete);
         Mainframe_Table_Bid_Settings.setComponentPopupMenu(menuBids);

        Mainframe_Txt_Log.append("Agent Id--->"+agentId+"\n\n");
        Mainframe_Txt_Log.append("Inital Role Assigned--->"+role+"\n\n");

         List<NameValuePair> params = new ArrayList<NameValuePair>();
         params.add(new BasicNameValuePair("password","abc"));

        String response=ServerCommunicator.sendHttpRequestWithoutFile(SERVER_URL+"/"+FETCH_SELLER_SERVLET,params);

        System.out.println(response);
        if(!response.equals("No Sellers Available"))
        {
            StringTokenizer st=new StringTokenizer(response,"^");
            while(st.hasMoreTokens())
            {
            Mainframe_Combo_Sellers.addItem(st.nextToken());
            }
        }
        else
        {
          JOptionPane.showMessageDialog(this, response);
        }


    }

    class productMenuHandlerBids implements ActionListener
    {
        public void actionPerformed(ActionEvent ae)
        {
            int row=Mainframe_Table_Bid_Settings.getSelectedRow();

            if(ae.getSource().equals(itemBidsBuy))
            {



           System.out.println("Entered Here");

            String seller=(String)Mainframe_Table_Bid_Settings.getValueAt(row,0);
            String productId=(String)Mainframe_Table_Bid_Settings.getValueAt(row,1);
            String agentId=(String)Mainframe_Table_Bid_Settings.getValueAt(row,2);
            String agentName=(String)Mainframe_Table_Bid_Settings.getValueAt(row,3);
            String productName=productId;
            String finalAmount=(String)Mainframe_Table_Bid_Settings.getValueAt(row,5);


            modelWon.insertRow(modelWon.getRowCount(),new String[]{seller,productId,agentId,agentName,finalAmount,"YES"});

             Iterator<BiddingItems> itr=biddingItemList.iterator();
                 while(itr.hasNext())
                 {
                     BiddingItems bitem=itr.next();
                     if(bitem.productId.equals(productId))
                     {
                         itr.remove();
                         break;

                     }

                 }


              List<NameValuePair> params1 = new ArrayList<NameValuePair>();
         params1.add(new BasicNameValuePair("agentid",agentId));
         params1.add(new BasicNameValuePair("agentname",agentName));
          params1.add(new BasicNameValuePair("productid",productId));
           params1.add(new BasicNameValuePair("productname",productName));
           params1.add(new BasicNameValuePair("seller",seller));
            params1.add(new BasicNameValuePair("price",finalAmount));
        String response1=ServerCommunicator.sendHttpRequestWithoutFile(SERVER_URL+"/"+BUY_SERVLET,params1);

        Mainframe_Txt_Log.append(response1+"\n\n");




            }
            else if(ae.getSource().equals(itemBidsDelete))
            {
                 String productId=(String)modelBidSettings.getValueAt(row,1);
                  Iterator<BiddingItems> itr=biddingItemList.iterator();
                 while(itr.hasNext())
                 {
                     BiddingItems bitem=itr.next();
                     if(bitem.productId.equals(productId))
                     {
                         itr.remove();
                         modelBidSettings.removeRow(row);

                     }

                 }

            }

        }
    }

     class productMenuHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent ae)
        {
            int row=Mainframe_Table_Products.getSelectedRow();
            String seller=(String)modelProducts.getValueAt(row,0);
            String productId=(String)modelProducts.getValueAt(row,2);
            float amount=Float.parseFloat((String)modelProducts.getValueAt(row,7));


            float finalAmount=Float.parseFloat(JOptionPane.showInputDialog("Enter Final Bid Amount"));
            float incrementAmount=Float.parseFloat(JOptionPane.showInputDialog("Enter Increment Amount"));

            if(biddingItemList==null)
            {
                 biddingItemList=new ArrayList<BiddingItems>();
                 biddingItemList.add(new BiddingItems(seller, productId, agentId, agentName, incrementAmount, finalAmount));
                  modelBidSettings.insertRow(modelBidSettings.getRowCount(),new String[]{seller, productId, agentId, ""+incrementAmount,""+ finalAmount,""+(amount+incrementAmount)});


                      List<NameValuePair> params1 = new ArrayList<NameValuePair>();
         params1.add(new BasicNameValuePair("agentid",agentId));
         params1.add(new BasicNameValuePair("agentname",agentName));
          params1.add(new BasicNameValuePair("productid",productId));
           params1.add(new BasicNameValuePair("seller",seller));
            params1.add(new BasicNameValuePair("increment",""+incrementAmount));
        String response1=ServerCommunicator.sendHttpRequestWithoutFile(SERVER_URL+"/"+BIDDING_SERVLET,params1);

        Mainframe_Txt_Log.append(response1+"\n\n");

                 timer=new Timer();
                 timer.schedule(new BidTask(),1000,15000);

            }
            else
            {
                 timer.cancel();
                 Iterator<BiddingItems> itr=biddingItemList.iterator();
                 boolean contains=false;
                 while(itr.hasNext())
                 {
                     BiddingItems bitem=itr.next();
                     if(bitem.productId.equals(productId))
                     {
                         bitem.increment=incrementAmount;
                         bitem.finalPrize=finalAmount;
                         contains=true;
                         break;

                     }

                 }
                 if(!contains)
                 {
                    biddingItemList.add(new BiddingItems(seller, productId, agentId, agentName, incrementAmount, finalAmount));
                    modelBidSettings.insertRow(modelBidSettings.getRowCount(),new String[]{seller, productId, agentId, ""+incrementAmount,""+ finalAmount,""+(amount+incrementAmount)});

                     List<NameValuePair> params1 = new ArrayList<NameValuePair>();
         params1.add(new BasicNameValuePair("agentid",agentId));
         params1.add(new BasicNameValuePair("agentname",agentName));
          params1.add(new BasicNameValuePair("productid",productId));
           params1.add(new BasicNameValuePair("seller",seller));
            params1.add(new BasicNameValuePair("increment",""+incrementAmount));
        String response1=ServerCommunicator.sendHttpRequestWithoutFile(SERVER_URL+"/"+BIDDING_SERVLET,params1);

        Mainframe_Txt_Log.append(response1+"\n\n");
                    timer=new Timer();
                   timer.schedule(new BidTask(),1000,15000);
                 }

            }
        }
    }

     class BidTask extends TimerTask
     {

         public void run()
         {




        for(int i=modelProducts.getRowCount()-1;i>=0;i--)
        {
            modelProducts.removeRow(i);
        }

          for(int i=modelBidSettings.getRowCount()-1;i>=0;i--)
        {
            modelBidSettings.removeRow(i);
        }


        String seller=(String)Mainframe_Combo_Sellers.getSelectedItem();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("seller",seller));

        String response=ServerCommunicator.sendHttpRequestWithoutFile(SERVER_URL+"/"+FETCH_PRODUCT_SERVLET,params);

        if(!response.equals("No Products Available"))
        {
            StringTokenizer st=new StringTokenizer(response,"~");
            while(st.hasMoreTokens())
            {
                String data=st.nextToken();
                StringTokenizer st1=new StringTokenizer(data,"^");
                String seller1=st1.nextToken();
                String agentid1=st1.nextToken();
                String productid1=st1.nextToken();
                String productname1=st1.nextToken();
                String startdate1=st1.nextToken();
                String enddate1=st1.nextToken();
                String initialprice1=st1.nextToken();
                String latestbidprice1=st1.nextToken();
                float finitialprice=Float.parseFloat(initialprice1);
                float flatestbidprice=Float.parseFloat(latestbidprice1);

                boolean contained=false;
                Iterator<BiddingItems> itrList= biddingItemList.iterator();
                while(itrList.hasNext())
                {
                    BiddingItems item=itrList.next();

                    if(!item.agentId.equals(agentid1)&&item.productId.equals(productid1)&&!agentid1.equals("Not Yet Started Bidding"))
                    {
                        System.out.println("Entered Here");
                        contained=true;

                        float increment=item.increment;
                        float finalamount=item.finalPrize;

                        System.out.println("Final Amount"+finalamount);
                        System.out.println("price"+(flatestbidprice+increment));

                        if(finalamount>=(flatestbidprice+increment))
                        {
                            
System.out.println("Entered Here");
                               List<NameValuePair> params1 = new ArrayList<NameValuePair>();
         params1.add(new BasicNameValuePair("agentid",agentId));
         params1.add(new BasicNameValuePair("agentname",agentName));
          params1.add(new BasicNameValuePair("productid",productid1));
           params1.add(new BasicNameValuePair("seller",seller));
            params1.add(new BasicNameValuePair("increment",""+increment));
        String response1=ServerCommunicator.sendHttpRequestWithoutFile(SERVER_URL+"/"+BIDDING_SERVLET,params1);

               Mainframe_Txt_Log.append(response1+"\n\n");

         


                            
                              modelBidSettings.insertRow(modelBidSettings.getRowCount(),new String[]{seller, productid1, agentid1,""+item.increment,""+item.finalPrize,""+(flatestbidprice+item.increment)});

                        }
                        
                    }
                    else
                        {
                            modelBidSettings.insertRow(modelBidSettings.getRowCount(),new String[]{seller1, productid1, agentid1, ""+item.increment,""+item.finalPrize,""+flatestbidprice});

                        }




                }

                 /*   if(!contained&&!agentid1.equals("Not Yet Started Bidding"))
                    {
                         modelBidSettings.insertRow(modelProducts.getRowCount(),new String[]{seller1, productid1, agentid1,null, ""+initialprice1,""+ finitialprice,""+flatestbidprice});
                    }*/
                 

                modelProducts.insertRow(modelProducts.getRowCount(),new String[]{seller1,agentid1,productid1,productname1,startdate1,enddate1,initialprice1,latestbidprice1});


            }
        }
         }
    }
       


         
     

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        Mainframe_Table_Products = new javax.swing.JTable();
        Mainframe_Combo_Sellers = new javax.swing.JComboBox();
        jButton3 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Mainframe_Table_Bid_Settings = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        Mainframe_Table_Won = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        Mainframe_Txt_Log = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(102, 255, 102));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 36));
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Bidding Client");

        jLabel2.setText("Sellers");

        jButton1.setText("Fetch Products");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        Mainframe_Table_Products.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(Mainframe_Table_Products);

        Mainframe_Combo_Sellers.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-------" }));

        jButton3.setText("Refresh");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(228, 228, 228)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(Mainframe_Combo_Sellers, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(53, 53, 53)
                        .addComponent(jButton1))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 911, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(421, 421, 421)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jButton1)
                    .addComponent(Mainframe_Combo_Sellers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3)
                .addGap(4, 4, 4))
        );

        jTabbedPane1.addTab("Search Products", jPanel2);

        Mainframe_Table_Bid_Settings.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(Mainframe_Table_Bid_Settings);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 931, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(79, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Bid Settings", jPanel3);

        Mainframe_Table_Won.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane4.setViewportView(Mainframe_Table_Won);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 931, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(108, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Won Products", jPanel4);

        Mainframe_Txt_Log.setColumns(20);
        Mainframe_Txt_Log.setRows(5);
        jScrollPane1.setViewportView(Mainframe_Txt_Log);

        jLabel3.setText("Message Info:");

        jButton2.setText("Logout");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 936, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 936, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(361, 361, 361)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 212, Short.MAX_VALUE)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:

        String seller=(String)Mainframe_Combo_Sellers.getSelectedItem();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("seller",seller));

        String response=ServerCommunicator.sendHttpRequestWithoutFile(SERVER_URL+"/"+FETCH_PRODUCT_SERVLET,params);

        if(!response.equals("No Products Available"))
        {
            StringTokenizer st=new StringTokenizer(response,"~");
            while(st.hasMoreTokens())
            {
                String data=st.nextToken();
                StringTokenizer st1=new StringTokenizer(data,"^");
                modelProducts.insertRow(modelProducts.getRowCount(),new String[]{st1.nextToken(),st1.nextToken(),st1.nextToken(),st1.nextToken(),st1.nextToken(),st1.nextToken(),st1.nextToken(),st1.nextToken()});
            }
        }
        else
        {
          JOptionPane.showMessageDialog(this, response);
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        for(int i=modelProducts.getRowCount()-1;i>=0;i--)
        {
            modelProducts.removeRow(i);
        }


        String seller=(String)Mainframe_Combo_Sellers.getSelectedItem();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("seller",seller));

        String response=ServerCommunicator.sendHttpRequestWithoutFile(SERVER_URL+"/"+FETCH_PRODUCT_SERVLET,params);

        if(!response.equals("No Products Available"))
        {
            StringTokenizer st=new StringTokenizer(response,"~");
            while(st.hasMoreTokens())
            {
                String data=st.nextToken();
                StringTokenizer st1=new StringTokenizer(data,"^");
                modelProducts.insertRow(modelProducts.getRowCount(),new String[]{st1.nextToken(),st1.nextToken(),st1.nextToken(),st1.nextToken(),st1.nextToken(),st1.nextToken(),st1.nextToken(),st1.nextToken()});
            }
        }
        else
        {
          JOptionPane.showMessageDialog(this, response);
        }

    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:

        for(int i=0;i<modelBidSettings.getRowCount();i++)

        {
            String seller=(String)Mainframe_Table_Products.getValueAt(i,0);
            String productId=(String)Mainframe_Table_Bid_Settings.getValueAt(i,1);
            String agentId=(String)Mainframe_Table_Bid_Settings.getValueAt(i,2);
            String agentName=(String)Mainframe_Table_Bid_Settings.getValueAt(i,3);
            String productName=productId;
            String finalAmount=(String)Mainframe_Table_Bid_Settings.getValueAt(i,6);




             Iterator<BiddingItems> itr=biddingItemList.iterator();
                 while(itr.hasNext())
                 {
                     BiddingItems bitem=itr.next();
                     if(bitem.productId.equals(productId))
                     {
                         itr.remove();

                     }

                 }


              List<NameValuePair> params1 = new ArrayList<NameValuePair>();
         params1.add(new BasicNameValuePair("agentid",agentId));
         params1.add(new BasicNameValuePair("agentname",agentName));
          params1.add(new BasicNameValuePair("productid",productId));
           params1.add(new BasicNameValuePair("productname",productName));
           params1.add(new BasicNameValuePair("seller",seller));
            params1.add(new BasicNameValuePair("price",finalAmount));
        String response1=ServerCommunicator.sendHttpRequestWithoutFile(SERVER_URL+"/"+LOGOUT_SERVLET,params1);
    }
        System.exit(1);


    }//GEN-LAST:event_jButton2ActionPerformed

  

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox Mainframe_Combo_Sellers;
    private javax.swing.JTable Mainframe_Table_Bid_Settings;
    private javax.swing.JTable Mainframe_Table_Products;
    private javax.swing.JTable Mainframe_Table_Won;
    private javax.swing.JTextArea Mainframe_Txt_Log;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables

}

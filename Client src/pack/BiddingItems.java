/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pack;

public class BiddingItems
{
    public String seller;
    public String productId;
    public String agentId;
    public String agentName;
    public float increment;
    public float finalPrize;

    public BiddingItems(String seller,String productId,String agentId,String agentName,float increment,float finalprize)
    {
        this.seller=seller;
        this.productId=productId;
        this.agentId=agentId;
        this.agentName=agentName;
        this.increment=increment;
        this.finalPrize=finalprize;
    }
    

}

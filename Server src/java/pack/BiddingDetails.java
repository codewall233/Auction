/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pack;

public class BiddingDetails
{
    public String seller;
    public String productid;
    public String productname;
    public String startdate;
    public String enddate;
    public float iniprice;
    public float finalprice;

    public String agentid;

    public BiddingDetails(String _seller,String _pid,String _pname,String _sdate,String _edate,float iniprice,float _finalprice)
    {
        this.seller=_seller;
        this.productid=_pid;
        this.productname=_pname;
        this.startdate=_sdate;
        this.enddate=_edate;
        this.iniprice=iniprice;
        this.finalprice=_finalprice;

    }

    public void setAgentID(String _agentid)
    {
        this.agentid=_agentid;

    }

     public void setFinalPrice(float _fprice)
    {
        this.finalprice=_fprice;

    }



}

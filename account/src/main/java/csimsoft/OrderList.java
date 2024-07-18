/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;


public class OrderList extends DatabasePageList
{
   public static final int ActiveOrders = 0;
   public static final int CompletedOrders = 1;

   public static final String ActiveOrdersName = "active";
   public static final String CompletedOrdersName = "completed";

   private int UserId = 0;
   private int OrderType = OrderList.ActiveOrders;
   private String TypeName = null;
   private ProcessNames Processes = null;
   private UserProfileInfo Info = new UserProfileInfo();
   private boolean IncludeId = false;

   public OrderList()
   {
      super();
   }

   public int getUserId()
   {
      return this.UserId;
   }

   public void setUserId(int userId, boolean includeInLink)
   {
      this.UserId = userId;
      this.IncludeId = includeInLink;
   }

   public UserProfileInfo getUserInfo()
   {
      return this.Info;
   }

   public void setType(String orderType)
   {
      this.OrderType = OrderList.ActiveOrders;
      this.TypeName = null;
      if(OrderList.CompletedOrdersName.equals(orderType))
      {
         this.OrderType = OrderList.CompletedOrders;
         this.TypeName = OrderList.CompletedOrdersName;
      }
   }

   public int getType()
   {
      return this.OrderType;
   }

   public String getTypeName()
   {
      return this.TypeName;
   }

   @Override
   public String getPageBaseLink()
   {
      String baseLink = "allorders.jsp?";
      if(this.UserId > 0)
      {
         baseLink = "distorders.jsp?";
         if(this.IncludeId)
            baseLink += "user=" + this.UserId + "&";
      }

      try
      {
         if(this.TypeName != null)
            baseLink += "otype=" + URLEncoder.encode(this.TypeName, "UTF-8") + "&";
      }
      catch(UnsupportedEncodingException uee)
      {
      }

      baseLink += "page=";
      return baseLink;
   }

   @Override
   public void setResults() throws SQLException
   {
      this.statement = this.connection.createStatement(
         ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      String condition = "";
      if(this.UserId > 0)
      {
         DatabaseUsers users = new DatabaseUsers(this);
         int groupId = users.getDistibutorGroup(this.UserId);
         condition = "AND users.groupid = " + groupId;
         if(this.IncludeId)
            users.getUserProfile(this.UserId, this.Info);
      }

      DatabaseProcesses processes = new DatabaseProcesses(this);
      this.Processes = processes.getOrderProcessMap();
      if(this.OrderType == OrderList.CompletedOrders)
      {
         this.results = this.statement.executeQuery(
            "SELECT users.email, userorders.orderid, userorders.orderdate, " +
            "userorders.userid, userorders.processid, userorders.srcnumber FROM users " +
            "INNER JOIN userorders ON users.userid = userorders.userid " +
            "WHERE userorders.processstep = -1 " + condition +
            " ORDER BY userorders.orderid DESC");
      }
      else
      {
         this.results = this.statement.executeQuery(
            "SELECT users.email, userorders.orderid, userorders.orderdate, " +
            "userorders.userid, userorders.processid, userorders.srcnumber FROM users " +
            "INNER JOIN userorders ON users.userid = userorders.userid " +
            "WHERE userorders.processstep > -1 " + condition +
            " ORDER BY userorders.orderid");
      }
   }

   public OrderInfo getNext() throws SQLException
   {
      OrderInfo order = new OrderInfo();
      order.setInfo(this.results);
      order.setProcessName(this.Processes.find(order.getProcessId()));
      return order;
   }
}

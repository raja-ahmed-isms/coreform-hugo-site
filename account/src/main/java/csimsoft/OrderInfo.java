/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.sql.ResultSet;
import java.sql.SQLException;


public class OrderInfo
{
   private int OrderId = 0;
   private int UserId = 0;
   private String Email = "";
   private String OrderDate = "";
   private int ProcessId = 0;
   private int ProcessStep = 0;
   private String ProcessName = "";
   private OrderProcessHandler Handler = null;
   private String LeadSource = "";

   public OrderInfo()
   {
   }

   public OrderInfo(int orderId)
   {
      this.OrderId = orderId;
   }

   public int getId()
   {
      return this.OrderId;
   }

   public void setId(int id)
   {
      this.OrderId = id;
   }

   public int getUserId()
   {
      return this.UserId;
   }

   public void setUserId(int id)
   {
      this.UserId = id;
   }

   public String getEmail()
   {
      return this.Email;
   }

   public void setEmail(String email)
   {
      this.Email = email == null ? "" : email;
   }

   public String getDate()
   {
      return this.OrderDate;
   }

   public void setDate(String date)
   {
      this.OrderDate = date == null ? "" : date;
   }

   public int getProcessId()
   {
      return this.ProcessId;
   }

   public void setProcessId(int id)
   {
      this.ProcessId = id;
   }

   public int getProcessStep()
   {
      return this.ProcessStep;
   }

   public void setProcessStep(int step)
   {
      this.ProcessStep = step;
   }

   public void incrementProcessStep()
   {
      this.ProcessStep += 1;
   }

   public String getProcessName()
   {
      return this.ProcessName;
   }

   public void setProcessName(String name)
   {
      this.ProcessName = name == null || name.isEmpty() ? "No Process" : name;
   }

   public OrderProcessHandler getHandler()
   {
      return this.Handler;
   }

   public void setHandler(OrderProcessHandler handler)
   {
      this.Handler = handler;
   }

   public String getLeadSource()
   {
      return this.LeadSource;
   }

   public void setLeadSource(String source)
   {
      this.LeadSource = source == null ? "" : source;
   }

   public void setInfo(ResultSet results) throws SQLException
   {
      setId(results.getInt("orderid"));
      setUserId(results.getInt("userid"));
      setEmail(results.getString("email"));
      setDate(results.getDate("orderdate").toString());
      setProcessId(results.getInt("processid"));
      setLeadSource(results.getString("srcnumber"));
   }

   public void setStep(ResultSet results) throws SQLException
   {
      setUserId(results.getInt("userid"));
      setProcessId(results.getInt("processid"));
      setProcessStep(results.getInt("processstep"));
   }
}

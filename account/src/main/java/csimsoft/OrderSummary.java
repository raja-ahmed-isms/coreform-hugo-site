/**
 * @(#)OrderSummary.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2011/4/1
 */

package csimsoft;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;


public class OrderSummary
{
   private int OrderId = 0;
   private int ProcessId = 0;
   private int ProcessStep = 0;
   private int StepCount = 0;
   private String OrderDate = null;
   private String PurchaseOrder = null;
   private String ReferenceNumber = null;
   private String LeadSource = null;
   private ArrayList<OrderSummaryLicense> Licenses =
      new ArrayList<OrderSummaryLicense>();
   private ArrayList<OrderSummaryDownload> Products =
      new ArrayList<OrderSummaryDownload>();

   public OrderSummary()
   {
   }

   public OrderSummary(int orderId)
   {
      this.OrderId = orderId;
   }

   public int getOrderId()
   {
      return this.OrderId;
   }

   public void setOrderId(int orderId)
   {
      if(this.OrderId != orderId)
      {
         this.OrderId = orderId;
         this.clear();
      }
   }

   public void clear()
   {
      this.ProcessId = 0;
      this.ProcessStep = 0;
      this.StepCount = 0;
      this.OrderDate = null;
      this.PurchaseOrder = null;
      this.ReferenceNumber = null;
      this.LeadSource = null;
      this.Licenses.clear();
      this.Products.clear();
   }

   public void setOrderSummary(DatabaseOrders orders) throws SQLException
   {
      // Get the information from the user orders table.
      orders.getOrderInfo(this.OrderId);
      if(orders.database.results.next())
      {
         this.OrderDate = orders.database.results.getDate("orderdate").toString();
         this.ProcessId = orders.database.results.getInt("processid");
         this.ProcessStep = orders.database.results.getInt("processstep");
         this.PurchaseOrder = orders.database.results.getString("ordernumber");
         this.ReferenceNumber = orders.database.results.getString("distnumber");
         this.LeadSource = orders.database.results.getString("srcnumber");
      }

      orders.database.results.close();

      // Get the number of process steps for the order from the
      // process handler.
      OrderProcessHandler handler = orders.getOrderHandler(this.OrderId);
      if(handler != null)
         this.StepCount = handler.getNumberOfSteps();

      // Get the list of products for the order.
      orders.getOrderProducts(this.OrderId, this.Products);

      // Get the download location for each of the products.
      DatabaseProducts products = new DatabaseProducts(orders.database);
      Iterator<OrderSummaryDownload> iter = this.Products.iterator();
      while(iter.hasNext())
      {
         OrderSummaryDownload product = iter.next();
         product.setLocation(products.getProductLocation(product.getId()));
      }

      // Get the list of user licenses for the order.
      orders.getOrderLicenses(this.OrderId);
      while(orders.database.results.next())
      {
         OrderSummaryLicense license = new OrderSummaryLicense();
         license.setLicense(orders.database.results);

         // Add the user license to the list.
         this.Licenses.add(license);
      }

      orders.database.results.close();

      // Get the license names for each of the user licenses.
      DatabaseLicenses licenses = new DatabaseLicenses(orders.database);
      Iterator<OrderSummaryLicense> jter = this.Licenses.iterator();
      while(jter.hasNext())
      {
         OrderSummaryLicense license = jter.next();
         license.setName(licenses.getLicenseName(license.getLicenseId()));
      }
   }

   public int getProcessId()
   {
      return this.ProcessId;
   }

   public int getProcessStep()
   {
      return this.ProcessStep;
   }

   public int getNumberOfSteps()
   {
      return this.StepCount;
   }

   public String getOrderDate()
   {
      if(this.OrderDate == null)
         return "";

      return this.OrderDate;
   }

   public boolean isPurchaseOrderEmpty()
   {
      return this.PurchaseOrder == null || this.PurchaseOrder.isEmpty();
   }

   public String getPurchaseOrder()
   {
      if(this.PurchaseOrder == null)
         return "";

      return this.PurchaseOrder;
   }

   public boolean isReferenceNumberEmpty()
   {
      return this.ReferenceNumber == null || this.ReferenceNumber.isEmpty();
   }

   public String getReferenceNumber()
   {
      if(this.ReferenceNumber == null)
         return "";

      return this.ReferenceNumber;
   }

   public boolean isLeadSourceEmpty()
   {
      return this.LeadSource == null || this.LeadSource.isEmpty();
   }

   public String getLeadSource()
   {
      if(this.LeadSource == null)
         return "";

      return this.LeadSource;
   }

   public boolean isLicensesEmpty()
   {
      return this.Licenses.isEmpty();
   }

   public int getLicenseCount()
   {
      return this.Licenses.size();
   }

   public OrderSummaryLicense getLicense(int index)
   {
      return this.Licenses.get(index);
   }

   public boolean isProductsEmpty()
   {
      return this.Products.isEmpty();
   }

   public int getProductCount()
   {
      return this.Products.size();
   }

   public OrderSummaryDownload getProduct(int index)
   {
      return this.Products.get(index);
   }
}
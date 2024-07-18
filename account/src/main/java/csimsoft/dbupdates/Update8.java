/**
 * @(#)Update8.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/1/24
 */

package csimsoft.dbupdates;

import csimsoft.ResultMessage;
import csimsoft.ResultMessageAdapter;
import java.sql.SQLException;


public class Update8 implements csimsoft.DatabaseUpdate
{

   public Update8()
   {
   }

   @Override
   public void updateDatabase(csimsoft.DatabaseUpdateParameters parameters)
      throws SQLException
   {
      ResultMessageAdapter message = parameters.getMessage();
      csimsoft.Database database = parameters.getDatabase();

      String[] names = {
         "CUBIT 13.1 lin32",
         "CUBIT-13.1-lin32",
         "CUBIT 13.1 lin32 trial",
         "CUBIT-13.1-lin32-trial",
         "CUBIT 13.1 lin64",
         "CUBIT-13.1-lin64",
         "CUBIT 13.1 lin64 trial",
         "CUBIT-13.1-lin64-trial"};
      java.util.ArrayList<Integer> mergeList = new java.util.ArrayList<Integer>();

      for(int i = 0; i < names.length; i++)
      {
         database.results = database.statement.executeQuery(
            "SELECT productid FROM products WHERE groupname = 'CUBIT' AND product = '" +
            names[i] + "'");
         if(database.results.next())
            mergeList.add(new Integer(database.results.getInt("productid")));

         database.results.close();
      }

      if(mergeList.size() == names.length)
      {
         // Iterate over all the downloads to be merged.
         java.util.Iterator<Integer> iter = mergeList.iterator();
         while(iter.hasNext())
         {
            int replacement = iter.next().intValue();
            if(!iter.hasNext())
               break;

            int old = iter.next().intValue();

            database.statement.executeUpdate(
               "UPDATE productlicenses SET productid = " + replacement +
               " WHERE productid = " + old);
            database.statement.executeUpdate(
               "UPDATE orderproducts SET productid = " + replacement +
               " WHERE productid = " + old);
            database.statement.executeUpdate(
               "UPDATE userdownloads SET productid = " + replacement +
               " WHERE productid = " + old);
            database.statement.executeUpdate(
               "UPDATE processlicense SET productid = " + replacement +
               " WHERE productid = " + old);
            database.statement.executeUpdate(
               "UPDATE userapprovals SET productid = " + replacement +
               " WHERE productid = " + old);

            database.statement.executeUpdate(
               "DELETE FROM openproducts WHERE productid = " + old);
            database.statement.executeUpdate(
               "DELETE FROM products WHERE productid = " + old);
         }

         database.connection.commit();
         message.setResult(ResultMessage.Success);
      }
      else
      {
         message.setResult(ResultMessage.Failure);
         message.addMessage("Unable to get all product ids.");
      }
   }
}
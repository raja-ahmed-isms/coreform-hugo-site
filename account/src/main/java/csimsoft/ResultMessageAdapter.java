/**
 * @(#)ResultMessageAdapter.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2010/6/22
 */

package csimsoft;


public interface ResultMessageAdapter
{
   void setResult(int result);

   void addException(Exception e);

   void addMessage(String extra);
}
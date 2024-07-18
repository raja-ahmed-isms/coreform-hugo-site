/**
 * @(#)DatabaseUpdate.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/1/24
 */

package csimsoft;

import java.sql.SQLException;


public interface DatabaseUpdate
{
   void updateDatabase(DatabaseUpdateParameters parameters) throws SQLException;
}
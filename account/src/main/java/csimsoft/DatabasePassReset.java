/**
 *
 * @author Mark Richardson
 */
package csimsoft;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import org.mindrot.jbcrypt.BCrypt;


public class DatabasePassReset extends DatabaseMethods
{

   public DatabasePassReset()
   {
      super();
   }

   public DatabasePassReset(Database db)
   {
      super(db);
   }

   public String getUserEmail(String token) throws SQLException
   {
      String email = null;
      PreparedStatement statement = this.database.connection.prepareStatement(
         "SELECT email, timecreated FROM passreset WHERE token = ?");
      this.database.addStatement(statement);
      statement.setString(1, token);
      this.database.results = statement.executeQuery();
      if(this.database.results.next()) {
         Timestamp created = (Timestamp)this.database.results.getObject("timecreated");
         //Expires in 10 minutes.
         java.util.Date now = new Date();
         if ((now.getTime() - created.getTime()) < 10 * 60 * 1000) {
            email = this.database.results.getString("email");
         } else {
            PreparedStatement deleteStatement = this.database.connection.prepareStatement(
               "DELETE FROM passreset WHERE token = ?");
            deleteStatement.setString(1, token);
            deleteStatement.executeUpdate();
         }
      } 
      this.database.results.close();
      return email;
   }

   public void addReset(String token, String email) throws SQLException
   {
      PreparedStatement statement = this.database.connection.prepareStatement(
         "INSERT INTO passreset (token, email) VALUES (?, ?)");
      statement.setString(1, token);
      statement.setString(2, email);
      statement.executeUpdate();
   }
}

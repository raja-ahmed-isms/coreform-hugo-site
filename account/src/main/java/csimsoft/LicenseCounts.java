package csimsoft;

import java.sql.PreparedStatement;

public class LicenseCounts 
{
    public int activeLicenses = -1;
    public int activeTrials = -1;

    public static LicenseCounts getCounts()
     {
         LicenseCounts counts = new LicenseCounts();
        Database database = new Database();
        try {
            database.connect();
            PreparedStatement query = database.connection.prepareStatement(
                "SELECT COUNT(*) FROM licenses WHERE expiration IS NULL AND userlicenseid in (SELECT userlicenseid FROM licenseactivations)");
            database.addStatement(query);
            database.results = query.executeQuery();
            database.results.next();
            int activePerpetualLicenses = database.results.getInt(1);
            database.results.close();

            database.statement = database.connection.createStatement();
            database.results = database.statement.executeQuery(
                "SELECT COUNT(*) FROM licenses WHERE expiration IS NOT NULL AND expiration > CURDATE() AND userlicenseid in (SELECT userlicenseid FROM licenseactivations)");
            database.results.next();
            int activeExpireLicenses = database.results.getInt(1);
            database.results.close();

            counts.activeLicenses = activePerpetualLicenses + activeExpireLicenses;
            AcctLogger.get().info("Active licenses: " + counts.activeLicenses);


            database.statement = database.connection.createStatement();
            database.results = database.statement.executeQuery(
                "SELECT COUNT(*) FROM licenses WHERE expiration IS NOT NULL AND expiration BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY) AND userlicenseid in (SELECT userlicenseid FROM licenseactivations)");
            database.results.next();
            counts.activeTrials = database.results.getInt(1);
            AcctLogger.get().info("Active trials: " + counts.activeTrials);
            database.results.close();

        }
        catch(Exception e) {
            AcctLogger.get().info("Exception getting counts: " + e);
        }
        return counts;
    }
}
/**
 * Description: Used to create tables in the database and query data from it.
 */

package data;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// In general look into the DAO pattern, will come in useful for stuff like that: https://www.baeldung.com/java-dao-pattern
public class DataSource {

    //Instance variables.
    private static final String DB_NAME = "rates.db";
    private static final String TABLE_NAME = "rates";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_COUNTRY = "country";
    private static final String COLUMN_RATE = "rate";
    private static final String TABLE_CREATE_START = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME;
    private static final String TABLE_CREATE_END = "(date TEXT, country TEXT, rate DOUBLE)";
    private static final Path path = FileSystems.getDefault().getPath(".");
    private static final String CONNECTION_STRING = "jdbc:sqlite:" + path.normalize().toAbsolutePath() + "\\" + DB_NAME;
    private static final String QUERY_RATES_DATE = "SELECT * FROM " + TABLE_NAME;
    private Connection conn;
    private static final DataSource instance = new DataSource();

    //Returns singleton.
    public static DataSource getInstance(){
        return instance;
    }

    //Public methods accessible to other classes which invoke methods from this class.
    public List<Rate> getRates(String date){
        return getFromTable(date);
    }
    public String getRate(String date){ return getRateFromTable(date); }
    
    // You can actually call that smth like saveRate etc. You can usually safely assume that the DB schema will be correct, and you dont need to run CREATE if it not exists.
    public void addRatesTable(List<Rate> rates, String date){ addTable(rates, date); }

    //Retrieves a rate for a given date and currency.
    // What if you receive NULL?
    private String getRateFromTable(String date){
        System.out.println("date = " + date);
        // How can a date be "connection check". If the "date" can be multiple things then better rename that.
        // What if the name is null? NPE
        if (date.equals("connection check")){
            return "echo connection check"; // Here the "getRate" will return a non-rate. Not ideal and can get confusing for the classes using the interface
        }else{
            String resultString = "";
            try{
                conn = DriverManager.getConnection(CONNECTION_STRING);
                Statement statement = conn.createStatement();
                String[] query = date.split("\\|");
                System.out.println("SELECT * FROM " + query[0] + " WHERE country = " + "\"" + query[1] + "\""); //For debugging purposes  ---> A lot of strings created. Use break points in the IDE instead or avoid "+".
                // Check the query builder to avoid the below many strings and hard to read: https://docs.oracle.com/javaee/6/tutorial/doc/bnbrg.html
                ResultSet result = statement.executeQuery("SELECT * FROM " + query[0] + " WHERE country = " + "\"" + query[1] + "\"");
                Rate returnedRate = new Rate(result.getString(COLUMN_DATE), 
                                             result.getString(COLUMN_COUNTRY), 
                                             result.getDouble(COLUMN_RATE)); // Easier to read when you align like that.
                
                resultString = (returnedRate.getDate() + "|" + returnedRate.getCountry() + "|" + returnedRate.getRate());
            }catch (SQLException e){
                System.out.println("Unable to retrieve requested rate: " + date);
                resultString = "Unable to retrieve requested rate!"; // Same, dont return unexpected results.
            }
            // Leave an empty line.
            return resultString;
        }
    }
    
    //Retrieves rates for a given date.
    // If the only diff between this and the above is the date vs date + ccy then the parameters AND the name should reflect this. Its fine to call it getRateByDate for example
    // What if you receive NULL?
    private List<Rate> getFromTable(String date){
        List<Rate> rates = new ArrayList<>();
        try{
            conn = DriverManager.getConnection(CONNECTION_STRING);
            StringBuilder sb = new StringBuilder(QUERY_RATES_DATE);
            sb.append(date.replaceAll("[^0-9]",""));        // What is the purpose of this? Also what if NPE?
            Statement statement = conn.createStatement();
            try{
//                System.out.println(sb.toString()); //For debugging purposes only  ---> Remove
                ResultSet results = statement.executeQuery(sb.toString());
                if (results != null){
                    while (results.next()){
                        Rate rate = new Rate(results.getString(COLUMN_DATE), 
                                             results.getString(COLUMN_COUNTRY), 
                                             results.getDouble(COLUMN_RATE));       // More readable like that.
                        
                        rates.add(rate);
                    }
                }else 
                    rates =  null;          // One statement per line, more readable that way. Also you are basically instead of returning empty you ll now return null on purpose.
            }catch (SQLException h){
                //Do nothing                // You are suppresing an issue, do you aim to overall return empty list or null in case of issues?
            }
            statement.close();
        }catch (SQLException e){
            System.out.println("SQLException caught in DataSource -> getFromTable()");
        }finally {
            try{
                if (conn != null){
                    conn.close();
                }
            }catch (SQLException f){
                System.out.println("SQLException caught in DataSource -> getFromTable() -> finally");
            }
            return rates.size() > 0 ? rates : null;     // If you've null-ed out the rates that will break with NPE. Try rates?.size()
        }
    }

    //Adds a table to the database using List of rates as content and date for table name.
    // addTable would mean that the only thing that this does is add a table to a DB. However it creates a table if it doesnt exist AND inserts a rate. Id rename and change that to saveRate or smth similar.
    // What if you receive NULLs?
    private void addTable(List<Rate> rates, String date){
        try{
            String tempdate = date.replaceAll("[^0-9]","");
            conn = DriverManager.getConnection(CONNECTION_STRING);
            Statement statement = conn.createStatement();
            statement.execute(TABLE_CREATE_START + tempdate + TABLE_CREATE_END); // Again i appreciate that its good that it creates its own schema effectively but its ok to assume that the DB is going to be setup in advance during release.
            // Remove commented code/ statements if they are not run.
            //            System.out.println((TABLE_CREATE_START + tempdate + TABLE_CREATE_END)); //For debugging purposes only
            for (Rate rate : rates) {
                // Remove commented code/ statements if they are not run.
                // INSERT INTO rates (date, country, rate) VALUES("2020-12-22", "ZAR", 18.3562)
                // Check the query builder to avoid the below many strings and hard to read: https://docs.oracle.com/javaee/6/tutorial/doc/bnbrg.html
                String query = ("INSERT INTO " + TABLE_NAME + tempdate + "(date, country, rate)" + " VALUES('" + rate.getDate() + "', '" + rate.getCountry() + "', " + rate.getRate() + ")");
                statement.execute(query);
            }
            System.out.println("New table " + TABLE_NAME + tempdate + " created successfully.");
            statement.close();
        }catch (SQLException e){
            System.out.println("SQLException caught in DataSource -> addRatesTable()");
        }finally {
            try{
                if (conn != null){
                    conn.close();
                }
            }catch (SQLException f){
                System.out.println("SQLException caught in DataSource -> addRatesTable() -> finally");
            }
        }
    }
// Remove blank lines.

}

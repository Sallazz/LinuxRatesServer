/**
 * Description: Used to create tables in the database and query data from it.
 */

package data;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
    public void addRatesTable(List<Rate> rates, String date){ addTable(rates, date); }

    //Retrieves a rate for a given date and currency.
    private String getRateFromTable(String date){
        System.out.println("date = " + date);
        if (date.equals("connection check")){
            return "echo connection check";
        }else{
            String resultString = "";
            try{
                conn = DriverManager.getConnection(CONNECTION_STRING);
                Statement statement = conn.createStatement();
                String[] query = date.split("\\|");
                System.out.println("SELECT * FROM " + query[0] + " WHERE country = " + "\"" + query[1] + "\""); //For debugging purposes
                ResultSet result = statement.executeQuery("SELECT * FROM " + query[0] + " WHERE country = " + "\"" + query[1] + "\"");
                Rate returnedRate = new Rate(result.getString(COLUMN_DATE), result.getString(COLUMN_COUNTRY), result.getDouble(COLUMN_RATE));
                resultString = (returnedRate.getDate() + "|" + returnedRate.getCountry() + "|" + returnedRate.getRate());
            }catch (SQLException e){
                System.out.println("Unable to retrieve requested rate: " + date);
                resultString = "Unable to retrieve requested rate!";
            }
            return resultString;
        }
    }

    //Retrieves rates for a given date.
    private List<Rate> getFromTable(String date){
        List<Rate> rates = new ArrayList<>();
        try{
            conn = DriverManager.getConnection(CONNECTION_STRING);
            StringBuilder sb = new StringBuilder(QUERY_RATES_DATE);
            sb.append(date.replaceAll("[^0-9]",""));
            Statement statement = conn.createStatement();
            try{
//                System.out.println(sb.toString()); //For debugging purposes only
                ResultSet results = statement.executeQuery(sb.toString());
                if (results != null){
                    while (results.next()){
                        Rate rate = new Rate(results.getString(COLUMN_DATE), results.getString(COLUMN_COUNTRY), results.getDouble(COLUMN_RATE));
                        rates.add(rate);
                    }
                }else rates =  null;
            }catch (SQLException h){
                //Do nothing
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
            return rates.size() > 0 ? rates : null;
        }
    }

    //Adds a table to the database using List of rates as content and date for table name.
    private void addTable(List<Rate> rates, String date){
        try{
            String tempdate = date.replaceAll("[^0-9]","");
            conn = DriverManager.getConnection(CONNECTION_STRING);
            Statement statement = conn.createStatement();
            statement.execute(TABLE_CREATE_START + tempdate + TABLE_CREATE_END);
//            System.out.println((TABLE_CREATE_START + tempdate + TABLE_CREATE_END)); //For debugging purposes only
            for (Rate rate : rates) {
                // INSERT INTO rates (date, country, rate) VALUES("2020-12-22", "ZAR", 18.3562)
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


}

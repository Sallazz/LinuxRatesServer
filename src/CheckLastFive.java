/**
 * Description: Checks the last five days to ensure their rates are stored in the
 * database. If not, invokes the getRates() method of the CurrencyScoopAPI class
 * and invokes the addRatesTable() method of the DataSource class.
 */

import com.mashape.unirest.http.exceptions.UnirestException;
import data.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CheckLastFive {

    //Instance variables.
    private DataSource data;
    private List<Rate> rates;

    //Constructor.
    public CheckLastFive(){
        System.out.println("Running check for rates from past five days...");
        data = DataSource.getInstance();
        rates = new ArrayList<>();
    }

    //Public access method to execute the class's methods.
    public void executeCheck(){
        checkForRates();
        System.out.println("Check complete.");
    }

    //Check the database for rates from each of the last five days.
    private void checkForRates(){
        LocalDate date;
        for (int i = 0; i < 5; i++){
            date = LocalDate.now().minusDays(i);
            checkTable(date.toString());
        }
    }

    //If table found, message displayed to advise user, if not invokes CurrencyScoopAPI class's method.
    private void checkTable(String date){
        try{
            rates = data.getRates(date);
            if (rates != null){
                System.out.println("Table with date " + date + " already exists.");
            }else{
                System.out.println("No table with date " + date + " found. Calling CurrencyScoopAPI...");
                checkAPI(date);
            }
        }catch (NullPointerException e){
            System.out.println("NullPointerException caught in CheckLastFive -> checkTable()");
        }catch (UnirestException g){
            System.out.println("UnirestException caught in CheckLastFive -> checkTable()");
        }
    }

    private void checkAPI(String date) throws UnirestException {
        String response = new CurrencyScoopAPI().getRates(date);
        rates = new ResponseInterpreter().getRates(response);
        System.out.println("Attempting to create a table with date " + date + "...");
        data.addRatesTable(rates, date);
    }























}

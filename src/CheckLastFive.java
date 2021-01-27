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

// The name of the class in this case should be a bit more generic. A class describes an object that will get created. A car, a human, a fruit, an entity. In general they should be nouns and not verbs. "CheckLastFive" is an operation rather than an entity. Change it to something like DataChecker or similar.
// For more info on class naming conventions check: https://www.oracle.com/java/technologies/javase/codeconventions-namingconventions.html#:~:text=Class%20names%20should%20be%20nouns,such%20as%20URL%20or%20HTML).

public class CheckLastFive {

    //Instance variables.
    private DataSource data;
    private List<Rate> rates;

    //Constructor.
    // Try to avoid obvious comments on that kind cause it clutters the page more and makes reading harder. Try to comment only to explain a tricky piece of logic. Usually the majority of the code should be self explanatory.
    public CheckLastFive(){
        // Consider using a logger instead of printing out in the console (at least thats better for enterprise systems).
        System.out.println("Running check for rates from past five days...");
        data = DataSource.getInstance();
        rates = new ArrayList<>();
    }

    //Public access method to execute the class's methods.
    public void executeCheck(){
        checkForRates();
        System.out.println("Check complete."); // again try the logger.
    }

    //Check the database for rates from each of the last five days.
    private void checkForRates(){ 
        // "checkForRates" doesnt exactly represent what is happening here. Here you check rates but for the last 5 days. Consider:
        //      - How would you name this method in order to describe EVERYTHING that is doing? Most likely: CheckRatesFromTableOrApiForLastFiveDays
        //      - In general, if its too long, or you need to put the words AND you should consider splitting the logic.
        //      - In this case either get the # of dates from outside if you want it parametirised or rename to checkRatesForLastFiveDays()
        LocalDate date;
        
        // Why do we need the for here? Cant we do here LocalData.now().minusDays(5)?
        for (int i = 0; i < 5; i++){ // instead of having a 5 here consider putting that in a constant and moving it to a static constants file. It helps in larger projects to keep all your constants in one place. 
            date = LocalDate.now()
                            .minusDays(i); // have only one statement per line. In this case change it like this. It helps with readability, especially in longer chained statements.
            
            checkTable(date.toString());
        }
    }

    //If table found, message displayed to advise user, if not invokes CurrencyScoopAPI class's method.
    // Based on my previous comments this name would more accurately be: checkTableOrApi. I suggest you moving out the API check into the check rates method.
    // This way that method will decide what to do in case the checkTable comes back empty. The S in SOLID is for "Single Responsibility" each method, each class etc must be doing one thing.
    private void checkTable(String date){
        try{
            rates = data.getRates(date);
            // Leave a blank line.
            if (rates != null){
                System.out.println("Table with date " + date + " already exists."); // Consider logger to try it out. Also you are creating too many strings here in your pool. Consider a StringBuilder if your application's performance is important.
            }else{
                System.out.println("No table with date " + date + " found. Calling CurrencyScoopAPI..."); // As above
                checkAPI(date); // As above, please move out
            }
        }catch (NullPointerException e){ // Is there a possibility that anyother exception might be thrown here? If yet and you wont catch it it will kill your programme. 
            System.out.println("NullPointerException caught in CheckLastFive -> checkTable()"); // Here you are effectively catching an exception and doing nothing with it. In both cases below whatever went wrong with your programme is suppresed and not addressed. Consider throwing it upwards, or addressing it. Especially since this method doesnt return anything, the programme above will be unaware that something didnt happen that should have happened and might end up in a wrong state.
        }catch (UnirestException g){
            System.out.println("UnirestException caught in CheckLastFive -> checkTable()"); // Consider you messages and who will be reading it? Support? dev? Consider writing your logs in more english focused text. Like API is down, will retry. or Table not found etc.
        }
    }
    
    // The name checkAPI it doesnt just check right?? It populates data as well. Please rename.
    private void checkAPI(String date) throws UnirestException {
        String response = new CurrencyScoopAPI().getRates(date);
        rates = new ResponseInterpreter().getRates(response);
        System.out.println("Attempting to create a table with date " + date + "...");
        data.addRatesTable(rates, date); // What if the API is down?
    }

    // Remove blank extra rows.






















}

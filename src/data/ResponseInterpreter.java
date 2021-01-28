/**
 * Description: Converts the response from CurrencyScoop into a List of rate objects.
 */

package data;

import java.util.ArrayList;
import java.util.List;

public class ResponseInterpreter {

    //Public method accessible to other classes which invokes the private method from this class.
    public List<Rate> getRates(String response){
        return getRatesPrivate(response);
    }

    private List<Rate> getRatesPrivate(String response){
        //Declare new List to be returned at end of method.
        List<Rate> rates = new ArrayList<>();
        String myString = response;     // Why assign that to another string? And please rename from "myString" to something that describes what this holds.

        //Get date of response.
        int dateStart = myString.indexOf("date") + 7;           // Its better to extract "date" to a specific static var somewhere else. Also what if the myString is null? NPE. 
        String date = myString.substring(dateStart, dateStart + 10);

        //Remove unnecessary characters so only currencies and rates remain.
        String s = myString.replaceAll("[^A-Z0-9.,]",""); // NPE? Also please rename "s"

        //Cut string to begin where rates start - at USD.
        int result = s.indexOf("USD");                      // Why is this singling out USD? What is the intended logic? Better parametirise if nothing else.
        if (result == -1){
            return null;
        }else{
            String x = s.substring(result);                 // Please rename "x".

            //Split each currency and create Rate object to populate in List.
            String[] curr = x.split(",");
            for (String cu : curr){
                rates.add(new Rate(date, cu.substring(0, 3), Double.parseDouble(cu.substring(3)))); // Better assign the above to named variables and use those here. Improves readability.
            }

            return rates;
        }
    }
// Remove blank line.
}

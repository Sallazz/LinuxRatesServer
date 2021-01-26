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
        String myString = response;

        //Get date of response.
        int dateStart = myString.indexOf("date") + 7;
        String date = myString.substring(dateStart, dateStart + 10);

        //Remove unnecessary characters so only currencies and rates remain.
        String s = myString.replaceAll("[^A-Z0-9.,]","");

        //Cut string to begin where rates start - at USD.
        int result = s.indexOf("USD");
        if (result == -1){
            return null;
        }else{
            String x = s.substring(result);

            //Split each currency and create Rate object to populate in List.
            String[] curr = x.split(",");
            for (String cu : curr){
                rates.add(new Rate(date, cu.substring(0, 3), Double.parseDouble(cu.substring(3))));
            }

            return rates;
        }
    }

}

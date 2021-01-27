/**
 * Description: Retrieves rates from Currency Scoop using Unirest library.
 */

package data;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.http.HttpResponse;

public class CurrencyScoopAPI {

    //Instance variable.
    private final String api_key = "25a5959cb6fa53d9ab2f8b35de93c761";

    //Public method accessible to other classes which invokes the private method from this class.
    public String getRates(String date) throws UnirestException {
        return getRatesPrivate(date);
    }

    //Get request used to retrieve rates for a given date.
    // Maybe call this one call API? I usually dont like adding scope terms in method names.
    private String getRatesPrivate(String date) throws UnirestException {
        // Can the get() return null? at which point you might get NPE?
        HttpResponse<String> response = Unirest.get("https://api.currencyscoop.com/v1/historical?api_key=" + api_key + "&base=GBP&date=" + date)
                .asString();
        
        // Can the response be null? At which point you might get NPE?
        return response.getBody();
    }

}

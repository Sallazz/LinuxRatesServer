/**
 * Description: Rate object which is used to store the date, country and currency rate.
 */

package data;

public class Rate {

    //Instance variables.
    private String date;
    private String country;
    private Double rate;

    //Constructor.
    public Rate(String date, String country, Double rate) {
        this.date = date;
        this.country = country;
        this.rate = rate;
    }

    //Get methods to return fields.
    public String getDate() {
        return date;
    }

    public String getCountry() {
        return country;
    }

    public Double getRate() {
        return rate;
    }
}

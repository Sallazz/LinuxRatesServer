/**
 * Description: Rate object which is used to store the date, country and currency rate.
 */

package data;

// Might be worht spliting an interface out of this and other objects of that kind, easier to mock for tests and better practice to achieve loose coupling.
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

package Tests;

import data.DataSource;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataSourceTest {

    DataSource data = new DataSource();
    // Make sure your tests' names reflect what they are testing. This way a person reading those will understand what they are testing without having to read the whole test (which can get big).
    @Test
    public void getRates() {
        assertNotNull(data.getRates("2021-01-25"));
    }

    @Test
    public void getRate() {
        assertEquals("2021-01-25|USD|1.36736972", data.getRate("rates20210125|USD"));
    }
}

package Tests;

import data.DataSource;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataSourceTest {

    DataSource data = new DataSource();

    // Make sure your tests' names specify what they are testing for. This way a person reading them will not have to read the whole test to understand what is happening but rather just the title.
    @Test
    public void getRates() {
        assertNotNull(data.getRates("2021-01-25"));
    }

    @Test
    public void getRate() {
        assertEquals("2021-01-25|USD|1.36736972", data.getRate("rates20210125|USD"));
    }
}

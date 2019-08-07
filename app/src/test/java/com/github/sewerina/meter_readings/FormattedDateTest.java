package com.github.sewerina.meter_readings;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Calendar;

public class FormattedDateTest {

    @Test
    public void testText() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, 7, 6);

        FormattedDate date = new FormattedDate(calendar.getTime());
        assertEquals("06.08.2019", date.text());
    }
}

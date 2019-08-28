package com.github.sewerina.meter_readings;

import com.github.sewerina.meter_readings.database.ReadingEntity;
import com.github.sewerina.meter_readings.ui.report.Report;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class ReportTest {
    private List<ReadingEntity> mReadings = new ArrayList<>();
    private Report mReport;
    private String mHomeAddress;

    @Before
    public void init() {
        ReadingEntity readingLast = new ReadingEntity();
        // For date setting = 01.09.2019 in reading
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, 8, 1);
        readingLast.date = calendar.getTime();
        readingLast.coldWater = 140;
        readingLast.hotWater = 35;
        readingLast.drainWater = readingLast.coldWater + readingLast.hotWater;
        readingLast.electricity = 190;
        readingLast.gas = 55;

        mReadings.add(readingLast);

        ReadingEntity readingFirst = new ReadingEntity();
        readingFirst.coldWater = 115;
        readingFirst.hotWater = 25;
        readingFirst.drainWater = readingFirst.coldWater + readingFirst.hotWater;
        readingFirst.electricity = 150;
        readingFirst.gas = 30;

        mReadings.add(readingFirst);
        mReport = new Report(mReadings);

        mHomeAddress = "Москва";
    }

    @Test
    public void testCreateReport() {
        assertNotNull(mReadings);
        assertNotNull(mReport);
        assertNotNull(mHomeAddress);
        assertTrue(!mHomeAddress.isEmpty());
    }

    @Test
    public void testHomeId() {
        assertTrue(mReport.getHomeId() >= 0);
    }

    @Test
    public void testDate() {
        assertNotNull(mReport.getDate());
    }

    @Test
    public void testColdWaterValue() {
        assertTrue(mReport.getColdWaterValue() >= 0);
        assertEquals(140 - 115, mReport.getColdWaterValue());
    }

    @Test
    public void testHotWaterValue() {
        assertTrue(mReport.getHotWaterValue() >= 0);
        assertEquals(35 - 25, mReport.getHotWaterValue());
    }

    @Test
    public void testDrainWaterValue() {
        assertTrue(mReport.getDrainWaterValue() >= 0);
        assertEquals(175 - 140, mReport.getDrainWaterValue());
    }

    @Test
    public void testElectricityValue() {
        assertTrue(mReport.getElectricityValue() >= 0);
        assertEquals(190 - 150, mReport.getElectricityValue());
    }

    @Test
    public void testGasValue() {
        assertTrue(mReport.getGasValue() >= 0);
        assertEquals(55 - 30, mReport.getGasValue());
    }

    @Test
    public void testReportMessageWhenAllComponentsFalse() {
        String expectation = "Расходы компонентов для дома Москва на дату 01.09.2019 составляют:";
        String reality = mReport
                .reportMessage(false, false, false, false, false, mHomeAddress);
        assertNotNull(reality);
        assertTrue(!reality.isEmpty());
        assertEquals(expectation, reality);
    }

    @Test
    public void testReportMessageWhenAllComponentsTrue() {
        String expectation = "Расходы компонентов для дома Москва на дату 01.09.2019 составляют:"
                + " хол.вода = 25 гор.вода = 10 канализация = 35 электричество = 40 gas = 25";
        String reality = mReport
                .reportMessage(true, true, true, true, true, mHomeAddress);
        assertNotNull(reality);
        assertTrue(!reality.isEmpty());
        assertEquals(expectation, reality);
    }

    @Test
    public void testReportMessageWhenDrainWaterAndGasFalse(){
        String expectation = "Расходы компонентов для дома Москва на дату 01.09.2019 составляют:"
                + " хол.вода = 25 гор.вода = 10 электричество = 40";
        String reality = mReport
                .reportMessage(true, true, false, true, false, mHomeAddress);
        assertNotNull(reality);
        assertTrue(!reality.isEmpty());
        assertEquals(expectation, reality);
    }

}

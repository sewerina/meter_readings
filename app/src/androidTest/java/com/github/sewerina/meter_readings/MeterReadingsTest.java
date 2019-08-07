package com.github.sewerina.meter_readings;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
public class MeterReadingsTest {
    private UiDevice mDevice;

    @Before
    public void startMainActivityFromHomeScreen() {
        mDevice = UiDevice.getInstance(getInstrumentation());
        mDevice.pressHome();

//        UiObject allAppsButton = mDevice
//                .findObject(new UiSelector().description("Apps"));
//        allAppsButton.clickAndWaitForNewWindow();
//        UiObject appTab = new UiObject(new UiSelector().text("ReadingApp"));
//        appTab.click();
    }

    @Test
    public void checkPreconditions() {
        assertThat(mDevice, notNullValue());
    }



}

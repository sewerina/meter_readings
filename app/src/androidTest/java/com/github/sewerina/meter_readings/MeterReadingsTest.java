package com.github.sewerina.meter_readings;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MeterReadingsTest {
    private static final int LAUNCH_TIMEOUT = 5000;
    private static final String BASIC_PACKAGE = "com.github.sewerina.meter_readings";
    private UiDevice mDevice;

    @Before
    public void startShellActivityFromHomeScreen() {
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(getInstrumentation());
        // Start from the home screen
        mDevice.pressHome();

        // Wait for launcher
        final String launcherPackage = getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

        // Launch the blueprint app
        Context context = getApplicationContext();
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(BASIC_PACKAGE);
        assertThat(intent, notNullValue());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);    // Clear out any previous instances
        context.startActivity(intent);

        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(BASIC_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
    }

    @Test
    public void checkPreconditions() {
        assertThat(mDevice, notNullValue());
    }

    private String getLauncherPackageName() {
        // Create launcher Intent
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        // Use PackageManager to get the launcher package name
        PackageManager pm = getApplicationContext().getPackageManager();
        ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }

    @Test
    public void testShellActivity() throws UiObjectNotFoundException {
        // Title
        mDevice.findObject(By.text("Показания")).isEnabled();

        // Spinner
        mDevice.findObject(By.textContains("из списка ниже")).isEnabled();

        UiObject2 spinnerHome = mDevice.findObject(By.res(BASIC_PACKAGE, "spinnerHomes"));
        if (spinnerHome.isEnabled() && spinnerHome.isClickable() && spinnerHome.isScrollable()) {
            spinnerHome.click();
            spinnerHome.clickAndWait(Until.newWindow(), 1500);
        }

        // Fab
        mDevice.wait(Until.findObject(By.res(BASIC_PACKAGE, "fab_addReading")), 500);
        UiObject2 fabAddReading = mDevice.findObject(By.res(BASIC_PACKAGE, "fab_addReading"));
        if (fabAddReading.isEnabled() && fabAddReading.isClickable()) {
            fabAddReading.clickAndWait(Until.newWindow(), 500);
        }

        // Open dialog & create new reading
        mDevice.wait(Until.findObject(By.text("Введите Ваши показания")), 500).isEnabled();

        UiObject2 etColdWater = mDevice.findObject(By.res(BASIC_PACKAGE, "et_coldWater"));
        if (etColdWater.isEnabled()) {
            etColdWater.setText("18");
        }

        UiObject2 etHotWater = mDevice.findObject(By.res(BASIC_PACKAGE, "et_hotWater"));
        if (etHotWater.isEnabled()) {
            etHotWater.setText("9");
        }

        UiObject2 etElectricity = mDevice.findObject(By.res(BASIC_PACKAGE, "et_electricity"));
        if (etElectricity.isEnabled()) {
            etElectricity.setText("20");
        }

        UiObject2 okBtn = mDevice.findObject(By.clazz(Button.class));
        if (okBtn.isEnabled() && okBtn.isClickable()) {
            okBtn.clickAndWait(Until.newWindow(), 1500);
        }

        // Recycler
        UiScrollable collection = new UiScrollable(new UiSelector().className(RecyclerView.class));
        int childCountAfterAdd = collection.getChildCount(new UiSelector().className(FrameLayout.class));

        assertTrue(childCountAfterAdd > 0);

        UiObject child = collection.getChildByInstance(new UiSelector().className(FrameLayout.class), 0);
        child.clickAndWaitForNewWindow();

        UiObject2 object = mDevice.findObject(By.res(BASIC_PACKAGE, "btn_delete"));
        object.clickAndWait(Until.newWindow(), 1500);

        collection = new UiScrollable(new UiSelector().className(RecyclerView.class));
        int childCountAfterDelete = collection.getChildCount(new UiSelector().className(FrameLayout.class));
        assertTrue(childCountAfterAdd > childCountAfterDelete);
    }
}
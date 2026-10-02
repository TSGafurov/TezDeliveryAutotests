package com.tezdelivery.tests;

import com.tezdelivery.config.TestConfig;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;

public class BaseTest {

    private static final Logger LOG = LoggerFactory.getLogger(BaseTest.class);

    protected static final String ADB_DEVICE = TestConfig.deviceUdid();

    protected AndroidDriver driver;

    // alwaysRun=true обязателен: под групповым фильтром (testng-safe.xml) TestNG на
    // повторной попытке после RetryOnce пропускает @BeforeMethod/@AfterMethod без
    // alwaysRun, т.к. они сами не в группе "safe" - driver остаётся null на retry
    // (воспроизведено в TezChakanaAutotests 2026-09-02).
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        printPreflightChecklist();

        // udid закреплён явно: при нескольких подключённых устройствах Appium может
        // создать сессию не на том и падать на каждом последующем тесте.
        UiAutomator2Options options = new UiAutomator2Options()
                .setUdid(ADB_DEVICE)
                .setAppPackage(TestConfig.appPackage())
                .setAppActivity(TestConfig.appActivity())
                .setNoReset(true)
                // noReset сохраняет логин, но без принудительного перезапуска приложение
                // продолжает с того экрана, где его оставили (воспроизведено 2026-10-02:
                // SmokeTest стартовал на Profil, а не на Buyurtmalar). forceAppLaunch
                // перезапускает процесс - каждый тест начинает с главного экрана.
                .amend("appium:forceAppLaunch", true)
                .setAutoGrantPermissions(false)
                .setNewCommandTimeout(Duration.ofSeconds(120));

        driver = new AndroidDriver(new URI(TestConfig.appiumUrl()).toURL(), options);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // versionName установленного приложения - эталон для "Versiya: x.y.z" в профиле.
    protected static String installedVersionName() throws IOException, InterruptedException {
        Process process = new ProcessBuilder("adb", "-s", ADB_DEVICE, "shell", "dumpsys", "package",
                TestConfig.appPackage()).start();
        String output = new String(process.getInputStream().readAllBytes());
        process.waitFor();
        return output.lines()
                .map(String::trim)
                .filter(line -> line.startsWith("versionName="))
                .map(line -> line.substring("versionName=".length()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("versionName не найден для " + TestConfig.appPackage()));
    }

    private void printPreflightChecklist() throws IOException, InterruptedException {
        boolean emulatorUp = isEmulatorConnected();
        boolean appiumUp = isAppiumReachable();

        LOG.info("=== Preflight checklist ===");
        LOG.info("[{}] Android emulator visible in `adb devices`", emulatorUp ? "OK" : "FAIL");
        LOG.info("[{}] Appium server reachable at {}", appiumUp ? "OK" : "FAIL", TestConfig.appiumUrl());
        LOG.info("[..] App data is kept as-is (pm clear skipped to preserve login session)");
        LOG.info("===========================");

        if (!emulatorUp) {
            throw new IllegalStateException("No Android emulator/device found. Start the emulator and check `adb devices`.");
        }
        if (!appiumUp) {
            throw new IllegalStateException("Appium server is not reachable at " + TestConfig.appiumUrl() + ". Start the Appium server first.");
        }
    }

    private boolean isEmulatorConnected() throws IOException, InterruptedException {
        Process process = new ProcessBuilder("adb", "devices").start();
        process.waitFor();
        String output = new String(process.getInputStream().readAllBytes());
        return output.lines().skip(1).anyMatch(line -> line.contains("\tdevice"));
    }

    private boolean isAppiumReachable() {
        try {
            URL statusUrl = new URI(TestConfig.appiumUrl() + "/status").toURL();
            HttpURLConnection connection = (HttpURLConnection) statusUrl.openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            connection.disconnect();
            return responseCode == 200;
        } catch (IOException | URISyntaxException e) {
            return false;
        }
    }
}

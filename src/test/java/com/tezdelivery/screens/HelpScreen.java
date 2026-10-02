package com.tezdelivery.screens;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

/**
 * "Yordam" (Profil -> "Yordam"). Пункты уводят во внешние приложения (звонилка, почта,
 * Telegram) - в тестах только проверяем наличие, не тапаем.
 */
public class HelpScreen extends BaseScreen {

    private static final By TITLE = AppiumBy.accessibilityId("Yordam");
    private static final By CALL_CENTER = AppiumBy.accessibilityId("Koll-markaz");
    private static final By EMAIL = AppiumBy.accessibilityId("Elektron pochta");
    private static final By TELEGRAM = AppiumBy.accessibilityId("Telegram orqali bog‘lanish");

    public HelpScreen(AndroidDriver driver) {
        super(driver);
    }

    public void verifyShown() {
        waitFor(TITLE);
    }

    public boolean areContactOptionsShown() {
        return isPresent(CALL_CENTER, WAIT_TIMEOUT)
                && isPresent(EMAIL, WAIT_TIMEOUT)
                && isPresent(TELEGRAM, WAIT_TIMEOUT);
    }
}

package com.tezdelivery.screens;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

import java.util.List;

/**
 * "Xabarnomalar" (колокольчик на главном). Карточка: "Yangi buyurtma\n02.10.2026\n14:12".
 * Тап по карточке и галочка "прочитать все" справа сверху меняют статус прочтения - в
 * safe-тестах не трогать.
 */
public class NotificationsScreen extends BaseScreen {

    private static final By TITLE = AppiumBy.accessibilityId("Xabarnomalar");
    private static final By NOTIFICATION_CARD = By.xpath(
            "//android.view.View[@clickable='true' and @scrollable='true' and string-length(@content-desc) > 0]");

    public NotificationsScreen(AndroidDriver driver) {
        super(driver);
    }

    public void verifyShown() {
        waitFor(TITLE);
    }

    public List<String> visibleNotifications() {
        waitFor(NOTIFICATION_CARD);
        return descs(NOTIFICATION_CARD);
    }
}

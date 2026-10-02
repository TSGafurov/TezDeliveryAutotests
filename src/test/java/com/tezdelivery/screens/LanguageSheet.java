package com.tezdelivery.screens;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

/**
 * Шторка "Tilni tanlang" (Profil -> "Til", она же при первом запуске). Выбранный язык в
 * дереве доступности не отражается (checked=false у всех радио). Крестик закрывает
 * шторку без смены языка - проверено вживую 2026-10-02.
 */
public class LanguageSheet extends BaseScreen {

    private static final By TITLE = AppiumBy.accessibilityId("Tilni tanlang");
    private static final By CLOSE = By.xpath(
            "//android.view.View[@content-desc='Tilni tanlang']/following-sibling::android.widget.ImageView[1]");
    private static final By UZBEK_LATIN = AppiumBy.accessibilityId("O'zbekcha");
    private static final By UZBEK_CYRILLIC = AppiumBy.accessibilityId("Ўзбекча");
    private static final By RUSSIAN = AppiumBy.accessibilityId("Ruscha");
    private static final By CONFIRM = AppiumBy.accessibilityId("Tasdiqlash");

    public LanguageSheet(AndroidDriver driver) {
        super(driver);
    }

    public void verifyShown() {
        waitFor(TITLE);
    }

    public boolean areAllLanguagesShown() {
        return isPresent(UZBEK_LATIN, WAIT_TIMEOUT)
                && isPresent(UZBEK_CYRILLIC, WAIT_TIMEOUT)
                && isPresent(RUSSIAN, WAIT_TIMEOUT)
                && isPresent(CONFIRM, WAIT_TIMEOUT);
    }

    public void close() {
        waitFor(CLOSE).click();
        waitUntilGone(TITLE);
    }
}

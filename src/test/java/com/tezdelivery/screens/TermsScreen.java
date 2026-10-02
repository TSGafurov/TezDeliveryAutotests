package com.tezdelivery.screens;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

/**
 * "Foydalanish shartlari" (Profil -> "Foydalanish shartlari"): заголовок и один узел с
 * полным текстом условий.
 */
public class TermsScreen extends BaseScreen {

    private static final String TITLE_TEXT = "Foydalanish shartlari";
    private static final By TITLE = AppiumBy.accessibilityId(TITLE_TEXT);
    private static final By BODY = By.xpath("//android.widget.ScrollView/android.view.View[1]");

    public TermsScreen(AndroidDriver driver) {
        super(driver);
    }

    public void verifyShown() {
        waitFor(TITLE);
    }

    public String text() {
        return desc(waitFor(BODY));
    }
}

package com.tezdelivery.screens;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

import java.util.List;

/**
 * "Hamyon" (Profil -> "Mening hamyonim"): баланс и "To‘lovlar tarixi".
 */
public class WalletScreen extends BaseScreen {

    // Шапка смержена в один узел: "Hamyon\nSizning balansingizda\n19 139 000 so'm".
    private static final By HEADER = By.xpath("//android.view.View[starts-with(@content-desc, 'Hamyon')]");
    private static final By HISTORY_TITLE = AppiumBy.accessibilityId("To‘lovlar tarixi");
    // "TEZ00913\nNaqd\nPepsi Market\n+ 12 000\n30.09.2026, 03:33"
    private static final By PAYMENT_ENTRY = By.xpath(
            "//android.widget.ImageView[starts-with(@content-desc, 'TEZ')]");

    public WalletScreen(AndroidDriver driver) {
        super(driver);
    }

    public void verifyShown() {
        waitFor(HEADER);
        waitFor(HISTORY_TITLE);
    }

    public String balanceText() {
        String[] lines = desc(waitFor(HEADER)).split("\n");
        return lines[lines.length - 1];
    }

    public List<String> visiblePayments() {
        waitFor(PAYMENT_ENTRY);
        return descs(PAYMENT_ENTRY);
    }
}

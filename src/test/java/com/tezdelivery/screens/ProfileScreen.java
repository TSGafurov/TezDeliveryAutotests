package com.tezdelivery.screens;

import com.tezdelivery.config.TestConfig;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

/**
 * "Profil": данные курьера, кошелёк, переключатель "Men tarmoqdaman", помощь, язык,
 * условия, версия, выход.
 *
 * "Men tarmoqdaman" управляет назначением РЕАЛЬНЫХ заказов курьеру - здесь только
 * чтение состояния, переключать нельзя. "Chiqish" -> "Tasdiqlash" разлогинивает
 * аккаунт; диалог закрывается только крестиком (отдельный узел), а не "Bekor qilish",
 * которая смержена с "Tasdiqlash" в один узел и тапалась бы по координате.
 */
public class ProfileScreen extends BaseScreen {

    private static final By TITLE = AppiumBy.accessibilityId("Profil");
    private static final By COURIER_ID_LABEL = AppiumBy.accessibilityId("Yetkazib beruvchi ID raqami");
    // В дереве значение ID стоит ПЕРЕД подписью.
    private static final By COURIER_ID = By.xpath(
            "//android.view.View[@content-desc='Yetkazib beruvchi ID raqami']/preceding-sibling::android.view.View[1]");
    private static final By WALLET = AppiumBy.accessibilityId("Mening hamyonim");
    private static final By ONLINE_LABEL = AppiumBy.accessibilityId("Men tarmoqdaman");
    private static final By ONLINE_SWITCH = By.className("android.widget.Switch");
    private static final By HELP = AppiumBy.accessibilityId("Yordam");
    private static final By LANGUAGE = AppiumBy.accessibilityId("Til");
    private static final By TERMS = AppiumBy.accessibilityId("Foydalanish shartlari");
    private static final By VERSION =
            AppiumBy.androidUIAutomator("new UiSelector().descriptionStartsWith(\"Versiya: \")");
    private static final By LOGOUT = AppiumBy.accessibilityId("Chiqish");
    private static final By LOGOUT_DIALOG = By.xpath(
            "//android.view.View[contains(@content-desc, 'profildan chiqishni istaysizmi')]");
    private static final By LOGOUT_DIALOG_CLOSE = By.xpath(
            "//android.view.View[contains(@content-desc, 'profildan chiqishni istaysizmi')]/android.widget.ImageView");

    public ProfileScreen(AndroidDriver driver) {
        super(driver);
    }

    public void verifyShown() {
        waitFor(TITLE);
        waitFor(COURIER_ID_LABEL);
    }

    private By phoneLocator() {
        return AppiumBy.accessibilityId(TestConfig.formattedPhoneNumber());
    }

    public boolean isPhoneShown() {
        return isPresent(phoneLocator(), WAIT_TIMEOUT);
    }

    // Порядок соседей в карточке: имя, телефон, звезда, рейтинг.
    public String courierName() {
        return desc(waitFor(By.xpath("//android.view.View[@content-desc='" + TestConfig.formattedPhoneNumber()
                + "']/preceding-sibling::android.view.View[1]")));
    }

    public String rating() {
        return desc(waitFor(By.xpath("//android.view.View[@content-desc='" + TestConfig.formattedPhoneNumber()
                + "']/following-sibling::android.view.View[1]")));
    }

    public String courierId() {
        return desc(waitFor(COURIER_ID));
    }

    public boolean areMenuItemsShown() {
        return isPresent(WALLET, WAIT_TIMEOUT)
                && isPresent(ONLINE_LABEL, WAIT_TIMEOUT)
                && isPresent(HELP, WAIT_TIMEOUT)
                && isPresent(LANGUAGE, WAIT_TIMEOUT)
                && isPresent(TERMS, WAIT_TIMEOUT)
                && isPresent(LOGOUT, WAIT_TIMEOUT);
    }

    public boolean isOnlineSwitchShown() {
        return isPresent(ONLINE_SWITCH, WAIT_TIMEOUT);
    }

    public String versionText() {
        return desc(waitFor(VERSION));
    }

    public WalletScreen openWallet() {
        waitFor(WALLET).click();
        WalletScreen screen = new WalletScreen(driver);
        screen.verifyShown();
        return screen;
    }

    public HelpScreen openHelp() {
        waitFor(HELP).click();
        HelpScreen screen = new HelpScreen(driver);
        screen.verifyShown();
        return screen;
    }

    public TermsScreen openTerms() {
        waitFor(TERMS).click();
        TermsScreen screen = new TermsScreen(driver);
        screen.verifyShown();
        return screen;
    }

    public LanguageSheet openLanguageSheet() {
        waitFor(LANGUAGE).click();
        LanguageSheet sheet = new LanguageSheet(driver);
        sheet.verifyShown();
        return sheet;
    }

    public void openLogoutDialog() {
        waitFor(LOGOUT).click();
        waitFor(LOGOUT_DIALOG);
    }

    public String logoutDialogText() {
        return desc(waitFor(LOGOUT_DIALOG));
    }

    public void closeLogoutDialog() {
        waitFor(LOGOUT_DIALOG_CLOSE).click();
        waitUntilGone(LOGOUT_DIALOG);
    }
}

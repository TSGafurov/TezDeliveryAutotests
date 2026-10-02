package com.tezdelivery.screens;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Главный экран курьера "Buyurtmalar": поиск по ID, табы "Faol buyurtmalar" /
 * "Buyurtmalar tarixi". Локаторы сняты 2026-10-02 (v1.0.9, узбекский интерфейс) - см.
 * docs/exploration-notes.md.
 */
public class OrdersScreen extends BaseScreen {

    private static final By TITLE = AppiumBy.accessibilityId("Buyurtmalar");
    // Колокольчик и аватар - соседи заголовка без собственных подписей (у колокольчика
    // content-desc - это бейдж непрочитанных, напр. "9+", он меняется).
    private static final By NOTIFICATIONS_BUTTON = By.xpath(
            "//android.widget.ImageView[@content-desc='Buyurtmalar']/following-sibling::android.widget.ImageView[1]");
    private static final By PROFILE_BUTTON = By.xpath(
            "//android.widget.ImageView[@content-desc='Buyurtmalar']/following-sibling::android.widget.ImageView[last()]");
    private static final By SEARCH_FIELD = By.className("android.widget.EditText");
    // content-desc табов содержит ещё и подсказку позиции: "Faol buyurtmalar\n2 varaqdan 1".
    private static final By ACTIVE_TAB =
            AppiumBy.androidUIAutomator("new UiSelector().descriptionStartsWith(\"Faol buyurtmalar\")");
    private static final By HISTORY_TAB =
            AppiumBy.androidUIAutomator("new UiSelector().descriptionStartsWith(\"Buyurtmalar tarixi\")");
    // Карточка: "30 - сентябрь\nTEZ00913\n2 000 so'm\nYetkazildi" (дата - только у первой
    // карточки в группе дня). При поиске совпадение подсвечивается и content-desc
    // разбивается на части ("TE\nZ00871"), поэтому ID из карточек читать до поиска.
    private static final By ORDER_CARD = By.xpath(
            "//android.view.View[@clickable='true' and contains(@content-desc, 'TEZ')]");
    private static final By EMPTY_STATE =
            AppiumBy.androidUIAutomator("new UiSelector().descriptionStartsWith(\"Hech qanday natija topilmadi\")");

    private static final Pattern ORDER_ID = Pattern.compile("TEZ\\d+");

    public OrdersScreen(AndroidDriver driver) {
        super(driver);
    }

    public void verifyShown() {
        waitFor(TITLE);
        waitFor(SEARCH_FIELD);
        waitFor(ACTIVE_TAB);
        waitFor(HISTORY_TAB);
    }

    public void openActiveTab() {
        waitFor(ACTIVE_TAB).click();
    }

    public void openHistoryTab() {
        waitFor(HISTORY_TAB).click();
        waitFor(ORDER_CARD);
    }

    public boolean isEmptyStateShown() {
        return isPresent(EMPTY_STATE, WAIT_TIMEOUT);
    }

    public List<String> visibleOrderCards() {
        waitFor(ORDER_CARD);
        return descs(ORDER_CARD);
    }

    public static String orderIdOf(String cardDesc) {
        Matcher matcher = ORDER_ID.matcher(cardDesc);
        if (!matcher.find()) {
            throw new IllegalStateException("В карточке заказа нет ID TEZxxxxx: " + cardDesc);
        }
        return matcher.group();
    }

    public OrderDetailsScreen openOrder(int index) {
        List<WebElement> cards = driver.findElements(ORDER_CARD);
        String id = orderIdOf(desc(cards.get(index)));
        cards.get(index).click();
        OrderDetailsScreen details = new OrderDetailsScreen(driver);
        details.verifyShown(id);
        return details;
    }

    public void search(String query) {
        WebElement field = waitFor(SEARCH_FIELD);
        field.click();
        field.sendKeys(query);
        sleep(java.time.Duration.ofSeconds(2));
    }

    public NotificationsScreen openNotifications() {
        waitFor(NOTIFICATIONS_BUTTON).click();
        NotificationsScreen screen = new NotificationsScreen(driver);
        screen.verifyShown();
        return screen;
    }

    public ProfileScreen openProfile() {
        waitFor(PROFILE_BUTTON).click();
        ProfileScreen screen = new ProfileScreen(driver);
        screen.verifyShown();
        return screen;
    }
}

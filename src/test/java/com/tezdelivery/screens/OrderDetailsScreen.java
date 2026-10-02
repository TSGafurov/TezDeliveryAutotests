package com.tezdelivery.screens;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

import java.util.List;

/**
 * Детали заказа (открываются из карточки на "Buyurtmalar"): заголовок с ID, табы
 * "Buyurtmalar" (товары) / "Buyurtma haqida" (получатель, адрес, оплата).
 * Телефон получателя кликабелен (вероятно, звонок клиенту) - не тапать.
 */
public class OrderDetailsScreen extends BaseScreen {

    // "Buyurtmalar\n2 varaqdan 1" (у таба есть хвост, у заголовков экранов - нет).
    private static final By PRODUCTS_TAB = By.xpath(
            "//android.view.View[@clickable='true' and starts-with(@content-desc, 'Buyurtmalar')]");
    private static final By INFO_TAB =
            AppiumBy.androidUIAutomator("new UiSelector().descriptionStartsWith(\"Buyurtma haqida\")");
    private static final By PRODUCTS_HEADER = AppiumBy.accessibilityId("Mahsulotlar");
    // "Suv Hydrolife gazsiz 330 ml\n(dona)\n2 000 so'm\n1x"
    private static final By PRODUCT_ITEM = By.xpath(
            "//android.widget.ImageView[contains(@content-desc, \"so'm\")]");

    private static final By RECIPIENT_LABEL = AppiumBy.accessibilityId("Qabul qiluvchi");
    private static final By PAYMENT_LABEL = AppiumBy.accessibilityId("To‘lov");
    private static final By PAYMENT_SUM_LABEL = AppiumBy.accessibilityId("To‘lov summasi");
    // Значения идут соседями сразу после подписи.
    private static final By RECIPIENT_NAME = By.xpath(
            "//android.view.View[@content-desc='Qabul qiluvchi']/following-sibling::android.view.View[1]");
    private static final By RECIPIENT_PHONE = By.xpath(
            "//android.view.View[@content-desc='Qabul qiluvchi']/following-sibling::android.view.View[2]");
    private static final By ADDRESS = By.xpath(
            "//android.view.View[@content-desc='Qabul qiluvchi']/following-sibling::android.view.View[3]");
    private static final By PAYMENT_METHOD = By.xpath(
            "//android.view.View[@content-desc='To‘lov']/following-sibling::android.view.View[1]");
    private static final By PAYMENT_SUM = By.xpath(
            "//android.view.View[@content-desc='To‘lov summasi']/following-sibling::android.view.View[1]");

    public OrderDetailsScreen(AndroidDriver driver) {
        super(driver);
    }

    public void verifyShown(String orderId) {
        waitFor(AppiumBy.accessibilityId(orderId));
        waitFor(PRODUCTS_TAB);
        waitFor(INFO_TAB);
    }

    public List<String> products() {
        waitFor(PRODUCTS_HEADER);
        waitFor(PRODUCT_ITEM);
        return descs(PRODUCT_ITEM);
    }

    public void openInfoTab() {
        waitFor(INFO_TAB).click();
        waitFor(RECIPIENT_LABEL);
        waitFor(PAYMENT_LABEL);
        waitFor(PAYMENT_SUM_LABEL);
    }

    public String recipientName() {
        return desc(waitFor(RECIPIENT_NAME));
    }

    public String recipientPhone() {
        return desc(waitFor(RECIPIENT_PHONE));
    }

    public String address() {
        return desc(waitFor(ADDRESS));
    }

    public String paymentMethod() {
        return desc(waitFor(PAYMENT_METHOD));
    }

    public String paymentSum() {
        return desc(waitFor(PAYMENT_SUM));
    }
}

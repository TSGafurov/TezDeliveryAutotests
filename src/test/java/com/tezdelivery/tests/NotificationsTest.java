package com.tezdelivery.tests;

import com.tezdelivery.screens.NotificationsScreen;
import com.tezdelivery.screens.OrdersScreen;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * "Xabarnomalar" - только просмотр: тап по карточке и "прочитать все" меняют статус
 * прочтения, их здесь нет.
 */
public class NotificationsTest extends BaseTest {

    @Test(groups = "safe", description = "NOTIF-01: колокольчик открывает список уведомлений с датой и временем")
    public void notificationsListShown() {
        OrdersScreen orders = new OrdersScreen(driver);
        orders.verifyShown();

        NotificationsScreen notifications = orders.openNotifications();
        List<String> items = notifications.visibleNotifications();

        assertFalse(items.isEmpty(), "Список уведомлений пуст");
        for (String item : items) {
            assertTrue(item.matches("(?s).+\\n\\d{2}\\.\\d{2}\\.\\d{4}\\n\\d{2}:\\d{2}"),
                    "Неожиданный формат уведомления: " + item);
        }
    }

    @Test(groups = "safe", description = "NOTIF-02: Back из уведомлений возвращает на главный")
    public void backFromNotificationsReturnsToOrders() {
        OrdersScreen orders = new OrdersScreen(driver);
        orders.verifyShown();

        orders.openNotifications().goBack();

        orders.verifyShown();
    }
}

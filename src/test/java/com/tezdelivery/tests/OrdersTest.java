package com.tezdelivery.tests;

import com.tezdelivery.screens.OrderDetailsScreen;
import com.tezdelivery.screens.OrdersScreen;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Главный экран "Buyurtmalar" и детали заказа из истории - только чтение. Проверки не
 * завязаны на конкретные заказы (история растёт), только на формат и связность данных.
 */
public class OrdersTest extends BaseTest {

    private static final String SUM_PATTERN = "\\d{1,3}( \\d{3})* so'm";

    @Test(groups = "safe", description = "ORD-01: на главном есть поиск и табы активных заказов/истории")
    public void ordersScreenLayout() {
        new OrdersScreen(driver).verifyShown();
    }

    @Test(groups = "safe", description = "ORD-02: таб активных заказов показывает карточки или пустое состояние")
    public void activeTabShowsOrdersOrEmptyState() {
        OrdersScreen orders = new OrdersScreen(driver);
        orders.verifyShown();
        orders.openActiveTab();

        assertTrue(orders.isEmptyStateShown() || !orders.visibleOrderCards().isEmpty(),
                "На табе активных заказов нет ни карточек, ни пустого состояния");
    }

    @Test(groups = "safe", description = "ORD-03: история - карточки с ID, суммой и статусом")
    public void historyShowsOrderCards() {
        OrdersScreen orders = new OrdersScreen(driver);
        orders.verifyShown();
        orders.openHistoryTab();

        List<String> cards = orders.visibleOrderCards();
        assertFalse(cards.isEmpty(), "История заказов пуста");
        for (String card : cards) {
            assertTrue(card.matches("(?s).*TEZ\\d+\\n" + SUM_PATTERN + "\\n.+"),
                    "Неожиданный формат карточки: " + card);
        }
    }

    @Test(groups = "safe", description = "ORD-04: детали заказа - ID из карточки и список товаров с ценой")
    public void orderDetailsShowProducts() {
        OrdersScreen orders = new OrdersScreen(driver);
        orders.verifyShown();
        orders.openHistoryTab();

        OrderDetailsScreen details = orders.openOrder(0);
        List<String> products = details.products();
        assertFalse(products.isEmpty(), "В заказе нет товаров");
        for (String product : products) {
            assertTrue(product.matches("(?s).+\\n" + SUM_PATTERN + "\\n\\d+x"),
                    "Неожиданный формат товара: " + product);
        }
    }

    @Test(groups = "safe", description = "ORD-05: детали заказа - получатель, адрес, оплата; сумма совпадает с карточкой")
    public void orderDetailsShowRecipientAndPayment() {
        OrdersScreen orders = new OrdersScreen(driver);
        orders.verifyShown();
        orders.openHistoryTab();
        String card = orders.visibleOrderCards().get(0);

        OrderDetailsScreen details = orders.openOrder(0);
        details.openInfoTab();

        assertFalse(details.recipientName().isBlank(), "Пустое имя получателя");
        assertTrue(details.recipientPhone().matches("\\+998 \\d{2} \\d{3} \\d{2} \\d{2}"),
                "Неожиданный формат телефона: " + details.recipientPhone());
        assertFalse(details.address().isBlank(), "Пустой адрес");
        assertFalse(details.paymentMethod().isBlank(), "Пустой способ оплаты");
        String sum = details.paymentSum();
        assertTrue(sum.matches(SUM_PATTERN), "Неожиданный формат суммы: " + sum);
        assertTrue(card.contains("\n" + sum + "\n"),
                "Сумма в деталях (" + sum + ") не совпадает с карточкой: " + card);
    }

    @Test(groups = "safe", description = "ORD-06: Back из деталей возвращает в историю")
    public void backFromDetailsReturnsToHistory() {
        OrdersScreen orders = new OrdersScreen(driver);
        orders.verifyShown();
        orders.openHistoryTab();
        String firstId = OrdersScreen.orderIdOf(orders.visibleOrderCards().get(0));

        orders.openOrder(0).goBack();

        orders.verifyShown();
        assertEquals(OrdersScreen.orderIdOf(orders.visibleOrderCards().get(0)), firstId);
    }

    @Test(groups = "safe", description = "ORD-07: поиск по номеру заказа из истории показывает этот заказ")
    public void searchByOrderNumberShowsOrder() {
        OrdersScreen orders = new OrdersScreen(driver);
        orders.verifyShown();
        orders.openHistoryTab();
        List<String> cards = orders.visibleOrderCards();
        String digits = OrdersScreen.orderIdOf(cards.get(cards.size() > 1 ? 1 : 0)).substring(3);

        orders.search(digits);

        // Совпадение подсвечивается, и content-desc рвётся на части ("TEZ\n00871") -
        // поэтому ищем по цифрам номера, а не по полному ID.
        assertTrue(orders.visibleOrderCards().stream().anyMatch(c -> c.contains(digits)),
                "Заказ с номером " + digits + " не найден поиском");
    }
}

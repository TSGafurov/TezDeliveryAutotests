package com.tezdelivery.tests;

import com.tezdelivery.screens.OrdersScreen;
import com.tezdelivery.screens.WalletScreen;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Известные баги приложения (см. "Наблюдения" в docs/exploration-notes.md). Тесты
 * read-only, но описывают ОЖИДАЕМОЕ поведение - пока баг не исправлен, они падают.
 * Поэтому вынесены в отдельный testng-known-bugs.xml и не входят в safe/полный регресс.
 */
public class KnownBugsTest extends BaseTest {

    private static final DateTimeFormatter PAYMENT_TIME = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

    @Test(groups = "known-bug", description = "BUG-01: поиск по ID в истории фильтрует список, а не только подсвечивает")
    public void searchFiltersHistory() {
        OrdersScreen orders = new OrdersScreen(driver);
        orders.verifyShown();
        orders.openHistoryTab();
        List<String> cards = orders.visibleOrderCards();
        String otherId = OrdersScreen.orderIdOf(cards.get(0));
        String digits = OrdersScreen.orderIdOf(cards.get(1)).substring(3);

        orders.search(digits);

        String otherDigits = otherId.substring(3);
        assertFalse(orders.visibleOrderCards().stream().anyMatch(c -> c.contains(otherDigits)),
                "Поиск по " + digits + " оставил в выдаче несовпадающий заказ " + otherId);
    }

    @Test(groups = "known-bug", description = "BUG-02: дата-разделитель истории на языке интерфейса (узбекский), а не по-русски")
    public void historyDateHeaderLocalized() {
        OrdersScreen orders = new OrdersScreen(driver);
        orders.verifyShown();
        orders.openHistoryTab();

        String dateHeader = orders.visibleOrderCards().get(0).split("\n")[0];
        assertFalse(dateHeader.matches(".*[А-Яа-яЁё].*"),
                "Дата в узбекском интерфейсе на кириллице: " + dateHeader);
    }

    @Test(groups = "known-bug", description = "BUG-03: история выплат в кошельке отсортирована от новых к старым")
    public void walletPaymentsSortedByTime() {
        OrdersScreen orders = new OrdersScreen(driver);
        orders.verifyShown();
        WalletScreen wallet = orders.openProfile().openWallet();

        List<LocalDateTime> times = wallet.visiblePayments().stream()
                .map(p -> p.substring(p.lastIndexOf('\n') + 1))
                .map(t -> LocalDateTime.parse(t, PAYMENT_TIME))
                .toList();
        for (int i = 1; i < times.size(); i++) {
            assertTrue(!times.get(i).isAfter(times.get(i - 1)),
                    "Выплата " + times.get(i) + " стоит ниже более старой " + times.get(i - 1));
        }
    }
}

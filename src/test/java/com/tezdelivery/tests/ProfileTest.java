package com.tezdelivery.tests;

import com.tezdelivery.screens.HelpScreen;
import com.tezdelivery.screens.LanguageSheet;
import com.tezdelivery.screens.OrdersScreen;
import com.tezdelivery.screens.ProfileScreen;
import com.tezdelivery.screens.TermsScreen;
import com.tezdelivery.screens.WalletScreen;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Профиль курьера и его разделы - только чтение. Переключатель "Men tarmoqdaman" не
 * трогаем (назначение реальных заказов), выход и смену языка только открываем и
 * закрываем крестиком.
 */
public class ProfileTest extends BaseTest {

    private ProfileScreen openProfile() {
        OrdersScreen orders = new OrdersScreen(driver);
        orders.verifyShown();
        return orders.openProfile();
    }

    @Test(groups = "safe", description = "PROF-01: данные курьера - имя, телефон аккаунта, рейтинг, ID")
    public void courierInfoShown() {
        ProfileScreen profile = openProfile();

        assertTrue(profile.isPhoneShown(), "В профиле нет телефона аккаунта");
        assertFalse(profile.courierName().isBlank(), "Пустое имя курьера");
        assertTrue(profile.rating().matches("[0-5](\\.\\d)?"), "Неожиданный рейтинг: " + profile.rating());
        assertFalse(profile.courierId().isBlank(), "Пустой ID курьера");
    }

    @Test(groups = "safe", description = "PROF-02: все пункты меню и переключатель онлайн-статуса на месте")
    public void menuItemsShown() {
        ProfileScreen profile = openProfile();

        assertTrue(profile.areMenuItemsShown(), "Не все пункты меню профиля показаны");
        assertTrue(profile.isOnlineSwitchShown(), "Нет переключателя 'Men tarmoqdaman'");
    }

    @Test(groups = "safe", description = "PROF-03: версия в профиле совпадает с установленной")
    public void versionMatchesInstalledApp() throws Exception {
        ProfileScreen profile = openProfile();

        assertEquals(profile.versionText(), "Versiya: " + installedVersionName());
    }

    @Test(groups = "safe", description = "PROF-04: кошелёк - баланс в сумах и история выплат по заказам")
    public void walletShowsBalanceAndPayments() {
        WalletScreen wallet = openProfile().openWallet();

        String balance = wallet.balanceText();
        assertTrue(balance.matches("\\d{1,3}( \\d{3})* so'm"), "Неожиданный формат баланса: " + balance);

        List<String> payments = wallet.visiblePayments();
        assertFalse(payments.isEmpty(), "История выплат пуста");
        for (String payment : payments) {
            // "TEZ00913\nNaqd\nPepsi Market\n+ 12 000\n30.09.2026, 03:33"
            assertTrue(payment.matches("TEZ\\d+\\n.+\\n.+\\n[+-] \\d{1,3}( \\d{3})*\\n\\d{2}\\.\\d{2}\\.\\d{4}, \\d{2}:\\d{2}"),
                    "Неожиданный формат выплаты: " + payment);
        }
    }

    @Test(groups = "safe", description = "PROF-05: помощь - колл-центр, почта, Telegram")
    public void helpShowsContactOptions() {
        HelpScreen help = openProfile().openHelp();

        assertTrue(help.areContactOptionsShown(), "Не все способы связи показаны");
    }

    @Test(groups = "safe", description = "PROF-06: условия использования открываются и не пустые")
    public void termsShown() {
        TermsScreen terms = openProfile().openTerms();

        assertTrue(terms.text().length() > 100, "Текст условий пустой или обрезан");
    }

    @Test(groups = "safe", description = "PROF-07: шторка языка - три языка, закрытие крестиком без смены")
    public void languageSheetOpensAndCloses() {
        ProfileScreen profile = openProfile();
        LanguageSheet sheet = profile.openLanguageSheet();

        assertTrue(sheet.areAllLanguagesShown(), "Не все языки показаны в шторке");
        sheet.close();

        profile.verifyShown();
    }

    @Test(groups = "safe", description = "PROF-08: диалог выхода с подтверждением, закрытие крестиком не разлогинивает")
    public void logoutDialogCanBeDismissed() {
        ProfileScreen profile = openProfile();

        profile.openLogoutDialog();
        String dialog = profile.logoutDialogText();
        assertTrue(dialog.contains("Bekor qilish") && dialog.contains("Tasdiqlash"),
                "В диалоге выхода нет кнопок отмены/подтверждения: " + dialog);
        profile.closeLogoutDialog();

        profile.verifyShown();
        assertTrue(profile.isPhoneShown(), "После закрытия диалога курьер разлогинен");
    }

    @Test(groups = "safe", description = "PROF-09: Back с разделов профиля возвращает в профиль, а из профиля - на главный")
    public void backNavigationFromProfileSections() {
        ProfileScreen profile = openProfile();

        profile.openWallet().goBack();
        profile.verifyShown();
        profile.openHelp().goBack();
        profile.verifyShown();
        profile.openTerms().goBack();
        profile.verifyShown();

        profile.goBack();
        new OrdersScreen(driver).verifyShown();
    }
}

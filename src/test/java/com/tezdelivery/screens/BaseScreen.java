package com.tezdelivery.screens;

import com.tezdelivery.config.TestConfig;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

/**
 * Common gesture/wait helpers shared by every screen object. Flutter app: locators are
 * content-desc (accessibility id), there are no resource-ids. Some CTA buttons (e.g.
 * "Davom ettirish" on the login screen) are merged into a full-screen accessibility node
 * and have to be tapped by coordinate - see docs/exploration-notes.md.
 */
public abstract class BaseScreen {

    protected static final Duration WAIT_TIMEOUT = Duration.ofSeconds(15);

    // Экран, на котором сняты координаты в screen-классах. Реальные тапы масштабируются
    // под фактический размер экрана устройства через scaledX()/scaledY().
    private static final int REFERENCE_SCREEN_WIDTH = TestConfig.referenceScreenWidth();
    private static final int REFERENCE_SCREEN_HEIGHT = TestConfig.referenceScreenHeight();

    protected final AndroidDriver driver;

    protected BaseScreen(AndroidDriver driver) {
        this.driver = driver;
    }

    protected WebElement waitFor(By locator) {
        return new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(d -> {
                    List<WebElement> found = d.findElements(locator);
                    return !found.isEmpty() && found.get(0).isDisplayed() ? found.get(0) : null;
                });
    }

    // Системный Back - на всех подэкранах (детали заказа, уведомления, профиль и его
    // разделы) возвращает на предыдущий экран. НЕ вызывать на главном "Buyurtmalar" -
    // там Back закрывает приложение.
    public void goBack() {
        driver.navigate().back();
    }

    // Flutter отдаёт подписи только через content-desc, text у узлов пустой.
    protected static String desc(WebElement element) {
        return element.getAttribute("content-desc");
    }

    protected List<String> descs(By locator) {
        return driver.findElements(locator).stream().map(BaseScreen::desc).toList();
    }

    protected boolean isPresent(By locator, Duration timeout) {
        try {
            new WebDriverWait(driver, timeout).until(d -> !d.findElements(locator).isEmpty());
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    // Закрытие диалога/шторки не убирает узел из дерева доступности мгновенно - есть
    // анимация закрытия. Ждём исчезновения вместо мгновенной проверки isEmpty().
    protected void waitUntilGone(By locator) {
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> d.findElements(locator).isEmpty());
    }

    protected boolean clickIfPresent(By locator, Duration timeout) {
        try {
            WebElement element = new WebDriverWait(driver, timeout)
                    .until(d -> {
                        List<WebElement> elements = d.findElements(locator);
                        return elements.isEmpty() ? null : elements.get(0);
                    });
            element.click();
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected void tapAt(int x, int y) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 0)
                .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(new Pause(finger, Duration.ofMillis(100)))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(List.of(tap));
    }

    protected void swipeUpOnScreen() {
        swipeVertically(0.8, 0.2);
    }

    protected void swipeDownOnScreen() {
        swipeVertically(0.2, 0.8);
    }

    private void swipeVertically(double fromFraction, double toFraction) {
        var size = driver.manage().window().getSize();
        int x = size.width / 2;
        int startY = (int) (size.height * fromFraction);
        int endY = (int) (size.height * toFraction);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 0)
                .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, startY))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(new Pause(finger, Duration.ofMillis(100)))
                .addAction(finger.createPointerMove(Duration.ofMillis(300), PointerInput.Origin.viewport(), x, endY))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(List.of(swipe));
    }

    protected void typeViaAdb(String text) {
        try {
            new ProcessBuilder("adb", "-s", TestConfig.deviceUdid(), "shell", "input", "text", text).start().waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Не удалось ввести текст через adb: " + text, e);
        }
    }

    protected void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected int scaledX(int referenceX) {
        int actualWidth = driver.manage().window().getSize().width;
        return (int) Math.round(referenceX * ((double) actualWidth / REFERENCE_SCREEN_WIDTH));
    }

    protected int scaledY(int referenceY) {
        int actualHeight = driver.manage().window().getSize().height;
        return (int) Math.round(referenceY * ((double) actualHeight / REFERENCE_SCREEN_HEIGHT));
    }
}

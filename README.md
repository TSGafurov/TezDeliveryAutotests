# TezDelivery Autotests

Appium + TestNG UI-автотесты для курьерского Android-приложения "Tez Delivery"
(`uz.agrobank.chakana_delivery`), Page Object Model (`screens/` + `BaseScreen` + `TestConfig`) -
та же схема, что и в TezChakanaAutotests.

## ⚠️ Реальный аккаунт на живом бэкенде

Сьют работает против **реального аккаунта курьера на продовом бэкенде** (тот же номер, что и
в TezChakana). Переключатель "Men tarmoqdaman" влияет на назначение реальных заказов, а
действия с активным заказом меняют его статус у реального клиента - такие тесты не
добавлять в сьюты без согласования с владельцем. Карта экранов и найденные баги -
в [`docs/exploration-notes.md`](docs/exploration-notes.md).

## Требования

- Java 21, Maven
- Запущенный Android-эмулятор или устройство, видимое в `adb devices`
- Локальный Appium-сервер (по умолчанию `http://127.0.0.1:4723`, см. `appium.url`)
- Установленное приложение `uz.agrobank.chakana_delivery`, курьер залогинен (тесты
  используют `noReset`, логин не повторяют)

## Настройка

`src/test/resources/config.properties` **не хранится в git** (реальный номер телефона):

```bash
cp src/test/resources/config.properties.example src/test/resources/config.properties
# заполнить phone.number
```

Любой ключ можно переопределить флагом `-D<key>=...` (см. `TestConfig.get()`).

## Запуск

```bash
mvn test                                                   # полный регресс (safe + mutating)
mvn test -DsuiteXmlFile=src/test/resources/testng-safe.xml # только read-only
mvn test -DsuiteXmlFile=src/test/resources/testng-known-bugs.xml # известные баги (падают до исправления)
```

## Уровни риска тестов

| Группа | Значение | Где смотреть |
|---|---|---|
| `safe` | Read-only, не меняет аккаунт и заказы | `testng-safe.xml` |
| `mutating` | Меняет данные обратимо (язык, прочтение уведомлений) | `testng.xml` |
| `destructive` | Меняет статус реальных заказов / онлайн-статус курьера | не подключены ни в один сьют |

## При падении теста

- `ScreenshotOnFailureListener` сохраняет скриншот в `target/screenshots/`.
- `RetryAnalyzerTransformer`/`RetryOnce` дают один автоматический повтор.
- Полный лог прогона - `target/logs/tests.log`.

## Структура

- `src/test/java/com/tezdelivery/screens/` - Page Object Model (по одному классу на экран)
- `src/test/java/com/tezdelivery/tests/` - тестовые классы (`BaseTest` - общий setup/teardown)
- `src/test/java/com/tezdelivery/config/TestConfig.java` - доступ к `config.properties`
- `src/test/resources/testng.xml` / `testng-safe.xml` - сьюты

package org.test.restaurant_service.telegram.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.response.ProductResponseDTO;
import org.test.restaurant_service.dto.response.ProductTypeTranslResponseDTO;
import org.test.restaurant_service.entity.*;
import org.test.restaurant_service.service.OrderService;
import org.test.restaurant_service.service.ProductService;
import org.test.restaurant_service.service.ProductTypeTranslationService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

@Service
public
class TextUtil {

    private final OrderService orderService;
    private final ProductService productService;

    @Getter
    private final String activationText = """
            Чтобы подтвердить ваше действие, выберите код активации:
            Он отображается на экране в правом верхнем углу, непосредственно над зоной заказа и оплаты.
            
            Это нужно, чтобы мы точно знали, что вы на месте и ваш заказ действительно активен 😊
            """;


    public String getBlacklistText(UUID userUUID) {
        String websiteLink = String.format("[parktown.md](http://195.133.93.67/#menu/%s)", userUUID);

        return """
                🚫 Вы временно заблокированы
                
                ❌ Вы дважды ввели неправильный код активации.
                ⏳ Блокировка действует 1 час.
                
                🔹 Вы можете:
                🌐 Перейти на сайт и оформить заказ: %s
                🧾 Подойти к точке заказа и оплаты и сделать заказ на месте
                
                📋 Доступные команды:
                🍽️ /menu — Меню ресторана
                🌐 /website — Перейти на сайт
                ❓ /help — Список команд
                ℹ️ /info — О боте
                """.formatted(websiteLink);
    }


    private final String helpTextRu =
            """
                    📖 <b>Доступные команды:</b>
                    
                    🍽️ /menu - <i>Показать меню</i>
                    🥂 /website - <i>Зайти на сайт</i>
                    🆔 /id - <i>Посмотреть айди</i>
                    ❓ /help - <i>Список доступных команд</i>
                    ℹ️ /info - <i>Информация о боте</i>
                    📁 /about - <i>Показать информацию профиля</i>
                    ✨ Используйте команды, чтобы сделать ваше настроение более радостным!
                    """;

    private final String infoTextRu =
            """
                    🤖 <b>Добро пожаловать!</b>
                    
                    Этот бот создан, чтобы сделать вашу жизнь проще и приятнее! 🌟
                    
                    С его помощью вы можете:
                    📝 Заказать еду на нашем сайте
                    📢 Получать самые свежие новости о мероприятиях PARK TOWN
                    
                    ✨ Мы всегда рады быть вам полезными!
                    """;

    public TextUtil(OrderService orderService, @Qualifier("productServiceWithS3Impl") ProductService productService) {
        this.orderService = orderService;
        this.productService = productService;
    }


    public String getInfoText(String language) {
        return infoTextRu;
    }


    public String getHelpText(String language) {
        return helpTextRu;
    }

    public StringBuilder getProductText(ProductResponseDTO productResponse) {
        StringBuilder productText = new StringBuilder();
        LocalTime cookingTime = productResponse.getCookingTime();
        productText.append("🍴 <b>Блюдо:</b> ").append(productResponse.getName()).append("\n");
        productText.append("✨ <i>Описание:</i> ").append(productResponse.getDescription()).append("\n");
        productText.append("📂 <i>Категория:</i> ").append(productResponse.getTypeName()).append("\n");
        productText.append("💰 <b>Стоимость:</b> ").append(productResponse.getPrice()).append(" лей\n");
        if (cookingTime != null) {
            productText.append("⏱️ <b>Время приготовления:</b> ")
                    .append(cookingTime.getMinute()).append(" минут\n");
        }
        productText.append("\n🍽️ Наслаждайтесь изысканным вкусом и уютной атмосферой! ❤️");


        return productText;
    }

    public String getProductTypeTextByType(String productType) {
        return "🍽️ Вы выбрали категорию <b>" + productType + "</b>!\n\n" +
                "Нажмите на блюдо, чтобы увидеть описание.\n";

    }


    public void addAllProductsToMenu(StringBuilder menuText) {
        menuText.append("🍽️ <i><b>Добро пожаловать в наше уютное меню!</b></i> \n\n")
                .append("✨ Здесь вы найдёте изысканные блюда, которые подарят вам наслаждение и радость! ✨\n\n");

        menuText.append("\n💌 Спасибо, что выбираете нас! Ваш вкус — наша забота! 💌\n")
                .append("🎉 <i>Введите номер категории или выберите ниже!</i> 🎉\n");
    }

    public String getErrorText(UUID userUUID, String language) {
        return String.format("🌐Заходите на наш сайт [parktown.md](http://195.133.93.67/#menu/%s).\n" +
                "🎁Участвуйте в розыгрышах, получайте промокоды и смотрите за новостями!", userUUID);
    }

    public String getMessageAfterRegister(UUID userUUID, String language) {
        return String.format("""
                🌐Заходите на наш сайт [parktown.md](http://195.133.93.67/#menu/%s).
                🎁Участвуйте в розыгрышах и смотрите за новостями!
                """, userUUID);
    }

    public String getWebSiteText(UUID userUUID, String language) {
        return String.format("🌟 Привет! Добро пожаловать на наш сайт: [parktown.md](http://195.133.93.67/#menu/%s). Мы рады, что вы с нами! 😊", userUUID);
    }

    public String getDefaultMessage(UUID userUUID, String language) {
        return String.format("""
                Неизвестная команда 🤯. Введите /help, чтобы увидеть доступные команды.
                
                Можете посмотреть наше меню /menu ☺ или сделать заказ у нас на сайте [parktown.md](http://195.133.93.67/#menu/%s).
                """, userUUID);
    }

    public String getUserInfo(User user) {
        TelegramUserEntity telegramUserEntity = user.getTelegramUserEntity();
        String language = telegramUserEntity.getLanguage().getCode();
        StringBuilder userInfoText = new StringBuilder();


        userInfoText.append("<i><b>Имя</b></i>: ").append(telegramUserEntity.getFirstname()).append("\n");
        userInfoText.append("<i><b>Никнейм</b></i>: ").append(telegramUserEntity.getUsername()).append("\n");
        userInfoText.append("<i><b>Сделано заказов</b></i>: ").append(orderService.getCountOrdersByUserChatId(telegramUserEntity.getChatId())).append("\n");


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime createdAt = telegramUserEntity.getCreatedAt();


        userInfoText.append("<i><b>Дата регистрации в боте</b></i>: ")
                .append(createdAt.format(formatter))
                .append("\n");
        userInfoText.append("<i><b>Номер чата</b></i>: ").append(telegramUserEntity.getChatId()).append("\n");


        return userInfoText.toString();
    }


    public String getTextForSendingOtpCode(String otp, String language) {
        return "Попытка входа в ваш аккаунт❗\n" +
                "Если вы не совершали попытки входа, проигнорируйте это сообщение.\n\n" +
                "Проверочный код для входа в аккаунт: " + otp + "\n";
    }

    public String getTextForGlobalDiscount(Discount discount, UUID userUUID, String language) {
        BigDecimal discountPercentage = discount.getDiscount();
        String code = discount.getCode();
        String description = discount.getDescription();

        ZoneId zoneId = ZoneId.of("Europe/Chisinau");
        ZonedDateTime validFromZoned = discount.getValidFrom().atZone(zoneId);
        ZonedDateTime validToZoned = discount.getValidTo().atZone(zoneId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM, HH:mm", new Locale(language.equals("ro") ? "ro" : "ru"));
        String formattedValidFrom = validFromZoned.format(formatter);
        String formattedValidTo = validToZoned.format(formatter);

        String userLink = String.format("[parktown.md](http://195.133.93.67/#menu/%s)", userUUID);


        return new StringBuilder()
                .append("🔥 Внимание! Специальное предложение! 🔥\n\n")
                .append("🎉 Скидка ").append(discountPercentage).append("% на все заказы!\n\n")
                .append("📅 Акция действует с ").append(formattedValidFrom).append(" до ").append(formattedValidTo).append("\n\n")
                .append("🎟 Используйте промокод: `").append(code).append("`\n\n")
                .append(description).append("\n\n")
                .append("⚡ Не упустите шанс сэкономить! Заходите на наш сайт и заказывайте прямо сейчас: ").append(userLink)
                .toString();
    }


    public String getTextForProductDiscount(ProductDiscount productDiscount, UUID userUUID, String language) {
        BigDecimal discountPercentage = productDiscount.getDiscount();
        String code = productDiscount.getCode();
        String description = productDiscount.getDescription();

        ZoneId zoneId = ZoneId.of("Europe/Chisinau");
        ZonedDateTime validFromZoned = productDiscount.getValidFrom().atZone(zoneId);
        ZonedDateTime validToZoned = productDiscount.getValidTo().atZone(zoneId);

        Locale locale = language.equals("ro") ? new Locale("ro") : new Locale("ru");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM, HH:mm", locale);
        String formattedValidFrom = validFromZoned.format(formatter);
        String formattedValidTo = validToZoned.format(formatter);

        String userLink = String.format("[parktown.md](http://195.133.93.67/#menu/%s)", userUUID);

        Product product = productDiscount.getProduct();
        String name = product.getName();
        BigDecimal priceWithDiscount = product.getPrice()
                .multiply(BigDecimal.ONE.subtract(discountPercentage.divide(BigDecimal.valueOf(100))))
                .setScale(2, RoundingMode.HALF_UP);


        return new StringBuilder()
                .append("🔥 Внимание! Специальное предложение! 🔥\n\n")
                .append("🎉 Скидка ").append(discountPercentage).append("% на блюдо '").append(product.getName()).append("'\n\n")
                .append("💰 Вместо ").append(product.getPrice()).append(" леев всего ").append(priceWithDiscount).append(" леев\n")
                .append("📅 Акция действует с ").append(formattedValidFrom).append(" до ").append(formattedValidTo).append("\n\n")
                .append("🎟 Используйте промокод: `").append(code).append("`\n\n")
                .append(description).append("\n\n")
                .append("⚡ Не упустите шанс сэкономить! Заходите на наш сайт и заказывайте прямо сейчас: ").append(userLink)
                .toString();

    }


    public String getTopWeekProducts(UUID userUUID, String language) {
        String userLink = String.format("http://195.133.93.67/#menu/%s", userUUID);
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductResponseDTO> top10 = productService.getTop10WeekProducts(pageable);

        StringBuilder sb = new StringBuilder();
        // Заголовок
        sb.append("*🔥 Топ-").append(top10.size()).append(" блюд недели!* 🔥\n")
                .append("────────────────────\n\n");

        // Список
        int idx = 1;
        for (ProductResponseDTO p : top10) {
            sb.append(String.format("*%d.* **%s**\n", idx, escapeMarkdown(p.getName())))
                    .append(String.format("💰 _%s lei_\n", p.getPrice()))
                    .append(String.format("> %s\n", getHotSlogan(language)))
                    .append("\n────────────────────\n\n");
            idx++;
        }

        // Подвал
        sb.append("💥 *Спешите попробовать!*\n")
                .append(String.format("👉 [Заказать сейчас](%s)", userLink));

        return sb.toString();
    }


    private String escapeMarkdown(String text) {
        return text
                .replace("_", "\\_")
                .replace("*", "\\*")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("(", "\\(")
                .replace(")", "\\)");
    }

    private String getHotSlogan(String language) {
        List<String> slogansRu = List.of(
                "Попробуй и влюбись! 💕",
                "Идеально для гурманов! 🍷",
                "Вкус, который покоряет! 🌟",
                "Не отказывай себе в удовольствии! 😋",
                "Это хит! 🔥"
        );

        return slogansRu.get(new Random().nextInt(slogansRu.size()));
    }

    public String getTextForConnection(String name, String email, String event, String phoneNumber, String
            message, String language) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("📞 <i>Новая заявка!</i>\n\n");
        stringBuilder.append("👤 <b>Имя:</b> ").append(name).append("\n");
        stringBuilder.append("✉️ <b>Email:</b> ").append(email).append("\n");
        stringBuilder.append("🎉 <b>Событие:</b> ").append(event).append("\n");
        stringBuilder.append("📞 <b>Телефон:</b> ").append(phoneNumber).append("\n");
        stringBuilder.append("📝 <b>Сообщение:</b> ").append(message).append("\n\n");
        stringBuilder.append("⚡ <i>Пожалуйста, свяжитесь с пользователем как можно скорее!</i>");


        return stringBuilder.toString();
    }


}

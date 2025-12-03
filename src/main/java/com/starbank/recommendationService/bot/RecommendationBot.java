package com.starbank.recommendationService.bot;

import com.starbank.recommendationService.model.RecommendationDto;
import com.starbank.recommendationService.service.RecommendationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.UUID;

@Component
public class RecommendationBot extends TelegramLongPollingBot {

    private final String botToken;
    private final String botName;
    private final RecommendationService recommendationService;
    private final JdbcTemplate jdbcTemplate;

    public RecommendationBot(@Value("${telegram.bot.token}") String botToken,
                             @Value("${telegram.bot.name:StarbankRecommendationBot}") String botName,
                             RecommendationService recommendationService,
                             JdbcTemplate jdbcTemplate) {
        super(botToken);
        this.botToken = botToken;
        this.botName = botName;
        this.recommendationService = recommendationService;
        this.jdbcTemplate = jdbcTemplate;

        System.out.println("Telegram Bot initialized: " + botName);
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String chatId = message.getChatId().toString();
            String text = message.getText();

            if (text.equals("/start")) {
                sendHelpMessage(chatId);
            } else if (text.startsWith("/recommend ")) {
                handleRecommendCommand(chatId, text);
            } else {
                sendHelpMessage(chatId);
            }
        }
    }

    private void handleRecommendCommand(String chatId, String text) {
        try {
            String username = text.substring("/recommend ".length()).trim();

            if (username.isEmpty()) {
                sendMessage(chatId, "Пожалуйста, укажите имя пользователя после команды /recommend");
                return;
            }

            UUID userId = findUserIdByUsername(username);

            if (userId == null) {
                sendMessage(chatId, "❌ Пользователь не найден");
                return;
            }

            var response = recommendationService.getRecommendations(userId);
            List<RecommendationDto> recommendations = response.recommendations();

            String userFullName = getUserFullName(userId);
            String recommendationsText = formatRecommendations(recommendations);

            String messageText = String.format("""
                👋 Здравствуйте, %s!
                
                🎯 Новые продукты для вас:
                %s
                """, userFullName, recommendationsText);

            sendMessage(chatId, messageText);

        } catch (Exception e) {
            e.printStackTrace();
            sendMessage(chatId, "❌ Произошла ошибка при обработке запроса");
        }
    }

    private UUID findUserIdByUsername(String username) {
        try {
            String sql = "SELECT id FROM users WHERE CONCAT(first_name, ' ', last_name) = ?";
            List<UUID> results = jdbcTemplate.queryForList(sql, UUID.class, username);

            if (!results.isEmpty()) {
                return results.get(0);
            }

            sql = "SELECT id FROM users WHERE first_name = ?";
            results = jdbcTemplate.queryForList(sql, UUID.class, username);

            return results.isEmpty() ? null : results.get(0);

        } catch (Exception e) {
            return null;
        }
    }

    private String getUserFullName(UUID userId) {
        String sql = "SELECT CONCAT(first_name, ' ', last_name) FROM users WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, userId);
        } catch (Exception e) {
            return "Пользователь";
        }
    }

    private String formatRecommendations(List<RecommendationDto> recommendations) {
        if (recommendations.isEmpty()) {
            return "📭 На данный момент для вас нет рекомендаций.\n\n" +
                    "Мы сообщим вам, когда появятся новые предложения!";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < recommendations.size(); i++) {
            RecommendationDto rec = recommendations.get(i);
            sb.append("🔹 ").append(rec.name()).append("\n");
            sb.append("   ").append(rec.text()).append("\n\n");
        }
        return sb.toString();
    }

    private void sendHelpMessage(String chatId) {
        String helpText = """
            🤖 *Бот рекомендаций банка "Стар"*
            
            Я помогу вам получить персонализированные рекомендации по банковским продуктам.
            
            *Доступные команды:*
            /start - показать это сообщение
            /recommend [Имя Фамилия] - получить рекомендации для пользователя
            
            *Пример использования:*
            `/recommend Иван Иванов`
            """;
        sendMessage(chatId, helpText);
    }

    private void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.enableMarkdown(true);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки сообщения в Telegram: " + e.getMessage());
        }
    }

    // Геттер для токена (может понадобиться)
    public String getBotToken() {
        return botToken;
    }
}
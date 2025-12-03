package com.starbank.recommendationService.config;



import com.starbank.recommendationService.bot.RecommendationBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramBotConfig {

    @Bean
    public TelegramBotsApi telegramBotsApi(RecommendationBot recommendationBot) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        if (recommendationBot != null) {
            botsApi.registerBot(recommendationBot);
            System.out.println("✅ Telegram bot registered successfully: " + recommendationBot.getBotUsername());
        } else {
            System.err.println("❌ RecommendationBot is null - check configuration");
        }
        return botsApi;
    }
}
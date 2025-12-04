# 🚀 Развертывание системы

## 📋 Предварительные требования

### Системные требования
- Java 17 или выше
- Maven 3.6+
- PostgreSQL 12+
- Доступ к Telegram Bot API

### Переменные окружения
```bash
# База данных
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/recommendation_db
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=your_password

# Telegram Bot
export TELEGRAM_BOT_TOKEN=8372520281:AAFop84jXsS6UqStVGZ-5UaMREeqDsXgSbA
export TELEGRAM_BOT_NAME=MyStarbankRecommendationBot

# H2 Database (read-only)

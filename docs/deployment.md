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
export TELEGRAM_BOT_8372520281:AAFop84jXsS6UqStVGZ-5UaMREeqDsXgSbA
export TELEGRAM_BOT_MyStarbankRecommendationBot

## 🐳 Docker развертывание

### Docker Compose
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: recommendation_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"
  
  app:
    build: .
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/recommendation_db
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: password
      TELEGRAM_BOT_TOKEN: ${TELEGRAM_BOT_TOKEN}
    ports:
      - "8080:8080"
    depends_on:
      - postgres

volumes:
  postgres_data:

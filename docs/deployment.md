# 🚀 Развертывание рекомендательного сервиса банка "Стар"

## 📋 Предварительные требования

### Системные требования
- **Java**: версия 17 или выше
- **Maven**: версия 3.6 или выше (для сборки)
- **PostgreSQL**: версия 12 или выше (для динамических правил)
- **Docker и Docker Compose** (опционально, для контейнеризации)
- **Telegram Bot Token** (полученный от @BotFather)
- **Файл базы данных H2**: `transaction.mv.db` (предоставляется в ТЗ)

### Поддерживаемые операционные системы
- Linux (Ubuntu 20.04+, CentOS 8+)
- macOS 10.15+
- Windows 10/11 (с WSL2 рекомендовано)

## ⚙️ Переменные окружения

### Обязательные переменные для продакшена

```bash
# Основная база данных (PostgreSQL)
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/recommendation_db
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=your_secure_password

# Telegram Bot
export TELEGRAM_BOT_TOKEN=8372520281:AAFop84jXsS6UqStVGZ-5UaMREeqDsXgSbA
export TELEGRAM_BOT_NAME=MyStarbankRecommendationBot

# Файл H2 базы данных (должен быть в папке ./data/)
# export SPRING_DATASOURCE_H2_URL=jdbc:h2:file:./data/transaction

🏗️ Сборка проекта
# Клонирование репозитория
git clone https://github.com/your-org/recommendation-service.git
cd recommendation-service

# Сборка с тестами
mvn clean package

# Сборка без тестов
mvn clean package -DskipTests

🐳 Docker развертывание
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

📊 Проверка развертывания
bash
# Проверка health-check
curl http://localhost:8080/actuator/health

# Проверка версии
curl http://localhost:8080/management/info

# Создание тестового правила
curl -X POST http://localhost:8080/rule \
  -H "Content-Type: application/json" \
  -d '{
    "product_name": "Test Product",
    "product_id": "147f6a0f-3b91-413b-ab99-87f081d60d5a",
    "product_text": "Test description",
    "rule": [
      {
        "query": "USER_OF",
        "arguments": ["DEBIT"],
        "negate": false
      }
    ]
  }'
🔧 Конфигурация для разных окружений
Разработка (application-dev.properties)
properties
spring.datasource.url=jdbc:h2:file:./data/transaction
spring.jpa.show-sql=true
logging.level.com.starbank=DEBUG
Продакшен (application-prod.properties)
properties
spring.datasource.url=jdbc:postgresql://${DB_HOST}:5432/recommendation_db
spring.jpa.show-sql=false
logging.level.com.starbank=INFO
management.endpoints.web.exposure.include=health,info,metrics
📈 Мониторинг и логи
Prometheus метрики
properties
management.endpoint.metrics.enabled=true
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.metrics.export.prometheus.enabled=true
Логирование в файл
properties
logging.file.name=logs/application.log
logging.file.max-size=10MB
logging.file.max-history=30
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
🆘 Устранение неполадок
Ошибка подключения к БД
bash
# Проверка подключения к PostgreSQL
psql -h localhost -U postgres -d recommendation_db

# Проверка наличия файла H2
ls -la ./data/transaction.mv.db
Ошибка Telegram бота
bash
# Проверка токена
echo "Токен бота: ${TELEGRAM_BOT_TOKEN:0:10}..."

# Тестирование бота через curl
curl https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/getMe
Очистка кеша
bash
# Через API
curl -X POST http://localhost:8080/management/clear-caches

# Через перезапуск
docker-compose restart app

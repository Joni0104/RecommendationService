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

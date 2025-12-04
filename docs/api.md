```yaml
openapi: 3.0.0
info:
  title: Recommendation Service API
  version: 1.0.0
  description: API для рекомендательного сервиса банка "Стар"

servers:
  - url: http://localhost:8080
    description: Local server

paths:
  /recommendation/{userId}:
    get:
      summary: Получить рекомендации для пользователя
      parameters:
        - name: userId
          in: path
          required: true
          schema:
            type: string
            format: uuid
      responses:
        '200':
          description: Список рекомендаций
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/RecommendationResponse'
  
  /rule:
    post:
      summary: Создать динамическое правило
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/DynamicRuleRequest'
      responses:
        '200':
          description: Правило создано
    
    get:
      summary: Получить все динамические правила
      responses:
        '200':
          description: Список правил
  
  /rule/{productId}:
    delete:
      summary: Удалить правило по productId
      parameters:
        - name: productId
          in: path
          required: true
          schema:
            type: string
            format: uuid
      responses:
        '204':
          description: Правило удалено
  
  /rule/stats:
    get:
      summary: Получить статистику срабатываний правил
      responses:
        '200':
          description: Статистика по правилам
  
  /management/clear-caches:
    post:
      summary: Очистить все кеши
      responses:
        '200':
          description: Кеши очищены
  
  /management/info:
    get:
      summary: Получить информацию о сервисе
      responses:
        '200':
          description: Информация о сервисе

components:
  schemas:
    RecommendationResponse:
      type: object
      properties:
        userId:
          type: string
          format: uuid
        recommendations:
          type: array
          items:
            $ref: '#/components/schemas/RecommendationDto'
    
    RecommendationDto:
      type: object
      properties:
        name:
          type: string
        id:
          type: string
          format: uuid
        text:
          type: string
    
    DynamicRuleRequest:
      type: object
      required:
        - productName
        - productId
        - productText
        - rule
      properties:
        productName:
          type: string
        productId:
          type: string
          format: uuid
        productText:
          type: string
        rule:
          type: array
          items:
            $ref: '#/components/schemas/RuleQuery'
    
    RuleQuery:
      type: object
      properties:
        query:
          type: string
          enum: [USER_OF, ACTIVE_USER_OF, TRANSACTION_SUM_COMPARE, TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW]
        arguments:
          type: array
          items:
            type: string
        negate:
          type: boolean

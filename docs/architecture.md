# 🏗️ Архитектура системы

## 📊 Диаграмма компонентов

```mermaid
graph TB
    subgraph "Внешние системы"
        A[Клиент API]
        B[Telegram]
        C[Оператор]
    end
    
    subgraph "Рекомендательный сервис"
        D[API Gateway]
        E[Recommendation Controller]
        F[Dynamic Rule Controller]
        G[Management Controller]
        H[Telegram Bot]
        
        I[Recommendation Service]
        J[Dynamic Rule Service]
        K[Cache Service]
        
        L[UserData Repository]
        M[DynamicRule Repository]
        N[RuleStatistic Repository]
    end
    
    subgraph "Базы данных"
        O[H2 - Read Only]
        P[PostgreSQL - Rules]
    end
    
    A --> D
    B --> H
    C --> D
    
    D --> E
    D --> F
    D --> G
    H --> I
    
    E --> I
    F --> J
    G --> K
    
    I --> J
    I --> L
    J --> M
    J --> N
    K --> L
    
    L --> O
    M --> P
    N --> P

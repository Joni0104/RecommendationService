package com.starbank.recommendationService.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
public class UserDataRepository {

    private final JdbcTemplate jdbcTemplate;

    // Кеши для разных типов запросов
    private final Cache<String, Boolean> userProductCache;
    private final Cache<String, Double> transactionSumCache;
    private final Cache<String, Boolean> depositWithdrawCompareCache;
    private final Cache<String, Integer> transactionCountCache;

    public UserDataRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

        // Инициализация кешей
        this.userProductCache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();

        this.transactionSumCache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();

        this.depositWithdrawCompareCache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();

        this.transactionCountCache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
    }

    public boolean hasProductType(UUID userId, String productType) {
        String cacheKey = userId + ":" + productType;
        return userProductCache.get(cacheKey, key ->
                executeHasProductType(userId, productType)
        );
    }

    public Double getTotalDepositsByProductType(UUID userId, String productType) {
        String cacheKey = userId + ":" + productType + ":DEPOSIT";
        return transactionSumCache.get(cacheKey, key ->
                executeGetTotalTransactions(userId, productType, "DEPOSIT")
        );
    }

    public Double getTotalSpendsByProductType(UUID userId, String productType) {
        String cacheKey = userId + ":" + productType + ":WITHDRAWAL";
        return transactionSumCache.get(cacheKey, key ->
                executeGetTotalTransactions(userId, productType, "WITHDRAWAL")
        );
    }

    public boolean isDepositsGreaterThanSpends(UUID userId, String productType) {
        String cacheKey = userId + ":" + productType + ":COMPARE";
        return depositWithdrawCompareCache.get(cacheKey, key -> {
            Double deposits = getTotalDepositsByProductType(userId, productType);
            Double spends = getTotalSpendsByProductType(userId, productType);
            return deposits > spends;
        });
    }

    public int getTransactionCountByProductType(UUID userId, String productType) {
        String cacheKey = userId + ":" + productType + ":COUNT";
        return transactionCountCache.get(cacheKey, key ->
                executeGetTransactionCount(userId, productType)
        );
    }

    // Приватные методы для выполнения SQL запросов
    private boolean executeHasProductType(UUID userId, String productType) {
        String sql = """
            SELECT COUNT(*) > 0 
            FROM transactions t
            JOIN products p ON t.product_id = p.id
            WHERE t.user_id = ? AND p.type = ?
            """;

        Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, userId, productType);
        return Boolean.TRUE.equals(result);
    }

    private Double executeGetTotalTransactions(UUID userId, String productType, String operationType) {
        String sql = """
            SELECT COALESCE(SUM(t.amount), 0)
            FROM transactions t
            JOIN products p ON t.product_id = p.id
            WHERE t.user_id = ? AND p.type = ? AND t.operation_type = ?
            """;

        Double result = jdbcTemplate.queryForObject(sql, Double.class, userId, productType, operationType);
        return result != null ? result : 0.0;
    }

    private int executeGetTransactionCount(UUID userId, String productType) {
        String sql = """
            SELECT COUNT(*)
            FROM transactions t
            JOIN products p ON t.product_id = p.id
            WHERE t.user_id = ? AND p.type = ?
            """;

        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, userId, productType);
        return result != null ? result : 0;
    }
}
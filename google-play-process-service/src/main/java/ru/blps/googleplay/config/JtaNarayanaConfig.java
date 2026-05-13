package ru.blps.googleplay.config;

import com.arjuna.ats.arjuna.common.arjPropertyManager;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.SystemException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

@Configuration
public class JtaNarayanaConfig {

    private final String transactionManagerId;

    public JtaNarayanaConfig(@Value("${spring.jta.transaction-manager-id:}") String transactionManagerId) {
        this.transactionManagerId = transactionManagerId;
    }

    @PostConstruct
    public void configureNarayana() {
        if (transactionManagerId != null && !transactionManagerId.isBlank()) {
            try {
                arjPropertyManager.getCoreEnvironmentBean().setNodeIdentifier(transactionManagerId);
            } catch (Exception e) {
                throw new IllegalStateException("Failed to configure Narayana node identifier", e);
            }
        }
    }

    @Bean
    public jakarta.transaction.UserTransaction narayanaUserTransaction() throws SystemException {
        return com.arjuna.ats.jta.UserTransaction.userTransaction();
    }

    @Bean
    public jakarta.transaction.TransactionManager narayanaTransactionManager() {
        return com.arjuna.ats.jta.TransactionManager.transactionManager();
    }

    @Bean
    public PlatformTransactionManager transactionManager(jakarta.transaction.UserTransaction ut,
                                                         jakarta.transaction.TransactionManager tm) {
        return new JtaTransactionManager(ut, tm);
    }
}

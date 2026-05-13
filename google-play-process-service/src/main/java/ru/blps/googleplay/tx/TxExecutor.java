package ru.blps.googleplay.tx;

import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

@Component
public class TxExecutor {

    private final TransactionTemplate required;

    public TxExecutor(PlatformTransactionManager transactionManager) {
        this.required = new TransactionTemplate(transactionManager);
    }

    public <T> T required(Supplier<T> action) {
        return required.execute(status -> action.get());
    }

    public void required(Runnable action) {
        required.executeWithoutResult(status -> action.run());
    }
}


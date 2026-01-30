package model;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class Transaction implements Serializable {
    private String id;
    private TransactionTypeEnum type;
    private Double amount;
    private LocalDateTime timestamp;

    public Transaction(String id, TransactionTypeEnum type, Double amount, LocalDateTime timestamp) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public TransactionTypeEnum getType() {
        return type;
    }

    public Double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format(
                "ID: %s | Type: %s | Amount: %.2f | Date: %s",
                id,
                type,
                amount,
                timestamp.format(FORMATTER)
        );
    }


    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

}

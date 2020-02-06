package goris.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    @Column(name="external_id")
    private UUID externalId;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private Currency currency;

    public Account() {
    }

    public Account(UUID externalId, BigDecimal amount, Currency currency) {
        this.externalId = externalId;
        this.amount = amount;
        this.currency = currency;
    }

    public long getId() {
        return id;
    }

    public UUID getExternalId() {
        return externalId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
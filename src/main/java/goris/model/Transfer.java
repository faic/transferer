package goris.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "transfers")
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private UUID externalId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="account_from", nullable = false)
    private Account accountFrom;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="account_to", nullable = false)
    private Account accountTo;
    private BigDecimal baseAmount;
    private BigDecimal dstAmount;

    public Transfer() {
    }

    public Transfer(
            UUID externalId,
            Account accountFrom,
            Account accountTo,
            BigDecimal baseAmount,
            BigDecimal dstAmount
    ) {
        this.externalId = externalId;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.baseAmount = baseAmount;
        this.dstAmount = dstAmount;
    }

    public long getId() {
        return id;
    }

    public UUID getExternalId() {
        return externalId;
    }

    public Account getAccountFrom() {
        return accountFrom;
    }

    public Account getAccountTo() {
        return accountTo;
    }

    public BigDecimal getBaseAmount() {
        return baseAmount;
    }

    public BigDecimal getDstAmount() {
        return dstAmount;
    }

}

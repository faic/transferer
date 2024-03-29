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
    @Column(name="external_id")
    private UUID externalId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="account_from", nullable = false)
    private Account accountFrom;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="account_to", nullable = false)
    private Account accountTo;
    @Column(name="base_amount")
    private BigDecimal baseAmount;
    @Column(name="dst_amount")
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

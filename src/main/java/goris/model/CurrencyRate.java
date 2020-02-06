package goris.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "currency_rates")
public class CurrencyRate {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    @Enumerated(EnumType.STRING)
    private Currency base;
    @Enumerated(EnumType.STRING)
    private Currency dst;
    private BigDecimal rate;

    public CurrencyRate() {
    }

    public CurrencyRate(Currency base, Currency dst, BigDecimal rate) {
        this.base = base;
        this.dst = dst;
        this.rate = rate;
    }

    public long getId() {
        return id;
    }

    public Currency getBase() {
        return base;
    }

    public Currency getDst() {
        return dst;
    }

    public BigDecimal getRate() {
        return rate;
    }
}

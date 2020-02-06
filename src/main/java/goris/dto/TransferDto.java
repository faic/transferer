package goris.dto;

public class TransferDto {
    String id;
    String from;
    String to;
    String baseAmount;
    String baseCurrency;
    String dstAmount;
    String dstCurrency;

    public TransferDto() {
    }

    public TransferDto(
            String id,
            String from,
            String to,
            String baseAmount,
            String baseCurrency,
            String dstAmount,
            String dstCurrency
    ) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.baseAmount = baseAmount;
        this.baseCurrency = baseCurrency;
        this.dstAmount = dstAmount;
        this.dstCurrency = dstCurrency;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(String baseAmount) {
        this.baseAmount = baseAmount;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getDstAmount() {
        return dstAmount;
    }

    public void setDstAmount(String dstAmount) {
        this.dstAmount = dstAmount;
    }

    public String getDstCurrency() {
        return dstCurrency;
    }

    public void setDstCurrency(String dstCurrency) {
        this.dstCurrency = dstCurrency;
    }
}

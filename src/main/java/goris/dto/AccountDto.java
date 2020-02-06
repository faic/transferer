package goris.dto;

public class AccountDto {
    private String id;
    private String amount;
    private String currency;

    public AccountDto(String id, String amount, String currency) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
    }

    public AccountDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}

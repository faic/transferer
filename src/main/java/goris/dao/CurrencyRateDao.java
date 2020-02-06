package goris.dao;

import goris.model.Currency;
import goris.model.CurrencyRate;

public interface CurrencyRateDao {
    void save(CurrencyRate currencyRate);
    CurrencyRate find(Currency base, Currency dst);
}

package goris.service;

import goris.dao.CurrencyRateHibernateDao;
import goris.model.Currency;
import goris.model.CurrencyRate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyService {
    private final CurrencyRateHibernateDao currencyRateDao;
    private final SessionFactory sessionFactory;

    private final static CurrencyRate[] INIT_RATES = {
            new CurrencyRate(Currency.RUB, Currency.USD, BigDecimal.valueOf(0.0166)),
            new CurrencyRate(Currency.RUB, Currency.EUR, BigDecimal.valueOf(0.0141)),
            new CurrencyRate(Currency.USD, Currency.RUB, BigDecimal.valueOf(60.63)),
            new CurrencyRate(Currency.USD, Currency.EUR, BigDecimal.valueOf(0.86)),
            new CurrencyRate(Currency.EUR, Currency.USD, BigDecimal.valueOf(1.15)),
            new CurrencyRate(Currency.EUR, Currency.RUB, BigDecimal.valueOf(70.11))
    };

    public CurrencyService(CurrencyRateHibernateDao currencyRateDao, SessionFactory sessionFactory) {
        this.currencyRateDao = currencyRateDao;
        this.sessionFactory = sessionFactory;
    }

    public void initRates() {
        for (CurrencyRate currencyRate : INIT_RATES) {
            saveCurrencyRate(currencyRate.getBase(), currencyRate.getDst(), currencyRate.getRate());
        }
    }

    public BigDecimal getAmount(BigDecimal baseAmount, Currency baseCurrency, Currency dstCurrency) {
        if (baseCurrency.equals(dstCurrency)) {
            return baseAmount;
        }
        try (Session session = sessionFactory.openSession()) {
            currencyRateDao.setCurrentSession(session);
            CurrencyRate currencyRate = currencyRateDao.find(baseCurrency, dstCurrency);
            return baseAmount.multiply(currencyRate.getRate()).setScale(2, RoundingMode.DOWN);
        }
    }

    public void saveCurrencyRate(Currency baseCurrency, Currency dsrCurrency, BigDecimal rate) {
        CurrencyRate currencyRate = new CurrencyRate(baseCurrency, dsrCurrency, rate);
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            currencyRateDao.setCurrentSession(session);
            currencyRateDao.setCurrentTransaction(transaction);
            currencyRateDao.save(currencyRate);
            transaction.commit();
        }  catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }
}

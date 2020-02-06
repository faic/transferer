package goris.dao;

import goris.model.Currency;
import goris.model.CurrencyRate;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

public class CurrencyRateHibernateDao  implements CurrencyRateDao {

    private Session currentSession;
    private Transaction currentTransaction;

    public CurrencyRateHibernateDao() {
    }

    @Override
    public void save(CurrencyRate currencyRate) {
        currentSession.persist(currencyRate);
    }

    public Session getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(Session currentSession) {
        this.currentSession = currentSession;
    }

    public void setCurrentTransaction(Transaction currentTransaction) {
        this.currentTransaction = currentTransaction;
    }

    public Transaction getCurrentTransaction() {
        return currentTransaction;
    }


    @Override
    public CurrencyRate find(Currency base, Currency dst) {
        Criteria currencyRateCriteria = currentSession.createCriteria(CurrencyRate.class);
        currencyRateCriteria.add(Restrictions.eq("base", base));
        currencyRateCriteria.add(Restrictions.eq("dst", dst));

        return (CurrencyRate) currencyRateCriteria.uniqueResult();
    }

}

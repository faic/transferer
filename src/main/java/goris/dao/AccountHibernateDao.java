package goris.dao;

import goris.model.Account;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.UUID;

public class AccountHibernateDao implements AccountDao {
    private Session currentSession;
    private Transaction currentTransaction;

    public AccountHibernateDao(){
    }

    @Override
    public void save(Account account) {
        currentSession.persist(account);
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
    public void update(Account account) {
        currentSession.update(account);
    }

    @Override
    public Account find(UUID externalId) {
        Criteria accountCriteria = currentSession.createCriteria(Account.class);
        accountCriteria.add(Restrictions.eq("externalId", externalId));
        return (Account) accountCriteria.uniqueResult();
    }

    @Override
    public Account getAccountForUpdate(long id) {
        return currentSession.get(Account.class, id, LockMode.PESSIMISTIC_WRITE);
    }
}

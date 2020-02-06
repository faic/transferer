package goris.service;

import goris.dao.AccountHibernateDao;
import goris.model.Account;
import goris.model.AccountException;
import goris.model.Currency;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.UUID;

public class AccountService {
    private final AccountHibernateDao accountDao;
    private final SessionFactory sessionFactory;

    public AccountService(
            AccountHibernateDao accountDao,
            SessionFactory sessionFactory
    ) {
        this.accountDao = accountDao;
        this.sessionFactory = sessionFactory;
    }

    public Account getAccount(UUID externalId) {
        try (Session session = sessionFactory.openSession()) {
            accountDao.setCurrentSession(session);
            return Optional.ofNullable(accountDao.find(externalId))
                    .orElseThrow(() -> new AccountException("No account with such id"));
        }
    }

    public Account saveAccount(BigDecimal amount, Currency currency) {
        Account account = new Account(UUID.randomUUID(), amount.setScale(2, RoundingMode.DOWN), currency);
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            accountDao.setCurrentSession(session);
            accountDao.setCurrentTransaction(transaction);
            accountDao.save(account);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
        return account;
    }

    public Account updateAccount(UUID externalId, BigDecimal amount) {
        Account account = getAccount(externalId);
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            accountDao.setCurrentSession(session);
            accountDao.setCurrentTransaction(transaction);
            account.setAmount(amount.setScale(2, RoundingMode.DOWN));
            accountDao.update(account);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
        return account;
    }
}

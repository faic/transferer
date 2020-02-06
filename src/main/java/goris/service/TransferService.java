package goris.service;

import goris.dao.AccountHibernateDao;
import goris.dao.TransferHibernateDao;
import goris.model.Account;
import goris.model.Transfer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.UUID;

public class TransferService {

    private final AccountHibernateDao accountDao;
    private final TransferHibernateDao transferDao;
    private final CurrencyService currencyService;
    private final SessionFactory sessionFactory;
    private final AccountService accountService;

    public TransferService(
            AccountHibernateDao accountDao,
            TransferHibernateDao transferDao,
            CurrencyService currencyService,
            SessionFactory sessionFactory,
            AccountService accountService
    ) {
        this.accountDao = accountDao;
        this.transferDao = transferDao;
        this.currencyService = currencyService;
        this.sessionFactory = sessionFactory;
        this.accountService = accountService;
    }

    public Optional<Transfer> getTransfer(UUID externalId) {
        try (Session session = sessionFactory.openSession()) {
            transferDao.setCurrentSession(session);
            return Optional.ofNullable(transferDao.find(externalId));
        } catch (Exception ex) {
            throw ex;
        }
    }

    public Transfer transfer(UUID from, UUID to, BigDecimal amount) {
        if (from.equals(to)) {
            throw new IllegalArgumentException("From equal to to");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Illegal amount");
        }
        Optional<Account> accFrom = accountService.getAccount(from);
        if (!accFrom.isPresent()) {
            throw new IllegalArgumentException("No such from account");
        }
        Optional<Account> accTo = accountService.getAccount(to);
        if (!accTo.isPresent()) {
            throw new IllegalArgumentException("No such to account");
        }
        BigDecimal baseAmount = amount.setScale(2, RoundingMode.DOWN);
        BigDecimal dstAmount = currencyService.getAmount(
                baseAmount,
                accFrom.get().getCurrency(),
                accTo.get().getCurrency());
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            accountDao.setCurrentSession(session);
            accountDao.setCurrentTransaction(transaction);
            Account accountFrom;
            Account accountTo;
            if (accFrom.get().getId() < accTo.get().getId()) {
                accountFrom = accountDao.getAccountForUpdate(accFrom.get().getId());
                accountTo = accountDao.getAccountForUpdate(accTo.get().getId());
            } else {
                accountTo = accountDao.getAccountForUpdate(accTo.get().getId());
                accountFrom = accountDao.getAccountForUpdate(accFrom.get().getId());
            }

            if (accountFrom.getAmount().compareTo(baseAmount) < 0) {
                throw new IllegalArgumentException("Illegal amount");
            }
            accountFrom.setAmount(accountFrom.getAmount().subtract(baseAmount));
            accountTo.setAmount(accountTo.getAmount().add(dstAmount));
            accountDao.update(accountFrom);
            accountDao.update(accountTo);
            Transfer transfer = new Transfer(
                    UUID.randomUUID(),
                    accountFrom,
                    accountTo,
                    baseAmount,
                    dstAmount);
            transferDao.setCurrentSession(session);
            transferDao.setCurrentTransaction(transaction);
            transferDao.save(transfer);
            transaction.commit();
            return transfer;
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

}

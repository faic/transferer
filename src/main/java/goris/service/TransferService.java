package goris.service;

import goris.dao.AccountHibernateDao;
import goris.dao.TransferHibernateDao;
import goris.model.Account;
import goris.model.Transfer;
import goris.model.TransferException;
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

    public Transfer getTransfer(UUID externalId) {
        try (Session session = sessionFactory.openSession()) {
            transferDao.setCurrentSession(session);
            return Optional.ofNullable(transferDao.find(externalId))
                    .orElseThrow(() -> new TransferException("No transfer with such id"));
        }
    }

    public Transfer transfer(UUID from, UUID to, BigDecimal amount) {
        if (from.equals(to)) {
            throw new TransferException("Both accounts has equal ids - no action required");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransferException("Amount is not greater than zero");
        }
        Account accFrom = accountService.getAccount(from);
        Account accTo = accountService.getAccount(to);
        BigDecimal baseAmount = amount.setScale(2, RoundingMode.DOWN);
        BigDecimal dstAmount = currencyService.getAmount(
                baseAmount,
                accFrom.getCurrency(),
                accTo.getCurrency());
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            accountDao.setCurrentSession(session);
            accountDao.setCurrentTransaction(transaction);
            if (accFrom.getId() < accTo.getId()) {
                accFrom = accountDao.getAccountForUpdate(accFrom.getId());
                accTo = accountDao.getAccountForUpdate(accTo.getId());
            } else {
                accTo = accountDao.getAccountForUpdate(accTo.getId());
                accFrom = accountDao.getAccountForUpdate(accFrom.getId());
            }

            if (accFrom.getAmount().compareTo(baseAmount) < 0) {
                throw new TransferException("Amount is greater than 'from' account current balance");
            }
            accFrom.setAmount(accFrom.getAmount().subtract(baseAmount));
            accTo.setAmount(accTo.getAmount().add(dstAmount));
            accountDao.update(accFrom);
            accountDao.update(accTo);
            Transfer transfer = new Transfer(
                    UUID.randomUUID(),
                    accFrom,
                    accTo,
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

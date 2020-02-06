package goris.dao;

import goris.model.Transfer;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.UUID;

public class TransferHibernateDao implements TransferDao {
    private Session currentSession;
    private Transaction currentTransaction;

    @Override
    public void save(Transfer transfer) {
        currentSession.persist(transfer);
    }

    @Override
    public Transfer find(UUID externalId) {
        Criteria transferCriteria = currentSession.createCriteria(Transfer.class);
        transferCriteria.add(Restrictions.eq("externalId", externalId));
        return (Transfer) transferCriteria.uniqueResult();
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


}

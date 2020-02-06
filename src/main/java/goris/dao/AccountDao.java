package goris.dao;

import goris.model.Account;

import java.util.UUID;

public interface AccountDao {
    void save(Account account);
    void update(Account account);
    Account find(UUID externalId);
    Account getAccountForUpdate(long id);
}

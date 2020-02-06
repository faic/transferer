package goris.dao;

import goris.model.Transfer;

import java.util.UUID;

public interface TransferDao {
    void save(Transfer transfer);
    Transfer find(UUID externalId);
}

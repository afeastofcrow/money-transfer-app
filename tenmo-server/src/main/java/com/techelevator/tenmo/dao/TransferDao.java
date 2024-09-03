package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    List<Transfer> getAllTransfers();

    List<Transfer> getAllTransfersByAccountId(int accountId);

    List<Transfer> getAllTransfersFromAccountId(int accountId);

    List<Transfer> getAllTransfersToAccountId(int accountId);

    List<Transfer> getPendingTransfersByAccountId(int accountId);

    Transfer getTransferByTransferId(int transferId);

    void createTransfer(Transfer transfer);

    Transfer updateTransferStatus(Transfer transfer);
}

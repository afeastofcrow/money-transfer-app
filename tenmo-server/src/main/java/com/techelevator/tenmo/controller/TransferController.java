package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
public class TransferController {
    private final TransferDao transferDao;

    public TransferController(TransferDao transferDao) { this.transferDao = transferDao;}

    @GetMapping("/transfers")
    public List<Transfer> getAllTransfers(){
        return transferDao.getAllTransfers();
    }

    @GetMapping("/transfers/accounts/{accountId}")
    public List<Transfer> getAllTransferByAccountId(@PathVariable int accountId) {
        return transferDao.getAllTransfersByAccountId(accountId);
    }

    @GetMapping("/transfers/accountsFrom/{accountId}")
    public List<Transfer> getAllTransfersFromAccountId(@PathVariable int accountId){
        return transferDao.getAllTransfersFromAccountId(accountId);
    }

    @GetMapping("/transfers/accountsTo/{accountId}")
    public List<Transfer> getAllTransfersToAccountId(@PathVariable int accountId){
        return transferDao.getAllTransfersToAccountId(accountId);
    }

    @GetMapping("/transfers/pendingTransfers/{accountId}")
    public List<Transfer> getPendingTransfersByAccountId(@PathVariable int accountId){
        return transferDao.getPendingTransfersByAccountId(accountId);
    }

    @GetMapping("/transfers/{transferId}")
    public Transfer getTransferByTransferId(@PathVariable int transferId){
        return transferDao.getTransferByTransferId(transferId);
    }

    //  I DONT THINK THIS SHOULD BE VOID
    @PostMapping("/transfers")
    public void createTransfer(@RequestBody Transfer transfer) {
        transferDao.createTransfer(transfer);
    }

    @PutMapping("/transfers/{transferId}")
    public Transfer updateTransferStatus(@RequestBody Transfer transfer, @PathVariable int transferId) {
        transfer.setTransferId(transferId);
        try {
           transferDao.updateTransferStatus(transfer);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transfer Not Found");
        }

        return transfer;
    }

}

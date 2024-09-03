package com.techelevator.tenmo.model;

import javax.validation.constraints.NotNull;

public class TransferStatus {
    @NotNull
    int transferStatusId;
    @NotNull
    String transferStatus;

    public TransferStatus() {}

    public TransferStatus(int transferStatusId, String transferStatus) {
        this.transferStatusId = transferStatusId;
        this.transferStatus = transferStatus;
    }
}

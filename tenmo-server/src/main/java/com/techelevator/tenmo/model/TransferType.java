package com.techelevator.tenmo.model;

import javax.validation.constraints.NotNull;

public class TransferType {
    @NotNull
    int transferTypeId;
    @NotNull
    String transferType;

    public TransferType(){}

    public TransferType(int transferTypeId, String transferType) {
        this.transferTypeId = transferTypeId;
        this.transferType = transferType;
    }
}

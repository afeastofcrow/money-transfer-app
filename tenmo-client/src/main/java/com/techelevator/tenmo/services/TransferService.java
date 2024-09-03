package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class TransferService {
    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public TransferService(String baseUrl) { this.baseUrl = baseUrl;}

    public Transfer[] getTransfersByAccountId(int accountId, String authToken) {
        Transfer[] transfers = null;
        try{
            ResponseEntity<Transfer[]> response = restTemplate.exchange(baseUrl + "/transfers/accounts/" + accountId,
                    HttpMethod.GET, makeAuthEntity(authToken), Transfer[].class);
            transfers = response.getBody();
        }catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return transfers;
    }

    public Transfer getTransferByTransferId(int transferId, String authToken) {
        Transfer transfer = null;
        try{
            ResponseEntity<Transfer> response = restTemplate.exchange(baseUrl + "/transfers/" + transferId,
                    HttpMethod.GET, makeAuthEntity(authToken), Transfer.class);
            transfer = response.getBody();
        }catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return transfer;
    }

    public Transfer sendFunds(Transfer transfer, String authToken) {
        Transfer returnedTransfer = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(authToken);
            HttpEntity<Transfer> entity =  new HttpEntity<>(transfer, headers);

            returnedTransfer = restTemplate.postForObject(baseUrl + "/transfers/", entity, Transfer.class);

        }  catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return returnedTransfer;
    }

    public void updateTransferStatus(Transfer transfer, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(authToken);
            HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);

            restTemplate.put(baseUrl + "/transfers/" + transfer.getTransferId(), entity);
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

    }

    public void createTransfer(Transfer transfer, String authToken) {
        restTemplate.postForObject(baseUrl + "/transfers", makeTransferEntity(transfer, authToken), Transfer.class);
    }
    private HttpEntity<Void> makeAuthEntity(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);

        return new HttpEntity<Transfer>(transfer, headers);
    }
}

package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;


public class AccountService {
    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();
    private final UserService userService;
    public AccountService(String url) {
        this.baseUrl = url;
        userService = new UserService(baseUrl);
    }

    public Account getAccountByUserId(int userId, String authToken) {
        Account account = null;

        try {
            ResponseEntity<Account> response = restTemplate.exchange(baseUrl + "/Users/Account/" + userId,
                    HttpMethod.GET,
                    makeAuthEntity(authToken),
                    Account.class);
            account = response.getBody();
        } catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return account;
    }

    public Account getAccountByAccountId(int accountId, String authToken) {
        Account account = null;

        try {
            ResponseEntity<Account> response = restTemplate.exchange(baseUrl + "/user/account/" + accountId,
                    HttpMethod.GET,
                    makeAuthEntity(authToken),
                    Account.class);
            account = response.getBody();
        } catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return account;
    }

//    public Account getAccountByUserName(String userName) {
 //       User user = userService.getUserByUsername(userName);
  //      if (user == null) {
   //         BasicLogger.log("Username Not Found" + userName);
   //         return null;
   //     }
   //     return getAccountByUserId(user.getId());
   // }

    public void updateAccountBalance(Account account, String authToken) {
        try {

            restTemplate.put(baseUrl + "Users/Account/" + account.getAccountId(), makeAccountEntity(account, authToken));
        } catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
    }

    private HttpEntity<Void> makeAuthEntity(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }

    private HttpEntity<Account> makeAccountEntity(Account account, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<Account>(account, headers);
    }
}

package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;


@RestController
@PreAuthorize("isAuthenticated()")
public class AccountController {
    private final AccountDao accountDao;

    public AccountController(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @GetMapping("/users/account")
    public List<Account> getAllAccounts() {
        return accountDao.getAllAccounts();
    }

    @GetMapping("/Users/Account/{user_id}")
    public Account getAccountByUserId(@PathVariable("user_id") int userId) {
        Account account = accountDao.getAccountByUserId(userId);
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account Not Found");
        } else {
            return account;
        }
    }

    @GetMapping("/user/account/{account_id}")
    public Account getAccountByAccountId(@PathVariable("account_id") int accountId) {
        Account account = accountDao.getAccountByAccountId(accountId);
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account Not Found");
        }
        return account;
    }



    @PutMapping("Users/Account/{account_id}")
    public Account updateAccount(@RequestBody Account account, @PathVariable("account_id") int accountId) {
        account.setAccountId(accountId);

        accountDao.updateAccount(account);
        return account;
    }
}

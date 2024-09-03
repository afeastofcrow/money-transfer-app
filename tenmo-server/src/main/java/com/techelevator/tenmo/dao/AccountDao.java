package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {


    public List<Account> getAllAccounts ();

    public   Account getAccountByAccountId(int accountId);

    Account getAccountByUserId(int userId);

    BigDecimal subtractFromAccountBalance(int accountId, BigDecimal amount);

    BigDecimal addToAccountBalance(int accountId, BigDecimal amount);

    BigDecimal getAccountBalance(int accountId);

    void updateAccount(Account account);







}

package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Component
public class JdbcAccountDao implements AccountDao{
    private final JdbcTemplate jdbc;

    public JdbcAccountDao(JdbcTemplate jdbc) { this.jdbc = jdbc; }


    @Override
    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM account";

        try{
            SqlRowSet results = jdbc.queryForRowSet(sql);

            while(results.next()) {
                Account account = mapRowToAccount(results);
                accounts.add(account);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return accounts;
    }

    @Override
    public Account getAccountByAccountId(int accountId) {
        Account account = new Account();
        String sql = "SELECT * FROM account WHERE account_id = ?";

        try {
            SqlRowSet results = jdbc.queryForRowSet(sql, accountId);

            if (results.next()) {
                account = mapRowToAccount(results);
            }

        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }

        return account;
    }

    @Override
    public Account getAccountByUserId(int userId) {
        Account account = new Account();
        String sql = "SELECT * FROM account WHERE user_id = ?";

        try {
            SqlRowSet results = jdbc.queryForRowSet(sql, userId);

            if (results.next()) {
                account = mapRowToAccount(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }

        return account;
    }

    @Override
    public BigDecimal subtractFromAccountBalance(int accountId, BigDecimal amount) {
        String sql = "UPDATE account SET balance = (balance - ?) WHERE account_id = ?";
        try {
            if(amount.compareTo(getAccountBalance(accountId)) <= 0) {
            jdbc.update(sql, amount, accountId);
        }
        }catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return getAccountBalance(accountId);
    }

    @Override
    public BigDecimal addToAccountBalance(int accountId, BigDecimal amount) {
        String sql = "UPDATE account SET balance = (balance + ?) WHERE account_id = ?";
       try {
           jdbc.update(sql, amount, accountId);
       }catch (CannotGetJdbcConnectionException e) {
           throw new DaoException("Unable to connect to server or database", e);
       }
        return getAccountBalance(accountId);
    }

    @Override
    public BigDecimal getAccountBalance(int accountId) {
        Account account = getAccountByAccountId(accountId);
        return account.getBalance();
    }

    private Account mapRowToAccount(SqlRowSet rs) {
        Account account = new Account();
        account.setAccountId(rs.getInt("account_id"));
        account.setUserId(rs.getInt("user_id"));
        account.setBalance(rs.getBigDecimal("balance"));
        return account;
    }

    public void updateAccount(Account account) {
        String sql = "UPDATE account SET balance = ? WHERE account_id = ?";

        try {
            jdbc.update(sql, account.getBalance(), account.getAccountId());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

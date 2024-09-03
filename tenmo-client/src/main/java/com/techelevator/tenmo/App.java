package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;

import java.math.BigDecimal;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final UserService userService = new UserService(API_BASE_URL);
    private final AccountService accountService = new AccountService(API_BASE_URL);
    private final TransferService transferService= new TransferService(API_BASE_URL);
    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }

    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }
    //working
    private void viewCurrentBalance() {
        BigDecimal currentBalance;
        Account account = accountService.getAccountByUserId(currentUser.getUser().getId(), currentUser.getToken());
        currentBalance = account.getBalance();
        System.out.println("Current balance is $: " + currentBalance);
    }
    // SOMEONE IMPLEMENT VIEW SINGLE TRANSACTION
    private void viewTransferHistory() {
        Account account = accountService.getAccountByUserId(currentUser.getUser().getId(), currentUser.getToken());
        Transfer[] transfers = transferService.getTransfersByAccountId(account.getAccountId(), currentUser.getToken());

        System.out.println("1.View All Transfers\n" + "2.View By Transfer ID\n" + "0.Cancel\n");
        int selected = consoleService.promptForInt("Please choose an option: ");

        switch (selected){
            case 1:
                System.out.println("-------------------------------------------");
                System.out.println("Transfers: ");
                System.out.println("ID          From/To                 Amount");
                System.out.println("-------------------------------------------");
                loopingTransfers();
                break;

            case 2:

                for(Transfer x : transfers) {
                    System.out.println("Transfer ID: " + x.getTransferId());
                }
                System.out.println();
                int transferId = consoleService.promptForInt("---------\n" + "Please enter transfer ID to view details (0 to cancel):");

                if(transferId == 0){
                    return;
                }

                Transfer selectedTransfer = transferService.getTransferByTransferId(transferId, currentUser.getToken());

                System.out.println("Transfer Details: ");
                System.out.println("-------------------------------------------");

                if (selectedTransfer != null) {
                    System.out.println("Transfer ID: " + selectedTransfer.getTransferId());
                    System.out.println("From Account ID: " + selectedTransfer.getAccountFromId());
                    System.out.println("To Account ID: " + selectedTransfer.getAccountToId());
                    System.out.println("Amount: $" + selectedTransfer.getAmount());
                    System.out.println("Transfer Type: " + getTransferType(selectedTransfer.getTransferTypeId()));
                    System.out.println("Transfer Status: " + getTransferStatus(selectedTransfer.getTransferStatusId()));
                } else {
                    System.out.println("No Transfer Found with ID: " + transferId);
                }
                break;

            case 0: break;
            default:
                System.out.println("Invalid Selection. Please choose 1, 2, or 0");

        }

    }
    // WORKING
    private void viewPendingRequests() {
        Account account = accountService.getAccountByUserId(currentUser.getUser().getId(), currentUser.getToken());
        Transfer[] transfers = transferService.getTransfersByAccountId(account.getAccountId(), currentUser.getToken());
        System.out.println("-------------------------------------------");
        System.out.println("Pending Requests: ");
        System.out.println("TransferID | To AccountID | From AccountID | Amount");
        System.out.println("-------------------------------------------");

        for (Transfer x : transfers) {
            if(x.getAccountFromId() == account.getAccountId() ){
                if (x.getTransferTypeId() == 1 && x.getTransferStatusId() == 1) {
                    System.out.print(x.getTransferId() + "    |   ");
                    System.out.print(x.getAccountToId() + "    |   ");
                    System.out.print(x.getAccountFromId() + "    |   ");
                    System.out.print("$" + x.getAmount() + "\n");
                }
           }

        }

        int transferId = 1;
        while (transferId != 0) {
            transferId = consoleService.promptForInt("\nPlease enter the TransferID for the pending request you would like to approve/reject or press 0 to exit:");
            Transfer transfer = transferService.getTransferByTransferId(transferId, currentUser.getToken());
            Account receiver = accountService.getAccountByAccountId(transfer.getAccountToId(), currentUser.getToken());
            if (transfer != null && transferId != 0) {
                System.out.println("TransferID: " + transfer.getTransferId());
                System.out.println("Request Amount: $" + transfer.getAmount());
                System.out.println("Request From AccountID: " + transfer.getAccountFromId());
                System.out.println("Request To AccountID: " + transfer.getAccountToId());
                int response = consoleService.promptForInt("Select 1 to keep pending, 2 to approve, 3 to reject:");

                transfer.setTransferStatusId(response);
                transferService.updateTransferStatus(transfer, currentUser.getToken());
                if(response == 2) {
                    if (account.getBalance().compareTo(transfer.getAmount()) < 0){
                        System.out.println("Insufficient Funds");
                        return;
                    }

                    account.setBalance(account.getBalance().subtract(transfer.getAmount()));
                    receiver.setBalance(receiver.getBalance().add(transfer.getAmount()));
                    accountService.updateAccountBalance(account, currentUser.getToken());
                    accountService.updateAccountBalance(receiver, currentUser.getToken());
                }
            }
        }
    }
    // WORKING
    private void sendBucks() {
        System.out.println("-------------------------------------------");
        System.out.println("Who would you like to send TE bucks to?");
        System.out.println("Users");
        System.out.println("User ID     UserName");
        System.out.println("-------------------------------------------");
        loopThroughUsers();
        int id = consoleService.promptForInt("Input the Users ID: ");
        BigDecimal amount = consoleService.promptForBigDecimal("Input Amount $: ");
        System.out.println(amount);

        Account from = accountService.getAccountByUserId(currentUser.getUser().getId(), currentUser.getToken());
        Account to = accountService.getAccountByUserId(id, currentUser.getToken());
        if(currentUser.getUser().getId() == id) {
            System.out.println("Cannot send TE Bucks to own account.");
            return;
        }
        BigDecimal zero = BigDecimal.valueOf(0.00);
        if(amount.compareTo(zero) <= 0) {
            System.out.println("Cannot send negative or 0 amount.");
            return;
        }
        if(from.getBalance().compareTo(amount) < 0){
            System.out.println("Insufficient Funds");
            return;
        }
        Transfer transfer = new Transfer();
        transfer.setAmount(amount);
        transfer.setTransferTypeId(2);
        transfer.setTransferStatusId(2);
        transfer.setAccountFromId(from.getAccountId());
        transfer.setAccountToId(to.getAccountId());

        Transfer returned = transferService.sendFunds(transfer, currentUser.getToken());
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        accountService.updateAccountBalance(from, currentUser.getToken());
        accountService.updateAccountBalance(to, currentUser.getToken());
        System.out.println("Funds have been Sent.");

    }

    private void requestBucks() {
        System.out.println("-------------------------------------------");
        System.out.println("Who would you like to request TE bucks from? ");
        loopThroughUsers();
        int id = consoleService.promptForInt("Input the Users ID: ");
        BigDecimal amount = consoleService.promptForBigDecimal("Input Amount: ");
        Account to = accountService.getAccountByUserId(currentUser.getUser().getId(), currentUser.getToken());
        Account from = accountService.getAccountByUserId(id, currentUser.getToken());
        if(currentUser.getUser().getId() == id) {
            System.out.println("Cannot request TE Bucks from own account.");
            return;
        }
        BigDecimal zero = BigDecimal.valueOf(0.00);
        if(amount.compareTo(zero) <= 0) {
            System.out.println("Cannot request negative or 0 amount.");
            return;
        }
        Transfer transfer =  new Transfer();
        transfer.setAccountFromId(from.getAccountId());
        transfer.setAccountToId(to.getAccountId());
        transfer.setAmount(amount);
        transfer.setTransferTypeId(1);
        transfer.setTransferStatusId(1);

        transferService.createTransfer(transfer, currentUser.getToken());
    }

    public void loopThroughUsers() {
        User[] users = userService.getAllUsers(currentUser.getToken());

        for(User x : users) {
            if(!x.getUsername().equals(currentUser.getUser().getUsername())) {
                System.out.println(x.getId() + "   " + x.getUsername());
            }
        }
    }

    public void loopingTransfers() {
        Account account = accountService.getAccountByUserId(currentUser.getUser().getId(), currentUser.getToken());
        Transfer[] transfers = transferService.getTransfersByAccountId(account.getAccountId(), currentUser.getToken());

        for (Transfer x : transfers) {
            String type = getTransferType(x.getTransferTypeId());
            String status = getTransferStatus(x.getTransferStatusId());

            System.out.println("Transfer ID: " + x.getTransferId());
            System.out.println("From Account ID: " + x.getAccountFromId());
            System.out.println("To Account ID: " + x.getAccountToId());
            System.out.println("Transfer Type: " + type);
            System.out.println("Transfer Status: " + status);
            System.out.println("Amount: $" + x.getAmount());
            System.out.println();
        }
    }
    public String getTransferStatus(int statusId) {
        switch (statusId) {
            case 1: return "Pending";
            case 2: return "Accepted";
            case 3: return "Rejected";
            default: return null;
        }
    }
    public String getTransferType(int transferTypeId) {
        switch (transferTypeId){
            case 1: return "Request";
            case 2: return "Send";
            default: return null;
        }
    }


}

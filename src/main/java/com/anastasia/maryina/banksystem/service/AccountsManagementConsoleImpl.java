package com.anastasia.maryina.banksystem.service;

import com.anastasia.maryina.banksystem.model.Bank;
import com.anastasia.maryina.banksystem.model.BankAccount;
import com.anastasia.maryina.banksystem.model.User;

import java.math.BigDecimal;
import java.util.*;

import static com.anastasia.maryina.banksystem.utils.StringConstants.*;

public class AccountsManagementConsoleImpl implements AccountsManagementConsole {

    private static final Scanner sc = new Scanner(System.in);

    private final BankService bankService;
    private final BankAccountService bankAccountService;
    private final TransactionService transactionService;

    public AccountsManagementConsoleImpl() {
        this.bankService = new BankServiceImpl();
        this.bankAccountService = new BankAccountServiceImpl();
        this.transactionService = new TransactionServiceImpl();
    }

    @Override
    public void run(User user) {
        boolean exit = false;
        while (!exit) {
            Bank bank = selectBank();
            int choice = 0;

            while (choice != 6) {
                List<BankAccount> bankAccounts = bankAccountService.findByUserAndBank(user, bank);
                printAccountsData(bankAccounts);
                displayMenuOptions();

                choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1 -> listAccountTransactions(bankAccounts);
                    case 2 -> makeTransfer(user, bankAccounts);
                    case 3 -> performDeposit(bankAccounts);
                    case 4 -> performWithdrawal(bankAccounts);
                    case 5 -> bankAccountService.createNewAccount(user, bank);
                    case 6 -> System.out.printf("Thanks for choosing %s%n", bank.getName());
                    default -> System.out.println(WRONG_CHOICE);
                }
            }

            exit = !proceedFurther();
        }
    }

    private void displayMenuOptions() {
        System.out.println("Select your next operation:");
        System.out.println(LINE_SEPARATOR);
        System.out.println("1. List account transactions");
        System.out.println("2. Make a transfer");
        System.out.println("3. Deposit");
        System.out.println("4. Withdraw");
        System.out.println("5. Open a new account");
        System.out.println("6. Exit");
        System.out.println(LINE_SEPARATOR);
    }

    private void listAccountTransactions(List<BankAccount> bankAccounts) {
        System.out.println("Choose the account number to see transactions list:");
        int accountChoice = sc.nextInt();
        BankAccount selectedAccount = bankAccounts.get(accountChoice - 1);
        transactionService.listTransactions(selectedAccount);
    }

    private void makeTransfer(User user, List<BankAccount> bankAccounts) {
        System.out.println("Select the transfer type:");
        System.out.println("1. Transfer to own account");
        System.out.println("2. Transfer to someone else's account");
        int transferType = sc.nextInt();
        sc.nextLine();

        switch (transferType) {
            case 1 -> performOwnTransfer(bankAccounts, user);
            case 2 -> performOtherTransfer(bankAccounts);
            default -> System.out.println(WRONG_CHOICE);
        }
    }

    private void performOwnTransfer(List<BankAccount> bankAccounts, User currentUser) {
        System.out.println(CHOOSE_SOURCE_ACCOUNT);
        int sourceAccountChoice = sc.nextInt();
        BankAccount sourceAccount = bankAccounts.get(sourceAccountChoice - 1);

        System.out.println("Choose the destination account:");
        List<BankAccount> allBankAccounts = bankAccountService.findByUser(currentUser);
        printAccountsData(allBankAccounts);
        int destinationAccountChoice = sc.nextInt();
        BankAccount destinationAccount = allBankAccounts.get(destinationAccountChoice - 1);

        System.out.printf("Enter the transfer amount in %s: ", sourceAccount.getCurrency());
        BigDecimal transferAmount = sc.nextBigDecimal();
        sc.nextLine();

        bankAccountService.makeTransfer(sourceAccount, destinationAccount, transferAmount);
    }

    private void performOtherTransfer(List<BankAccount> bankAccounts) {
        System.out.println(CHOOSE_SOURCE_ACCOUNT);
        int sourceAccountChoice = sc.nextInt();
        BankAccount sourceAccount = bankAccounts.get(sourceAccountChoice - 1);

        System.out.println("Enter the beneficiary's account ID:");
        String beneficiaryAccountId = sc.nextLine();
        Optional<BankAccount> beneficiaryAccount = bankAccountService.findById(UUID.fromString(beneficiaryAccountId));

        while (beneficiaryAccount.isEmpty()) {
            System.out.printf("Bank account # %s doesn't exist.%n", beneficiaryAccountId);
            System.out.println(LINE_SEPARATOR);
            System.out.println("1. Try again. ");
            System.out.println("2. Exit. ");
            int i = sc.nextInt();
            sc.nextLine();

            switch (i) {
                case 1 -> {
                    System.out.println("Set Username : ");
                    beneficiaryAccountId = sc.next();
                    beneficiaryAccount = bankAccountService.findById(UUID.fromString(beneficiaryAccountId));
                }
                case 2 -> {
                    return;
                }
                default -> System.out.println(WRONG_CHOICE);
            }
        }

        System.out.printf("Enter the transfer amount in %s: ", sourceAccount.getCurrency());
        BigDecimal transferAmount = sc.nextBigDecimal();
        sc.nextLine();

        bankAccountService.makeTransfer(sourceAccount, beneficiaryAccount.get(), transferAmount);
    }

    private void performDeposit(List<BankAccount> bankAccounts) {
        System.out.println(CHOOSE_SOURCE_ACCOUNT);
        int sourceAccountChoice = sc.nextInt();
        BankAccount sourceAccount = bankAccounts.get(sourceAccountChoice - 1);

        System.out.printf("Enter the deposit amount in %s: ", sourceAccount.getCurrency());
        BigDecimal depositAmount = sc.nextBigDecimal();
        sc.nextLine();

        bankAccountService.deposit(sourceAccount, depositAmount);
    }

    private void performWithdrawal(List<BankAccount> bankAccounts) {
        System.out.println(CHOOSE_SOURCE_ACCOUNT);
        int sourceAccountChoice = sc.nextInt();
        BankAccount sourceAccount = bankAccounts.get(sourceAccountChoice - 1);

        System.out.printf("Enter the withdrawal amount in %s: ", sourceAccount.getCurrency());
        BigDecimal withdrawalAmount = sc.nextBigDecimal();
        sc.nextLine();

        bankAccountService.withdraw(sourceAccount, withdrawalAmount);
    }

    private boolean proceedFurther() {
        System.out.println("Do you want to proceed:");
        System.out.println(LINE_SEPARATOR);
        System.out.println("1. Yes");
        System.out.println("2. No");
        System.out.println(LINE_SEPARATOR);
        int choice = sc.nextInt();
        sc.nextLine();

        switch (choice) {
            case 1 -> {
                return true;
            }
            case 2 -> {
                return false;
            }
            default -> {
                System.out.println(WRONG_CHOICE);
                return false;
            }
        }
    }

    private void printAccountsData(List<BankAccount> bankAccounts) {
        System.out.println("Accounts list: ");
        for (int i = 0; i < bankAccounts.size(); i++) {
            BankAccount account = bankAccounts.get(i);
            System.out.printf("%d. Bank: %s, Account type: %s, Currency: %s, total amount: %s. Account number: %s%n",
                    i + 1, account.getOwner().getBank().getName(), account.getOwner().getClientType(), account.getCurrency(), account.getTotalAmount(), account.getId());
        }
    }

    private Bank selectBank() {
        System.out.println("Select the bank or add a new one:");
        List<Bank> banks = bankService.findAll();
        Map<Integer, Bank> bankMap = new HashMap<>();

        for (int i = 0; i < banks.size(); i++) {
            Bank bank = banks.get(i);
            bankMap.put(i + 1, bank);
            System.out.printf("%d. %s%n", i + 1, bank.getName());
        }
        System.out.printf("0. Add a new bank.%n");
        int bankChoice;
        while (true) {
            bankChoice = sc.nextInt();
            if (bankChoice >= 0 && bankChoice <= banks.size()) {
                break;
            } else {
                System.out.println("Invalid input. Please enter a valid bank number.");
            }
        }
        if (bankChoice == 0) {
            return bankService.createNewBank();
        }
        return bankMap.get(bankChoice);
    }

}

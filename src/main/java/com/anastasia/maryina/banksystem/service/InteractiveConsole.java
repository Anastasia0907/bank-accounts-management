package com.anastasia.maryina.banksystem.service;

import com.anastasia.maryina.banksystem.exceptions.BadCredentialsException;
import com.anastasia.maryina.banksystem.model.User;

import java.util.Scanner;

public class InteractiveConsole implements InteractiveConsoleService {

    private static final Scanner sc = new Scanner(System.in);

    private final UserService userService;
    private final AccountsManagementConsole accountsManagementConsole;

    public InteractiveConsole() {
        this.userService = new UserServiceImpl();
        this.accountsManagementConsole = new AccountsManagementConsole();
    }

    @Override
    public void run() {

        int choice = 0;
        while (choice != 3) {

            System.out.println("\n-------------------");
            System.out.println("Welcome to online banking 'SimonBank'");
            System.out.println("-------------------\n");
            System.out.println("1. Register.");
            System.out.println("2. Login.");
            System.out.println("3. Exit.");
            System.out.print("\nEnter your choice : ");
            choice = sc.nextInt();
            sc.nextLine();
            switch (choice) {
                case 1 -> userService.registerUser();
                case 2 -> {
                    User user;
                    try {
                        user = userService.login();
                    } catch (BadCredentialsException e) {
                        break;
                    }
                    System.out.printf("Hello, %s%n", user.getName());
                    accountsManagementConsole.manageBankAccounts(user);
                }
                case 3 -> System.out.println("\nThank you for choosing SimonBank.");
                default -> System.out.println("Wrong choice !");
            }
        }
    }

}

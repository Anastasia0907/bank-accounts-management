package com.anastasia.maryina.banksystem.service;

import com.anastasia.maryina.banksystem.dao.UserDAO;
import com.anastasia.maryina.banksystem.exceptions.BadCredentialsException;
import com.anastasia.maryina.banksystem.model.User;

import java.util.Scanner;

public class UserServiceImpl implements UserService {

    private static final Scanner sc = new Scanner(System.in);

    private final UserDAO userDAO;

    public UserServiceImpl() {
        this.userDAO = new UserDAO();
    }

    @Override
    public User registerUser() {

        System.out.print("Enter your Name : ");
        String firstName = sc.nextLine();
        System.out.print("Enter your personal identifier : ");
        String pid = sc.nextLine();
        while (userDAO.existsByPid(pid)) {
            System.out.println("User with this PID already exists.");
            System.out.println("-------------------");
            System.out.println("1. Try again. ");
            System.out.println("2. Exit. ");
            var choice = sc.nextInt();
            sc.nextLine();
            switch (choice) {
                case 1:
                    System.out.println("Set personal identifier : ");
                    pid = sc.next();
                    break;
                case 2:
                    return null;
                default:
                    System.out.println("Wrong choice !");
            }
        }
        System.out.println("Set Username : ");
        String username = sc.next();
        while (userDAO.existsByUsername(username)) {
            System.out.println("Username already exists.");
            System.out.println("-------------------");
            System.out.println("1. Set again. ");
            System.out.println("2. Exit. ");
            var choice = sc.nextInt();
            sc.nextLine();
            switch (choice) {
                case 1:
                    System.out.println("Set Username : ");
                    username = sc.next();
                    break;
                case 2:
                    return null;
                default:
                    System.out.println("Wrong choice !");
            }
        }
        System.out.println("Set a password:");
        String password = sc.next();
        sc.nextLine();

        var user = new User(firstName, username, password, pid);
        user = userDAO.save(user);
        return user;
    }

    @Override
    public User login() {
        System.out.println("Enter username : ");
        var username = sc.next();
        sc.nextLine();
        System.out.println("Enter password : ");
        var password = sc.next();
        sc.nextLine();
        var user = userDAO.findByUsernameAndPassword(username, password);
        if (user.isEmpty()) {
            System.out.println("Wrong username/password.");
            throw new BadCredentialsException();
        }
        return user.get();
    }
}

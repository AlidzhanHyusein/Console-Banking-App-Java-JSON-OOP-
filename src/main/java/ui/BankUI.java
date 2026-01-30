package ui;

import com.sun.tools.javac.Main;
import model.Account;
import model.Transaction;
import service.BankService;

import java.io.Console;
import java.util.List;
import java.util.Scanner;

public class BankUI {
    private final Scanner scanner = new Scanner(System.in);
    private final BankService bankService = new BankService();
    private Account loggedInAccount;


    public void start(){
        bankService.loadData();
        startMenu();
    }


    private void startMenu() {
        while (true) {
            System.out.println("\n=== WELCOME TO THE BANK SYSTEM ===");
            System.out.println("1. Login");
            System.out.println("2. Create account");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> loginUI();
                case "2" -> createAccountUI();
                case "3" -> exitProgram();
                default -> System.out.println("Invalid option");
            }

            if (loggedInAccount != null) {
                mainMenu();
            }
        }
    }

    private void mainMenu() {
        while (true) {
            System.out.println("\n=== BANK SYSTEM ===");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Transfer");
            System.out.println("4. Check balance");
            System.out.println("5. Transaction history");
            System.out.println("6. Exit");
            System.out.print("Choose option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> createAccountUI();
                case "2" -> depositUI();
                case "3" -> withdrawUI();
                case "4" -> transferUI();
                case "5" -> balanceUI();
                case "6" -> transactionHistoryUI();
                case "7" -> exitProgram();
                default -> System.out.println("Invalid option");
            }
        }
    }

    private void createAccountUI() {
        try {
            System.out.print("Enter account number: ");
            String number = scanner.nextLine();

            System.out.print("Enter holder name: ");
            String name = scanner.nextLine();

            System.out.print("Enter initial balance: ");
            double balance = Double.parseDouble(scanner.nextLine());

            System.out.print("Enter Pin:");
            String pin = readMaskedPin();

            bankService.createAccount(number, name, balance,pin);
            bankService.saveData();

            System.out.println("Account created successfully!");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void loginUI() {
        while (true) {
            try {
                System.out.print("Enter account number (or 0 to go back): ");
                String number = scanner.nextLine();

                if (number.equals("0")) {
                    return;
                }

                System.out.print("Enter PIN: ");
                String pin = readMaskedPin();

                loggedInAccount = bankService.login(number, pin);
                System.out.println("Login successful!");
                return;

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }



    private void depositUI(){
        try {

            System.out.print("Enter amount: ");
            double amount = Double.parseDouble(scanner.nextLine());
            bankService.deposit(loggedInAccount.getAccountNumber(), amount);
            bankService.saveData();
        } catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void withdrawUI(){
        try {

            System.out.print("Enter amount: ");
            double amount = Double.parseDouble(scanner.nextLine());
            bankService.withdraw(loggedInAccount.getAccountNumber(), amount);
            bankService.saveData();
        } catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void transferUI(){
        try {
            System.out.print("To account: ");
            String toAccount = scanner.nextLine();

            System.out.print("Amount: ");
            double amount = Double.parseDouble(scanner.nextLine());
            bankService.transfer(loggedInAccount.getAccountNumber(), toAccount,amount);
            bankService.saveData();
        } catch (Exception e ){
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void balanceUI(){
        try {

            System.out.print("Balance: " + loggedInAccount.getBalance());
        } catch (Exception e ){
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void transactionHistoryUI(){
        try {
            bankService.getTransactionHistory(loggedInAccount.getAccountNumber());
        } catch (Exception e ){
            System.out.println("Error: " + e.getMessage());
        }
    }
    private void exitProgram() {
        bankService.saveData();
        System.out.println("Goodbye!");
        System.exit(0);
    }

    private String readMaskedPin() {
        try {
            Console console = System.console();
            if (console != null) {
                char[] pinArray = console.readPassword("Enter PIN: ");
                return new String(pinArray);
            }
        } catch (Exception ignored) {}

        return readMaskedPinFallback();
    }

    private String readMaskedPinFallback() {
        StringBuilder pin = new StringBuilder();
        try {
            while (true) {
                int ch = System.in.read();

                if (ch == '\n' || ch == '\r') {
                    break;
                }

                if (ch == 8 || ch == 127) { // backspace
                    if (pin.length() > 0) {
                        pin.deleteCharAt(pin.length() - 1);
                        System.out.print("\b \b");
                    }
                } else {
                    pin.append((char) ch);
                    System.out.print("*");
                }
            }
        } catch (Exception ignored) {}

        System.out.println();
        return pin.toString();
    }


}

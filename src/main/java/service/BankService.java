package service;

import adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import exception.InsufficientFundsException;
import exception.InvalidAccountException;
import exception.InvalidAmountException;
import model.Account;
import model.Transaction;
import model.TransactionTypeEnum;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

public class BankService {

    private Map<String, Account> accounts = new HashMap<>();

    private String generateId() {
        return UUID.randomUUID().toString();
    }


    public void createAccount(String accountNumber,String holderName,Double initialBalance,String pin) throws InvalidAccountException,InvalidAmountException {

        if (accounts.containsKey(accountNumber)) {
            throw new InvalidAccountException("This account already exist");
        }

        if(initialBalance == null || initialBalance < 0){
            throw new InvalidAmountException("Amount cannot be null or less than 0");
        }

        if(holderName == null || holderName.isBlank()){
            throw new InvalidAccountException("Holder name must not blank");
        }
        Account account = new Account(accountNumber,holderName,initialBalance,pin);
        accounts.put(accountNumber,account);
    }

    public Account login(String accountNumber,String pin) throws InvalidAccountException {
        Account account = getAccount(accountNumber);


        if(account.isLocked()){
            throw new InvalidAccountException("This account is locked due to too many failed attempts.");
        }
        if(!account.getPin().equals(pin)){
            int attempts = account.getFailedAttempts() + 1;
            account.setFailedAttempts(attempts);

            if(attempts >= 3){
                account.setLocked(true);
                saveData();
                throw new InvalidAccountException("Account locked after 3 failed attempts.");
            }

            saveData();
            throw new InvalidAccountException("Incorrect PIN. Attempts left: " + (3 - attempts));
        }

        saveData();
        account.setFailedAttempts(0);
        return account;
    }


    public Account getAccount(String accountNumber) throws InvalidAccountException{
        if(accountNumber == null){
            throw new InvalidAccountException("This account number must not be empty");
        }
        if(accountNumber.isBlank()){
            throw new InvalidAccountException("This account number must not be empty");
        }
        if(!accounts.containsKey(accountNumber)){
            throw new InvalidAccountException("This account does not exist");
        }

        return accounts.get(accountNumber);
    }

    public void deposit(String accountNumber,Double amount) throws InvalidAccountException,InvalidAmountException {
        if(amount == null || amount <= 0){
            throw new InvalidAmountException("Amount must be greater than zero");
        }

        Account account =getAccount(accountNumber);
        account.increaseBalance(amount);
        Transaction t = new Transaction(generateId(),TransactionTypeEnum.DEPOSIT,amount,LocalDateTime.now());
        account.addTransaction(t);
    }


    public double withdraw(String accountNumber,Double amount) throws InvalidAmountException,InvalidAccountException,InsufficientFundsException{
        if(amount == null || amount <= 0){
            throw new InvalidAmountException("Amount must be greater than zero");
        }

        Account account = getAccount(accountNumber);

        if(account.getBalance() < amount){
            throw new InsufficientFundsException("Account balance must be greater or equal to amount of withdrawal");
        }

        account.decreaseBalance(amount);
        Transaction t = new Transaction(generateId(),TransactionTypeEnum.WITHDRAWAL,amount,LocalDateTime.now());

        account.addTransaction(t);
        return amount;
    }

    public void transfer(String fromAccountNumber,String toAccountNumber,Double amount) throws InvalidAmountException,InvalidAccountException,InsufficientFundsException {
        if(amount == null || amount <= 0){
            throw new InvalidAmountException("Amount must be greater than zero");
        }
        Account fromAccount = getAccount(fromAccountNumber);
        Account toAccount = getAccount(toAccountNumber);
        if(fromAccount.getBalance() < amount){
            throw new InsufficientFundsException("Insufficient funds for transfer");
        }

        fromAccount.decreaseBalance(amount);
        toAccount.increaseBalance(amount);
        Transaction t = new Transaction(generateId(),TransactionTypeEnum.OUTGOING,amount,LocalDateTime.now());
        Transaction y = new Transaction(generateId(),TransactionTypeEnum.INCOMING,amount,LocalDateTime.now());

        fromAccount.addTransaction(t);
        toAccount.addTransaction(y);

    }

    public Double getBalance(String accountNumber) throws Exception {
        Account account = getAccount(accountNumber);
        return account.getBalance();
    }

    public List<Transaction> getTransactionHistory(String accountNumber) throws Exception {
        Account account = getAccount(accountNumber);
        return account.getTransactions();
    }

    public void saveData() {
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .setPrettyPrinting()
                    .create();

            String json = gson.toJson(accounts);
            Files.writeString(Path.of("bank.json"), json);

        } catch (Exception e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }


    public void loadData() {
        try {
            Path path = Path.of("bank.json");
            if (!Files.exists(path)) {
                return;
            }

            String json = Files.readString(path);

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .create();

            Type type = new TypeToken<Map<String, Account>>(){}.getType();
            accounts = gson.fromJson(json, type);

            if (accounts == null) {
                accounts = new HashMap<>();
            }

        } catch (Exception e) {
            System.out.println("Error loading data: " + e.getMessage());
            accounts = new HashMap<>();
        }
    }



}

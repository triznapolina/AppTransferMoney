package model;

import exception.InsufficientFundsException;

public class Account {
    private String accountNumber;
    private int balance;

    public Account(String accountNumber, int balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public int getBalance() {
        return balance;
    }

    public void debit(int amount) {
        if (amount > balance) {
            throw new InsufficientFundsException("Недостаточно средств на счете " + accountNumber);
        }
        balance -= amount;
    }

    public void credit(int amount) {
        balance += amount;
    }
}

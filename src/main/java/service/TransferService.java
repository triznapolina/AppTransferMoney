package service;

import exception.InvalidAccountException;
import exception.InvalidTransferAmountException;
import model.Account;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class TransferService {
    private Map<String, Account> accounts = new HashMap<>();

    public void addAccount(Account account) {
        accounts.put(account.getAccountNumber(), account);
    }

    public void transfer(String fromAccount, String toAccount, int amount) {
        if (!accounts.containsKey(fromAccount)) {
            throw new InvalidAccountException("Счет " + fromAccount + " не существует.");
        }
        if (!accounts.containsKey(toAccount)) {
            throw new InvalidAccountException("Счет " + toAccount + " не существует.");
        }
        if (amount <= 0) {
            throw new InvalidTransferAmountException("Сумма перевода должна быть положительной.");
        }

        Account sender = accounts.get(fromAccount);
        Account receiver = accounts.get(toAccount);

        sender.debit(amount);
        receiver.credit(amount);
    }

    public Map<String, Account> getAccounts() {
        return accounts;
    }

    public void saveAccountsToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Map.Entry<String, Account> entry : accounts.entrySet()) {
                writer.write(entry.getKey() + " " + entry.getValue().getBalance());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении данных о счетах: " + e.getMessage());
        }
    }

    public void loadAccountsFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length == 2) {
                    String accountNumber = parts[0];
                    int balance = Integer.parseInt(parts[1]);
                    addAccount(new Account(accountNumber, balance));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Ошибка при загрузке данных о счетах: " + e.getMessage());
        }
    }
}

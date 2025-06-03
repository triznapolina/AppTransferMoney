package controller;

import service.FileProcesser;
import service.TransferService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class TransferController {
    private TransferService transferService = new TransferService();
    private FileProcesser fileProcessor = new FileProcesser();
    private final String ACCOUNTS_FILE = "src/main/java/files_other/information_account/information_account.txt";

    public void start() {
        transferService.loadAccountsFromFile(ACCOUNTS_FILE);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Введите 1 для парсинга файлов, 2 для вывода списка всех переводов или 3 для выхода: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    parseFiles();
                    transferService.saveAccountsToFile(ACCOUNTS_FILE);
                    break;
                case 2:
                    displayReportFromFile("src/main/java/files_other/archive/report.txt");
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Некорректный ввод.");
            }
        }
    }


    private void parseFiles() {
        String inputDir = "src/main/java/files_other/input";
        String archiveDir = "src/main/java/files_other/archive";
        String reportFilename = "report.txt";

        try {
            fileProcessor.processTransferFiles(inputDir, archiveDir, reportFilename, transferService);
        } catch (IOException e) {
            System.err.println("Ошибка обработки файлов: " +e.getMessage());
        }
    }

    public void displayReportFromFile(String filePath) {
        if (!Files.exists(Paths.get(filePath))) {
            System.out.println("Файл с отчетом не найден. Переводы не производились.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("->");
                if (parts.length == 2) {
                    String fromAccount = parts[0].trim();
                    String toAccountAndBalance = parts[1].trim();
                    String[] toParts = toAccountAndBalance.split(":");
                    if (toParts.length == 2) {
                        String toAccount = toParts[0].trim();
                        try {
                            int balance = Integer.parseInt(toParts[1].trim());
                            System.out.println(fromAccount + " -> " + toAccount + ": " + balance);
                        } catch (NumberFormatException e) {
                            System.err.println("Ошибка парсинга баланса в строке: " + line);
                        }
                    } else {
                        System.err.println("Неверный формат строки: " + line);
                    }
                } else {
                    System.err.println("Неверный формат строки: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        }
    }
}

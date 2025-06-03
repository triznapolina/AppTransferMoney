package service;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileProcesser {

    private static final Pattern PATTERN = Pattern.compile("(\\d{5}-\\d{5})\\s+(\\d{5}-\\d{5})\\s+(\\d+)");

    public void processTransferFiles(String inputDir, String archiveDir, String reportFilename, TransferService transferService) throws IOException {
        File inputDirectory = new File(inputDir);
        File archiveDirectory = new File(archiveDir);

        if (!inputDirectory.exists() || !inputDirectory.isDirectory()) {
            throw new IllegalArgumentException("Входная директория не существует или не является директорией: " + inputDir);
        }

        File[] files = inputDirectory.listFiles((dir, name) -> name.endsWith(".txt"));

        System.out.println("Найдено файлов '.txt': " + (files == null ? 0 : files.length));

        if (files == null || files.length == 0) {
            System.out.println("Нет подходящих файлов для обработки.");
            return;
        }

        if (!archiveDirectory.exists() && !archiveDirectory.mkdirs()) {
            System.out.println("Не удалось создать директорию архива: " + archiveDir);
            return;
        }

        File reportFile = new File(archiveDirectory, reportFilename);
        try (BufferedWriter reportWriter = new BufferedWriter(new FileWriter(reportFile))) {
            for (File file : files) {
                processFile(file, reportWriter, transferService);
            }
        }
    }


    private void processFile(File file, BufferedWriter reportWriter, TransferService transferService) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = PATTERN.matcher(line);
                if (matcher.matches()) {
                    String fromAccount = matcher.group(1);
                    String toAccount = matcher.group(2);
                    int balance;
                    try {
                        balance = Integer.parseInt(matcher.group(3));
                        transferService.transfer(fromAccount, toAccount, balance);
                        reportWriter.write(fromAccount + " -> " + toAccount + ": " + balance + System.lineSeparator());
                    } catch (NumberFormatException e) {
                        System.err.println("Ошибка преобразования баланса в число в строке: " + line);
                    } catch (Exception e) {
                        System.err.println("Ошибка обработки перевода в строке: " + line + " : " + e.getMessage());
                    }
                } else {
                    System.out.println("Строка пропущена, так как не соответствует шаблону: " + line);
                    continue;
                }
            }
        }
    }



}

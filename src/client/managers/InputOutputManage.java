package client.managers;

import client.Client;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

/**
 * Управляет вводом-выводом.
 * Поддерживает два режима: чтение с консоли (по умолчанию) и чтение из файла (скрипты).
 * Также отвечает за вывод сообщений пользователю и отслеживание ошибок при выполнении скриптов.
 * @author Алиса
 */

public class InputOutputManage {
    Scanner scan;
    Scanner fileScan;
    private boolean readFromFile;
    private boolean scriptHasError;
    private List<String> openedFiles = new ArrayList<>();
    private Stack<Scanner> scannerStack = new Stack<>();
    private Stack<Boolean> fileReadingStack = new Stack<>();

    public InputOutputManage(){
        scan = new Scanner(System.in);
        readFromFile = false;
        scriptHasError = false;
    }
    public void write(String line){
        System.out.println(line);
    }

    /**
     * Считывает одну строку ввода.
     * Источник ввода зависит от режима: консоль или файл
     * @return считанная строка
     */
    public String read(){
        if (readFromFile) {
            if (fileScan.hasNextLine()) {
                return fileScan.nextLine();
            } else {
                write("Файл скрипта закончился. Возврат в консоль.");
                stopFileReading("");
                return "";
            }
        }
        else {
            if (scan.hasNextLine()) {
                return scan.nextLine();
            } else {
                Client.inout.write("(Ctrl + D), ввод закончен.");
                System.exit(0);
                return "";
            }
        }
    }

    /** Переключает режим ввода на чтение из указанного файла.
     * Если файл не найден, выводит сообщение об ошибке и остаётся в режиме консоли
     * @param filename имя файла для чтения (путь к файлу)
     */

    public void startFileReading(String filename) {
        if (openedFiles.contains(filename)) {
            write("Ошибка: рекурсивный вызов скрипта! Файл " + filename + " уже открыт.");
            write("Стек открытых файлов: " + openedFiles);
            scriptHasError = true;
            readFromFile = false;
            stopFileReading(filename);
            return;
        }
        try {
            Scanner newScan = new Scanner(new File(filename));
            scannerStack.push(fileScan);
            fileReadingStack.push(readFromFile);

            fileScan = newScan;
            openedFiles.add(filename);
            readFromFile = true;
            scriptHasError = false;
        } catch (FileNotFoundException e) {
            write("Файл не найден: " + filename);
            openedFiles.clear();
            readFromFile = false;
        }
    }

    /**
     * Завершает режим чтения из файла.
     */

    public void stopFileReading(String filename) {
        if (fileScan != null) {
            fileScan.close();
        }
        openedFiles.remove(filename);

        if (!scannerStack.isEmpty()) {
            fileScan = scannerStack.pop();
            readFromFile = fileReadingStack.pop();
            scannerStack.clear();
        } else {
            fileScan = null;
            readFromFile = false;
            if (!openedFiles.isEmpty()) {
                openedFiles.clear();
            }
        }
    }
    /**
     * Проверяет, есть ли ещё строки для чтения в текущем источнике.
     * В режиме файла проверяет, остались ли непрочитанные строки.
     * @return true, если есть следующая строка, иначе false
     */

    public boolean hasNextLine() {
        if (readFromFile) {
            return fileScan.hasNextLine();
        }
        return true;
    }

    /**
     * Устанавливает флаг ошибки выполнения скрипта
     * Используется для сигнализации о том, что в процессе выполнения скрипта произошла ошибка
     * @param error true, если ошибка произошла, иначе false
     */

    public void setScriptError(boolean error) {
        scriptHasError = error;
    }

    public boolean isScriptHasError() {
        return scriptHasError;
    }

    public void closeScan(){
        scan.close();
    }

    public boolean isReadingFromFile() {
        return readFromFile;
    }

    public List<String> getOpenedFiles() {
        return openedFiles;
    }
}
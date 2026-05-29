package client.managers;

import client.Client;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

/**
 * Класс для управления потоками ввода и вывода (консоль и файлы).
 * Реализует логику переключения между интерактивным режимом и чтением скриптов,
 * а также предотвращает бесконечную рекурсию при вызове файлов.
 *
 * @author Алиса
 * @version 1.0
 */
public class InputOutputManage {

    /** Сканнер для чтения данных из стандартного потока ввода (консоли) */
    private Scanner scan;

    /** Текущий буферизированный поток для чтения из файла */
    private BufferedReader fileScan;

    /** Флаг, указывающий, происходит ли чтение из файла в данный момент */
    private boolean readFromFile;

    /** Флаг наличия ошибки при исполнении скрипта */
    private boolean scriptHasError;

    /** Список путей к файлам, открытым в данный момент (для контроля рекурсии) */
    private final List<String> openedFiles = new ArrayList<>();

    /** Стек для хранения потоков чтения при вложенных вызовах скриптов */
    private final Stack<BufferedReader> readerStack = new Stack<>();

    /** Стек имен файлов для корректного управления историей открытых ресурсов */
    private final Stack<String> fileNameStack = new Stack<>();

    /**
     * Инициализирует менеджер ввода-вывода.
     * Устанавливает стандартный сканнер и сбрасывает флаги чтения файлов.
     */
    public InputOutputManage() {
        scan = new Scanner(System.in);
        readFromFile = false;
        scriptHasError = false;
    }

    /**
     * Выводит строку в стандартный поток вывода.
     *
     * @param line текст для вывода
     */
    public void write(String line) {
        System.out.println(line);
    }

    /**
     * Считывает следующую строку данных.
     * Если активирован режим чтения из файла, берет строку из {@link #fileScan}.
     * При достижении конца файла автоматически возвращается к предыдущему ридеру или консоли.
     *
     * @return считанная строка или пустая строка при завершении работы (Ctrl+D)
     */
    public String read() {
        if (readFromFile) {
            try {
                String line = fileScan.readLine();
                if (line != null) {
                    return line;
                } else {
                    write("Файл скрипта закончился. Возврат назад.");
                    stopFileReading("");
                    return read();
                }
            } catch (IOException e) {
                write("Ошибка чтения файла: " + e.getMessage());
                stopFileReading("");
                return read();
            }
        } else {
            if (scan.hasNextLine()) {
                return scan.nextLine();
            } else {
                Client.inout.write("(Ctrl + D), ввод закончен.");
                System.exit(0);
                return "";
            }
        }
    }

    /**
     * Инициирует процесс чтения команд из указанного файла.
     * Проверяет файл на наличие циклической рекурсии. Если файл уже открыт выше по стеку,
     * устанавливает флаг ошибки и прерывает операцию.
     *
     * @param filename путь к файлу со скриптом
     */
    public void startFileReading(String filename) {
        if (openedFiles.contains(filename)) {
            write("Ошибка: рекурсивный вызов скрипта! Файл " + filename + " уже открыт.");
            write("Стек открытых файлов: " + openedFiles);
            scriptHasError = true;
            return;
        }

        try {
            FileInputStream fis = new FileInputStream(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader newReader = new BufferedReader(isr);

            if (readFromFile && fileScan != null) {
                readerStack.push(fileScan);
            }

            fileScan = newReader;
            openedFiles.add(filename);
            fileNameStack.push(filename);

            readFromFile = true;
            scriptHasError = false;

        } catch (FileNotFoundException e) {
            write("Файл не найден: " + filename);
        }
    }

    /**
     * Завершает чтение текущего файла и восстанавливает предыдущее состояние ввода.
     *
     * @param filename имя закрываемого файла (может быть пустым при автоматическом закрытии)
     */
    public void stopFileReading(String filename) {
        try {
            if (fileScan != null) {
                fileScan.close();
                fileScan = null;
            }
        } catch (IOException e) {
            write("Ошибка при закрытии файла.");
        }

        if (!fileNameStack.isEmpty()) {
            openedFiles.remove(fileNameStack.pop());
        } else if (!filename.isEmpty()) {
            openedFiles.remove(filename);
        }

        if (!readerStack.isEmpty()) {
            fileScan = readerStack.pop();
            readFromFile = true;
        } else {
            fileScan = null;
            readFromFile = false;
            scriptHasError = false;
        }
    }

    /** @return {@code true}, если есть данные для чтения (в файле или консоли) */
    public boolean hasNextLine() {
        if (readFromFile) {
            if (fileScan == null) return false;
            try {
                fileScan.mark(1);
                int nextChar = fileScan.read();
                fileScan.reset();
                return nextChar != -1;
            } catch (IOException e) {
                return false;
            }
        }
        return scan.hasNextLine();
    }

    /** @param error состояние ошибки скрипта для установки */
    public void setScriptError(boolean error) {
        scriptHasError = error;
    }

    /** @return {@code true}, если в процессе выполнения текущего скрипта возникла ошибка */
    public boolean isScriptHasError() {
        return scriptHasError;
    }

    /** Закрывает основной сканнер консоли. */
    public void closeScan() {
        scan.close();
    }

    /** @return {@code true}, если текущий источник данных — файл */
    public boolean isReadingFromFile() {
        return readFromFile;
    }
}

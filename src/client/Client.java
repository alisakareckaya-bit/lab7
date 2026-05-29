package client;

import client.managers.*;
import common.CommandPacket;
import common.ResponsPacket;
import common.Movie;

import java.io.IOException;

/**
 * Главный класс клиента, управляющий жизненным циклом приложения,
 * обработкой адреса подключения и консольным циклом ввода-вывода.
 */
public class Client {
    public static InputOutputManage inout = new InputOutputManage();
    public static ParserManager parser = new ParserManager();

    public static ConnectManager connectManager;
    public static ComandManager coMan;

    private static String host = "127.0.0.1";
    private static int port = 8885;

    private static void initializeConnectionAddress(String[] hostAndPortArgs) {
        if (hostAndPortArgs.length == 0) {
            Client.inout.write("Не были введены порт и хост. Пожалуйста, введите их сейчас.");

            Client.inout.write("Введите хост (стандартный: " + host + "):");
            String inputHost = Client.inout.read().trim();
            if (!inputHost.isEmpty()) {
                host = inputHost;
            }
            Client.inout.write("Введите порт (стандартный: " + port + "):");
            String inputPort = Client.inout.read().trim();
            if (!inputPort.isEmpty()) {
                try {
                    port = Integer.parseInt(inputPort);
                } catch (NumberFormatException e) {
                    Client.inout.write("Неверный формат порта. Использован стандартный: " + port);
                }
            }

            Client.inout.write("Подключение к серверу " + host + ":" + port);
            return;
        }

        if (hostAndPortArgs.length == 1) {
            try {
                port = Integer.parseInt(hostAndPortArgs[0]);
            } catch (NumberFormatException e) {
                host = hostAndPortArgs[0];
            }
        } else {
            try {
                port = Integer.parseInt(hostAndPortArgs[0]);
                host = hostAndPortArgs[1];
            } catch (NumberFormatException e) {
                try {
                    port = Integer.parseInt(hostAndPortArgs[1]);
                    host = hostAndPortArgs[0];
                } catch (NumberFormatException ex) {
                    Client.inout.write("Неправильно были введены порт и хост, выбрано стандартное значение - " + port + ", " + host);
                }
            }
        }
        Client.inout.write("Выбрано значение - " + port + ", " + host);
    }

    public static void main(String[] args) {
        initializeConnectionAddress(args);

        connectManager = new ConnectManager(host, port);
        coMan = new ComandManager(connectManager);

        if (!testConnection()) {
            inout.write("Не удалось подключиться к серверу " + host + ":" + port);
            inout.write("Проверьте, запущен ли сервер, и правильность хоста/порта.");
            inout.write("Программа будет завершена.");
            System.exit(1);
        }

        inout.write("Подключение успешно!");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (connectManager != null) connectManager.close();
            } catch (IOException ignored) {}
        }));

        while (true) {
            inout.write("Введите команду:");
            String line = inout.read();
            if (line == null) break;

            line = line.trim();
            if (line.isEmpty()) continue;

            CommandPacket commandPacket = parser.parse(line);
            if (commandPacket != null) {
                run(commandPacket);
            } else {
                String mainCommand = line.split(" ")[0];
                if (!parser.getCommands().containsKey(mainCommand.toLowerCase())) {
                    inout.write("Команда '" + mainCommand + "' не распознана. Введите 'help' для просмотра списка доступных команд.");
                }
            }
        }
    }

    public static void run(CommandPacket commandPacket) {
        if (commandPacket == null) {
            inout.write("Такой команды не существует");
            return;
        }

        try {
            ResponsPacket responsPacket = coMan.execute(commandPacket);

            if (responsPacket == null) {
                return;
            }

            if (responsPacket.getData() != null) {
                if ("show".equals(commandPacket.getType()) || "filter_less_than_minimal_point".equals(commandPacket.getType())) {
                    @SuppressWarnings("unchecked")
                    java.util.Stack<Movie> movieStack = (java.util.Stack<Movie>) responsPacket.getData();

                    for (Movie movie : movieStack) {
                        Client.inout.write("Фильм [ID: " + movie.getId() + "] -> " + movie.toString());
                    }
                } else {
                    Client.inout.write(responsPacket.getData().toString());
                }
            } else {
                if (responsPacket.getMessage() != null) {
                    Client.inout.write(responsPacket.getMessage());
                }
            }
        } catch (Exception e) {
            Client.inout.write("Непредвиденная ошибка при обработке команды: " + e.getMessage());
        }
    }

    private static boolean testConnection() {
        try {
            CommandPacket testPacket = new CommandPacket("info", null, null);
            ResponsPacket response = coMan.execute(testPacket);
            return response != null && !"Таймаут".equals(response.getMessage());
        } catch (Exception e) {
            return false;
        }
    }
}
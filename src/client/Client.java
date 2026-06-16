package client;

import client.managers.*;
import common.CommandPacket;
import common.ResponsPacket;
import common.Movie;

import java.util.List;

/**
 * Главный класс приложения.
 * Отвечает за запуск программы, настройку адреса сервера,
 * регистрацию/вход в систему и бесконечный цикл обработки команд.
 */

public class Client {
    public static InputOutputManage inout = new InputOutputManage();
    public static ParserManager parser = new ParserManager();

    public static ConnectManager connectManager;
    public static ComandManager coMan;

    private static String host = "127.0.0.1";
    private static int port = 8885;
    private static String lgn = null;
    private static String pw = null;

    /**
     * Разбирается с хостом и портом для подключения.
     * Если при запуске программы ничего не передали, то интерактивно
     * спрашивает их через консоль или ставит стандартные значения.
     */

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

    /**
     * Точка старта программы. Инициализирует сеть, проверяет,
     * живой ли сервер, и если всё ок — заставляет авторизоваться
     * и начинает бесконечно принимать команды из консоли.
     */

    public static void main(String[] args) {
        initializeConnectionAddress(args);
        connectManager = new ConnectManager(host,port);
        coMan = new ComandManager(connectManager);
        inout.write("Проверка соединения с сервером...\n");
        if (!testConnection()) {
            inout.write("Ошибка: Сервер недоступен по адресу " + host + ":" + port + ".\n");
            inout.write("Убедитесь, что сервер запущен, и перезапустите клиент.\n");
            try {
                connectManager.close();
            } catch (Exception ignored) {}
            return;
        }
        inout.write("Соединение с сервером успешно установлено!\n");
        while (lgn == null){
            entryAccount();
        }

        while (true){
            inout.write("Введите команду:");
            String line = inout.read().trim();
            CommandPacket commandPacket= parser.parse(line, lgn, pw);
            run(commandPacket);

        }
    }

    /**
     * Берет готовую команду, отправляет её на сервер через менеджер чанков
     * и красиво выводит результат. Если это команда show, то сама
     * сортирует полученные фильмы по их длине.
     */

    public static void run(CommandPacket commandPacket) {
        if (commandPacket != null) {
            try {
                ResponsPacket responsePacket = coMan.execute(commandPacket);
                if (responsePacket.getMessage() != null ) {
                    if (responsePacket.getData()!=null) {
                        if (responsePacket.getData().equals(9000)) {
                            Client.inout.write("Хотите ли вы перезайти в аккаунт или заново зарегистрироваться? Yes/No");
                            String ans = CheckValue.checkValuesNull("ответ").toLowerCase();
                            if (ans.equals("yes")) {
                                entryAccount();
                                return;

                            }
                        }
                    }
                    if (commandPacket.getType().equals("show")){
                        if (responsePacket.getData() instanceof List<?>) {
                            List<Movie> movies = (List<Movie>) responsePacket.getData();
                            if (movies.isEmpty()) {
                                Client.inout.write("Коллекция пуста");
                            }else{
                                for (Movie movie : movies) {
                                    if (movie != null) {
                                        Client.inout.write(movie.toString() + "\n");
                                    }
                                }
                            }
                        }

                    } else {
                        if (responsePacket.getMessage() != null && !responsePacket.getMessage().isEmpty()) {
                            Client.inout.write(responsePacket.getMessage());
                        }
                    }
                } else {
                    if (responsePacket.getMessage() != null) {
                        Client.inout.write(responsePacket.getMessage());
                    }

                    if (responsePacket.getData()!=null ){
                        Client.inout.write(responsePacket.getData().toString());
                    }
                }
            } catch (NullPointerException e ){
                Client.inout.write("Вы не были подключены к серверу.");
            }
        } else {inout.write("Такой команды не существует");}
    }

    /**
     * Управляет выбором пользователя: войти в аккаунт или зарегистрироваться.
     */

    private static void entryAccount(){
        while (true){
            inout.write("Пожалуйста, зарегистрируйтесь или войдите в аккаунт");
            inout.write("Введите r, если хотите зарегистрироваться, и l, если хотите войти в аккаунт ");
            String operation = CheckValue.checkValuesNull("операции");
            if (operation.equals("r")) {
                boolean flag = registrat();
                if (flag){
                    break;
                }
            } else if (operation.equals("l")) {
                boolean flag = logIn();
                if (flag){
                    break;
                }
            }
        }

    }

    /**
     * Запрашивает логин и пароль, проверяет их длину и отправляет на сервер пакет 'login'.
     * Если сервер одобряет данные, сохраняет их в переменные lgn и pw.
     */

    private static boolean logIn(){
        String logn;
        while (true){
            inout.write("Длина логина должна быть более 4");
            String testlogn = CheckValue.checkValuesNull("логина");
            if (testlogn.length()>=4) {
                logn=testlogn;
                break;
            }
        }
        String passw;
        while (true){
            inout.write("Длина пароля должна быть 6 или более");
            String testp = CheckValue.checkValuesNull("пароля");
            if (testp.length()>=6) {
                passw=testp;
                break;
            }
        }
        CommandPacket comPac = new CommandPacket("login", null,null,logn,passw);
        ResponsPacket responsePacket = coMan.execute(comPac);
        if (responsePacket.getData()!=null){
            Client.lgn = logn;
            Client.pw = passw;
            inout.write("Вы вошли в аккаунт");
            return true;
        }
        inout.write(responsePacket.getMessage());
        return false;
    }

    /**
     * Регистрирует новый аккаунт на сервере с валидацией длины логина/пароля.
     */

    private static boolean registrat(){
        String logn;
        while (true){
            inout.write("Длина логина должна быть больше 4");
            String testlogn = CheckValue.checkValuesNull("логина");
            if (testlogn.length()>4) {
                logn=testlogn;
                break;
            }
        }
        String passw;
        while (true){
            inout.write("Длина пароля должна быть 6 или более");
            String testp = CheckValue.checkValuesNull("пароля");
            if (testp.length()>=6) {
                passw=testp;
                break;
            }
        }

        CommandPacket comPac = new CommandPacket("registration", null,null,logn,passw);
        ResponsPacket responsePacket = coMan.execute(comPac);
        if (responsePacket.getData()!=null){
            Client.lgn=logn;
            Client.pw = passw;
            inout.write("Вы вошли в аккаунт");
            return true;
        }
        inout.write(responsePacket.getMessage());
        return false;
    }

    /**
     * "Пингует" сервер командой 'info' перед началом работы.
     * Возвращает true, если сервер ответил чем-то вменяемым, а не упал по таймауту.
     */

    private static boolean testConnection() {
        try {
            CommandPacket testPacket = new CommandPacket("info", null, null, "ping", "ping");
            ResponsPacket response = coMan.execute(testPacket);
            return response != null
                    && response.getMessage() != null
                    && !response.getMessage().contains("Таймаут")
                    && !response.getMessage().contains("Ошибка");
        } catch (Exception e) {
            return false;
        }
    }
}
package server;
import common.Movie;
//import server.comands.Save;
import server.meneger.*;
import server.module.ConnectModule;
import server.meneger.SendingManagerServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;



public class Server {
    public static final Logger logger = LogManager.getLogger(Server.class);
        private static int port = 8885;
        private static String filename = "file.csv";
        private static boolean running = true;
        private static CollectionManager collectionManager;
        public static ParseManagerServer parseManagerServer;
        private static ResponseManager responseManager;
        private static SendingManagerServer sendingManager;
        private static ConnectModule connectModule;
    public static DatabaseManager dbman = DatabaseManager.getInstance();

        public static void main(String[] args) {
            parseArgs(args);

            collectionManager = new CollectionManager();
            parseManagerServer = new ParseManagerServer(collectionManager);
            responseManager = new ResponseManager(parseManagerServer);
            sendingManager = new SendingManagerServer();
/**
 * Загружает все фильмы из БД в коллекцию при запуске сервера.
 */
            Stack<Movie> movie = dbman.getDB();
            collectionManager.setMovie(movie);

            try {
                connectModule = new ConnectModule(port, responseManager, sendingManager);
            } catch (IOException e) {
                logger.error("Не удалось запустить сервер на порту " + port + ": " + e.getMessage());
                return;
            }

            startConsoleHandler();

            connectModule.run();

        }

        private static void parseArgs(String[] args) {
            if (args.length != 0) {
                for (String arg : args) {
                    try {
                        int parsedPort = Integer.parseInt(arg);
                        port = parsedPort;
                    } catch (NumberFormatException e) {
                        filename = arg;
                    }
                }
            }
            logger.info("Порт: " + port + ", файл: " + filename);
        }

        private static void startConsoleHandler() {
            new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                try {
                    while (running) {
                        String input = scanner.nextLine().trim();

                        if (input.equalsIgnoreCase("exit")) {
                            logger.info("Завершение работы сервера...");
                            running = false;
                            if (connectModule != null) {
                                connectModule.stop();
                            }
                            System.exit(0);

                        } else if (input.equalsIgnoreCase("help")) {
                            logger.info("Доступные команды сервера:");
                            logger.info("  exit - завершить работу сервера");
                            logger.info("  help - показать эту справку");
                        }
                    }
                } catch (NoSuchElementException e) {
                    logger.info("Консольный ввод завершён");
                    System.exit(0);
                }
            }).start();
        }

        public static CollectionManager getCollectionManager() {
            return collectionManager;
        }
    }

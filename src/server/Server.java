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
        private static FileManager fileManager = new FileManager();

        public static void main(String[] args) {
            parseArgs(args);

            collectionManager = new CollectionManager();
            parseManagerServer = new ParseManagerServer(collectionManager);
            responseManager = new ResponseManager(parseManagerServer);
            sendingManager = new SendingManagerServer();

            loadCollection();
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

        private static void loadCollection() {
            fileManager.readCSV(filename);

            int maxId = collectionManager.getMovie().stream()
                    .filter(Objects::nonNull)
                    .mapToInt(Movie::getId)
                    .max()
                    .orElse(0);

            try {
                common.Generatic.setId(maxId + 1);
            } catch (Exception e) {
                logger.warn("Не удалось установить Generatic.setId: " + e.getMessage());
            }
            collectionManager.sortByLength();
            logger.info("Коллекция загружена. Размер: " + collectionManager.getMovie().size());
        }

        private static void saveCollection() {
            try {
                fileManager.writeCSV(filename);
                logger.info("Коллекция сохранена в файл: " + filename + " (фильмов: " + collectionManager.getMovie().size() + ")");
            } catch (Exception e) {
                logger.error("Ошибка при сохранении коллекции: " + e.getMessage());
            }
        }

        private static void startConsoleHandler() {
            new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                try {
                    while (running) {
                        String input = scanner.nextLine().trim();

                        if (input.equalsIgnoreCase("save")) {
                            logger.info("Экстренное сохранение коллекции");
                            saveCollection();

                        } else if (input.equalsIgnoreCase("exit")) {
                            logger.info("Завершение работы сервера...");
                            saveCollection();
                            running = false;
                            if (connectModule != null) {
                                connectModule.stop();
                            }
                            System.exit(0);

                        } else if (input.equalsIgnoreCase("help")) {
                            logger.info("Доступные команды сервера:");
                            logger.info("  save - сохранить коллекцию в CSV файл");
                            logger.info("  exit - завершить работу сервера");
                            logger.info("  help - показать эту справку");
                        }
                    }
                } catch (NoSuchElementException e) {
                    logger.info("Консольный ввод завершён");
                    saveCollection();
                    System.exit(0);
                }
            }).start();
        }

        public static String getFilename() {
            return filename;
        }

        public static CollectionManager getCollectionManager() {
            return collectionManager;
        }
    }
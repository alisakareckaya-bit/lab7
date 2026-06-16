package server.module;
import common.CommandPacket;
import common.ResponsPacket;
import common.SerializationUtils;
import common.manager.ChunkManager;
import server.Server;
import server.meneger.ResponseManager;
import server.meneger.SendingManagerServer;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Сетевой модуль сервера для организации многопоточного приёма и обработки UDP-пакетов.
 * Обеспечивает асинхронный конвейер обработки запросов.
 * Конвейер состоит из трёх этапов:
 *   READ: Многопоточное чтение и десериализация чанков запроса (Cached Thread Pool).
 *   PROCESS: Многопоточное выполнение полученных команд (Cached Thread Pool).
 *   WRITE: Многопоточная отправка сформированных ответов клиентам (Fixed Thread Pool).
 * @author Алиса
 * @version 1.0
 */

public class ConnectModule {
        private final int port;
        private final ResponseManager responseManager;
        private final SendingManagerServer sendingManagerServer;
        private DatagramChannel channel;
        private boolean running = true;
        private static final int BUFFER_SIZE = 4096;
        private final ExecutorService READ = Executors.newCachedThreadPool();
        private final ExecutorService PROCESS = Executors.newCachedThreadPool();
        private final ExecutorService WRITE = Executors.newFixedThreadPool(10);
        public ConnectModule(int port, ResponseManager responseManager, SendingManagerServer sendingManager) throws IOException {
            this.port = port;
            this.responseManager = responseManager;
            this.sendingManagerServer = sendingManager;
            channel = DatagramChannel.open();
            channel.bind(new InetSocketAddress(port));
            channel.configureBlocking(true);
        }

        public void run() {
            try {
                while (running) {
                    // Изолированное выделение буфера под каждый пакет
                    ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                    SocketAddress clientAddress = channel.receive(buffer);
                    if (clientAddress == null) {
                        continue;
                    }
                    buffer.flip();
                    byte[] data = new byte[buffer.remaining()];
                    buffer.get(data);

                    READ.submit(() -> {
                        try {
                            ChunkManager.Chunk chunk = (ChunkManager.Chunk) SerializationUtils.deserialize(data);
                            CommandPacket command = responseManager.processChunk(chunk);
                            if (command != null) {
                                Server.logger.info("Получена команда: " + command.getType());
                                PROCESS.submit(() -> {
                                    try {
                                        ResponsPacket response = responseManager.executeCommand(command);
                                        Server.logger.info("Сформирован ответ для: " + clientAddress);
                                        WRITE.submit(() -> {
                                            try {
                                                sendingManagerServer.send(channel, response, clientAddress, chunk.getRequestId());
                                            } catch (Exception e) {
                                                Server.logger.error("Ошибка при отправке ответа: " + e.getMessage());
                                            }
                                        });

                                    } catch (Exception e) {
                                        Server.logger.error("Ошибка выполнения команды: " + e.getMessage());
                                    }
                                });
                            }
                        } catch (Exception e) {
                            Server.logger.error("Ошибка при обработке пакета в пуле READ: " + e.getMessage());
                        }
                    });
                }
            } catch (Exception e) {
                if (running) {
                    Server.logger.error("Критическая ошибка сервера: " + e.getMessage());
                }
            } finally {
                stop();
            }
        }

    /**
     * Корректно останавливает работу сетевого модуля.
     * Переводит флаг работы в false, инициирует закрытие всех используемых пулов потоков
     * и закрывает сетевой UDP-канал. Если метод уже был вызван ранее, повторное выполнение
     * игнорируется для предотвращения дублирования системных логов.
     */

        public void stop() {
            if (!running) {
                return;
            }
            running = false;
            // Инициирование остановки исполнителей задач
            READ.shutdown();
            PROCESS.shutdown();
            WRITE.shutdown();
            Server.logger.info("Пулы потоков остановлены");
            if (channel != null) {
                try {
                    channel.close();
                    Server.logger.info("Сервер был успешно остановлен");
                } catch (IOException e) {
                    Server.logger.error("Ошибка при закрытии канала");
                }
            }
        }
    }
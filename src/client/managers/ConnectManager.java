package client.managers;

import client.Client;
import common.CommandPacket;
import common.manager.ChunkManager;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * Сетевой менеджер нижнего уровня, отвечающий за управление UDP-каналом.
 * <p>
 * Обеспечивает инициализацию, закрытие неблокирующего сетевого канала {@link DatagramChannel},
 * а также выполнение базовых операций ввода-вывода (I/O) — отправку команд и чтение сырых байт датаграмм.
 * </p>
 *
 * @author Alisa
 */
public class ConnectManager {
    /**
     * Размер буфера для чтения одной датаграммы (2048 байт).
     * Рассчитан с запасом под максимальный размер чанка и оверхед Java-сериализации.
     */
    private static final int BUFFER_SIZE = 4096;

    /** Неблокирующий сетевой канал для обмена данными по протоколу UDP. */
    private DatagramChannel channel;

    /** Хост (IP-адрес или доменное имя) удаленного сервера. */
    private final String host;

    /** Порт удаленного сервера. */
    private final int port;

    /** Менеджер, инкапсулирующий логику безопасной отправки чанков в неблокирующем режиме. */
    private final SendingManager sendingManager = new SendingManager();

    /**
     * Создает новый менеджер соединений и инициализирует неблокирующий UDP-канал.
     *
     * @param host хост сервера для отправки пакетов
     * @param port порт сервера для отправки пакетов
     */
    public ConnectManager(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            channel = DatagramChannel.open();
            channel.configureBlocking(false);
        } catch (IOException e) {
            Client.inout.write("Не удалось создать канал: " + e.getMessage());
        }
    }

    /**
     * Передает команду высокоуровневому менеджеру отправки для её нарезки и передачи на сервер.
     *
     * @param command пакет команды для отправки на сервер
     * @see SendingManager#send(CommandPacket, DatagramChannel, String, int)
     */
    public void send(CommandPacket command) {
        sendingManager.send(command, channel, host, port);
    }

    /**
     * Пытается прочитать одну входящую UDP-датаграмму из сетевого буфера операционной системы.
     * <p>
     * Так как канал работает в неблокирующем режиме, метод не останавливает выполнение потока
     * в ожидании данных. Если в сокете пусто, возврат происходит мгновенно.
     * </p>
     *
     * @return массив байт, содержащий сериализованный объект {@link ChunkManager.Chunk},
     *         или {@code null}, если на момент вызова данные в канале отсутствуют
     * @throws IOException если произошла ошибка ввода-вывода при чтении из канала
     */
    public byte[] receiveData() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        InetSocketAddress sender = (InetSocketAddress) channel.receive(buffer);

        if (sender == null) {
            return null;
        }

        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        return data;
    }

    /**
     * Корректно освобождает системные ресурсы и закрывает сетевой канал, если он был открыт.
     *
     * @throws IOException если произошла ошибка ввода-вывода при закрытии канала
     */
    public void close() throws IOException {
        if (channel != null) {
            channel.close();
        }
    }
}

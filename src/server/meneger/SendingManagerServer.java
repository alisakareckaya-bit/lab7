package server.meneger;
import common.ResponsPacket;
import common.SerializationUtils;
import common.manager.ChunkManager;
import server.Server;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.List;
public class SendingManagerServer {
        /**
         * Отправляет ответ клиенту по UDP с разбиением на чанки
         *
         * @param channel       датаграмный канал (НЕ DatagramSocket!)
         * @param response      ответ для отправки
         * @param clientAddress адрес клиента (SocketAddress, полученный при приёме)
         * @param requestId     ID запроса от клиента
         */
        public void send(DatagramChannel channel, ResponsPacket response,
                         SocketAddress clientAddress, int requestId) {
            try {
                byte[] data = SerializationUtils.serialize(response);

                List<ChunkManager.Chunk> chunks = ChunkManager.split(data, requestId);

                for (ChunkManager.Chunk chunk : chunks) {
                    byte[] chunkData = SerializationUtils.serialize(chunk);
                    ByteBuffer buffer = ByteBuffer.wrap(chunkData);

                    while (buffer.hasRemaining()) {
                        int sent = channel.send(buffer, clientAddress);
                        if (sent == 0) {
                            Thread.sleep(1);
                        }
                    }
                }

            } catch (Exception exception) {
                Server.logger.info("Не удалось отправить ответ клиенту: " + exception.getMessage());
            }
        }
    }

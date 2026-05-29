package client.managers;

import client.Client;
import common.CommandPacket;
import common.SerializationUtils;
import common.manager.ChunkManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.List;

public class SendingManager {
    private  int requestId = 0;

    public void send(CommandPacket command, DatagramChannel channel, String host, int port) {
        try {
            byte[] data = SerializationUtils.serialize(command);

            int currentRequestId = ++requestId;

            List<ChunkManager.Chunk> chunks = ChunkManager.split(data, currentRequestId);

            for (ChunkManager.Chunk chunk : chunks) {
                byte[] chunkData = SerializationUtils.serialize(chunk);
                ByteBuffer buffer = ByteBuffer.wrap(chunkData);

                InetSocketAddress address = new InetSocketAddress(host, port);
                while (buffer.hasRemaining()) {
                    int sent = channel.send(buffer, address);
                    if (sent == 0) {
                        Thread.sleep(1);
                    }
                }
            }

        } catch (IOException | InterruptedException exception) {
            Client.inout.write("Не удалось отправить команду: " + exception.getMessage());
        }
    }
}
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
public class ConnectModule {
        private final int port;
        private final ResponseManager responseManager;
        private final SendingManagerServer sendingManagerServer;
        private DatagramChannel channel;
        private boolean running = true;
    private static final int BUFFER_SIZE = 4096;
        public ConnectModule(int port, ResponseManager responseManager, SendingManagerServer sendingManager) throws IOException {
            this.port = port;
            this.responseManager = responseManager;
            this.sendingManagerServer = sendingManager;
            channel = DatagramChannel.open();
            channel.bind(new InetSocketAddress(port));
            channel.configureBlocking(false);
        }

        public void run() {
            try {
                while (running) {
                    ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                    SocketAddress clientAddress = channel.receive(buffer);
                    if (clientAddress == null) {
                        continue;
                    }
                    buffer.flip();
                    byte[] data = new byte[buffer.remaining()];
                    buffer.get(data);


                    ChunkManager.Chunk chunk = (ChunkManager.Chunk) SerializationUtils.deserialize(data);

                    CommandPacket command = responseManager.processChunk(chunk);
                    if (command != null) {
                        Server.logger.info("Получена команда: " + command.getType());
                        ResponsPacket response = responseManager.executeCommand(command);
                        sendingManagerServer.send(channel, response, clientAddress, chunk.getRequestId());
                    }
                }

            } catch (Exception e) {
                Server.logger.error("Ошибка: " + e.getMessage());
            } finally {
                stop();
            }
        }

        public void stop() {
            running = false;
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                    Server.logger.error("Ошибка при закрытии канала");
                }
            }
        }
    }




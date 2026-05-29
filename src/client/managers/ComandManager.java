package client.managers;

import client.Client;
import common.CommandPacket;
import common.ResponsPacket;
import common.SerializationUtils;
import common.manager.ChunkManager;
import java.util.HashMap;
import java.util.Map;

public class ComandManager {
    private static final int TIMEOUT_MS = 5000;
    private static final int CLEANUP_INTERVAL = 10;

    private final ConnectManager connectManager;
    private int callCounter = 0;

    private final Map<Integer, Map<Integer, ChunkManager.Chunk>> pendingResponses = new HashMap<>();
    private final Map<Integer, Long> requestTimestamps = new HashMap<>();

    public ComandManager(ConnectManager connectManager) {
        this.connectManager = connectManager;
    }

    public ResponsPacket execute(CommandPacket command) {
        try {
            connectManager.send(command);

            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < TIMEOUT_MS) {

                ResponsPacket response = tryProcessIncomingPacket();
                if (response != null) {
                    return response;
                }

                Thread.sleep(1);
            }

            Client.inout.write("Таймаут: сервер не ответил за " + TIMEOUT_MS + " мс");
            return new ResponsPacket("Таймаут", null);

        } catch (Exception e) {
            Client.inout.write("Ошибка при обмене с сервером: " + e.getMessage());
            return new ResponsPacket("Ошибка: " + e.getMessage(), null);
        } finally {
            cleanupOldRequests();
        }
    }

    private ResponsPacket tryProcessIncomingPacket() {
        try {
            byte[] data = connectManager.receiveData();
            if (data == null) {
                return null;
            }

            ChunkManager.Chunk chunk = (ChunkManager.Chunk) SerializationUtils.deserialize(data);
            requestTimestamps.put(chunk.getRequestId(), System.currentTimeMillis());

            Map<Integer, ChunkManager.Chunk> chunksMap = pendingResponses.computeIfAbsent(
                    chunk.getRequestId(),
                    k -> new HashMap<>()
            );
            chunksMap.put(chunk.getChunkNumber(), chunk);

            if (ChunkManager.isComplete(chunksMap, chunk.getTotalChunks())) {
                byte[] fullData = ChunkManager.join(chunksMap, chunk.getTotalChunks());
                pendingResponses.remove(chunk.getRequestId());
                requestTimestamps.remove(chunk.getRequestId());

                if (fullData != null) {
                    return (ResponsPacket) SerializationUtils.deserialize(fullData);
                }
            }
            return null;

        } catch (Exception e) {
            return null;
        }
    }

    private void cleanupOldRequests() {
        callCounter++;
        if (callCounter % CLEANUP_INTERVAL != 0) return;

        long now = System.currentTimeMillis();
        requestTimestamps.entrySet().removeIf(entry -> {
            if (now - entry.getValue() > TIMEOUT_MS) {
                pendingResponses.remove(entry.getKey());
                return true;
            }
            return false;
        });
    }
}

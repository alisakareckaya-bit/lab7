package server.meneger;

import common.CommandPacket;
import common.ResponsPacket;
import common.SerializationUtils;
import common.manager.ChunkManager;

import java.util.*;

public class ResponseManager {
    private final ParseManagerServer parserManager;  // ← только parserManager

    private final Map<Integer, Map<Integer, ChunkManager.Chunk>> pendingRequests = new HashMap<>();
    private final Map<Integer, Long> requestTimestamps = new HashMap<>();

    private static final int TIMEOUT_MS = 5000;
    private static final int CLEANUP_INTERVAL = 10;
    private int callCounter = 0;

    public ResponseManager(ParseManagerServer parserManager) {  // ← только один параметр
        this.parserManager = parserManager;
    }

    public CommandPacket processChunk(ChunkManager.Chunk chunk) {
        int requestId = chunk.getRequestId();
        requestTimestamps.put(requestId, System.currentTimeMillis());

        Map<Integer, ChunkManager.Chunk> chunksMap = pendingRequests.computeIfAbsent(
                requestId,
                k -> new HashMap<>()
        );
        chunksMap.put(chunk.getChunkNumber(), chunk);

        if (ChunkManager.isComplete(chunksMap, chunk.getTotalChunks())) {
            byte[] fullData = ChunkManager.join(chunksMap, chunk.getTotalChunks());
            pendingRequests.remove(requestId);
            requestTimestamps.remove(requestId);

            if (fullData != null) {
                try {
                    return (CommandPacket) SerializationUtils.deserialize(fullData);
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }

    public ResponsPacket executeCommand(CommandPacket command) {
        return parserManager.parseCommand(command);
    }

    public void cleanupOldRequests() {
        callCounter++;
        if (callCounter % CLEANUP_INTERVAL != 0) return;

        long now = System.currentTimeMillis();

        // Исправлено: собираем ключи в список
        List<Integer> expiredKeys = new ArrayList<>();
        for (Map.Entry<Integer, Long> entry : requestTimestamps.entrySet()) {
            if (now - entry.getValue() > TIMEOUT_MS) {
                expiredKeys.add(entry.getKey());
            }
        }

        for (int key : expiredKeys) {
            pendingRequests.remove(key);
            requestTimestamps.remove(key);
        }
    }
}
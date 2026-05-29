package common.manager;

import java.io.Serializable;
import java.util.*;

public class ChunkManager {
    private static final int MAX_PAYLOAD_SIZE = 1024;

    /**
     * Разбивает данные на чанки для отправки по UDP
     * @param data исходные байты
     * @param requestId уникальный ID запроса
     * @return список чанков
     */
    public static List<Chunk> split(byte[] data, int requestId) {
        if (data == null || data.length == 0) {
            List<Chunk> chunks = new ArrayList<>();
            chunks.add(new Chunk(requestId, 1, 0, new byte[0]));
            return chunks;
        }

        int totalChunks = (int) Math.ceil(data.length / (double) MAX_PAYLOAD_SIZE);
        List<Chunk> chunks = new ArrayList<>();

        for (int i = 0; i < totalChunks; i++) {
            int offset = i * MAX_PAYLOAD_SIZE;
            int length = Math.min(MAX_PAYLOAD_SIZE, data.length - offset);
            byte[] chunkData = Arrays.copyOfRange(data, offset, offset + length);
            chunks.add(new Chunk(requestId, totalChunks, i, chunkData));
        }
        return chunks;
    }

    /**
     * Собирает данные из чанков.
     * @param chunksMap мапа чанков (номер чанка → Chunk)
     * @param totalChunks ожидаемое количество чанков
     * @return восстановленные байты, или null если не все чанки собраны или есть дубликаты
     */
    public static byte[] join(Map<Integer, Chunk> chunksMap, int totalChunks) {
        if (chunksMap == null || chunksMap.size() != totalChunks) {
            return null;
        }

        @SuppressWarnings("unchecked")
        Chunk[] ordered = new Chunk[totalChunks];

        for (Map.Entry<Integer, Chunk> entry : chunksMap.entrySet()) {
            int index = entry.getKey();
            Chunk chunk = entry.getValue();

            if (index < 0 || index >= totalChunks) {
                return null;
            }
            if (ordered[index] != null) {
                return null;
            }
            ordered[index] = chunk;
        }

        for (int i = 0; i < totalChunks; i++) {
            if (ordered[i] == null) {
                return null;
            }
        }

        int totalLength = 0;
        for (Chunk chunk : ordered) {
            totalLength += chunk.getData().length;
        }

        byte[] result = new byte[totalLength];
        int position = 0;
        for (Chunk chunk : ordered) {
            byte[] data = chunk.getData();
            System.arraycopy(data, 0, result, position, data.length);
            position += data.length;
        }

        return result;
    }

    /**
     * Проверяет, собраны ли все чанки
     * @param chunksMap мапа чанков (номер → чанк)
     * @param totalChunks ожидаемое количество чанков
     * @return true если количество чанков в мапе равно ожидаемому
     */
    public static boolean isComplete(Map<Integer, Chunk> chunksMap, int totalChunks) {
        return chunksMap != null && chunksMap.size() == totalChunks;
    }

    /**
     * Класс-контейнер для одного чанка.
     * Сериализуется стандартным механизмом Java.
     */
    public static class Chunk implements Serializable {
        private static final long serialVersionUID = 1L;

        private final int requestId;
        private final int totalChunks;
        private final int chunkNumber;
        private final byte[] data;

        public Chunk(int requestId, int totalChunks, int chunkNumber, byte[] data) {
            this.requestId = requestId;
            this.totalChunks = totalChunks;
            this.chunkNumber = chunkNumber;
            this.data = data;
        }

        public int getRequestId() { return requestId; }
        public int getTotalChunks() { return totalChunks; }
        public int getChunkNumber() { return chunkNumber; }
        public byte[] getData() { return data; }

        public boolean isLast() {
            return chunkNumber == totalChunks - 1;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Chunk chunk = (Chunk) obj;
            return requestId == chunk.requestId &&
                    totalChunks == chunk.totalChunks &&
                    chunkNumber == chunk.chunkNumber;
        }

        @Override
        public int hashCode() {
            return Objects.hash(requestId, totalChunks, chunkNumber);
        }

        @Override
        public String toString() {
            return "Chunk{" + chunkNumber + "/" + totalChunks +
                    ", req=" + requestId + ", size=" + data.length + '}';
        }
    }
}
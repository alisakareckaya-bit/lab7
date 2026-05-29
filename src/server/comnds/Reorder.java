package server.comnds;

import common.CommandPacket;
import common.Movie;
import common.ResponsPacket;
import server.Server;
import server.interfcs.Comands;

import java.util.Collections;
import java.util.Stack;

public class Reorder implements Comands {

    @Override
    public ResponsPacket executer(CommandPacket commandPacket) {
        try {
            Stack<Movie> collection = Server.getCollectionManager().getMovie();
            if (collection == null || collection.isEmpty()) {
                return new ResponsPacket("Коллекция пуста. Нечего сортировать.\n", null);
            }
            Collections.reverse(collection);
            return new ResponsPacket("Порядок элементов в коллекции изменен на обратный.\n", null);
        } catch (Exception e) {
            return new ResponsPacket("Ошибка при сортировке коллекции: " + e.getMessage() + "\n", null);
        }
    }
    @Override
    public String toString() {
        return "Отсортировывает коллекцию в порядке, обратном нынешнему";
    }
}

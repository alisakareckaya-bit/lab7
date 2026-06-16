package server.comnds;

import common.CommandPacket;
import common.Movie;
import common.ResponsPacket;
import server.Server;
import server.interfcs.Comands;

import java.util.Collections;
import java.util.List;

public class Reorder implements Comands {

    @Override
    public ResponsPacket executer(CommandPacket commandPacket) {
        List<Movie> movie = Server.getCollectionManager().getMovie();
        synchronized (movie) {
            if (movie == null || movie.isEmpty()) {
                return new ResponsPacket("Коллекция пуста. Нечего сортировать.\n", null);
            }
            Collections.reverse(movie);
        }
        return new ResponsPacket("Порядок элементов в коллекции изменен на обратный.\n", null);
    }
    @Override
    public String toString() {
        return "reorder: Отсортировывает коллекцию в порядке, обратном нынешнему";
    }
}

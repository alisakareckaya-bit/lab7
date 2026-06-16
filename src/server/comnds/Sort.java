package server.comnds;

import common.CommandPacket;
import common.Movie;
import common.ResponsPacket;
import server.Server;
import server.interfcs.Comands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Sort implements Comands {
    @Override
    public ResponsPacket executer(CommandPacket commandPacket) {
        List<Movie> movies = Server.getCollectionManager().getMovie();
        synchronized (movies){
            if (movies.isEmpty()){
                return new ResponsPacket("Коллекция пуста. Нечего сортировать.", null);
            }
            movies.sort(Comparator.comparing(Movie::getLength));
        }

        return new ResponsPacket("Коллекция отсортирована в естественном порядке.", null);
    }

    @Override
    public String toString() {
        return "sort: Сортирует коллекцию в естественном порядке";
    }
}

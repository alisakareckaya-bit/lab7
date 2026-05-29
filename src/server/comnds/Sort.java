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

        List<Movie> list = new ArrayList<>(movies);

        list.sort(Comparator.comparing(Movie::getLength));

        movies.clear();
        movies.addAll(list);
        return new ResponsPacket("Коллекция отсортирована в естественном порядке.", null);
    }

    @Override
    public String toString() {
        return "Сортирует коллекцию в естественном порядке";
    }
}

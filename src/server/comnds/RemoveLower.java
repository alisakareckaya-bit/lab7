package server.comnds;

import common.CommandPacket;
import common.Movie;
import common.ResponsPacket;
import server.Server;
import server.interfcs.Comands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RemoveLower implements Comands {

    @Override
    public ResponsPacket executer(CommandPacket commandPacket) {
            Movie movie = commandPacket.getMovie();
            String author = commandPacket.getLog();
            if (movie==null) {
                return new ResponsPacket("Ошибка: Не передан фильм для сравнения", null);
            }
            List<Movie> movieList = Server.getCollectionManager().getMovie();
            List<Movie> forRemove = new ArrayList<>();
            synchronized (movieList){
                for (Movie movie1 : movieList){
                    if (movie1.getLength()<movie.getLength() && movie1.getLogin().equals(author)){
                        forRemove.add(movie1);
                    }
                }
            }
            if (forRemove.isEmpty()){
                return new ResponsPacket("В коллекции нет ваших элементов, которые меньше заданного", null);
            }

            int removedCount = 0;
            synchronized (movieList){
                Iterator<Movie> iterator = movieList.iterator();
                while (iterator.hasNext()) {
                    Movie movieNew = iterator.next();
                    if (forRemove.contains(movieNew)) {
                        if (Server.dbman.delete(movieNew.getId(), author)) {
                            iterator.remove();
                            removedCount++;
                        }
                    }
                }
            }
        if (removedCount > 0) {
            return new ResponsPacket("Удалено элементов: " + removedCount, null);
        } else {
            return new ResponsPacket("Нет элементов, меньших заданного", null);

        }
    }

    @Override
    public String toString() {
        return "remove_lower: Удаляет из коллекции все элементы, меньшие, чем заданный";
    }
}

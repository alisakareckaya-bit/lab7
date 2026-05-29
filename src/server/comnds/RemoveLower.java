package server.comnds;

import common.CommandPacket;
import common.Movie;
import common.ResponsPacket;
import server.Server;
import server.interfcs.Comands;

import java.util.Iterator;

public class RemoveLower implements Comands {

    @Override
    public ResponsPacket executer(CommandPacket commandPacket) {
            Movie movieComp = commandPacket.getMovie();
            Iterator<Movie> iterator = Server.getCollectionManager().getMovie().iterator();
            int removedCount = 0;
            while (iterator.hasNext()) {
                Movie movie = iterator.next();
                if (movie.getLength() < movieComp.getLength()) {
                    iterator.remove();
                    removedCount++;
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
        return "Удаляет из коллекции все элементы, меньшие, чем заданный";
    }
}

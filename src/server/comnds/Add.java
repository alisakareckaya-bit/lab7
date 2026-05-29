package server.comnds;

import common.CommandPacket;
import common.Movie;
import common.ResponsPacket;
import server.Server;
import server.interfcs.Comands;
import server.meneger.CollectionManager;

public class Add implements Comands {
    private final CollectionManager collectionManager;
    private Movie movie;
    public Add(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    @Override
    public ResponsPacket executer(CommandPacket command) {
        Movie movie = command.getMovie();

        movie.setId(common.Generatic.generateId());
        collectionManager.add(movie);

        return new ResponsPacket("Фильм успешно добавлен в коллекцию", null);
    }
}

package server.comnds;

import common.CommandPacket;
import common.Movie;
import common.ResponsPacket;
import server.Server;
import server.interfcs.Comands;
import server.meneger.CollectionManager;

public class Add implements Comands {
    private final CollectionManager collectionManager;
    public Add(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    @Override
    public ResponsPacket executer(CommandPacket command) {
        Movie movie = command.getMovie();
        String author = command.getLog();
        Integer id =  Server.dbman.add(movie, author);
        if (id == null) {
            return new ResponsPacket("Ошибка: Не удалось добавить объект в базу данных. (Скорее всего элемент с данным id уже есть в коллекции)", null);
        }
        movie.setId(id);
        movie.setLogin(author);
        collectionManager.add(movie);
        Server.logger.info("Пользователь " + author + " добавил элемент с ID: " + id);
        return new ResponsPacket("Фильм успешно добавлен в коллекцию", null);
    }
    @Override
    public String toString() {
        return "add: Добавляет элемент в коллекцию";
    }
}

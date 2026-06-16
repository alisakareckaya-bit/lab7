package server.comnds;

import common.CommandPacket;
import common.Movie;
import common.ResponsPacket;
import server.Server;
import server.interfcs.Comands;

import java.util.List;
import java.util.Stack;

public class Remove implements Comands {
    @Override
    public ResponsPacket executer(CommandPacket commandPacket) {
        Integer id;
        String author = commandPacket.getLog();
        String[] args = commandPacket.getArgs();
        try {
            id = Integer.parseInt(args[0]);
        }catch (Exception e){
            return new ResponsPacket("Ошибка: ID должен быть целым числом", null);
        }
        if (Server.dbman.delete(id, author)){
            Server.getCollectionManager().removeById(id);
            return  new ResponsPacket("Объект под ID " + id + " был удален.", null);
        }
        List<Movie> mov = Server.getCollectionManager().getMovie();
        boolean exists;
        synchronized (mov){
            exists = mov.stream().anyMatch(movie -> movie.getId().equals(id));
        }
            if (exists){
                return new ResponsPacket("Объект " + id + " не ваш, у вас нет прав, чтобы удалять его", null);
            }
        return new ResponsPacket("Значения с ID " + id + " нет в коллекции.", null);
    }

    @Override
    public String toString() {
        return "remove: Удаляет элемент из коллекции по его id";
    }
}

package server.comnds;

import common.CommandPacket;
import common.Movie;
import common.ResponsPacket;
import server.interfcs.Comands;
import server.meneger.CollectionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Show implements Comands {
    private final CollectionManager collectionManager;

    public Show(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public ResponsPacket executer(CommandPacket command) {
        List<Movie> stack = collectionManager.getMovie();
        List<Movie> snapshot;
        synchronized (stack){
            if (stack.isEmpty()){
                return new ResponsPacket("Коллекция пуста", null);
            }
            snapshot = new ArrayList<>(stack);
        }
        return new ResponsPacket("Объекты коллекции:", snapshot);
    }

    @Override
    public String toString() {
        return "show: Выводит все элементы коллекции";
    }
}
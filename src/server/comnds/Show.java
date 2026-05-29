package server.comnds;

import common.CommandPacket;
import common.Movie;
import common.ResponsPacket;
import server.interfcs.Comands;
import server.meneger.CollectionManager;

import java.util.Stack;

public class Show implements Comands {
    private final CollectionManager collectionManager;

    public Show(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public ResponsPacket executer(CommandPacket command) {
        Stack<Movie> stack = collectionManager.getMovie();

        if (stack.isEmpty()) {
            return new ResponsPacket("Коллекция пуста", null);
        }

        return new ResponsPacket("Коллекция:", stack);
    }

    @Override
    public String toString() {
        return "Выводит все элементы коллекции";
    }
}
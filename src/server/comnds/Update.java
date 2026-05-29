package server.comnds;

import common.CommandPacket;
import common.Movie;
import common.ResponsPacket;
import server.Server;
import server.interfcs.Comands;

import java.util.ListIterator;
import java.util.Stack;

public class Update implements Comands {
    @Override
    public ResponsPacket executer(CommandPacket commandPacket) {
        String[] args = commandPacket.getArgs();

        String keyStr = args[0];

        Stack<Movie> stack = Server.getCollectionManager().getMovie();
        if (stack.isEmpty()) {
            return new ResponsPacket("Коллекция пуста. Нечего обновлять.", null);
        }

        try {

            Integer id = Integer.parseInt(keyStr);
            boolean updated = findAndUpdate(stack, id, commandPacket.getMovie());

            if (updated) {
                return new ResponsPacket("Элемент с ID " + id + " успешно обновлен\n", null);
            } else {
                return new ResponsPacket("Элемент с ID " + id + " не найден в коллекции\n", null);
            }

        } catch (NumberFormatException e) {
            return new ResponsPacket("ID должен быть числом типа int.\n", null);
        }
    }
    private boolean findAndUpdate(Stack<Movie> stack, Integer id, Movie movie) {
        ListIterator<Movie> iterator = stack.listIterator();

        while (iterator.hasNext()) {
            Movie current = iterator.next();
            if (current.getId().equals(id)) {
                movie.setId(current.getId());
                iterator.set(movie);
                return true;
            }
        }
        return false;
    }
        @Override
        public String toString () {
            return "Обновляет значение элемента коллекции, id которого равен заданному";
        }
    }

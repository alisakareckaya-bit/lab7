package server.comnds;

import common.CommandPacket;
import common.Movie;
import common.ResponsPacket;
import server.Server;
import server.interfcs.Comands;

import javax.swing.*;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

public class Update implements Comands {
    @Override
    public ResponsPacket executer(CommandPacket commandPacket) {
        String[] args = commandPacket.getArgs();
        String author = commandPacket.getLog();
        Movie newMovie = commandPacket.getMovie();

        if (args == null || args.length == 0) {
            return new ResponsPacket("Ошибка: Не передан ID объекта для обновления.", null);
        }
        if (newMovie == null) {
            return new ResponsPacket("Ошибка: Не переданы новые данные для фильма.", null);
        }

        Integer id;
        try {
            id = Integer.parseInt(args[0].trim());
        } catch (NumberFormatException e) {
            return new ResponsPacket("ID должен быть числом типа int.\n", null);
        }
        List<Movie> stack = Server.getCollectionManager().getMovie();
        // Проверяем существование и права владения под синхронизацией
        boolean exists = false;
        boolean owner = false;

        synchronized (stack) {
            if (stack.isEmpty()) {
                return new ResponsPacket("Коллекция пуста. Нечего обновлять.", null);
            }
            for (Movie movie : stack) {
                if (movie.getId().equals(id)) {
                    exists = true;
                    if (movie.getLogin().equals(author)) {
                        owner = true;
                    }
                    break;
                }
            }
        }
        if (!exists) {
            return new ResponsPacket("Элемент с ID " + id + " не найден в коллекции\n", null);
        }
        if (!owner) {
            return new ResponsPacket("Элемент с ID " + id + " принадлежит другому пользователю. У вас нет прав на его изменение.\\n", null);
        }
        newMovie.setId(id);
        newMovie.setLogin(author);
        if (Server.dbman.update(newMovie, author)) {
            synchronized (stack) {
                findAndUpdate(stack, id, newMovie);
            }
            Server.logger.info("Пользователь " + author + " успешно обновил элемент с ID " + id);
            return new ResponsPacket("Элемент с ID " + id + " успешно обновлен\n", null);
        } else {
            return new ResponsPacket("Ошибка: Не удалось обновить элемент в базе данных.\n", null);
        }
    }
    private void findAndUpdate(List<Movie> stack, Integer id, Movie movie) {
        ListIterator<Movie> iterator = stack.listIterator();
        while (iterator.hasNext()) {
            Movie movie1 = iterator.next();
            if (movie1.getId().equals(id)) {
                iterator.set(movie);
                break;
            }
        }
    }
        @Override
        public String toString () {
            return "update: Обновляет значение элемента коллекции, id которого равен заданному";
        }
    }

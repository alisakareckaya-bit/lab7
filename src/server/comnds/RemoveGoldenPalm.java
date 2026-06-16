package server.comnds;


import common.CommandPacket;
import common.Movie;
import common.ResponsPacket;
import server.Server;
import server.interfcs.Comands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class RemoveGoldenPalm implements Comands {

    @Override
    public ResponsPacket executer(CommandPacket commandPacket) {
        String[] args = commandPacket.getArgs();
        String author = commandPacket.getLog();
        if (args == null || args.length == 0){
            return new ResponsPacket("Ошибка: Не передан аргумент goldenPalmCount.", null);
        }
        int targetGoldenPalmCount;
        try {
            targetGoldenPalmCount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e){
            return new ResponsPacket("Ошибка: Значение goldenPalmCount должно быть int.", null);
        }
        List<Movie> movieStack = Server.getCollectionManager().getMovie();

        synchronized (movieStack) {
            if (movieStack == null || movieStack.isEmpty()) {
                return new ResponsPacket("Коллекция пуста. Нечего удалять.\n", null);
            }
        }
        List<Movie> forRemove = new ArrayList<>();
        synchronized (movieStack) {
            for (Movie movie : movieStack) {
                if (movie.getGoldenPalmCount() == targetGoldenPalmCount && movie.getLogin().equals(author)) {
                    forRemove.add(movie);
                }
            }
        }
        if (forRemove.isEmpty()) {
            return new ResponsPacket("В коллекции нет ВАШИХ элементов с goldenPalmCount = " + targetGoldenPalmCount, null);
        }

        int countToRemove = 0;

        synchronized (movieStack){
            Iterator<Movie> iterator = movieStack.iterator();
            while ((iterator.hasNext())){
                Movie movieNew = iterator.next();
                if (forRemove.contains(movieNew)) {
                    if (Server.dbman.delete(movieNew.getId(),author)) {
                        iterator.remove();
                        countToRemove++;
                    }
                }
            }
        }
        if (countToRemove == 0) {
            return new ResponsPacket("Элементы с goldenPalmCount = " + targetGoldenPalmCount + " не найдены.\n", null);
        } else {
            Server.logger.info("Удалено элементов с goldenPalmCount = " + targetGoldenPalmCount + ": " + countToRemove);
            return new ResponsPacket("Удалено элементов: " + countToRemove + "\nКоллекция обновлена.", null);
        }
    }

    @Override
    public String toString() {
        return "remove_palm: Удаляет из коллекции все элементы, значение поля goldenPalmCount которого эквивалентно заданному";
    }
}

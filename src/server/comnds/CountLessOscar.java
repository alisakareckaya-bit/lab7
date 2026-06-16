package server.comnds;

import common.CommandPacket;
import common.Movie;
import common.ResponsPacket;
import server.Server;
import server.interfcs.Comands;

import java.util.List;
import java.util.Stack;

public class CountLessOscar implements Comands {

    @Override
    public ResponsPacket executer(CommandPacket commandPacket) {
        String[] args = commandPacket.getArgs();
        if (args == null || args.length == 0){
            return new ResponsPacket("Ошибка: Не передан аргумент oscarsCount.", null);
        }
        long targetOscarsCount;
        try {
            targetOscarsCount = Long.parseLong(args[0]);
        }catch (NumberFormatException e){
            return new ResponsPacket("Ошибка: Значение oscarsCount должно быть long.", null);
        }
        List<Movie> movieStack = Server.getCollectionManager().getMovie();
        long count = 0;
        synchronized (movieStack) {
            if (movieStack.isEmpty()) {
                return new ResponsPacket("Коллекция пуста. Количество элементов: 0\n", null);
            }

            for (Movie movie : movieStack) {
                if (movie.getOscarsCount() < targetOscarsCount) {
                    count++;
                }
            }
        }

            return new ResponsPacket("Количество фильмов с количеством Оскаров меньше " + targetOscarsCount + ": " + count + "\n", null);


    }

    @Override
    public String toString() {
        return "count_oscar: Выводит количество элементов, значение поля oscarsCount которых меньше заданного";
    }
}
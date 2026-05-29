package server.comnds;

import common.CommandPacket;
import common.Movie;
import common.ResponsPacket;
import server.Server;
import server.interfcs.Comands;

import java.util.Stack;

public class CountLessOscar implements Comands {

    @Override
    public ResponsPacket executer(CommandPacket commandPacket) {
        String[] args = commandPacket.getArgs();
            long targetOscarsCount;
            targetOscarsCount = Long.parseLong(args[0]);
            Stack<Movie> movieStack = Server.getCollectionManager().getMovie();
            if (movieStack.isEmpty()) {
                return  new ResponsPacket("Коллекция пуста. Количество элементов: 0\n", null);
            }

            long count = 0;
            for (Movie movie : movieStack) {
                if (movie.getOscarsCount() < targetOscarsCount) {
                    count++;
                }
            }

            return new ResponsPacket("Количество фильмов с количеством Оскаров меньше " + targetOscarsCount + ": " + count + "\n", null);


    }

    @Override
    public String toString() {
        return "Выводит количество элементов, значение поля oscarsCount которых меньше заданного";
    }
}
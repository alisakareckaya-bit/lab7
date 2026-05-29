package server.comnds;

import common.CommandPacket;
import common.Movie;
import common.ResponsPacket;
import server.Server;
import server.interfcs.Comands;

import java.util.*;

public class PrintAscending implements Comands {
    @Override
    public ResponsPacket executer(CommandPacket commandPacket) {
            Stack<Movie> movieStack = Server.getCollectionManager().getMovie();

            if (movieStack == null || movieStack.isEmpty()) {
                return new ResponsPacket("Коллекция пуста. Нечего выводить.\n", null);
            }

            List<Movie> sortedList = new ArrayList<>(movieStack);
            sortedList.sort(Comparator.comparing(Movie::getLength));
            StringBuilder result = new StringBuilder();

            for (int i = 0; i < sortedList.size(); i++) {
                result.append(i + 1).append(". ").append(sortedList.get(i).toString()).append("\n");
            }

            return new ResponsPacket(result.toString(), null);
    }


    @Override
    public String toString() {
        return "Вывести элементы коллекции в порядке возрастания";
    }
}

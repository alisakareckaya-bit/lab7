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
            List<Movie> movieStack = Server.getCollectionManager().getMovie();
            List<Movie> sortedList;
            synchronized (movieStack) {
                if (movieStack == null || movieStack.isEmpty()) {
                    return new ResponsPacket("Коллекция пуста. Нечего выводить.\n", null);
                }
                sortedList = new ArrayList<>(movieStack);
            }

            sortedList.sort(Comparator.comparing(Movie::getLength));
            StringBuilder result = new StringBuilder();

            for (int i = 0; i < sortedList.size(); i++) {
                result.append(i + 1).append(". ").append(sortedList.get(i).toString()).append("\n");
            }

            return new ResponsPacket(result.toString(), null);
    }


    @Override
    public String toString() {
        return "print_ascending: Вывести элементы коллекции в порядке возрастания";
    }
}

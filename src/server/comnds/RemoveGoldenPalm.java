package server.comnds;


import common.CommandPacket;
import common.Movie;
import common.ResponsPacket;
import server.Server;
import server.interfcs.Comands;

import java.util.Stack;

public class RemoveGoldenPalm implements Comands {

    @Override
    public ResponsPacket executer(CommandPacket commandPacket) {
        String[] args = commandPacket.getArgs();
            Stack<Movie> movieStack = Server.getCollectionManager().getMovie();

            if (movieStack == null || movieStack.isEmpty()) {
                return new ResponsPacket("Коллекция пуста. Нечего удалять.\n", null);
            }
            int targetGoldenPalmCount;
            targetGoldenPalmCount = Integer.parseInt(args[0]);
            int countToRemove = 0;
            for (Movie movie : movieStack) {
                if (movie.getGoldenPalmCount() == targetGoldenPalmCount) {
                    countToRemove++;
                }
            }

            if (countToRemove == 0) {
                return new ResponsPacket("Элементы с goldenPalmCount = " + targetGoldenPalmCount + " не найдены.\n", null);
            }

            boolean removed = movieStack.removeIf(movie -> movie.getGoldenPalmCount() == targetGoldenPalmCount);

            if (removed) {
                Server.logger.info("Удалено элементов с goldenPalmCount = " + targetGoldenPalmCount + ": " + countToRemove);
                return new ResponsPacket("Удалено элементов: " + countToRemove + "\nКоллекция обновлена.", null);
            } else {
                return new ResponsPacket("Не удалось удалить элементы.\n", null);
            }

    }

    @Override
    public String toString() {
        return "Удаляет из коллекции все элементы, значение поля goldenPalmCount которого эквивалентно заданному";
    }
}

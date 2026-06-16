package client.commands;

import client.Client;
import client.interfaces.Comand;
import common.CommandPacket;
import common.Movie;
public class Add implements Comand {
    @Override
    public CommandPacket implementCommand(String[] arg, String login, String pw) {
        if (arg.length > 0) {
            Client.inout.write("Команда add не принимает аргументы в строке.");
        }

        Movie movie = new Movie();

        movie.setLogin(login);

        return new CommandPacket("add", new String[0], movie, login, pw);
    }

    @Override
    public String toString() {
        return "Добавляет новый элемент в коллекцию";
    }
}
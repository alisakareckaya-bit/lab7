package client.commands;

import client.interfaces.Comand;
import common.CommandPacket;
import common.Movie;
public class Add implements Comand {

    private Movie movie;
    @Override
    public CommandPacket implementCommand(String[] arg) {
        Movie movie = new Movie();
        return new CommandPacket("add", null, movie);
    }
    @Override
    public String toString() {
        return "Добавляет новый элемент в коллекцию";
    }
}

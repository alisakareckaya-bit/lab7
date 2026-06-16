package client.commands;

import client.Client;
import client.interfaces.Comand;
import common.CommandPacket;
import common.Movie;

public class RemoveLower implements Comand {

    @Override
    public CommandPacket implementCommand(String[] args, String login, String pw) {
        Movie mov = new Movie();
        if (args.length == 0) {
            Client.inout.write("Введите элемент, с которым будут сравниваться другие");
            return new CommandPacket("remove_lower", null, mov, login, pw);
        }else {
            Client.inout.write("У этой команды отсутствуют параметры, но мы все равно ее выполним, а ты запомни на будущее;)\n");
            return new CommandPacket("remove_lower", null, mov, login, pw);
        }
    }

    @Override
    public String toString() {
        return "Удаляет из коллекции все элементы, меньшие, чем заданный";
    }
}

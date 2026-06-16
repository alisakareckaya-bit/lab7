package client.commands;

import client.Client;
import client.interfaces.Comand;
import common.CommandPacket;

public class Info implements Comand {

    @Override
    public CommandPacket implementCommand(String[] arg, String login, String pw) {
        if (arg.length == 0) {
            return new CommandPacket("info", null, null, login, pw);
        } else {
            Client.inout.write("У этой команды нет параметров, но мы все равно ее выполнили. Знай на будущее)");
            return new CommandPacket("info", null, null, login, pw);
        }
    }
    @Override
    public String toString () {
        return "Выводит информацию о коллекции";
    }
}

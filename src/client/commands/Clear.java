package client.commands;


import client.Client;
import client.interfaces.Comand;
import common.CommandPacket;

public class Clear implements Comand {
    @Override
    public CommandPacket implementCommand(String[] args, String login, String pw) {
        if (args.length == 0) {
            return new CommandPacket("clear", null, null, login, pw);
        } else {
            Client.inout.write("У этой команды нет параметров, но мы все равно ее выполнили. Знай на будущее)");
            return new CommandPacket("clear", null, null, login, pw);
        }
    }
}

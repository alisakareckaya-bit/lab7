package client.commands;


import client.Client;
import client.interfaces.Comand;
import common.CommandPacket;

public class Clear implements Comand {
    @Override
    public CommandPacket implementCommand(String[] args) {
        if (args.length==0) {
            return new CommandPacket("clear", null, null);
        } else {
            Client.inout.write("У этой команды нет параметров, но мы все равно ее выполнили. Знай на будущее)");
            return new CommandPacket("clear", null, null);
        }
    }
    @Override
    public String toString() {
        return "Очищает все элементы коллекции";
    }
}

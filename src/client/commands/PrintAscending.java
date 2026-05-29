package client.commands;


import client.interfaces.Comand;
import common.CommandPacket;

public class PrintAscending implements Comand {


    @Override
    public CommandPacket implementCommand(String[] args) {

        return new CommandPacket("print_ascending", null, null);
    }

    @Override
    public String toString() {
        return "Вывести элементы коллекции в порядке возрастания";
    }
}

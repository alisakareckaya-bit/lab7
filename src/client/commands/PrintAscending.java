package client.commands;


import client.interfaces.Comand;
import common.CommandPacket;

public class PrintAscending implements Comand {


    @Override
    public CommandPacket implementCommand(String[] args, String login, String pw) {

        return new CommandPacket("print_ascending", null, null, login, pw);
    }

    @Override
    public String toString() {
        return "Вывести элементы коллекции в порядке возрастания";
    }
}

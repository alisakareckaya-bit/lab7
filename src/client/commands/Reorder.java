package client.commands;

import client.interfaces.Comand;
import common.CommandPacket;

public class Reorder implements Comand {
    @Override
    public CommandPacket implementCommand(String[] args, String login, String pw) {

        return new CommandPacket("reorder", null, null, login, pw);
    }

    @Override
    public String toString() {
        return "Отсортировывает коллекцию в порядке, обратном нынешнему";
    }
}

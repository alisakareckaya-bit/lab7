package client.commands;

import client.interfaces.Comand;
import common.CommandPacket;

public class Sort implements Comand {

    @Override
    public CommandPacket implementCommand(String[] args, String login, String pw) {
        return new CommandPacket("sort", null, null, login, pw);
    }

    @Override
    public String toString() {
        return "Сортирует коллекцию в естественном порядке";
    }
}

package client.commands;

import client.interfaces.Comand;
import common.CommandPacket;

public class Sort implements Comand {

    @Override
    public CommandPacket implementCommand(String[] args) {
        return new CommandPacket("sort", null, null);
    }

    @Override
    public String toString() {
        return "Сортирует коллекцию в естественном порядке";
    }
}

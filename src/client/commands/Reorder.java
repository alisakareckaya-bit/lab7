package client.commands;

import client.interfaces.Comand;
import common.CommandPacket;

public class Reorder implements Comand {
    @Override
    public CommandPacket implementCommand(String[] args) {

        return new CommandPacket("reorder", null, null);
    }

    @Override
    public String toString() {
        return "Отсортировывает коллекцию в порядке, обратном нынешнему";
    }
}

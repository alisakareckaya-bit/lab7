package client.commands;

import client.Client;
import client.interfaces.Comand;
import common.CommandPacket;

public class Help implements Comand {
    @Override
    public CommandPacket implementCommand(String[] args) {
        Client.inout.write("Доступные команды приложения:");
        for (String cmd : Client.parser.getCommands().keySet()) {
            Client.inout.write(cmd + ": " + Client.parser.getCommands().get(cmd).toString());

        }
        return null;
    }
    @Override
    public String toString() {
        return "Выводит список всех доступных команд";
    }
}
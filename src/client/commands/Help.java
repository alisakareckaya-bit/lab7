package client.commands;

import client.Client;
import client.interfaces.Comand;
import common.CommandPacket;

public class Help implements Comand {
    @Override
    public CommandPacket implementCommand(String[] args, String login, String pw) {
        return new CommandPacket("help", null, null, login, pw);
    }
    @Override
    public String toString() {
        return "Выводит список всех доступных команд";
    }
}
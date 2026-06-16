package client.commands;

import client.interfaces.Comand;
import common.CommandPacket;

public class Exit implements Comand {
    @Override
    public CommandPacket implementCommand(String[] args, String login, String pw){
        System.exit(0);
        return null;
    }
    @Override
    public String toString() {
        return "Завершения работы клиентского приложения";
    }
}

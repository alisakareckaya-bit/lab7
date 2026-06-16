package client.commands;


import client.Client;
import client.interfaces.Comand;
import client.managers.CheckValue;
import common.CommandPacket;
import common.Movie;

import java.util.Stack;

public class Update implements Comand {

    private String keyStr;

    @Override
    public CommandPacket implementCommand(String[] args, String login, String pw) {
        if (args.length == 0) {
            Client.inout.write("Введите ID элемента для обновления:\n");
            keyStr = CheckValue.checkValuesNull("id объекта, который хотите обновить:");
        } else if (args.length == 1) {
            keyStr = args[0].trim();
        } else {
            Client.inout.write("Было введено больше одного параметра, все превышающие параметры не учитываются\n");
            keyStr = args[0].trim();
        }

        try {
            Integer.parseInt(keyStr);
            String[] id = new String[]{keyStr};
            Movie movie = new Movie();
            return new CommandPacket("update", id, movie, login, pw);
        } catch (NumberFormatException e) {
            Client.inout.write("id является типом int");
            return null;
        }
    }

    @Override
    public String toString() {
        return "Обновляет значение элемента коллекции, id которого равен заданному";
    }
}

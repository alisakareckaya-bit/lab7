package client.commands;

import client.Client;
import client.interfaces.Comand;
import client.managers.CheckValue;
import common.CommandPacket;

public class Remove implements Comand {


    private String idStr;

    @Override
    public CommandPacket implementCommand(String[] arg) {
        if (arg.length == 0) {
            Client.inout.write("Введите id объекта, который хотите удалить:\n");
            idStr = CheckValue.checkValuesNull("id объекта, который хотите удалить,");
        } else if (arg.length == 1) {
            idStr = arg[0].trim();
        } else {
            Client.inout.write("Было введено больше одного параметра, все превышающие параметры не учитываются\n");
            idStr = arg[0].trim();
        }

        try {
            int id = Integer.parseInt(idStr);
            String[] ids = {idStr};
            return new CommandPacket("remove", ids, null);

        } catch (NumberFormatException e) {
            Client.inout.write("id не является типам int");
            return null;
        }
    }


    @Override
    public String toString() {
        return "Удаляет элемент из коллекции по его id";
    }
}

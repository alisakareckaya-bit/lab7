package client.commands;

import client.Client;
import client.interfaces.Comand;
import common.CommandPacket;

public class CountLessOscar implements Comand {

    @Override
    public CommandPacket implementCommand(String[] args, String login, String pw) {
            if (args == null || args.length == 0) {
                Client.inout.write("Не указано значение oscarsCount.\n");
                return null;
            }

            long targetOscarsCount;
            try {
                targetOscarsCount = Long.parseLong(args[0]);
                if (targetOscarsCount < 0) {
                    Client.inout.write("Количество Оскаров не может быть отрицательным.\n");
                    return null;
                }
            } catch (NumberFormatException e) {
                Client.inout.write("Значение должно быть целым числом типа long.\n");
                return null;
            }
            String[] sendArgs = {String.valueOf(targetOscarsCount)};
            return new CommandPacket("count_oscar", sendArgs, null, login, pw);
        }


        @Override
        public String toString () {
            return "Выводит количество элементов, значение поля oscarsCount которых меньше заданного";

    }
}
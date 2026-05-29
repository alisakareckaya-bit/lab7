package client.commands;


import client.Client;
import client.interfaces.Comand;
import common.CommandPacket;

public class RemoveGoldenPalm implements Comand {

    @Override
    public CommandPacket implementCommand(String[] args) {
        if (args == null || args.length == 0) {
            Client.inout.write("Не указано значение goldenPalmCount.\n");
            return null;
        }

        int targetGoldenPalmCount;
        try {
            targetGoldenPalmCount = Integer.parseInt(args[0]);
            if (targetGoldenPalmCount < 0) {
                Client.inout.write("Количество Золотых Пальм не может быть отрицательным.\n");
                return null;
            }
        } catch (NumberFormatException e) {
            Client.inout.write("Значение должно быть типа int.\n");
            return null;
        }
        String[] sendArgs = {String.valueOf(targetGoldenPalmCount)};
        return new CommandPacket("remove_palm", sendArgs, null);
    }



    @Override
    public String toString() {
        return "Удаляет из коллекции все элементы, значение поля goldenPalmCount которого эквивалентно заданному";
    }
}

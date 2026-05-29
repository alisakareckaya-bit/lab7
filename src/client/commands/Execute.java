package client.commands;

import client.Client;
import client.interfaces.Comand;
import client.managers.CheckValue;
import common.CommandPacket;

public class Execute implements Comand {
    private String filename;
    @Override
    public CommandPacket implementCommand(String[] args) {
        if (args.length == 0) {
            Client.inout.write("Введите название файла, из которого хотите читать скрипт:\n");
            filename = CheckValue.checkValuesNull("название файла, из которого хотите читать скрипт");
        } else if (args.length == 1) {
            filename = args[0].trim();
        } else {
            Client.inout.write("Вы ввели больше аргументов, чем надо, первый будет принят как название скрипта, а остальные будут откинуты\n");
            filename = args[0].trim();
        }

        Client.inout.startFileReading(filename);

        if (!Client.inout.isReadingFromFile()) {
            Client.inout.stopFileReading(filename);
            return null;
        }

        boolean hadError = false;
        while (Client.inout.hasNextLine()) {
            String command = Client.inout.read();
            if (command.trim().isEmpty()) {
                continue;
            }

            Client.run(Client.parser.parse(command));

            if (Client.inout.isScriptHasError()) {
                Client.inout.write("В файле была совершена ошибка ввода данных.\n");
                hadError = true;
                return null;
            }
        }

        Client.inout.stopFileReading(filename);

        if (!hadError) {
            Client.inout.setScriptError(false);
        }
        return new CommandPacket("execute", null, null);
    }

    @Override
    public String toString() {
        return "Считать и исполнить скрипт из указанного файла.";
    }
}
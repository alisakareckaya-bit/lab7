package server.meneger;

import common.CommandPacket;
import common.ResponsPacket;
import server.Server;
import server.comnds.*;
import server.interfcs.Comands;

import java.util.HashMap;
import java.util.Map;

public class ParseManagerServer {

    private Map<String, Comands> commands;
    private final CollectionManager collectionManager;

    public ParseManagerServer(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
        commands = new HashMap<>();
        commands.put("help", new Help());
        commands.put("info", new Info());
        commands.put("show", new Show(collectionManager));
        commands.put("add", new Add(collectionManager));
        commands.put("update", new Update());
        commands.put("remove", new Remove());
        commands.put("remove_lower", new RemoveLower());
        commands.put("sort", new Sort());
        commands.put("reorder", new Reorder());
        commands.put("print_ascending", new PrintAscending());
        commands.put("count_oscar", new CountLessOscar());
        commands.put("remove_palm", new RemoveGoldenPalm());
        commands.put("execute", new Execute());
        commands.put("clear", new Clear());
    }
    public ResponsPacket parseCommand(CommandPacket comPac){
        if (comPac == null || comPac.getType() == null) {
            return new ResponsPacket("Ошибка: Сервер получил пустой пакет команды", null);
        }
        String command_name = comPac.getType().toLowerCase().trim();

        if (this.commands.containsKey(command_name)) {
            Comands command = this.commands.get(command_name);

            ResponsPacket responsePacket = command.executer(comPac);
            return responsePacket;
        }else {
            Server.logger.info("Неизвестная команда");
            return new ResponsPacket("Неизвестная команда на сервере", null);
        }
    }

    public Map<String, Comands> getCommands() {
        return commands;
    }
}
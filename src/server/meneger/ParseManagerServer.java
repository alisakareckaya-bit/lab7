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
        commands.put("login", new Login());
        commands.put("registration", new Registration());
    }
    public ResponsPacket parseCommand(CommandPacket comPac){
        String command_name = comPac.getType().toLowerCase().trim();
        if (comPac == null || comPac.getType() == null) {
            return new ResponsPacket("Ошибка: Сервер получил пустой пакет команды", null);
        }
        // 2. ПОТОМ проверяем и создаем таблицы
        if (!DatabaseManager.getInstance().checkAndCreateTables()) {
            Server.logger.error("Не удалось проверить/создать таблицы");
            return new ResponsPacket("Ошибка инициализации базы данных", null);
        }
        if (!command_name.equals("login") && !command_name.equals("registration")){
            if (!DatabaseManager.getInstance().repeatConnect()) {
                return new ResponsPacket("База данных не доступна", null);
            }
            if (!DatabaseManager.getInstance().checkUserPassword(comPac.getLog(),comPac.getPassword())){
                Server.logger.error("Пароль не совпал с необходимым");
                return new ResponsPacket("Пароль не совпадает с необходимым", 9000);
            }
        }


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
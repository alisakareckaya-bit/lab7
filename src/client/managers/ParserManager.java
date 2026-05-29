package client.managers;

import client.Client;
import client.commands.*;
import client.interfaces.Comand;
import common.CommandPacket;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс, отвечающий за распознавание и запуск команд.
 * Содержит реестр всех доступных команд приложения и сопоставляет текстовый ввод
 * пользователя с конкретными объектами-реализациями интерфейса {@link Comand}.
 *
 * @author Алиса
 * @version 1.0
 */
public class ParserManager {

    /** Словарь, где ключ — строковое имя команды, а значение — объект класса этой команды */
    private Map<String, Comand> commands;

    /**
     * Конструктор парсера.
     * Инициализирует карту и регистрирует в ней все доступные операции приложения.
     */
    public ParserManager() {
        commands = new HashMap<>();
        commands.put("exit", new Exit());
        commands.put("help", new Help());
        commands.put("info", new Info());
        commands.put("show", new Show());
        commands.put("add", new Add());
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

    /**
     * Разбирает входную строку, выделяет имя команды и передает управление соответствующему объекту.
     * Осуществляет нормализацию строки (удаление лишних пробелов) и разделение на имя команды и аргументы.
     *
     * @param line сырая строка текста, введенная пользователем или прочитанная из скрипта
     * @return {@code true}, если команда найдена и выполнена;
     *         {@code false}, если команда не существует или в процессе её выполнения возникла ошибка
     */
    public CommandPacket parse(String line) {
        String[] command = line.trim().replaceAll("\\s++", " ").split(" ");

        if (commands.containsKey(command[0])) {
            Comand com = commands.get(command[0]);
            try {
                return com.implementCommand(Arrays.copyOfRange(command, 1, command.length));
            } catch (Exception e) {
                Client.inout.write("Ошибка при формировании команды\n");
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Предоставляет доступ к полному списку зарегистрированных команд.
     * Используется для формирования справочного сообщения в команде {@link Help}.
     *
     * @return карта всех доступных команд приложения
     */
    public Map<String, Comand> getCommands() {
        return commands;
    }
}

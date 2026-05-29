package server.comnds;

import common.CommandPacket;
import common.ResponsPacket;
import server.Server;
import server.interfcs.Comands;

public class Help implements Comands {
    @Override
    public ResponsPacket executer(CommandPacket command) {
        StringBuilder text = new StringBuilder();
        for (Comands cmd : Server.parseManagerServer.getCommands().values()) {
            text.append(cmd.toString()).append("\n");
        }

        return new ResponsPacket(text.toString(), null);
    }
    @Override
    public String toString() {
        return "help : Выводит список всех доступных команд";
    }
}

package server.comnds;

import common.CommandPacket;
import common.ResponsPacket;
import server.Server;
import server.interfcs.Comands;

public class Help implements Comands {
    String text;
    @Override
    public ResponsPacket executer(CommandPacket command) {
        text = "";
        for (Comands i : Server.parseManagerServer.getCommands().values()) {
            if(i.getClass()!= Login.class && i.getClass()!= Registration.class){
                text = text + i.toString() + "\n";
            }
        }
        return new ResponsPacket(text, null);
    }
    @Override
    public String toString() {
        return "help : Выводит список всех доступных команд";
    }
}

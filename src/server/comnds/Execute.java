package server.comnds;

import common.CommandPacket;
import common.ResponsPacket;
import server.interfcs.Comands;


public class Execute implements Comands {
    @Override
    public ResponsPacket executer(CommandPacket commandPacket) {
        return new ResponsPacket("Было выполнено чтение из скрипта.",null);
    }

    @Override
    public String toString(){return "Выполняет скрипт из указанного вами файла.";}
}

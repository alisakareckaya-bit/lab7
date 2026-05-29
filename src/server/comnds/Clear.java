package server.comnds;

import common.CommandPacket;
import common.ResponsPacket;
import server.Server;
import server.interfcs.Comands;

public class Clear implements Comands {

    @Override
    public ResponsPacket executer(CommandPacket command) {
            Server.getCollectionManager().clear();
        Server.logger.warn("Пользователь запросил полную очистку коллекции.");
            return new ResponsPacket("Коллекция очищена.", null);
        }
        @Override
        public String toString(){
            return "clear: Очищает словарь";
        }
    }

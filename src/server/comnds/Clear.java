package server.comnds;

import common.CommandPacket;
import common.ResponsPacket;
import server.Server;
import server.interfcs.Comands;

public class Clear implements Comands {

    @Override
    public ResponsPacket executer(CommandPacket command) {
        try{
            String author = command.getLog();
            Server.dbman.clearDB(author);
            Server.getCollectionManager().setMovie(Server.dbman.getDB());
            return new ResponsPacket("Все ваши элементы были удалены из коллекции", null);
        } catch (Exception e) {
            return new ResponsPacket("Ошибка при отчистке коллекции", null);
        }
        }
        @Override
        public String toString(){
            return "clear: Очищает словарь";
        }
    }

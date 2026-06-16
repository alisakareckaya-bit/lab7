package server.comnds;


import common.CommandPacket;
import common.ResponsPacket;
import server.Server;
import server.interfcs.Comands;

public class Info implements Comands {
    String text = "";
    @Override
    public ResponsPacket executer(CommandPacket commandPacket) {
        text = "Тип коллекции: Stack \n"
                + "Количество элементов: " + Server.getCollectionManager().getMovie().size() + "\n"
                + "Время создания коллекции: " + Server.getCollectionManager().getTime();
        return new ResponsPacket("Информация о коллекции", text);
    }
    @Override
    public String toString() {
        return "info: выводит информацию о коллекции (тип, дата инициализации, количество элементов)";
    }
}

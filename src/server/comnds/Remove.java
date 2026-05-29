package server.comnds;

import common.CommandPacket;
import common.ResponsPacket;
import server.Server;
import server.interfcs.Comands;

public class Remove implements Comands {
    @Override
    public ResponsPacket executer(CommandPacket commandPacket) {
        String[] args = commandPacket.getArgs();
        int id = Integer.parseInt(args[0]);

        if (Server.getCollectionManager().removeById(id)) {
                return new ResponsPacket("Элемент с id " + id + " успешно удалён.\n", null);
            } else {
            return new ResponsPacket("Элемент с id " + id + " не найден в коллекции.\n", null);
        }

    }

    @Override
    public String toString() {
        return "Удаляет элемент из коллекции по его id";
    }
}

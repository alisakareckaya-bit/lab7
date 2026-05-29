package server.interfcs;

import common.CommandPacket;
import common.ResponsPacket;


public interface Comands {
    ResponsPacket executer(CommandPacket command);
}

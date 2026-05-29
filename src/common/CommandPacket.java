package common;

import java.io.Serializable;

public class CommandPacket implements Serializable {
    private String type;
    private String[] args;
    private Movie movie;
    public CommandPacket(String type, String[] args, Movie movie){
        this.args = args;
        this.movie = movie;
        this.type = type;
    }

    public String getType(){return type;}

    public String[] getArgs() {
        return args;
    }

    public Movie getMovie() {
        return movie;
    }
    @Override
    public String toString() {
        return "CommandPacket{type='" + type + "', argsCount=" + (args != null ? args.length : 0) + "}";
    }
}

package common;

import java.io.Serializable;

public class CommandPacket implements Serializable {
    private String type;
    private String[] args;
    private Movie movie;
    private String log;
    private String password;
    public CommandPacket(String type, String[] args, Movie movie, String log, String password){
        this.args = args;
        this.movie = movie;
        this.type = type;
        this.log = log;
        this.password = password;
    }

    public String getType(){return type;}

    public String[] getArgs() {
        return args;
    }

    public Movie getMovie() {
        return movie;
    }
    public String getPassword() {
        return password;
    }

    public String getLog() {
        return log;
    }
    @Override
    public String toString() {
        return "CommandPacket{type='" + type + "', argsCount=" + (args != null ? args.length : 0) + "}";
    }
}

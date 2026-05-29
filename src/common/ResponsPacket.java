package common;

import java.io.Serializable;

public class ResponsPacket implements Serializable {
    private String message;
    private Object data;

    public ResponsPacket(String message, Object data){
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}

package server.comnds;

import common.CommandPacket;
import common.ResponsPacket;
import server.Server;
import server.interfcs.Comands;
/**
 * Команда для входа пользователя в систему.
 * Проверяет логин и пароль через базу данных.
 * При успехе возвращает код 1 для сохранения сессии.
 */
public class Login implements Comands {
    /**
     * Выполняет аутентификацию пользователя.
     *
     * @param commandPacket пакет с логином и паролем
     * @return ResponsPacket с сообщением и кодом 1 при успешном входе,
     *         либо с сообщением об ошибке
     */
    @Override
    public ResponsPacket executer(CommandPacket commandPacket){
            String author = commandPacket.getLog();
            String pw = commandPacket.getPassword();

            boolean success = Server.dbman.checkUserPassword(author, pw);

            if (success){
                Server.logger.info("Успешный вход {}",author);
                return new ResponsPacket("Успешно вошли в аккаунт", 1);
            } else {
                return new ResponsPacket("Неверный логин или пароль (либо БД недоступна)", null);
            }

    }
    @Override
    public String toString(){
        return "";
    }
}

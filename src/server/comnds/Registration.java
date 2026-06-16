package server.comnds;

import common.CommandPacket;
import common.ResponsPacket;
import server.Server;
import server.interfcs.Comands;
import server.meneger.PasswdManager;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;
/**
 * Команда для регистрации нового пользователя.
 * Генерирует случайную соль, хэширует пароль и сохраняет данные в БД.
 */
public class Registration implements Comands {
    /**
     * Генератор криптографически безопасных случайных чисел для создания соли.
     */
    private static final SecureRandom random = new SecureRandom();
    /**
     * Выполняет регистрацию.
     * Создаёт соль (16 байт), хэширует пароль через SHA-1 с солью,
     * сохраняет логин, хэш и соль в таблицу users.
     *
     * @param commandPacket пакет с логином и паролем пользователя
     * @return ResponsPacket с сообщением об успехе (data=1) или ошибке
     */
    @Override
    public ResponsPacket executer(CommandPacket commandPacket){
        String author = commandPacket.getLog();
        String pw = commandPacket.getPassword();
        try {
            Connection connect = Server.dbman.getConnection();
            if (connect == null) {
                return new ResponsPacket("База данных не доступна",null);
            }
            String insertUser = """
                    insert into users(login,password,salt) values (?,?,?);
                    """;

            byte[] byteSalt = new byte[16];
            random.nextBytes(byteSalt);
            String salt = Base64.getEncoder().encodeToString(byteSalt);
            String hashPassword = PasswdManager.doSecret(pw,salt);
            try (PreparedStatement ps = connect.prepareStatement(insertUser)) {
                ps.setString(1, author);
                ps.setString(2, hashPassword);
                ps.setString(3, salt);
                ps.executeUpdate();
            }
            Server.logger.info("Успешная регистрация {}", author);
            return new ResponsPacket("Пользователь зарегистрирован", 1);

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                return new ResponsPacket("Ошибка: Пользователь с таким логином уже существует", null);
            }
            return new ResponsPacket("Ошибка в регистрации ", null);
        }
    }



    @Override
    public String toString() {
        return "";
    }
}

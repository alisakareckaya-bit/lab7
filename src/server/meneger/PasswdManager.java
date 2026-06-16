package server.meneger;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Properties;
/**
 * Менеджер для безопасного хэширования паролей с использованием SHA-1,
 * соли и перца (секретного ключа из файла paper.properties).
 */
public class PasswdManager {
    /**
     * Секретный ключ (pepper), загружаемый из paper.properties.
     */
    private static final String PAPER = readPaper();
    /**
     * Загружает pepper из файла или возвращает значение по умолчанию.
     *
     * @return строка pepper для хэширования
     */
    private static String readPaper(){
        Properties prop = new Properties();
        try (FileInputStream input = new FileInputStream("paper.properties")) {
            prop.load(input);
            String PEPPER = prop.getProperty("secret.pepper");
            return PEPPER;
        } catch (IOException ex) {
            return "paper_for_seventh_laba";
        }
    }
    /**
     * Хэширует пароль с солью по алгоритму SHA-1.
     *
     * @param pw    пароль пользователя
     * @param SALT  соль (уникальная для каждого пользователя)
     * @return хэш в Base64 или null при ошибке
     */
    public static String doSecret(String pw, String SALT){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest((PAPER+pw+SALT).getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}

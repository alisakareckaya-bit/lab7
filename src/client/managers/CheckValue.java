package client.managers;

import client.Client;

/**
 * Вспомогательный класс для валидации данных, вводимых пользователем.
 * Обеспечивает проверку на пустые строки и принудительный повторный ввод.
 *
 * @author Алиса
 * @version 1.0
 */
public class CheckValue {

    /**
     * Проверяет считанную из потока ввода строку на пустоту.
     * Если пользователь вводит пустую строку или строку из пробелов, метод выводит предупреждение
     * и запрашивает ввод повторно до тех пор, пока не будет получено валидное значение.
     *
     * @param name имя поля, для которого проверяется значение (используется в тексте ошибки)
     * @return очищенная от лишних пробелов (trimmed) непустая строка
     */
    public static String checkValuesNull(String name) {
        String test = Client.inout.read();
        while (test.trim().isEmpty()) {
            Client.inout.write(name + " не может быть null\n");
            Client.inout.write("Введите значение " + name + " :\n");
            test = Client.inout.read();
        }
        return test.trim();
    }
}

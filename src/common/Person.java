package common;



import client.Client;
import client.managers.CheckValue;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс, представляющий личность (оператора фильма).
 * Содержит персональные данные, включая уникальный номер паспорта.
 * Реализует интерфейс {@link Comparable} для сравнения операторов по характеристикам.
 *
 * @author Алиса
 * @version 1.0
 */
public class Person implements Comparable<Person>, Serializable {

    /** Имя личности. Не null, не может быть пустым. */
    private String name;

    /** Рост личности. Не null, значение должно быть больше 0. */
    private Integer height;

    /**
     * Уникальный идентификатор паспорта.
     * Не пустой, длина до 35 символов, уникален для всей программы.
     */
    private String passportID;

    /** Цвет волос. Может быть null. */
    private Color hairColor;

    /** Национальность (страна). Может быть null. */
    private Country nationality;

    /** Набор для контроля уникальности паспортных данных на уровне всех экземпляров класса. */
    private static Set<String> passportSet = new HashSet<>();

    /**
     * Конструктор для интерактивного создания личности через консоль.
     * Запускает последовательный опрос всех полей пользователя.
     */
    public Person() {
        Client.inout.write("Введите данные оператора фильма\n");
        setName();
        setHeight();
        setPassport();
        setColor();
        setCoutry();
    }

    /**
     * Конструктор для программного создания объекта.
     *
     * @param name имя
     * @param height рост
     * @param passportID номер паспорта (должен быть уникальным)
     * @param hairColor цвет волос
     * @param nationality национальность
     */
    public Person(String name, Integer height, String passportID, Color hairColor, Country nationality) {
        this.name = name;
        this.height = height;
        this.passportID = passportID;
        this.hairColor = hairColor;
        this.nationality = nationality;
    }

    /**
     * Запрашивает и устанавливает паспортные данные.
     * Проверяет уникальность через {@link #passportSet} и длину строки (до 35 символов).
     */
    private void setPassport() {
        Client.inout.write("Введите паспортные данные оператора:\n");
        String passportID = Client.inout.read();
        while (passportID.length() > 35 || passportID.trim().isEmpty() || passportSet.contains(passportID)) {
            if (passportSet.contains(passportID)) {
                Client.inout.write("Паспортные данные оператора '" + passportID + "' уже используются\n");
            } else {
                Client.inout.write("Данные не должны быть пустой строкой и не должны быть больше 35 знаков\n");
            }
            Client.inout.write("Введите паспортные данные оператора:\n");
            passportID = Client.inout.read();
        }
        this.passportID = passportID;
        passportSet.add(passportID);
    }

    /**
     * Устанавливает рост с проверкой на положительное целое число.
     */
    private void setHeight() {
        Integer height;
        while (true) {
            Client.inout.write("Введите рост оператора:\n");
            String testHeight = CheckValue.checkValuesNull("роста оператора");
            try {
                height = Integer.parseInt(testHeight);
                if (height <= 0) {
                    Client.inout.write("Рост оператора не может быть меньше или равен 0\n");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                Client.inout.write("Рост оператора должен быть числом типа Integer\n");
            }
        }
        this.height = height;
    }

    /** Устанавливает имя оператора. */
    private void setName() {
        Client.inout.write("Введите имя оператора:\n");
        name = CheckValue.checkValuesNull("имя оператора");
    }

    /** Устанавливает цвет волос на основе выбора пользователя. */
    private void setColor() {
        Client.inout.write("Выберите цвет волос оператора: blue, yellow, white\n");
        String test = CheckValue.checkValuesNull("цвет волос ");
        this.hairColor = switch (test.toLowerCase()) {
            case "blue" -> Color.BLUE;
            case "yellow" -> Color.YELLOW;
            case "white" -> Color.WHITE;
            default -> {
                Client.inout.write("Введено неверное значение. Было выбрано значение по умолчанию: white\n");
                yield Color.WHITE;
            }
        };
    }

    /** Устанавливает национальность на основе выбора пользователя. */
    private void setCoutry() {
        Client.inout.write("Выберите национальность оператора: uk, spain, china, south_korea, north_korea\n");
        String testCount = CheckValue.checkValuesNull("национальности ");
        this.nationality = switch (testCount.toLowerCase()) {
            case "uk" -> Country.UNITED_KINGDOM;
            case "spain" -> Country.SPAIN;
            case "china" -> Country.CHINA;
            case "south_korea" -> Country.SOUTH_KOREA;
            case "north_korea" -> Country.NORTH_KOREA;
            default -> {
                Client.inout.write("Введено неверное значение. Было выбрано значение по умолчанию: north_korea\n");
                yield Country.NORTH_KOREA;
            }
        };
    }

    public Color getHairColor() { return hairColor; }
    public Country getNationality() { return nationality; }
    public String getPassportID() { return passportID; }
    public String getName() { return name; }
    public Integer getHeight() { return height; }

    @Override
    public String toString() {
        return "Данные оператора: "
                + "имя: " + name + ", "
                + "рост: " + height + ", "
                + "цвет волос: " + hairColor + ", "
                + "национальность: " + nationality + ", "
                + "паспортные данные: " + passportID;
    }

    /**
     * Сравнивает двух операторов.
     * Логика сравнения: сначала по длине имени, затем по росту.
     *
     * @param person объект для сравнения
     * @return результат сравнения
     */
    @Override
    public int compareTo(Person person) {
        int nameCompare = Integer.compare(this.name.length(), person.name.length());
        if (nameCompare != 0) return nameCompare;
        return Integer.compare(this.height, person.height);
    }
}

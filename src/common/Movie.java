package common;

import client.Client;
import client.managers.CheckValue;

import java.io.Serializable;
import java.time.LocalDate;

public class Movie implements Comparable<Movie>, Serializable {

    /** Уникальный идентификатор. Не null, больше 0, генерируется автоматически. */
    private Integer id;

    /** Название фильма. Не null, не может быть пустым. */
    private String name;

    /** Координаты фильма. Не null. */
    private Coordinates coordinates;

    /** Дата создания объекта. Не null, генерируется автоматически. */
    private LocalDate creationDate;

    /** Количество премий Оскар. Значение должно быть больше 0. */
    private long oscarsCount;

    /** Количество наград «Золотая пальмовая ветвь». Значение должно быть больше 0. */
    private int goldenPalmCount;

    /** Продолжительность фильма. Не null, больше 0. */
    private Long length;

    /** Жанр фильма. Не null. */
    private MovieGenre genre;

    /** Оператор фильма. Может быть null. */
    private Person operator;
    private String login;

    /**
     * Конструктор для интерактивного создания объекта.
     * Автоматически генерирует ID и дату создания, остальные поля запрашивает у пользователя.
     */
    public Movie() {
        id = Generatic.generateId();
        setName();
        coordinates = new Coordinates();
        creationDate = LocalDate.now();
        setOscarsCount();
        setGoldenPalmCount();
        setGenre();
        setLength();
        operator = new Person();
    }

    /**
     * Конструктор для программного создания объекта (например, при загрузке из файла).
     *
     * @param id уникальный идентификатор
     * @param name название фильма
     * @param coordinates координаты
     * @param creationDate дата создания
     * @param oscarsCount количество Оскаров
     * @param goldenPalmCount количество Золотых Пальм
     * @param length продолжительность
     * @param genre жанр
     * @param operator оператор
     */
    public Movie(Integer id, String name, Coordinates coordinates, LocalDate creationDate,
                 long oscarsCount, int goldenPalmCount, Long length, MovieGenre genre, Person operator, String login) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.oscarsCount = oscarsCount;
        this.goldenPalmCount = goldenPalmCount;
        this.length = length;
        this.genre = genre;
        this.operator = operator;
        this.login = login;
    }

    /**
     * Интерактивно устанавливает название фильма.
     * @return введенное название
     */
    private String setName() {
        Client.inout.write("Введите название фильма:\n");
        name = CheckValue.checkValuesNull("Название фильма");
        return name;
    }

    /**
     * Интерактивно устанавливает жанр фильма через выбор из предложенных вариантов.
     * В случае неверного ввода устанавливается значение по умолчанию — DRAMA.
     */
    private void setGenre() {
        Client.inout.write("Выберите жанр фильма: drama, musical, adventure, thriller\n");
        String test = CheckValue.checkValuesNull("жанр ");
        this.genre = switch (test.toLowerCase()) {
            case "drama" -> MovieGenre.DRAMA;
            case "musical" -> MovieGenre.MUSICAL;
            case "adventure" -> MovieGenre.ADVENTURE;
            case "thriller" -> MovieGenre.THRILLER;
            default -> {
                Client.inout.write("Введено неверное значение. Было выбрано значение по умолчанию: drama\n");
                yield MovieGenre.DRAMA;
            }
        };
    }

    /**
     * Интерактивно устанавливает количество Оскаров с проверкой на тип и положительное значение.
     */
    private void setOscarsCount() {
        long count;
        while (true) {
            Client.inout.write("Введите количество Оскаров:\n");
            String testOscar = CheckValue.checkValuesNull("количества Оскаров");
            try {
                count = Long.parseLong(testOscar);
                if (count < 0) {
                    Client.inout.write("Количество Оскаров не может быть меньше 0\n");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                Client.inout.write("Количество Оскаров должно быть типа long\n");
            }
        }
        this.oscarsCount = count;
    }

    /**
     * Интерактивно устанавливает количество Золотых Пальм.
     */
    private void setGoldenPalmCount() {
        int count;
        while (true) {
            Client.inout.write("Введите количество Золотых Пальм:\n");
            String testPalm = CheckValue.checkValuesNull("количества Золотых Пальм");
            try {
                count = Integer.parseInt(testPalm);
                if (count < 0) {
                    Client.inout.write("Количество Золотых Пальм не может быть меньше 0\n");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                Client.inout.write("Количество Золотых Пальм должно быть типа int\n");
            }
        }
        this.goldenPalmCount = count;
    }

    /**
     * Интерактивно устанавливает продолжительность фильма.
     */
    private void setLength() {
        Long len;
        while (true) {
            Client.inout.write("Введите длину:\n");
            String testLength = CheckValue.checkValuesNull("длина");
            try {
                len = Long.parseLong(testLength);
                if (len <= 0) {
                    Client.inout.write("Длина не может быть меньше или равна 0\n");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                Client.inout.write("Длина должна быть типа Long\n");
            }
        }
        this.length = len;
    }
    public void setLogin(String login) {
        this.login = login;
    }

    public LocalDate getCreationDate() { return creationDate; }
    public Integer getId() { return id; }
    public long getOscarsCount() { return oscarsCount; }
    public Long getLength() { return length; }
    public MovieGenre getGenre() { return genre; }
    public int getGoldenPalmCount() { return goldenPalmCount; }
    public Person getOperator() { return operator; }
    public String getName() { return name; }
    public Coordinates getCoordinates() { return coordinates; }

    /** @param id уникальный идентификатор для установки (используется при обновлении) */
    public void setId(Integer id) { this.id = id; }

    @Override
    public String toString() {
        return " Данные фильма: "
                + "id: " + id + ", "
                + "имя: " + name + ", "
                + "координаты: (" + coordinates.getX() + ", " + coordinates.getY() + "), "
                + "дата: " + creationDate + ", "
                + "кол-во Оскаров: " + oscarsCount + ", "
                + "кол-во золотых пальм: " + goldenPalmCount + ", "
                + "длина: " + length + ", "
                + "жанр: " + genre + ", "
                + (operator != null ? operator.toString() : "оператор: null") + ", ";
    }
    public String getLogin() {
        return login;
    }
    /**
     * Сравнивает текущий фильм с другим.
     * Порядок сравнения: длина названия, координаты, Оскары, Золотые Пальмы, длина фильма, оператор.
     *
     * @param o другой объект Movie для сравнения
     * @return результат сравнения
     */
    @Override
    public int compareTo(Movie o) {
        int nameCompare = Integer.compare(o.getName().length(), this.name.length());
        if (nameCompare != 0) return nameCompare;

        int coorCompare = o.getCoordinates().compareTo(this.coordinates);
        if (coorCompare != 0) return coorCompare;

        int oscarCompare = Long.compare(o.getOscarsCount(), this.oscarsCount);
        if (oscarCompare != 0) return oscarCompare;

        int goldeCompare = Integer.compare(o.getGoldenPalmCount(), this.goldenPalmCount);
        if (goldeCompare != 0) return goldeCompare;

        int lenghCompare = Long.compare(o.getLength(), this.length);
        if (lenghCompare != 0) return lenghCompare;

        return o.getOperator().compareTo(this.operator);
    }
}

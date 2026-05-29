package common;


import client.Client;
import client.managers.CheckValue;

import java.io.Serializable;

/**
 * Класс, представляющий координаты объекта.
 * Реализует интерфейс {@link Comparable} для возможности сравнения и сортировки локаций.
 *
 * @author Алиса
 * @version 1.0
 */
public class Coordinates implements Comparable<Coordinates>, Serializable {
    private static final long serialVersionUID = 1L;
    /** Координата X. Ограничений по значению нет. */
    private long x;

    /**
     * Координата Y.
     * Ограничение: максимальное значение — 170. Поле не может быть null.
     */
    private Float y;

    /**
     * Конструктор для интерактивного создания координат через консоль.
     * Последовательно запрашивает значения X и Y у пользователя.
     */
    public Coordinates() {
        setX();
        setY();
    }

    /**
     * Конструктор для программного создания координат.
     *
     * @param x координата X (long)
     * @param y координата Y (Float, не больше 170)
     */
    public Coordinates(long x, Float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Запрашивает у пользователя значение X и проверяет его на соответствие типу long.
     * Цикл продолжается до ввода корректных данных.
     */
    private void setX() {
        long x;
        while (true) {
            Client.inout.write("Введите значение X:\n");
            String testX = CheckValue.checkValuesNull("X");
            try {
                x = Long.parseLong(testX);
                break;
            } catch (NumberFormatException e) {
                Client.inout.write("X должно быть типа long\n");
            }
        }
        this.x = x;
    }

    /**
     * Запрашивает у пользователя значение Y и проверяет его на соответствие типу float
     * и установленным ограничениям (максимум 170, отсутствие null).
     */
    private void setY() {
        Client.inout.write("Введите значение Y:\n");
        while (true) {
            try {
                String testY = CheckValue.checkValuesNull("Y").replace(",", ".");
                Float y = Float.parseFloat(testY);

                if (y > 170) {
                    Client.inout.write("Y не может быть больше 170\n");
                    Client.inout.write("Введите значение Y для координат:\n");
                } else {
                    this.y = y;
                    break;
                }
            } catch (NumberFormatException e) {
                Client.inout.write("Y должно быть типа float\n");
            }
        }
    }

    /** @return значение координаты Y */
    public Float getY() {
        return y;
    }

    /** @return значение координаты X */
    public long getX() {
        return x;
    }

    /**
     * Сравнивает текущий объект координат с другим.
     * Сначала производится сравнение по координате X, затем по Y.
     *
     * @param o объект для сравнения
     * @return отрицательное число, ноль или положительное число в зависимости от результата сравнения
     */
    @Override
    public int compareTo(Coordinates o) {
        int xComp = Long.compare(o.getX(), x);
        if (xComp != 0) {
            return xComp;
        }

        int yComp = Float.compare(o.getY(), y);
        return yComp;
    }
}

package server.meneger;

import common.Movie;

import java.util.*;

/**
 * Класс-менеджер для управления коллекцией объектов {@link Movie}.
 * Хранит коллекцию в виде {@link Stack} и предоставляет методы для манипуляции данными:
 * добавление, очистка, удаление по ID и сортировка.
 *
 * @author Алиса
 * @version 1.0
 */
public class CollectionManager {

    /** Основная коллекция для хранения объектов фильмов */
    private Stack<Movie> Movie;

    /** Время инициализации менеджера (создания коллекции) */
    private Date time;

    /**
     * Конструктор менеджера коллекции.
     * Инициализирует пустой стек фильмов и устанавливает текущую дату как время создания.
     */
    public CollectionManager() {
        Movie = new Stack<>();
        time = new Date();
    }

    /**
     * Добавляет новый фильм в коллекцию (на вершину стека).
     *
     * @param movie объект фильма для добавления
     */
    public void add(Movie movie) {
        Movie.push(movie);
    }

    /**
     * Полностью очищает коллекцию, удаляя все элементы.
     */
    public void clear() {
        Movie.clear();
    }

    /**
     * Возвращает текущую коллекцию фильмов.
     *
     * @return объект {@link Stack}, содержащий все фильмы
     */
    public Stack<Movie> getMovie() {
        return Movie;
    }

    /**
     * Возвращает время создания менеджера коллекции.
     *
     * @return объект {@link Date} с временем инициализации
     */
    public Date getTime() {
        return time;
    }

    /**
     * Удаляет фильм из коллекции по его уникальному идентификатору.
     *
     * @param id идентификатор фильма для удаления
     * @return {@code true}, если элемент был найден и удален; {@code false} в противном случае
     */
    public boolean removeById(long id) {
        return Movie.removeIf(movie -> movie.getId() == id);
    }

    /**
     * Сортирует коллекцию с использованием заданного компаратора.
     *
     * @param compartor объект, определяющий логику сравнения элементов {@link Movie}
     */
    public void sort(Comparator<Movie> compartor) {
        Movie.sort(compartor);
    }
    /**
     * Сортирует коллекцию по длине фильма (естественный порядок).
     */
    public void sortByLength() {
        List<Movie> list = new ArrayList<>(Movie);
        list.sort((m1, m2) -> m1.getLength().compareTo(m2.getLength()));
        Movie.clear();
        Movie.addAll(list);
    }
}

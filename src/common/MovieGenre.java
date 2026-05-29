package common;

import java.io.Serializable;

/**
 * Перечисление доступных жанров кино.
 * Используется для классификации объектов типа {@link Movie}.
 *
 * @author Алиса
 * @version 1.0
 */
public enum MovieGenre  implements Serializable {
    /** Драматический фильм */
    DRAMA,

    /** Музыкальный фильм или мюзикл */
    MUSICAL,

    /** Приключенческий фильм */
    ADVENTURE,

    /** Триллер */
    THRILLER;
}

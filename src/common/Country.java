package common;

import java.io.Serializable;

/**
 * Перечисление стран.
 * Используется для указания географической принадлежности (гражданства) личности.
 *
 * @author Алиса
 * @version 1.0
 */
public enum Country implements Serializable {
    /** Великобритания */
    UNITED_KINGDOM,

    /** Испания */
    SPAIN,

    /** Китай */
    CHINA,

    /** Южная Корея */
    SOUTH_KOREA,

    /** Северная Корея */
    NORTH_KOREA;
}

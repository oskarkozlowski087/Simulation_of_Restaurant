package environment;

/**
 * Typy komórek występujące na planszy restauracji.
 * Określają przeznaczenie danego pola na mapie symulacji.
 */
public enum CellType {
    /** Sala restauracyjna - miejsce dla klientów i stolików */
    HALL,
    /** Ściana - nieprzechodnia przeszkoda */
    WALL,
    /** Miejsce pod stolik */
    TABLE,
    /** Kuchenka - służy do przygotowywania posiłków */
    STOVE,
    /** Bufet (lada) - miejsce przekazywania zamówień między kelnerami a kucharzami */
    BUFFER,
    /** Kuchnia - strefa robocza dla kucharzy */
    KITCHEN
}

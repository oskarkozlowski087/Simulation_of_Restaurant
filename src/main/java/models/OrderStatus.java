package models;

/**
 * Reprezentuje możliwe stany, przez które przechodzi zamówienie w restauracji.
 * Sekwencja: ZLOZONE -> W_BUFORZE -> W_PRZYGOTOWANIU -> GOTOWE -> DOSTARCZONE.
 */
public enum OrderStatus {
    /** Zamówienie zostało złożone przez klienta */
    ZLOZONE,
    /** Zamówienie czeka w buforze na kucharza */
    W_BUFORZE,
    /** Zamówienie jest w trakcie przygotowywania przez kucharza */
    W_PRZYGOTOWANIU,
    /** Danie jest gotowe do odbioru przez kelnera */
    GOTOWE,
    /** Danie zostało dostarczone klientowi */
    DOSTARCZONE
}
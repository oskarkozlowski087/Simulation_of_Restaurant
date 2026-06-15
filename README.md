# Symulacja Restauracji
miejsce na gifa z symulacją do zrobienia

Projekt ten to interaktywna symulacja środowiska restauracyjnego, zbudowana w oparciu o architekturę obiektową w języku Java. 

Głównym założeniem symulacji jest zaprezentowanie, jak zmieniają się koszty, liczba zadowolonych (bądź nie) klientów oraz liczba przygotowanych dań w zależności od początkowych parametrów (np. liczby stolików, kucharzy, kelnerów). Cała symulacja przedstawiona jest graficznie na dwuwymiarowej siatce, aby umożliwić łatwą i przejrzystą obserwację tego, jak zachodzą poszczególne scenariusze w zależności od dobranych ustawień.

## ⚙️ Stack Technologiczny
* **Język:** Java 17
* **GUI:** JavaFX
* **Zarządzanie projektem:** Maven

---

## 🧠 Aktorzy Symulacji i Ich Logika

Całość opiera się na wyraźnym podziale odpowiedzialności, aby ściśle spełniać założenia programowania obiektowego. Wszystkie metody odpowiedzialne za logiczne podejmowanie decyzji znajdują się wewnątrz klas agentów (dziedziczących po `MovingAgent`), którzy co tick symulacji odpalają metodę `scanBoard()`.

### 1. Klient (`Client`)
W pełni samowystarczalna jednostka sterowana dynamicznym parametrem cierpliwości (`patience`).
* **Autonomiczne szukanie miejsca:** Po wejściu do restauracji agenci samodzielnie przeszukują dostępną pulę stolików, fizycznie je "rezerwują" i wyznaczają trasę dojścia.
* **Presja czasu i cykl życia:** Klient traci cierpliwość na różnych etapach (w kolejce do wejścia, czekając na obsługę). Jeśli spadnie ona do zera, agent zwalnia zajmowane zasoby i opuszcza symulację, płynnie przechodząc przez kolejne stany swojego cyklu życia.

### 2. Kelner (`Waiter`)
Agent odpowiadający za dystrybucję dań wydanych przez kucharzy, jak również za zbieranie zamówień bezpośrednio od klientów.
* **Smart Targeting:** Przeszukuje salę w celu oceny poziomu zniecierpliwienia klientów. W pierwszej kolejności obsługuje stoliki, które są najbliżej opuszczenia lokalu z powodu zbyt długiego czasu oczekiwania.
* **Bezpieczne interakcje:** Aby uniknąć błędów wyścigu (np. kilku kelnerów idących do tego samego klienta), zaimplementowano mechanizm wzajemnego zapinania referencji. Kelner fizycznie "rezerwuje" klienta w pamięci, stając się dla innych niewidocznym.

### 3. Kucharz (`Cook`)
Zajmuje się przygotowaniem posiłków. Nasłuchuje zmian na wspólnym buforze wydawczym, symuluje czas obróbki posiłku i modyfikuje statusy zamówień, odkładając je z powrotem na blat.
* **Logika priorytetów:** Kucharz w pierwszej kolejności podejmuje się przygotowania dania, które zostało dostarczone do bufora najwcześniej (kolejkowanie). Każdy agent jest również przypisany do swojej własnej, unikalnej kuchenki.

---

## 🛠️ Rozwiązania Techniczne i Mechaniki

* **Dynamiczny Pathfinding (Manhattan Distance):** Agenci nie są przypisani do statycznych punktów. Używając geometrii miejskiej, w czasie rzeczywistym obliczają, która komórka (np. lady wydawkowej) znajduje się najbliżej ich aktualnej pozycji i to do niej wyznaczają trasę.
* **System Kolizji (Grid Blocking):** Przemieszczanie jest rygorystycznie kontrolowane. Agent nie wejdzie na zajętą kratkę, dzięki czemu symulacja zachowuje pełną przejrzystość, czytelność i logikę przestrzenną.
* **Garbage Collection i "Klienci-Widma":** Projekt rygorystycznie podchodzi do zwalniania referencji. Każda interakcja (np. podanie posiłku) kończy się zresetowaniem połączeń między obiektami Kelnera i Klienta, całkowicie eliminując wycieki pamięci i ryzyko błędów typu `NullPointerException` w sytuacji nagłego opuszczenia lokalu przez klienta.

---

## 🚀 Jak uruchomić projekt

Projekt jest w pełni skonfigurowany pod narzędzie Maven, co rozwiązuje problemy z zarządzaniem zewnętrzną biblioteką JavaFX. Przed startem symulacji, użytkownik może zdefiniować parametry brzegowe (liczbę agentów i zasobów) za pomocą interfejsu graficznego.

Aby uruchomić aplikację z GUI, sklonuj repozytorium i użyj wbudowanego pluginu z poziomu terminala:
```bash
mvn javafx:run

$m2 = "$env:USERPROFILE\.m2\repository"
$mods = "$m2\org\openjfx\javafx-controls\17.0.2\javafx-controls-17.0.2-win.jar;$m2\org\openjfx\javafx-graphics\17.0.2\javafx-graphics-17.0.2-win.jar;$m2\org\openjfx\javafx-base\17.0.2\javafx-base-17.0.2-win.jar"
$cp = "target\classes"

java --module-path "$mods" --add-modules javafx.controls -cp "$cp" gui.RestaurantApp

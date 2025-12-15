@echo off
echo Compilando GymTracker...
javac -cp "lib/*" src/*.java -d .
if %errorlevel% == 0 (
    echo Compilacion exitosa!
    echo Ejecuta con: java -cp ".;lib/*" Main
) else (
    echo Error en la compilacion
    pause
)



#!/usr/bin/env bash
if [ $# -ne 1 ]; then
  echo "Uso: ./probar.sh <nombre-de-la-evidencia> (ejemplo: ./probar.sh mp1-rojo)"; exit 2
fi
nombre="$1"
case "$nombre" in
  *[!A-Za-z0-9_-]* ) echo "Nombre inválido: «$nombre». Usa solo letras, números, _ y - (ejemplo: mp1-rojo)"; exit 2 ;;
esac
archivo="evidencia/$nombre.txt"
mkdir -p evidencia
inicio=$(mktemp)
./mvnw -B -ntp test > "$archivo" 2>&1
codigo=$?
awk -v dir="$PWD/" '
  /BUILD (SUCCESS|FAILURE)/ { if (!fin) print substr($0, index($0, "BUILD")); fin = 1; next }
  /---\s+/                  { next }
  /(AssertionFailedError|MultipleFailuresError): / { sub(/^\t[A-Za-z0-9]*:? /, "", $0); print "      " $0; cont = 0; next }
  /\[INFO\] (\+--|\|)/     { sub(/^\t/, "", $0); print substr($0, 8); next }
  /\[ERROR\] (Failures|Errors):/ { print substr($0, 9); next }
  /\[ERROR\]   [A-Z][A-Za-z0-9]*Test/ { print substr($0, 9); cont = 1; next }
  cont && /^\t/             { if (cont) { l = $0; sub(/^\t/, "", l); print "    " l; cont = 0 } next }
  /Tests run:/              { print substr($0, index($0, "Tests run:")); next }
  /COMPILATION ERROR/       { print "NO COMPILA:"; fin = 1; next }
  /^\[ERROR\] \/.*\.java:\[/ { l = substr($0, 9); sub(dir, "", l); print l; next }
  /  (symbol|required|found|reason):/ { print; next }
' "$archivo"

if [ "$codigo" -ne 0 ] && ! grep -qE '(Tests run:|COMPILATION ERROR)' "$archivo"; then
  echo "Maven falló antes de correr las pruebas. Las últimas 25 líneas:"
  tail -25 "$archivo"
fi
if [ "$codigo" -eq 0 ] && ! grep -q 'Tests run:' "$archivo"; then
  echo "⚠️ No corrió ninguna prueba. (Si ya escribiste pruebas, revisa «Si algo falla».)"
fi
if [ "$codigo" -ne 0 ] && [ -d target/surefire-reports ]; then
  echo; echo "===== DETALLE de cada clase con fallas (target/surefire-reports) ====="
  find target/surefire-reports -name '*.txt' -newer "$inicio" -exec grep -lE '(FAILURE!|ERROR)' {} + | sort | xargs cat 2>/dev/null
fi >> "$archivo"
rm -f "$inicio"
echo "→ salida completa en $archivo"
exit "$codigo"

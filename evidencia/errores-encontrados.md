# Errores encontrados en la versión del compañero

Mi suite (MP-1 a MP-4) atrapó sola 3 de los 5 errores: `ComisionTest.segunElNumeroDeRetiro`, `RetiroTest.retirarExactamenteElLimiteSePermite`, `TransferenciaTest$CuandoSeRechaza.sinSaldoNoCambiaNada`.

| Queja | Prueba que la reproduce | Qué estaba mal en el código (la línea y por qué) | ¿Mi suite ya lo atrapaba? |
|---|---|---|---|
| 1 | `tercerRetiroGratis` | En `comisionDelRetiro`: usaba `< RETIROS_GRATIS` en vez de `<= RETIROS_GRATIS`, cobrando comisión en el 3.er retiro. | Sí (`ComisionTest`) |
| 2 | `retiroExactoDelLimite` | En `retirar`: la condición de límite usaba `>= 0` en lugar de `> 0`, rechazando retiros exactamente iguales a $5,000.00. | Sí (`RetiroTest`) |
| 3 | `transferenciaRechazadaNoCambiaElSaldo` | En `transferir`: restaba el monto al saldo antes de verificar fondos, dejando la cuenta en saldo negativo antes de lanzar la excepción. | Sí (`TransferenciaTest`) |
| 4 | `laComisionApareceEnElHistorial` | En `retirar`: se eliminó el bloque que agregaba el movimiento de tipo `COMISION` al historial cuando la comisión era mayor a cero. | No |
| 5 | `elHistorialNoSePuedeModificar` | En `getMovimientos`: retornaba la lista mutable directa `movimientos` en vez de una copia inmutable con `List.copyOf()`, permitiendo alterarla desde fuera. | No |

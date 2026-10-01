package com.academia.banco;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class RetiroTest {

    @Test
    void retirarRestaDelSaldo() {
        CuentaBancaria cuenta = new CuentaBancaria("Ana");
        cuenta.depositar(new BigDecimal("1000.00"));
        cuenta.retirar(new BigDecimal("300.00"));
        assertEquals(new BigDecimal("700.00"), cuenta.getSaldo());
    }

    @Test
    void retirarCeroSeRechaza() {
        CuentaBancaria cuenta = new CuentaBancaria("Ana");
        cuenta.depositar(new BigDecimal("1000.00"));
        assertThrows(IllegalArgumentException.class, () -> cuenta.retirar(new BigDecimal("0.00")));
    }

    @Test
    void retirarUnMontoNegativoSeRechaza() {
        CuentaBancaria cuenta = new CuentaBancaria("Ana");
        cuenta.depositar(new BigDecimal("1000.00"));
        assertThrows(IllegalArgumentException.class, () -> cuenta.retirar(new BigDecimal("-50.00")));
    }

    @Test
    void sinSaldoSuficienteSeRechazaYNoCambiaNada() {
        CuentaBancaria cuenta = new CuentaBancaria("Ana");
        cuenta.depositar(new BigDecimal("100.00"));
        assertThrows(SaldoInsuficienteException.class, () -> cuenta.retirar(new BigDecimal("150.00")));
        assertAll(
                () -> assertEquals(new BigDecimal("100.00"), cuenta.getSaldo()),
                () -> assertEquals(0, cuenta.getRetiros()),
                () -> assertEquals(1, cuenta.getMovimientos().size()));
    }

    @Test
    void elMensajeDiceCuantoTienesYCuantoNecesitas() {
        CuentaBancaria cuenta = new CuentaBancaria("Ana");
        cuenta.depositar(new BigDecimal("100.00"));
        SaldoInsuficienteException e = assertThrows(SaldoInsuficienteException.class,
                () -> cuenta.retirar(new BigDecimal("150.00")));
        assertEquals("Saldo insuficiente: tienes $100.00 y se necesitan $150.00", e.getMessage());
    }

    @Test
    void retirarExactamenteElLimiteSePermite() {
        CuentaBancaria cuenta = new CuentaBancaria("Ana");
        cuenta.depositar(new BigDecimal("6000.00"));
        cuenta.retirar(new BigDecimal("5000.00"));
        assertEquals(new BigDecimal("1000.00"), cuenta.getSaldo());
    }

    @Test
    void retirarUnCentavoMasDelLimiteSeRechaza() {
        CuentaBancaria cuenta = new CuentaBancaria("Ana");
        cuenta.depositar(new BigDecimal("6000.00"));
        LimiteExcedidoException e = assertThrows(LimiteExcedidoException.class,
                () -> cuenta.retirar(new BigDecimal("5000.01")));
        assertTrue(e.getMessage().contains("límite") || e.getMessage().contains("limite"));
    }

    @Test
    void unMontoConFraccionDeCentavoSeRechaza() {
        CuentaBancaria cuenta = new CuentaBancaria("Ana");
        cuenta.depositar(new BigDecimal("1000.00"));
        assertThrows(IllegalArgumentException.class, () -> cuenta.retirar(new BigDecimal("10.005")));
    }
}

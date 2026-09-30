package com.academia.banco;

import java.math.BigDecimal;

public record Movimiento(TipoMovimiento tipo, BigDecimal monto) {
}

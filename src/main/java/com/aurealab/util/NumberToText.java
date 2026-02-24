package com.aurealab.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberToText {


    private final String[] UNIDADES = {"", "UN ", "DOS ", "TRES ", "CUATRO ", "CINCO ", "SEIS ", "SIETE ", "OCHO ", "NUEVE "};
    private final String[] DECENAS = {"DIEZ ", "ONCE ", "DOCE ", "TRECE ", "CATORCE ", "QUINCE ", "DIECISEIS ", "DIECISIETE ", "DIECIOCHO ", "DIECINUEVE ", "VEINTE ", "TREINTA ", "CUARENTA ", "CINCUENTA ", "SESENTA ", "SETENTA ", "OCHENTA ", "NOVENTA "};
    private final String[] CENTENAS = {"", "CIENTO ", "DOSCIENTOS ", "TRESCIENTOS ", "CUATROCIENTOS ", "QUINIENTOS ", "SEISCIENTOS ", "SETECIENTOS ", "OCHOCIENTOS ", "NOVECIENTOS "};

    public String convertir(BigDecimal monto) {
        // Redondeamos a 0 decimales para la parte textual y obtenemos el long
        long numero = monto.setScale(0, RoundingMode.HALF_UP).longValue();

        if (numero == 0) return "CERO ";
        if (numero == 100) return "CIEN ";

        return procesar(numero).trim();
    }

    private String procesar(long n) {
        if (n < 10) return UNIDADES[(int) n];
        if (n < 20) return DECENAS[(int) (n - 10)];
        if (n < 30) return "VEINTI" + UNIDADES[(int) (n % 10)];
        if (n < 100) {
            int d = (int) (n / 10);
            int u = (int) (n % 10);
            return DECENAS[d + 8] + (u > 0 ? "Y " + UNIDADES[u] : "");
        }
        if (n < 1000) {
            return CENTENAS[(int) (n / 100)] + procesar(n % 100);
        }
        if (n < 1000000) {
            long miles = n / 1000;
            long resto = n % 1000;
            String strMiles = (miles == 1) ? "MIL " : procesar(miles) + "MIL ";
            return strMiles + procesar(resto);
        }
        if (n < 2000000) {
            return "UN MILLON " + procesar(n % 1000000);
        }
        if (n < 1000000000000L) {
            long millones = n / 1000000;
            long resto = n % 1000000;
            return procesar(millones) + "MILLONES " + procesar(resto);
        }
        return "NÚMERO EXCESIVO";
    }
}
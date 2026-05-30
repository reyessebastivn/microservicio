package com.example.ProductosJSS.utils;

public final class RutUtils {
    private RutUtils() {}

    /** Deja solo dígitos y K, en mayúscula. */
    public static String clean(String v) {
        return v == null ? "" : v.replaceAll("[^0-9kK]", "").toUpperCase();
    }

    /** Valida RUN chileno con algoritmo módulo 11. Acepta con/sin puntos/guion. */
    public static boolean isValid(String rut) {
        String c = clean(rut);
        if (c.length() < 2) return false;

        String body = c.substring(0, c.length() - 1);
        char dv = c.charAt(c.length() - 1);

        int sum = 0, mul = 2;
        for (int i = body.length() - 1; i >= 0; i--) {
            sum += Character.getNumericValue(body.charAt(i)) * mul;
            mul = (mul == 7) ? 2 : mul + 1;
        }
        int res = 11 - (sum % 11);
        char digito;
        if (res == 11) digito = '0';
        else if (res == 10) digito = 'K';
        else digito = (char) ('0' + res);

        return Character.toUpperCase(dv) == digito;
    }

    /** Devuelve formateado con puntos y guion. Ej: 208283154 -> 20.828.315-4 */
    public static String format(String rut) {
        String c = clean(rut);
        if (c.length() < 2) return rut;

        String body = c.substring(0, c.length() - 1);
        String dv = c.substring(c.length() - 1);

        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (int i = body.length() - 1; i >= 0; i--) {
            sb.append(body.charAt(i));
            count++;
            if (count == 3 && i > 0) {
                sb.append('.');
                count = 0;
            }
        }
        sb.reverse();
        sb.append('-').append(dv);
        return sb.toString();
    }
}

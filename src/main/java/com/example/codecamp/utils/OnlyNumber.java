package com.example.codecamp.utils;

public class OnlyNumber {


    /**
     * Remove todos os caracteres que não são dígitos de uma String.
     * Útil para telefones, CPFs, CNPJs, CEPs, etc.
     *
     * @param value String original com máscara (ex: "(11) 98888-7777")
     * @return String contendo apenas os números (ex: "11988887777")
     */
    public static String removeNonDigits(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.replaceAll("\\D", "");
    }

    /**
     * Valida de forma básica se um número de telefone brasileiro é válido
     * após a limpeza (considerando 10 dígitos para fixo ou 11 dígitos para celular com DDD).
     *
     * @param phone String do telefone (com ou sem formatação)
     * @return true se tiver 10 ou 11 dígitos numéricos.
     */
    public static boolean isValidPhone(String phone) {
        String cleanPhone = removeNonDigits(phone);

        // Telefone fixo com DDD = 10 dígitos / Celular com DDD e 9º dígito = 11 dígitos
        return cleanPhone.length() == 10 || cleanPhone.length() == 11;
    }
}

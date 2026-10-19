package com.example.codecamp.utils;

public class CpfUtils {


    /**
     * Valida um número de CPF (com ou sem formatação).
     *
     * @param cpf String contendo o CPF.
     * @return true se o CPF for válido, false caso contrário.
     */
    public static boolean isValidCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return false;
        }

        // Remove tudo que não for dígito
        cpf = cpf.replaceAll("\\D", "");

        // Verifica se tem 11 dígitos ou se é uma sequência de números iguais (ex: 111.111.111-11)
        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            // Cálculo do 1º dígito verificador
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            }
            int firstDigit = 11 - (sum % 11);
            if (firstDigit > 9) {
                firstDigit = 0;
            }

            // Valida o 1º dígito
            if (firstDigit != Character.getNumericValue(cpf.charAt(9))) {
                return false;
            }

            // Cálculo do 2º dígito verificador
            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
            }
            int secondDigit = 11 - (sum % 11);
            if (secondDigit > 9) {
                secondDigit = 0;
            }

            // Valida o 2º dígito
            return secondDigit == Character.getNumericValue(cpf.charAt(10));

        } catch (Exception e) {
            return false;
        }
    }
}

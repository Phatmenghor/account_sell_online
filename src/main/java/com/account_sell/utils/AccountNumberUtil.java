package com.account_sell.utils;

import lombok.experimental.UtilityClass;

import java.util.*;

@UtilityClass
public class AccountNumberUtil {

    private final Random RANDOM = new Random();

    /**
     * Generates a list of account numbers based on the input pattern and filter type
     *
     * @param inputPattern the pattern to include in the account numbers
     * @param filterType the type of filter (CONTAIN, START_WITH, END_WITH)
     * @param count the number of account numbers to generate
     * @param minPrice minimum price for filtering
     * @param maxPrice maximum price for filtering
     * @return a list of account numbers with prices
     */
    public List<Map.Entry<String, Double>> generateAccountNumbers(
            String inputPattern, String filterType, int count, double minPrice, double maxPrice) {

        Set<String> generatedNumbers = new HashSet<>();
        List<Map.Entry<String, Double>> result = new ArrayList<>();
        int attempts = 0;
        int maxAttempts = count * 20; // Limit attempts to avoid infinite loop

        while (result.size() < count && attempts < maxAttempts) {
            attempts++;
            String accountNumber;

            switch (filterType) {
                case "START_WITH":
                    accountNumber = generateStartsWith(inputPattern);
                    break;
                case "END_WITH":
                    accountNumber = generateEndsWith(inputPattern);
                    break;
                case "CONTAIN":
                default:
                    accountNumber = generateContains(inputPattern);
                    break;
            }

            if (!generatedNumbers.contains(accountNumber)) {
                double price = PatternUtil.calculatePrice(accountNumber);

                // Check if the price is within the required range
                if (price >= minPrice && price <= maxPrice) {
                    generatedNumbers.add(accountNumber);
                    result.add(new AbstractMap.SimpleEntry<>(accountNumber, price));
                }
            }
        }

        // Sort by price in descending order
        result.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        return result;
    }

    /**
     * Generates account numbers starting with the specified input.
     *
     * @param input the input pattern to start with
     * @return a generated account number
     */
    private String generateStartsWith(String input) {
        if (input.length() >= 9) {
            return input.substring(0, 9);
        }

        StringBuilder sb = new StringBuilder(input);
        while (sb.length() < 9) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * Generates account numbers ending with the specified input.
     *
     * @param input the input pattern to end with
     * @return a generated account number
     */
    private String generateEndsWith(String input) {
        if (input.length() >= 9) {
            return input.substring(input.length() - 9);
        }

        StringBuilder sb = new StringBuilder();
        int remainingDigits = 9 - input.length();
        for (int i = 0; i < remainingDigits; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        sb.append(input);
        return sb.toString();
    }

    /**
     * Generates account numbers containing the specified input.
     *
     * @param input the input pattern to include
     * @return a generated account number
     */
    private String generateContains(String input) {
        if (input.length() >= 9) {
            return input.substring(0, 9);
        }

        int remainingDigits = 9 - input.length();
        int position = RANDOM.nextInt(remainingDigits + 1);

        StringBuilder sb = new StringBuilder();
        // Add random digits before the input
        for (int i = 0; i < position; i++) {
            sb.append(RANDOM.nextInt(10));
        }

        // Add the input
        sb.append(input);

        // Add random digits after the input
        while (sb.length() < 9) {
            sb.append(RANDOM.nextInt(10));
        }

        return sb.toString();
    }

    /**
     * Generates a special account number with a specific pattern
     *
     * @return a 9-digit account number
     */
    public String generateSpecialNumber(String pattern) {
        switch (pattern) {
            case "SAME_3_DIGITS":
                return generateWithSame3Digits();
            case "CONTAINS_168":
                return generateWith168();
            case "SEQUENTIAL":
                return generateSequential();
            case "PAIRS":
                return generateWithPairs();
            default:
                return generateRandomAccountNumber();
        }
    }

    /**
     * Generates a random 9-digit account number.
     *
     * @return a random 9-digit account number
     */
    public String generateRandomAccountNumber() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    // Helper methods to generate specific patterns

    private String generateWithSame3Digits() {
        int digit = RANDOM.nextInt(10);
        int groupIndex = RANDOM.nextInt(3);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            if (i / 3 == groupIndex) {
                sb.append(digit);
            } else {
                sb.append(RANDOM.nextInt(10));
            }
        }
        return sb.toString();
    }

    private String generateWith168() {
        // Place "168" at a random position in the account number
        int position = RANDOM.nextInt(7); // 9-3+1 positions possible

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            if (i == position) {
                sb.append("168");
                i += 2; // Skip next 2 positions
            } else {
                sb.append(RANDOM.nextInt(10));
            }
        }
        return sb.toString();
    }

    private String generateSequential() {
        int length = RANDOM.nextInt(5) + 5; // 5-9 sequential digits
        int start = RANDOM.nextInt(10 - length + 1);
        boolean ascending = RANDOM.nextBoolean();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((start + (ascending ? i : -i) + 10) % 10);
        }

        // Fill remaining digits
        while (sb.length() < 9) {
            sb.append(RANDOM.nextInt(10));
        }

        return sb.toString();
    }

    private String generateWithPairs() {
        int pairCount = RANDOM.nextInt(3) + 2; // 2-4 pairs
        StringBuilder sb = new StringBuilder();

        // Generate different digits for pairs
        Set<Integer> digits = new HashSet<>();
        while (digits.size() < pairCount) {
            digits.add(RANDOM.nextInt(10));
        }

        List<Integer> digitList = new ArrayList<>(digits);

        // Add pairs
        for (int i = 0; i < pairCount; i++) {
            int digit = digitList.get(i);
            sb.append(digit).append(digit);
        }

        // Fill remaining digits
        while (sb.length() < 9) {
            sb.append(RANDOM.nextInt(10));
        }

        return sb.toString();
    }
}
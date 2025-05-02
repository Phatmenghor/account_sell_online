package com.account_sell.utils;

import com.account_sell.enumation.AccountType;
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
        return generateAccountNumbers(inputPattern, filterType, count, minPrice, maxPrice, AccountType.NORMAL);
    }

    /**
     * Generates a list of account numbers based on the input pattern, filter type, and account type
     *
     * @param inputPattern the pattern to include in the account numbers
     * @param filterType the type of filter (CONTAIN, START_WITH, END_WITH)
     * @param count the number of account numbers to generate
     * @param minPrice minimum price for filtering
     * @param maxPrice maximum price for filtering
     * @param accountType the type of account to generate
     * @return a list of account numbers with prices
     */
    public List<Map.Entry<String, Double>> generateAccountNumbers(
            String inputPattern, String filterType, int count, double minPrice, double maxPrice, AccountType accountType) {

        // Handle different account types
        switch (accountType) {
            case CASA:
                return generateCasaAccounts(inputPattern, filterType, count, minPrice, maxPrice);
            case LOAN:
                return generateLoanAccounts(inputPattern, filterType, count, minPrice, maxPrice);
            case FD_RD:
                return generateFdRdAccounts(inputPattern, filterType, count, minPrice, maxPrice);
            case DOB:
                return generateDobAccounts(inputPattern, filterType, count, minPrice, maxPrice);
            case PHONE:
                return generatePhoneAccounts(inputPattern, filterType, count, minPrice, maxPrice);
            case NORMAL:
            default:
                return generateNormalAccounts(inputPattern, filterType, count, minPrice, maxPrice);
        }
    }

    /**
     * Generate CASA accounts with proper constraints
     */
    private List<Map.Entry<String, Double>> generateCasaAccounts(
            String inputPattern, String filterType, int count, double minPrice, double maxPrice) {

        List<Map.Entry<String, Double>> result = new ArrayList<>();
        Set<String> generatedNumbers = new HashSet<>();

        // For longer input patterns (6 digits or more)
        if (inputPattern.length() >= 6) {
            // We need to truncate to ensure it fits with the prefix
            String truncatedPattern = inputPattern.substring(0, Math.min(inputPattern.length(), 6));

            // For longer patterns, we'll focus on preserving the pattern
            // and generating both 000 and 001 versions
            String account1 = "000" + truncatedPattern;
            double price1 = PatternUtil.calculatePrice(account1);

            if (price1 >= minPrice && price1 <= maxPrice) {
                result.add(new AbstractMap.SimpleEntry<>(account1, price1));
            }

            String account2 = "001" + truncatedPattern;
            double price2 = PatternUtil.calculatePrice(account2);

            if (price2 >= minPrice && price2 <= maxPrice) {
                result.add(new AbstractMap.SimpleEntry<>(account2, price2));
            }

            // If we still need more accounts, generate variations by changing the first digit
            for (int i = 0; i < 10 && result.size() < count; i++) {
                String accountVar = "00" + i + truncatedPattern.substring(1);
                if (!generatedNumbers.contains(accountVar)) {
                    double priceVar = PatternUtil.calculatePrice(accountVar);
                    if (priceVar >= minPrice && priceVar <= maxPrice) {
                        result.add(new AbstractMap.SimpleEntry<>(accountVar, priceVar));
                        generatedNumbers.add(accountVar);
                    }
                }
            }
        } else {
            // For sequential or shorter patterns, use the original approach
            if (inputPattern == null || inputPattern.isEmpty() || "0".equals(inputPattern)) {
                // Sequential generation from 000000001 to 001999999
                int startNumber = 1;
                int endNumber = 1999999;

                for (int i = startNumber; i <= endNumber && result.size() < count; i++) {
                    String accountNumber;
                    if (i <= 999999) {
                        accountNumber = String.format("000%06d", i);
                    } else {
                        accountNumber = String.format("001%06d", i - 1000000);
                    }

                    double price = PatternUtil.calculatePrice(accountNumber);
                    if (price >= minPrice && price <= maxPrice) {
                        result.add(new AbstractMap.SimpleEntry<>(accountNumber, price));
                    }
                }
            } else {
                // For shorter patterns, generate more variations based on the filter type
                int attempts = 0;
                int maxAttempts = count * 20;

                while (result.size() < count && attempts < maxAttempts) {
                    attempts++;

                    String accountNumber;

                    switch (filterType) {
                        case "START_WITH":
                            // For START_WITH, ensure the pattern is at the beginning after the prefix
                            accountNumber = "00" + (result.size() % 2) + inputPattern;
                            // Pad if needed
                            while (accountNumber.length() < 9) {
                                accountNumber += RANDOM.nextInt(10);
                            }
                            break;

                        case "END_WITH":
                            // For END_WITH, ensure the pattern is at the end
                            StringBuilder sbEnd = new StringBuilder("00");
                            sbEnd.append(result.size() % 2); // Alternate between 000 and 001

                            // Fill the middle with random digits
                            int fillLength = 9 - inputPattern.length() - 3;
                            for (int i = 0; i < fillLength; i++) {
                                sbEnd.append(RANDOM.nextInt(10));
                            }

                            sbEnd.append(inputPattern);
                            accountNumber = sbEnd.toString();
                            break;

                        case "CONTAIN":
                        default:
                            // For CONTAIN, place the pattern somewhere in the middle
                            StringBuilder sbContain = new StringBuilder("00");
                            sbContain.append(result.size() % 2); // Alternate between 000 and 001

                            // Determine how much space is available after the prefix
                            int availableSpace = 6;
                            int patternSpace = inputPattern.length();

                            if (patternSpace < availableSpace) {
                                // We have room to place the pattern with padding
                                int position = RANDOM.nextInt(availableSpace - patternSpace + 1);

                                // Add random digits before the pattern
                                for (int i = 0; i < position; i++) {
                                    sbContain.append(RANDOM.nextInt(10));
                                }

                                // Add the pattern
                                sbContain.append(inputPattern);

                                // Add random digits after the pattern to fill 9 digits
                                while (sbContain.length() < 9) {
                                    sbContain.append(RANDOM.nextInt(10));
                                }
                            } else {
                                // Pattern fits exactly or needs truncation
                                sbContain.append(inputPattern.substring(0, Math.min(patternSpace, 6)));
                            }

                            accountNumber = sbContain.toString();
                            break;
                    }

                    // Ensure the account number is exactly 9 digits
                    if (accountNumber.length() > 9) {
                        accountNumber = accountNumber.substring(0, 9);
                    }

                    if (!generatedNumbers.contains(accountNumber)) {
                        double price = PatternUtil.calculatePrice(accountNumber);
                        if (price >= minPrice && price <= maxPrice) {
                            generatedNumbers.add(accountNumber);
                            result.add(new AbstractMap.SimpleEntry<>(accountNumber, price));
                        }
                    }
                }
            }
        }

        // Sort by price in descending order
        result.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        return result;
    }

    /**
     * Generate Loan accounts
     * Format: 4XXXXXXXX (starts with 4)
     */
    private List<Map.Entry<String, Double>> generateLoanAccounts(
            String inputPattern, String filterType, int count, double minPrice, double maxPrice) {

        List<Map.Entry<String, Double>> result = new ArrayList<>();
        Set<String> generatedNumbers = new HashSet<>();
        int attempts = 0;
        int maxAttempts = count * 20;

        // If no pattern is provided, generate sequential LOAN accounts
        if (inputPattern == null || inputPattern.isEmpty() || "4".equals(inputPattern)) {
            // Start from 400000000
            for (int i = 0; i < count; i++) {
                StringBuilder sb = new StringBuilder("4");
                // Generate 8 random digits to complete the account number
                for (int j = 0; j < 8; j++) {
                    sb.append(RANDOM.nextInt(10));
                }
                String accountNumber = sb.toString();

                double price = PatternUtil.calculatePrice(accountNumber);
                if (price >= minPrice && price <= maxPrice) {
                    result.add(new AbstractMap.SimpleEntry<>(accountNumber, price));
                }
            }
        } else {
            // For pattern-based generation
            while (result.size() < count && attempts < maxAttempts) {
                attempts++;

                String accountNumber;

                switch (filterType) {
                    case "START_WITH":
                        // For START_WITH, the pattern follows the 4 prefix
                        accountNumber = "4" + inputPattern;
                        // Truncate if too long
                        if (accountNumber.length() > 9) {
                            accountNumber = accountNumber.substring(0, 9);
                        }
                        // Pad if needed
                        while (accountNumber.length() < 9) {
                            accountNumber += RANDOM.nextInt(10);
                        }
                        break;

                    case "END_WITH":
                        // For END_WITH, ensure the pattern is at the end
                        if (inputPattern.length() >= 9) {
                            // If the pattern is 9+ digits, use just the first 9
                            accountNumber = inputPattern.substring(0, 9);
                            // If it doesn't start with 4, force it
                            if (!accountNumber.startsWith("4")) {
                                accountNumber = "4" + accountNumber.substring(1);
                            }
                        } else {
                            // Calculate how many prefix digits we need
                            int prefixLength = 9 - inputPattern.length();

                            // Start with 4
                            StringBuilder sb = new StringBuilder("4");

                            // Fill the rest of the prefix with random digits
                            for (int i = 1; i < prefixLength; i++) {
                                sb.append(RANDOM.nextInt(10));
                            }

                            // Append the pattern
                            sb.append(inputPattern);
                            accountNumber = sb.toString();
                        }
                        break;

                    case "CONTAIN":
                    default:
                        // For CONTAIN, ensure 4 prefix and place pattern in the remaining digits
                        if (inputPattern.length() > 8) {
                            // If pattern is too long, truncate it to fit after 4
                            accountNumber = "4" + inputPattern.substring(0, 8);
                        } else {
                            // We have room to place the pattern with random padding
                            StringBuilder sb = new StringBuilder("4");

                            // Calculate available space and position
                            int availableSpace = 8; // 9 total - 1 for prefix
                            int patternSpace = inputPattern.length();
                            int position = RANDOM.nextInt(availableSpace - patternSpace + 1);

                            // Add random digits before the pattern
                            for (int i = 0; i < position; i++) {
                                sb.append(RANDOM.nextInt(10));
                            }

                            // Add the pattern
                            sb.append(inputPattern);

                            // Add random digits after the pattern to fill 9 digits
                            while (sb.length() < 9) {
                                sb.append(RANDOM.nextInt(10));
                            }

                            accountNumber = sb.toString();
                        }
                        break;
                }

                // Ensure account number is exactly 9 digits and starts with 4
                if (accountNumber.length() > 9) {
                    accountNumber = accountNumber.substring(0, 9);
                }

                // Force the 4 prefix if it's not there
                if (!accountNumber.startsWith("4")) {
                    accountNumber = "4" + accountNumber.substring(1);
                }

                if (!generatedNumbers.contains(accountNumber)) {
                    double price = PatternUtil.calculatePrice(accountNumber);
                    if (price >= minPrice && price <= maxPrice) {
                        generatedNumbers.add(accountNumber);
                        result.add(new AbstractMap.SimpleEntry<>(accountNumber, price));
                    }
                }
            }
        }

        // Sort by price in descending order
        result.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        return result;
    }


    /**
     * Generate FD/RD accounts
     * Format: 8XXXXXXXX (starts with 8)
     */
    private List<Map.Entry<String, Double>> generateFdRdAccounts(
            String inputPattern, String filterType, int count, double minPrice, double maxPrice) {

        List<Map.Entry<String, Double>> result = new ArrayList<>();
        Set<String> generatedNumbers = new HashSet<>();
        int attempts = 0;
        int maxAttempts = count * 20;

        // If no pattern is provided, generate random FD/RD accounts
        if (inputPattern == null || inputPattern.isEmpty() || "8".equals(inputPattern)) {
            // Generate random FD/RD accounts starting with 8
            for (int i = 0; i < count; i++) {
                StringBuilder sb = new StringBuilder("8");
                // Generate 8 random digits to complete the account number
                for (int j = 0; j < 8; j++) {
                    sb.append(RANDOM.nextInt(10));
                }
                String accountNumber = sb.toString();

                double price = PatternUtil.calculatePrice(accountNumber);
                if (price >= minPrice && price <= maxPrice) {
                    result.add(new AbstractMap.SimpleEntry<>(accountNumber, price));
                }
            }
        } else {
            // For pattern-based generation
            while (result.size() < count && attempts < maxAttempts) {
                attempts++;

                String accountNumber;

                switch (filterType) {
                    case "START_WITH":
                        // For START_WITH, the pattern follows the 8 prefix
                        accountNumber = "8" + inputPattern;
                        // Truncate if too long
                        if (accountNumber.length() > 9) {
                            accountNumber = accountNumber.substring(0, 9);
                        }
                        // Pad if needed
                        while (accountNumber.length() < 9) {
                            accountNumber += RANDOM.nextInt(10);
                        }
                        break;

                    case "END_WITH":
                        // For END_WITH, ensure the pattern is at the end
                        if (inputPattern.length() >= 9) {
                            // If the pattern is 9+ digits, use just the first 9
                            accountNumber = inputPattern.substring(0, 9);
                            // If it doesn't start with 8, force it
                            if (!accountNumber.startsWith("8")) {
                                accountNumber = "8" + accountNumber.substring(1);
                            }
                        } else {
                            // Calculate how many prefix digits we need
                            int prefixLength = 9 - inputPattern.length();

                            // Start with 8
                            StringBuilder sb = new StringBuilder("8");

                            // Fill the rest of the prefix with random digits
                            for (int i = 1; i < prefixLength; i++) {
                                sb.append(RANDOM.nextInt(10));
                            }

                            // Append the pattern
                            sb.append(inputPattern);
                            accountNumber = sb.toString();
                        }
                        break;

                    case "CONTAIN":
                    default:
                        // For CONTAIN, ensure 8 prefix and place pattern in the remaining digits
                        if (inputPattern.length() > 8) {
                            // If pattern is too long, truncate it to fit after 8
                            accountNumber = "8" + inputPattern.substring(0, 8);
                        } else {
                            // We have room to place the pattern with random padding
                            StringBuilder sb = new StringBuilder("8");

                            // Calculate available space and position
                            int availableSpace = 8; // 9 total - 1 for prefix
                            int patternSpace = inputPattern.length();
                            int position = RANDOM.nextInt(availableSpace - patternSpace + 1);

                            // Add random digits before the pattern
                            for (int i = 0; i < position; i++) {
                                sb.append(RANDOM.nextInt(10));
                            }

                            // Add the pattern
                            sb.append(inputPattern);

                            // Add random digits after the pattern to fill 9 digits
                            while (sb.length() < 9) {
                                sb.append(RANDOM.nextInt(10));
                            }

                            accountNumber = sb.toString();
                        }
                        break;
                }

                // Ensure account number is exactly 9 digits and starts with 8
                if (accountNumber.length() > 9) {
                    accountNumber = accountNumber.substring(0, 9);
                }

                // Force the 8 prefix if it's not there
                if (!accountNumber.startsWith("8")) {
                    accountNumber = "8" + accountNumber.substring(1);
                }

                if (!generatedNumbers.contains(accountNumber)) {
                    double price = PatternUtil.calculatePrice(accountNumber);
                    if (price >= minPrice && price <= maxPrice) {
                        generatedNumbers.add(accountNumber);
                        result.add(new AbstractMap.SimpleEntry<>(accountNumber, price));
                    }
                }
            }
        }

        // Sort by price in descending order
        result.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        return result;
    }

    /**
     * Generate DOB format accounts
     * Format: 0XXXXXXXX (starts with 0)
     * Range: 001011975 - 031122100
     */
    private List<Map.Entry<String, Double>> generateDobAccounts(
            String inputPattern, String filterType, int count, double minPrice, double maxPrice) {

        List<Map.Entry<String, Double>> result = new ArrayList<>();
        Set<String> generatedNumbers = new HashSet<>();
        int attempts = 0;
        int maxAttempts = count * 20;

        // If the input pattern is exactly 9 digits
        if (inputPattern.length() == 9) {
            // Force it to start with 0
            String accountNumber = "0" + inputPattern.substring(1, 9);
            double price = PatternUtil.calculatePrice(accountNumber);
            if (price >= minPrice && price <= maxPrice) {
                result.add(new AbstractMap.SimpleEntry<>(accountNumber, price));
            }
        }
        // If the input pattern is longer than 9 digits
        else if (inputPattern.length() > 9) {
            // Truncate to 9 digits and ensure it starts with 0
            String accountNumber = "0" + inputPattern.substring(1, 9);
            double price = PatternUtil.calculatePrice(accountNumber);
            if (price >= minPrice && price <= maxPrice) {
                result.add(new AbstractMap.SimpleEntry<>(accountNumber, price));
            }
        }
        // For shorter patterns
        else {
            while (result.size() < count && attempts < maxAttempts) {
                attempts++;

                String accountNumber;

                switch (filterType) {
                    case "START_WITH":
                        // For START_WITH, the pattern follows the 0 prefix
                        accountNumber = "0" + inputPattern;
                        // Pad if needed
                        while (accountNumber.length() < 9) {
                            accountNumber += RANDOM.nextInt(10);
                        }
                        break;

                    case "END_WITH":
                        // For END_WITH, ensure the pattern is at the end
                        StringBuilder sbEnd = new StringBuilder("0");

                        // Fill the middle with random digits
                        int fillLength = 9 - inputPattern.length() - 1;
                        for (int i = 0; i < fillLength; i++) {
                            sbEnd.append(RANDOM.nextInt(10));
                        }

                        sbEnd.append(inputPattern);
                        accountNumber = sbEnd.toString();
                        break;

                    case "CONTAIN":
                    default:
                        // For CONTAIN, place the pattern somewhere in the number
                        StringBuilder sbContain = new StringBuilder("0");

                        // Calculate available space and position
                        int availableSpace = 8; // 9 total - 1 for prefix
                        int patternSpace = inputPattern.length();

                        if (patternSpace < availableSpace) {
                            // We have room to place the pattern with padding
                            int position = RANDOM.nextInt(availableSpace - patternSpace + 1);

                            // Add random digits before the pattern
                            for (int i = 0; i < position; i++) {
                                sbContain.append(RANDOM.nextInt(10));
                            }

                            // Add the pattern
                            sbContain.append(inputPattern);

                            // Add random digits after the pattern to fill 9 digits
                            while (sbContain.length() < 9) {
                                sbContain.append(RANDOM.nextInt(10));
                            }
                        } else {
                            // Pattern is too long, truncate it
                            sbContain.append(inputPattern.substring(0, Math.min(patternSpace, 8)));
                        }

                        accountNumber = sbContain.toString();
                        break;
                }

                // Ensure account number is exactly 9 digits and starts with 0
                if (accountNumber.length() > 9) {
                    accountNumber = accountNumber.substring(0, 9);
                }

                // Force the 0 prefix if it's not there
                if (!accountNumber.startsWith("0")) {
                    accountNumber = "0" + accountNumber.substring(1);
                }

                if (!generatedNumbers.contains(accountNumber)) {
                    double price = PatternUtil.calculatePrice(accountNumber);
                    if (price >= minPrice && price <= maxPrice) {
                        generatedNumbers.add(accountNumber);
                        result.add(new AbstractMap.SimpleEntry<>(accountNumber, price));
                    }
                }
            }
        }

        // Sort by price in descending order
        result.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        return result;
    }

    /**
     * Generate Phone Number accounts
     * Format: 0XXXXXXXX (starts with 0)
     * Range: 010000000 - 099999999
     */
    private List<Map.Entry<String, Double>> generatePhoneAccounts(
            String inputPattern, String filterType, int count, double minPrice, double maxPrice) {

        List<Map.Entry<String, Double>> result = new ArrayList<>();

        // First check if the input is already a valid 9-digit phone number starting with 0
        if (inputPattern.length() == 9 && inputPattern.startsWith("0") &&
                inputPattern.charAt(1) >= '1' && inputPattern.charAt(1) <= '9') {
            // This is already a valid phone number - use it as is
            result.add(new AbstractMap.SimpleEntry<>(inputPattern, 0.0)); // Phone numbers are free
            return result;
        }

        // Check if the input is 8 digits that looks like a phone number without the leading 0
        else if (inputPattern.length() == 8 && Character.isDigit(inputPattern.charAt(0)) &&
                inputPattern.charAt(0) >= '1' && inputPattern.charAt(0) <= '9') {
            // This looks like a phone number without the leading 0, add it
            String phoneNumber = "0" + inputPattern;
            result.add(new AbstractMap.SimpleEntry<>(phoneNumber, 0.0)); // Phone numbers are free
            return result;
        }

        // If the input has a different format, create a phone number that contains the pattern
        else {
            StringBuilder phoneNumber = new StringBuilder("0");

            // Ensure second digit is 1-9
            phoneNumber.append(1 + RANDOM.nextInt(9));

            // Add the pattern if it's not too long
            if (inputPattern.length() <= 7) {
                // Add the pattern
                phoneNumber.append(inputPattern);

                // Fill the rest with random digits
                while (phoneNumber.length() < 9) {
                    phoneNumber.append(RANDOM.nextInt(10));
                }
            } else {
                // Pattern too long, truncate it
                phoneNumber.append(inputPattern.substring(0, 7));
            }

            // Ensure it's exactly 9 digits
            if (phoneNumber.length() > 9) {
                phoneNumber.setLength(9);
            }

            result.add(new AbstractMap.SimpleEntry<>(phoneNumber.toString(), 0.0)); // Phone numbers are free
            return result;
        }
    }

    /**
     * Generate normal accounts (default behavior)
     */
    private List<Map.Entry<String, Double>> generateNormalAccounts(
            String inputPattern, String filterType, int count, double minPrice, double maxPrice) {

        return generateWithPattern(inputPattern, filterType, count, minPrice, maxPrice);
    }

    /**
     * Generate account numbers with a specific pattern and filter type
     */
    private List<Map.Entry<String, Double>> generateWithPattern(
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
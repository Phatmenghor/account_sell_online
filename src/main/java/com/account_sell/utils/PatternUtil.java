package com.account_sell.utils;

import com.account_sell.enumation.PriceRange;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class PatternUtil {

    /**
     * Calculates price based on the account number pattern
     * @param accountNumber 9-digit account number
     * @return price based on the pattern
     */
    public double calculatePrice(String accountNumber) {
        // Check patterns in order of price (highest to lowest)

        // $10,000 patterns
        if (containsSame8or9Digits(accountNumber) ||
                containsSame6DigitsPlus168(accountNumber) ||
                contains3SetsOfSame3DigitsInOrder(accountNumber) ||
                containsSame5DigitsPlus2SetsOf2Digits(accountNumber) ||
                containsSame7DigitsGreaterOrEqualTo8(accountNumber)) {
            return PriceRange.PREMIUM_10000.getPrice();
        }

        // $5,000 patterns
        if (containsSame5DigitsPlus168(accountNumber) ||
                contains4PairsInOrder(accountNumber) ||
                containsSame6DigitsPlus3SameDigits(accountNumber) ||
                containsSame7DigitsLessThan8(accountNumber) ||
                containsSame4DigitsInMiddle(accountNumber)) {
            return PriceRange.PREMIUM_5000.getPrice();
        }

        // $3,000 patterns
        if (contains9NumbersInOrder(accountNumber) ||
                containsSame5DigitsPlus3SameDigits(accountNumber) ||
                containsSame5DigitsPlus4SameDigits(accountNumber) ||
                containsSame6DigitsAtStartOrEnd(accountNumber)) {
            return PriceRange.HIGH_3000.getPrice();
        }

        // $2,500 patterns
        if (contains3SetsOfSame3DigitsNotInOrder(accountNumber) ||
                containsSame4DigitsPlus168(accountNumber)) {
            return PriceRange.HIGH_2500.getPrice();
        }

        // $1,500 patterns
        if (contains7to8NumbersInOrder(accountNumber) ||
                contains3PairsInOrder(accountNumber)) {
            return PriceRange.HIGH_1500.getPrice();
        }

        // $1,000 patterns
        if (containsSame4DigitsPlus5NumbersInOrder(accountNumber) ||
                containsSame5DigitsAtStartOrEnd(accountNumber)) {
            return PriceRange.HIGH_1000.getPrice();
        }

        // $500 patterns
        if (contains2SetsOfSame3Digits(accountNumber)) {
            return PriceRange.MID_500.getPrice();
        }

        // $100 patterns
        if (contains6NumbersInOrder(accountNumber) ||
                containsSame5DigitsInMiddle(accountNumber)) {
            return PriceRange.MID_100.getPrice();
        }

        // $50 patterns
        if (containsSame4DigitsAtStartOrEnd(accountNumber)) {
            return PriceRange.MID_50.getPrice();
        }

        // $20 patterns
        if (containsSame3Digits(accountNumber) ||
                contains168(accountNumber)) {
            return PriceRange.LOW_20.getPrice();
        }

        // Default price
        return PriceRange.DEFAULT.getPrice();
    }

    /**
     * Get the price range description for the given price
     *
     * @param price the calculated price
     * @return price range description
     */
    public String getPriceRangeDescription(double price) {
        return PriceRange.fromPrice(price).getRangeDescription();
    }

    // Pattern to check if a number contains the same 3 digits
    public boolean containsSame3Digits(String accountNumber) {
        // Convert to groups of 3 digits
        String[] groups = splitIntoGroups(accountNumber);

        for (String group : groups) {
            if (allDigitsSame(group)) {
                return true;
            }
        }
        return false;
    }

    // Pattern to check if a number contains 2 sets of the same 3 digits
    public boolean contains2SetsOfSame3Digits(String accountNumber) {
        // Convert to groups of 3 digits
        String[] groups = splitIntoGroups(accountNumber);

        Map<String, Integer> groupCounts = new HashMap<>();
        for (String group : groups) {
            if (allDigitsSame(group)) {
                groupCounts.put(group, groupCounts.getOrDefault(group, 0) + 1);
            }
        }

        return groupCounts.values().stream().anyMatch(count -> count >= 2);
    }

    // Pattern to check if a number contains 3 sets of the same 3 digit number not in order
    public boolean contains3SetsOfSame3DigitsNotInOrder(String accountNumber) {
        // Convert to groups of 3 digits
        String[] groups = splitIntoGroups(accountNumber);

        // Check if all three groups are different and each has same digits
        return groups.length == 3
                && allDigitsSame(groups[0])
                && allDigitsSame(groups[1])
                && allDigitsSame(groups[2])
                && !isInAscendingOrDescendingOrder(groups);
    }

    // Pattern to check if a number contains 3 sets of the same 3 digit number in order
    public boolean contains3SetsOfSame3DigitsInOrder(String accountNumber) {
        // Convert to groups of 3 digits
        String[] groups = splitIntoGroups(accountNumber);

        // Check if all three groups are different and each has same digits
        return groups.length == 3
                && allDigitsSame(groups[0])
                && allDigitsSame(groups[1])
                && allDigitsSame(groups[2])
                && isInAscendingOrDescendingOrder(groups);
    }

    // Pattern to check if contains 168
    public boolean contains168(String accountNumber) {
        return accountNumber.contains("168");
    }

    // Pattern to check if contains the same 4 digit numbers + 168
    public boolean containsSame4DigitsPlus168(String accountNumber) {
        return contains168(accountNumber) && containsConsecutiveSameDigits(accountNumber, 4);
    }

    // Pattern to check if contains the same 5 digits numbers + 168
    public boolean containsSame5DigitsPlus168(String accountNumber) {
        return contains168(accountNumber) && containsConsecutiveSameDigits(accountNumber, 5);
    }

    // Pattern to check if contains the same 6 digits numbers + 168
    public boolean containsSame6DigitsPlus168(String accountNumber) {
        return contains168(accountNumber) && containsConsecutiveSameDigits(accountNumber, 6);
    }

    // Pattern to check if a number contains 6 numbers in order
    public boolean contains6NumbersInOrder(String accountNumber) {
        return containsConsecutiveNumbers(accountNumber, 6);
    }

    // Pattern to check if a number contains 7 to 8 numbers in order
    public boolean contains7to8NumbersInOrder(String accountNumber) {
        return containsConsecutiveNumbers(accountNumber, 7) || containsConsecutiveNumbers(accountNumber, 8);
    }

    // Pattern to check if a number contains 9 numbers in order
    public boolean contains9NumbersInOrder(String accountNumber) {
        return accountNumber.equals("123456789") || accountNumber.equals("987654321");
    }

    // Pattern to check if a number contains 3 pairs in order
    public boolean contains3PairsInOrder(String accountNumber) {
        // Check for patterns like 112233
        Pattern pattern = Pattern.compile("(\\d)\\1(\\d)\\2(\\d)\\3");
        Matcher matcher = pattern.matcher(accountNumber);
        return matcher.find();
    }

    // Pattern to check if a number contains 4 pairs in order
    public boolean contains4PairsInOrder(String accountNumber) {
        // Check for patterns like 11223344
        Pattern pattern = Pattern.compile("(\\d)\\1(\\d)\\2(\\d)\\3(\\d)\\4");
        Matcher matcher = pattern.matcher(accountNumber);
        return matcher.find();
    }

    // Pattern to check if a number contains the same 4 digits in the middle
    public boolean containsSame4DigitsInMiddle(String accountNumber) {
        if (accountNumber.length() < 4) return false;

        for (int i = 1; i <= accountNumber.length() - 4; i++) {
            String substr = accountNumber.substring(i, i + 4);
            if (allDigitsSame(substr)) {
                return true;
            }
        }
        return false;
    }

    // Pattern to check if a number contains the same 4 digits at start or end
    public boolean containsSame4DigitsAtStartOrEnd(String accountNumber) {
        if (accountNumber.length() < 4) return false;

        String first4 = accountNumber.substring(0, 4);
        String last4 = accountNumber.substring(accountNumber.length() - 4);

        return allDigitsSame(first4) || allDigitsSame(last4);
    }

    // Pattern to check if a number contains the same 4 digit number + 5 numbers in order
    public boolean containsSame4DigitsPlus5NumbersInOrder(String accountNumber) {
        // Check for 4 same digits
        boolean has4SameDigits = false;
        for (int i = 0; i <= accountNumber.length() - 4; i++) {
            String substr = accountNumber.substring(i, i + 4);
            if (allDigitsSame(substr)) {
                has4SameDigits = true;
                break;
            }
        }

        // Check for 5 consecutive numbers
        return has4SameDigits && containsConsecutiveNumbers(accountNumber, 5);
    }

    // Pattern to check if a number contains 2 sets of the same 4 digits numbers
    public boolean contains2SetsOfSame4Digits(String accountNumber) {
        Map<Character, Integer> digitCount = new HashMap<>();

        for (char c : accountNumber.toCharArray()) {
            digitCount.put(c, digitCount.getOrDefault(c, 0) + 1);
        }

        int count = 0;
        for (int value : digitCount.values()) {
            if (value >= 4) {
                count++;
            }
        }

        return count >= 2;
    }

    // Pattern to check if a number contains the same 5 digits in the middle
    public boolean containsSame5DigitsInMiddle(String accountNumber) {
        if (accountNumber.length() < 5) return false;

        for (int i = 1; i <= accountNumber.length() - 5; i++) {
            String substr = accountNumber.substring(i, i + 5);
            if (allDigitsSame(substr)) {
                return true;
            }
        }
        return false;
    }

    // Pattern to check if a number contains the same 5 digits at start or end
    public boolean containsSame5DigitsAtStartOrEnd(String accountNumber) {
        if (accountNumber.length() < 5) return false;

        String first5 = accountNumber.substring(0, 5);
        String last5 = accountNumber.substring(accountNumber.length() - 5);

        return allDigitsSame(first5) || allDigitsSame(last5);
    }

    // Pattern to check if a number contains the same 5 digits + the same 3 digit number
    public boolean containsSame5DigitsPlus3SameDigits(String accountNumber) {
        // Check for 5 same digits
        boolean has5SameDigits = false;
        char digit5 = ' ';

        for (int i = 0; i <= accountNumber.length() - 5; i++) {
            String substr = accountNumber.substring(i, i + 5);
            if (allDigitsSame(substr)) {
                has5SameDigits = true;
                digit5 = substr.charAt(0);
                break;
            }
        }

        if (!has5SameDigits) return false;

        // Check for 3 same digits of a different number
        for (int i = 0; i <= accountNumber.length() - 3; i++) {
            String substr = accountNumber.substring(i, i + 3);
            if (allDigitsSame(substr) && substr.charAt(0) != digit5) {
                return true;
            }
        }

        return false;
    }

    // Pattern to check if a number contains the same 5 digits + 2 sets of the same 2 digit number
    public boolean containsSame5DigitsPlus2SetsOf2Digits(String accountNumber) {
        // First check for 5 same digits
        boolean has5SameDigits = false;

        for (int i = 0; i <= accountNumber.length() - 5; i++) {
            String substr = accountNumber.substring(i, i + 5);
            if (allDigitsSame(substr)) {
                has5SameDigits = true;
                break;
            }
        }

        if (!has5SameDigits) return false;

        // Check for 2 pairs
        Pattern pattern = Pattern.compile("(\\d)\\1.*?(\\d)\\2");
        Matcher matcher = pattern.matcher(accountNumber);
        return matcher.find();
    }

    // Pattern to check if a number contains the same 5 digits + the same 4 digit number
    public boolean containsSame5DigitsPlus4SameDigits(String accountNumber) {
        // Check for 5 same digits
        boolean has5SameDigits = false;
        char digit5 = ' ';

        for (int i = 0; i <= accountNumber.length() - 5; i++) {
            String substr = accountNumber.substring(i, i + 5);
            if (allDigitsSame(substr)) {
                has5SameDigits = true;
                digit5 = substr.charAt(0);
                break;
            }
        }

        if (!has5SameDigits) return false;

        // Check for 4 same digits of a different number
        for (int i = 0; i <= accountNumber.length() - 4; i++) {
            String substr = accountNumber.substring(i, i + 4);
            if (allDigitsSame(substr) && substr.charAt(0) != digit5) {
                return true;
            }
        }

        return false;
    }

    // Pattern to check if a number contains the same 6 or 7 digits in the middle
    public boolean containsSame6or7DigitsInMiddle(String accountNumber) {
        return containsConsecutiveSameDigitsInMiddle(accountNumber, 6) ||
                containsConsecutiveSameDigitsInMiddle(accountNumber, 7);
    }

    // Pattern to check if a number contains the same 6 digits at start or end
    public boolean containsSame6DigitsAtStartOrEnd(String accountNumber) {
        if (accountNumber.length() < 6) return false;

        String first6 = accountNumber.substring(0, 6);
        String last6 = accountNumber.substring(accountNumber.length() - 6);

        return allDigitsSame(first6) || allDigitsSame(last6);
    }

    // Pattern to check if a number contains the same 6 digits + the same 3 digit number
    public boolean containsSame6DigitsPlus3SameDigits(String accountNumber) {
        // Check for 6 same digits
        boolean has6SameDigits = false;
        char digit6 = ' ';

        for (int i = 0; i <= accountNumber.length() - 6; i++) {
            String substr = accountNumber.substring(i, i + 6);
            if (allDigitsSame(substr)) {
                has6SameDigits = true;
                digit6 = substr.charAt(0);
                break;
            }
        }

        if (!has6SameDigits) return false;

        // Check for 3 same digits of a different number
        for (int i = 0; i <= accountNumber.length() - 3; i++) {
            String substr = accountNumber.substring(i, i + 3);
            if (allDigitsSame(substr) && substr.charAt(0) != digit6) {
                return true;
            }
        }

        return false;
    }

    // Pattern to check if a number contains the same 7 digits (value < 8)
    public boolean containsSame7DigitsLessThan8(String accountNumber) {
        for (int i = 0; i <= accountNumber.length() - 7; i++) {
            String substr = accountNumber.substring(i, i + 7);
            if (allDigitsSame(substr) && substr.charAt(0) < '8') {
                return true;
            }
        }
        return false;
    }

    // Pattern to check if a number contains the same 7 digits (value >= 8)
    public boolean containsSame7DigitsGreaterOrEqualTo8(String accountNumber) {
        for (int i = 0; i <= accountNumber.length() - 7; i++) {
            String substr = accountNumber.substring(i, i + 7);
            if (allDigitsSame(substr) && substr.charAt(0) >= '8') {
                return true;
            }
        }
        return false;
    }

    // Pattern to check if a number contains the same 8 or 9 digits
    public boolean containsSame8or9Digits(String accountNumber) {
        // Check for 8 same digits
        for (int i = 0; i <= accountNumber.length() - 8; i++) {
            String substr = accountNumber.substring(i, i + 8);
            if (allDigitsSame(substr)) {
                return true;
            }
        }

        // Check if all 9 digits are the same
        return accountNumber.length() == 9 && allDigitsSame(accountNumber);
    }

    // Helper methods
    private String[] splitIntoGroups(String accountNumber) {
        int length = accountNumber.length();
        int groupCount = length / 3;
        String[] groups = new String[groupCount];

        for (int i = 0; i < groupCount; i++) {
            int startIndex = i * 3;
            int endIndex = Math.min(startIndex + 3, length);
            groups[i] = accountNumber.substring(startIndex, endIndex);
        }

        return groups;
    }

    private boolean allDigitsSame(String str) {
        if (str.isEmpty()) return false;
        char first = str.charAt(0);
        for (int i = 1; i < str.length(); i++) {
            if (str.charAt(i) != first) {
                return false;
            }
        }
        return true;
    }

    private boolean isInAscendingOrDescendingOrder(String[] groups) {
        if (groups.length < 2) return false;

        // Extract first digit of each group
        int[] values = new int[groups.length];
        for (int i = 0; i < groups.length; i++) {
            values[i] = groups[i].charAt(0) - '0';
        }

        // Check if ascending
        boolean ascending = true;
        for (int i = 1; i < values.length; i++) {
            if (values[i] <= values[i-1]) {
                ascending = false;
                break;
            }
        }

        // Check if descending
        boolean descending = true;
        for (int i = 1; i < values.length; i++) {
            if (values[i] >= values[i-1]) {
                descending = false;
                break;
            }
        }

        return ascending || descending;
    }

    private boolean containsConsecutiveNumbers(String accountNumber, int length) {
        if (accountNumber.length() < length) return false;

        // Check for ascending sequence
        for (int i = 0; i <= accountNumber.length() - length; i++) {
            boolean isSequential = true;
            for (int j = i + 1; j < i + length; j++) {
                int curr = accountNumber.charAt(j) - '0';
                int prev = accountNumber.charAt(j-1) - '0';
                if (curr != prev + 1) {
                    isSequential = false;
                    break;
                }
            }
            if (isSequential) return true;
        }

        // Check for descending sequence
        for (int i = 0; i <= accountNumber.length() - length; i++) {
            boolean isSequential = true;
            for (int j = i + 1; j < i + length; j++) {
                int curr = accountNumber.charAt(j) - '0';
                int prev = accountNumber.charAt(j-1) - '0';
                if (curr != prev - 1) {
                    isSequential = false;
                    break;
                }
            }
            if (isSequential) return true;
        }

        return false;
    }

    private boolean containsConsecutiveSameDigits(String accountNumber, int count) {
        if (accountNumber.length() < count) return false;

        for (int i = 0; i <= accountNumber.length() - count; i++) {
            String substr = accountNumber.substring(i, i + count);
            if (allDigitsSame(substr)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsConsecutiveSameDigitsInMiddle(String accountNumber, int count) {
        if (accountNumber.length() < count + 2) return false; // Need at least one digit on each side

        for (int i = 1; i <= accountNumber.length() - count - 1; i++) {
            String substr = accountNumber.substring(i, i + count);
            if (allDigitsSame(substr)) {
                return true;
            }
        }
        return false;
    }
}
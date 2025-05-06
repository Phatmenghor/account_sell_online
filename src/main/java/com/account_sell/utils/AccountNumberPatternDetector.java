package com.account_sell.utils;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class AccountNumberPatternDetector {

    // Detect patterns in account numbers (helper methods for PatternUtil)
    
    /**
     * Detects if the account number has repeating consecutive digits
     * 
     * @param accountNumber the account number to check
     * @param repetitions the number of consecutive repetitions to look for
     * @return true if the pattern is found
     */
    public boolean hasConsecutiveRepeats(String accountNumber, int repetitions) {
        if (accountNumber == null || accountNumber.length() < repetitions) {
            return false;
        }
        
        // Create regex pattern for N consecutive identical digits
        String regex = "(\\d)\\1{" + (repetitions - 1) + "}";
        return Pattern.compile(regex).matcher(accountNumber).find();
    }
    
    /**
     * Detects if the account number has sequential digits
     * 
     * @param accountNumber the account number to check
     * @param length the length of the sequence to look for
     * @return true if a sequential pattern is found
     */
    public boolean hasSequentialDigits(String accountNumber, int length) {
        if (accountNumber == null || accountNumber.length() < length) {
            return false;
        }
        
        // Check for ascending sequences like "123", "234", etc.
        for (int i = 0; i <= accountNumber.length() - length; i++) {
            boolean isAscending = true;
            for (int j = i + 1; j < i + length; j++) {
                if (accountNumber.charAt(j) - accountNumber.charAt(j - 1) != 1) {
                    isAscending = false;
                    break;
                }
            }
            if (isAscending) return true;
        }
        
        // Check for descending sequences like "987", "876", etc.
        for (int i = 0; i <= accountNumber.length() - length; i++) {
            boolean isDescending = true;
            for (int j = i + 1; j < i + length; j++) {
                if (accountNumber.charAt(j - 1) - accountNumber.charAt(j) != 1) {
                    isDescending = false;
                    break;
                }
            }
            if (isDescending) return true;
        }
        
        return false;
    }
    
    /**
     * Detects if the account number contains specific lucky number patterns
     * 
     * @param accountNumber the account number to check
     * @return true if a lucky pattern is found
     */
    public boolean hasLuckyPattern(String accountNumber) {
        return accountNumber.contains("168") || 
               accountNumber.contains("888") || 
               accountNumber.contains("999");
    }
    
    /**
     * Calculates a rarity score for the account number from 1-10
     * 
     * @param accountNumber the account number to check
     * @return a rarity score from 1-10
     */
    public int calculateRarityScore(String accountNumber) {
        int score = 0;
        
        // Score for consecutive repeats
        if (hasConsecutiveRepeats(accountNumber, 9)) score += 10;
        else if (hasConsecutiveRepeats(accountNumber, 8)) score += 9;
        else if (hasConsecutiveRepeats(accountNumber, 7)) score += 8;
        else if (hasConsecutiveRepeats(accountNumber, 6)) score += 7;
        else if (hasConsecutiveRepeats(accountNumber, 5)) score += 6;
        else if (hasConsecutiveRepeats(accountNumber, 4)) score += 4;
        else if (hasConsecutiveRepeats(accountNumber, 3)) score += 2;
        
        // Score for sequential digits
        if (hasSequentialDigits(accountNumber, 9)) score += 8;
        else if (hasSequentialDigits(accountNumber, 7)) score += 6;
        else if (hasSequentialDigits(accountNumber, 5)) score += 4;
        else if (hasSequentialDigits(accountNumber, 3)) score += 2;
        
        // Score for lucky patterns
        if (hasLuckyPattern(accountNumber)) score += 3;
        
        // Cap score at 10
        return Math.min(score, 10);
    }
}
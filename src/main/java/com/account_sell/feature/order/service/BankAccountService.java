package com.account_sell.feature.order.service;

import com.account_sell.feature.order.dto.response.BankAccountInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@Slf4j
public class BankAccountService {

    @Value("${bank.soap.url:http://192.168.127.31:7003/CPB.MB.TWS/services}")
    private String bankSoapUrl;
    
    @Value("${bank.soap.username:APPLOS}")
    private String username;
    
    @Value("${bank.soap.password:WWss123!@#}")
    private String password;
    
    /**
     * Validate if account exists in the bank system
     * 
     * @param accountNumber Account number to validate
     * @return true if account exists and is valid
     */
    public boolean validateBankAccount(String accountNumber) {
        log.info("Validating bank account number: {} with SOAP API", accountNumber);
        
        try {
            // Get the SOAP response
            String soapResponse = sendSoapRequest(accountNumber);
            
            // Check if the response contains account details or an error message
            boolean isValid = soapResponse.contains("<ns4:SHORTTITLE>") && 
                   !soapResponse.contains("No records were found that matched the selection criteria");
                   
            log.info("Account validation result for {}: {}", accountNumber, isValid ? "Valid" : "Invalid");
            return isValid;
            
        } catch (Exception e) {
            log.error("Error validating bank account {}: {}", accountNumber, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Get detailed account information from bank
     * 
     * @param accountNumber Account number to get info for
     * @return BankAccountInfo or null if not found
     */
    public BankAccountInfo getAccountInfo(String accountNumber) {
        log.info("Getting bank account info for: {} with SOAP API", accountNumber);
        
        try {
            // Get the SOAP response
            String soapResponse = sendSoapRequest(accountNumber);
            
            // Check if account exists
            boolean exists = soapResponse.contains("<ns4:SHORTTITLE>") && 
                   !soapResponse.contains("No records were found that matched the selection criteria");
                   
            if (!exists) {
                log.info("Account {} not found in bank system", accountNumber);
                return null;
            }
            
            // Parse response to extract account information
            return parseAccountInfoResponse(soapResponse);
            
        } catch (Exception e) {
            log.error("Error getting bank account info for {}: {}", accountNumber, e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Send SOAP request to bank API and get response
     */
    private String sendSoapRequest(String accountNumber) throws Exception {
        // Create SOAP request
        String soapRequest = createSoapRequest(accountNumber);
        
        // Set up HTTP connection
        URL url = new URL(bankSoapUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
        connection.setRequestProperty("SOAPAction", "");
        connection.setDoOutput(true);
        
        // Send request
        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] requestBytes = soapRequest.getBytes(StandardCharsets.UTF_8);
            outputStream.write(requestBytes);
            outputStream.flush();
        }
        
        // Read response
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        
        // Close connection
        connection.disconnect();
        
        return response.toString();
    }
    
    /**
     * Create SOAP request XML for account validation
     */
    private String createSoapRequest(String accountNumber) {
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                "xmlns:dev=\"http://temenos.com/DEVMB3\">" +
                "   <soapenv:Header/>" +
                "   <soapenv:Body>" +
                "      <dev:ToGetAccountInfo>" +
                "         <WebRequestCommon>" +
                "            <company/>" +
                "            <password>" + password + "</password>" +
                "            <userName>" + username + "</userName>" +
                "         </WebRequestCommon>" +
                "         <CPBGETACCOUNTINFOType>" +
                "            <enquiryInputCollection>" +
                "               <columnName>@ID</columnName>" +
                "               <criteriaValue>" + accountNumber + "</criteriaValue>" +
                "               <operand>EQ</operand>" +
                "            </enquiryInputCollection>" +
                "         </CPBGETACCOUNTINFOType>" +
                "      </dev:ToGetAccountInfo>" +
                "   </soapenv:Body>" +
                "</soapenv:Envelope>";
    }
    
    /**
     * Parse SOAP response to extract account information
     */
    private BankAccountInfo parseAccountInfoResponse(String soapResponse) {
        // Create builder for account info
        BankAccountInfo.BankAccountInfoBuilder builder = BankAccountInfo.builder()
                .accountExists(true);
        
        // Parse currency
        extractTagValue(soapResponse, "<ns4:ACCOUNTCCY>", "</ns4:ACCOUNTCCY>")
            .ifPresent(builder::currency);
        
        // Parse account title
        extractTagValue(soapResponse, "<ns4:SHORTTITLE>", "</ns4:SHORTTITLE>")
            .ifPresent(builder::accountTitle);
        
        // Parse customer ID
        extractTagValue(soapResponse, "<ns4:CUSTOMER>", "</ns4:CUSTOMER>")
            .ifPresent(builder::customerId);
        
        // Parse status code
        extractTagValue(soapResponse, "<ns4:STATUSCODE>", "</ns4:STATUSCODE>")
            .ifPresent(builder::statusCode);
        
        // Parse phone number
        extractTagValue(soapResponse, "<ns4:PHNO>", "</ns4:PHNO>")
            .ifPresent(builder::phoneNumber);
        
        // Parse opening date
        extractTagValue(soapResponse, "<ns4:OPENINGDATE>", "</ns4:OPENINGDATE>")
            .ifPresent(builder::openingDate);
        
        return builder.build();
    }
    
    /**
     * Helper method to extract value between XML tags
     */
    private java.util.Optional<String> extractTagValue(String xml, String startTag, String endTag) {
        int startPos = xml.indexOf(startTag);
        if (startPos < 0) {
            return java.util.Optional.empty();
        }
        
        startPos += startTag.length();
        int endPos = xml.indexOf(endTag, startPos);
        if (endPos < 0) {
            return java.util.Optional.empty();
        }
        
        String value = xml.substring(startPos, endPos).trim();
        return java.util.Optional.of(value);
    }
}
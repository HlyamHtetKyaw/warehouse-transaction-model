package com.aplusbinary.binarypixor.doc.service.impl;

import com.aplusbinary.binarypixor.doc.config.OpenAIConfig;
import com.aplusbinary.binarypixor.doc.dto.NLPParsingResult;
import com.aplusbinary.binarypixor.doc.model.Invoice;
import com.aplusbinary.binarypixor.doc.model.InvoiceItem;
import com.aplusbinary.binarypixor.doc.model.Vendor;
import com.aplusbinary.binarypixor.doc.service.NLPService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class NLPServiceImpl implements NLPService {

    private static final Logger logger = LoggerFactory.getLogger(NLPServiceImpl.class);
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    private final WebClient webClient;
    private final String model;
    private final ObjectMapper objectMapper;

    public NLPServiceImpl(OpenAIConfig openAIConfig) {
        this.model = openAIConfig.getModelName();
        this.webClient = WebClient.builder()
                .baseUrl(OPENAI_API_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openAIConfig.getApiKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.objectMapper = new ObjectMapper();
        logger.info("NLP Service initialized with OpenAI model: {}", model);
    }

    @Override
    public Invoice parseInvoiceEntities(String rawText) throws Exception {
        logger.info("Parsing invoice entities from text (length: {})", rawText.length());

        String prompt = buildPrompt(rawText);
        String requestBody = buildOpenAIRequest(prompt);
        
        try {
            String response = webClient.post()
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            logger.info("Received AI response: {}", response);
            
            return parseResponse(response, rawText);
            
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            // Handle specific HTTP errors from OpenAI API
            int statusCode = e.getStatusCode().value();
            String responseBody = e.getResponseBodyAsString();
            
            if (statusCode == 429) {
                throw new Exception("OpenAI Rate Limit (429): Too many requests. Wait and try again. Response: " + responseBody);
            } else if (statusCode == 401) {
                throw new Exception("OpenAI Authentication Error (401): Invalid API key. Check your OpenAI API key configuration. Response: " + responseBody);
            } else if (statusCode == 400) {
                throw new Exception("OpenAI Bad Request (400): Invalid request format. Response: " + responseBody);
            } else if (statusCode == 500) {
                throw new Exception("OpenAI Server Error (500): OpenAI service is temporarily unavailable. Try again later. Response: " + responseBody);
            } else {
                throw new Exception("OpenAI API Error (" + statusCode + "): " + e.getMessage() + ". Response: " + responseBody);
            }
        } catch (Exception e) {
            // Handle other exceptions (network, timeout, etc.)
            if (e.getMessage().contains("Connection") || e.getMessage().contains("timeout")) {
                throw new Exception("Network Error: Cannot connect to OpenAI API. Check your internet connection. Original error: " + e.getMessage());
            } else {
                throw new Exception("Unexpected error calling OpenAI API: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public NLPParsingResult parseInvoiceEntitiesWithData(String rawText) throws Exception {
        logger.info("Parsing invoice entities from text with data capture (length: {})", rawText.length());

        String prompt = buildPrompt(rawText);
        String requestBody = buildOpenAIRequest(prompt);
        
        try {
            String response = webClient.post()
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            logger.info("Received AI response for data capture: {}", response);
            
            Invoice parsedInvoice = parseResponse(response, rawText);
            
            // Return result with request/response data for training
            return new NLPParsingResult(parsedInvoice, requestBody, response);
            
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            // Handle specific HTTP errors from OpenAI API
            int statusCode = e.getStatusCode().value();
            String responseBody = e.getResponseBodyAsString();
            
            if (statusCode == 429) {
                throw new Exception("OpenAI Rate Limit (429): Too many requests. Wait and try again. Response: " + responseBody);
            } else if (statusCode == 401) {
                throw new Exception("OpenAI Authentication Error (401): Invalid API key. Check your OpenAI API key configuration. Response: " + responseBody);
            } else if (statusCode == 400) {
                throw new Exception("OpenAI Bad Request (400): Invalid request format. Response: " + responseBody);
            } else if (statusCode == 500) {
                throw new Exception("OpenAI Server Error (500): OpenAI service is temporarily unavailable. Try again later. Response: " + responseBody);
            } else {
                throw new Exception("OpenAI API Error (" + statusCode + "): " + e.getMessage() + ". Response: " + responseBody);
            }
        } catch (Exception e) {
            // Handle other exceptions (network, timeout, etc.)
            if (e.getMessage().contains("Connection") || e.getMessage().contains("timeout")) {
                throw new Exception("Network Error: Cannot connect to OpenAI API. Check your internet connection. Original error: " + e.getMessage());
            } else {
                throw new Exception("Unexpected error calling OpenAI API: " + e.getMessage(), e);
            }
        }
    }

    private String buildOpenAIRequest(String prompt) throws Exception {
        ObjectNode request = objectMapper.createObjectNode();
        request.put("model", model);
        request.put("temperature", 0.0);
        request.put("max_tokens", 1500);
        
        ArrayNode messages = request.putArray("messages");
        
        ObjectNode systemMessage = messages.addObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are an expert at extracting structured data from invoices. Always respond with valid JSON only, no additional text.");
        
        ObjectNode userMessage = messages.addObject();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        
        return objectMapper.writeValueAsString(request);
    }

    private String buildPrompt(String rawText) {
        return String.format("""
            Extract the following information from this invoice text and return ONLY a JSON object with this exact structure:
            {
              "invoiceNumber": "string",
              "invoiceDate": "YYYY-MM-DD",
              "vendorName": "string",
              "vendorAddress": "string",
              "vendorPhone": "string",
              "vendorEmail": "string",
              "vendorTaxId": "string (VAT number, Tax ID, EIN, etc.)",
              "subtotal": number,
              "taxAmount": number,
              "totalAmount": number,
              "currency": "string (ISO code like USD, EUR, GBP, etc.)",
              "items": [
                {
                  "description": "string",
                  "quantity": number,
                  "unitPrice": number,
                  "amount": number,
                  "productCode": "string"
                }
              ]
            }
            
            Rules:
            - Use null for any field that cannot be found
            - Date must be in YYYY-MM-DD format
            - Numbers should be numeric values without currency symbols
            - Extract all line items found in the invoice
            
            Invoice Text:
            %s
            """, rawText);
    }

    private Invoice parseResponse(String response, String rawText) throws Exception {
        try {
            // Parse OpenAI response
            JsonNode responseNode = objectMapper.readTree(response);
            String content = responseNode.get("choices").get(0).get("message").get("content").asText();
            
            // Clean the response if it contains markdown code blocks
            String cleanedResponse = content.trim();
            if (cleanedResponse.startsWith("```json")) {
                cleanedResponse = cleanedResponse.substring(7);
            } else if (cleanedResponse.startsWith("```")) {
                cleanedResponse = cleanedResponse.substring(3);
            }
            if (cleanedResponse.endsWith("```")) {
                cleanedResponse = cleanedResponse.substring(0, cleanedResponse.length() - 3);
            }
            cleanedResponse = cleanedResponse.trim();

            JsonNode jsonNode = objectMapper.readTree(cleanedResponse);
            
            Invoice invoice = new Invoice();
            invoice.setRawText(rawText);
            
            // Parse basic fields
            String invoiceDateString = getStringValue(jsonNode, "invoiceDate");
            logger.info("OpenAI returned invoice date: '{}' for raw text: '{}'", invoiceDateString, rawText);
            
            invoice.setInvoiceNumber(getStringValue(jsonNode, "invoiceNumber"));
            invoice.setInvoiceDate(parseDate(invoiceDateString));
            
            // Parse vendor info and create Vendor object
            Vendor vendor = new Vendor();
            vendor.setName(getStringValue(jsonNode, "vendorName"));
            vendor.setAddress(getStringValue(jsonNode, "vendorAddress"));
            vendor.setPhone(getStringValue(jsonNode, "vendorPhone"));
            vendor.setEmail(getStringValue(jsonNode, "vendorEmail"));
            vendor.setTaxId(getStringValue(jsonNode, "vendorTaxId"));
            invoice.setVendor(vendor);
            
            // Parse monetary fields
            invoice.setSubtotal(getBigDecimalValue(jsonNode, "subtotal"));
            invoice.setTaxAmount(getBigDecimalValue(jsonNode, "taxAmount"));
            invoice.setTotalAmount(getBigDecimalValue(jsonNode, "totalAmount"));
            invoice.setCurrency(getStringValue(jsonNode, "currency", "USD"));
            
            // Parse items
            if (jsonNode.has("items") && jsonNode.get("items").isArray()) {
                for (JsonNode itemNode : jsonNode.get("items")) {
                    InvoiceItem item = new InvoiceItem();
                    item.setDescription(getStringValue(itemNode, "description"));
                    item.setQuantity(getIntValue(itemNode, "quantity"));
                    item.setUnitPrice(getBigDecimalValue(itemNode, "unitPrice"));
                    item.setAmount(getBigDecimalValue(itemNode, "amount"));
                    item.setProductCode(getStringValue(itemNode, "productCode"));
                    
                    invoice.addItem(item);
                }
            }
            
            logger.info("Successfully parsed invoice: {} with {} items", 
                    invoice.getInvoiceNumber(), invoice.getItems().size());
            
            return invoice;
            
        } catch (Exception e) {
            logger.error("Failed to parse AI response", e);
            throw new Exception("Failed to parse invoice data from AI response: " + e.getMessage(), e);
        }
    }

    private String getStringValue(JsonNode node, String fieldName) {
        return getStringValue(node, fieldName, null);
    }

    private String getStringValue(JsonNode node, String fieldName, String defaultValue) {
        if (node.has(fieldName) && !node.get(fieldName).isNull()) {
            return node.get(fieldName).asText();
        }
        return defaultValue;
    }

    private BigDecimal getBigDecimalValue(JsonNode node, String fieldName) {
        if (node.has(fieldName) && !node.get(fieldName).isNull()) {
            try {
                return new BigDecimal(node.get(fieldName).asText());
            } catch (Exception e) {
                logger.warn("Failed to parse BigDecimal from field: {}", fieldName);
            }
        }
        return null;
    }

    private Integer getIntValue(JsonNode node, String fieldName) {
        if (node.has(fieldName) && !node.get(fieldName).isNull()) {
            return node.get(fieldName).asInt();
        }
        return null;
    }

    private LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        
        try {
            return LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            logger.warn("Failed to parse date: {}", dateString);
            return null;
        }
    }
}

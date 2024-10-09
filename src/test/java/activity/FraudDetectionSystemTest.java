package activity;

import org.junit.Before;
import org.junit.Test;

import activity.FraudDetectionSystem.FraudCheckResult;
import activity.FraudDetectionSystem.Transaction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

import activity.FraudDetectionSystem.Transaction;
import activity.FraudDetectionSystem.FraudCheckResult;
import activity.FraudDetectionSystem;


public class FraudDetectionSystemTest {

    // testando se junit tá funcionando
    @Test
    public void testIsJUnitWorking(){
        assertEquals(2, 1+1);
    }

    FraudDetectionSystem system = new FraudDetectionSystem();

    @Test
    void testLargeTransactionAmount() {
        // Cenário: Transação acima de 10.000
        Transaction currentTransaction = new Transaction(15000.0, LocalDateTime.now(), "New York");
        List<Transaction> previousTransactions = new ArrayList<>();
        List<String> blacklistedLocations = new ArrayList<>();
        
        FraudCheckResult result = system.checkForFraud(currentTransaction, previousTransactions, blacklistedLocations);
        
        assertTrue(result.isFraudulent);
        assertFalse(result.isBlocked);
        assertTrue(result.verificationRequired);
        assertEquals(50, result.riskScore);
    }

    @Test
    void testExcessiveTransactionsInLastHour() {
        // Cenário: Mais de 10 transações na última hora
        Transaction currentTransaction = new Transaction(500.0, LocalDateTime.now(), "New York");
        List<Transaction> previousTransactions = new ArrayList<>();
        
        // Criar 11 transações recentes dentro de 60 minutos
        for (int i = 0; i < 11; i++) {
            previousTransactions.add(new Transaction(100.0, LocalDateTime.now().minusMinutes(5 * i), "New York"));
        }
        List<String> blacklistedLocations = new ArrayList<>();
        
        FraudCheckResult result = system.checkForFraud(currentTransaction, previousTransactions, blacklistedLocations);
        
        assertFalse(result.isFraudulent);
        assertTrue(result.isBlocked);
        assertFalse(result.verificationRequired);
        assertEquals(30, result.riskScore);
    }

    @Test
    void testLocationChangeWithinShortTimeFrame() {
        // Cenário: Mudança de localização em um curto espaço de tempo (< 30 min)
        Transaction currentTransaction = new Transaction(1000.0, LocalDateTime.now(), "London");
        List<Transaction> previousTransactions = new ArrayList<>();
        previousTransactions.add(new Transaction(500.0, LocalDateTime.now().minusMinutes(20), "Paris"));
        List<String> blacklistedLocations = new ArrayList<>();
        
        FraudCheckResult result = system.checkForFraud(currentTransaction, previousTransactions, blacklistedLocations);
        
        assertTrue(result.isFraudulent);
        assertFalse(result.isBlocked);
        assertTrue(result.verificationRequired);
        assertEquals(20, result.riskScore);
    }

    @Test
    void testTransactionInBlacklistedLocation() {
        // Cenário: Transação em local da lista negra
        Transaction currentTransaction = new Transaction(700.0, LocalDateTime.now(), "Restricted Area");
        List<Transaction> previousTransactions = new ArrayList<>();
        List<String> blacklistedLocations = Arrays.asList("Restricted Area");
        
        FraudCheckResult result = system.checkForFraud(currentTransaction, previousTransactions, blacklistedLocations);
        
        assertFalse(result.isFraudulent);
        assertTrue(result.isBlocked);
        assertFalse(result.verificationRequired);
        assertEquals(100, result.riskScore);
    }

    @Test
    void testLowRiskTransaction() {
        // Cenário: Transação normal, sem risco identificado
        Transaction currentTransaction = new Transaction(500.0, LocalDateTime.now(), "New York");
        List<Transaction> previousTransactions = new ArrayList<>();
        List<String> blacklistedLocations = new ArrayList<>();
        
        FraudCheckResult result = system.checkForFraud(currentTransaction, previousTransactions, blacklistedLocations);
        
        assertFalse(result.isFraudulent);
        assertFalse(result.isBlocked);
        assertFalse(result.verificationRequired);
        assertEquals(0, result.riskScore);
    }

    @Test
    void testCombinationOfRisks() {
        // Cenário: Combinação de múltiplos fatores de risco (montante alto e mudança de localização)
        Transaction currentTransaction = new Transaction(15000.0, LocalDateTime.now(), "Berlin");
        List<Transaction> previousTransactions = new ArrayList<>();
        previousTransactions.add(new Transaction(300.0, LocalDateTime.now().minusMinutes(20), "Hamburg"));
        List<String> blacklistedLocations = new ArrayList<>();
        
        FraudCheckResult result = system.checkForFraud(currentTransaction, previousTransactions, blacklistedLocations);
        
        assertTrue(result.isFraudulent);
        assertFalse(result.isBlocked);
        assertTrue(result.verificationRequired);
        assertEquals(50 + 20, result.riskScore);  // Montante alto (50) + Mudança de local (20)
    }

}

package activity;

import org.junit.Before;
import org.junit.Test;

import activity.FlightBookingSystem.BookingResult;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

import activity.FlightBookingSystem.BookingResult;

public class FlightBookingSystemTest {

    // testando se junit tá funcionando
    @Test
    public void testIsJUnitWorking(){
        assertEquals(2, 1+1);
    }


    FlightBookingSystem system = new FlightBookingSystem();

    @Test
    void testBookingWithoutAvailableSeats() {
        BookingResult result = system.bookFlight(5, LocalDateTime.now(), 3, 200.0, 50, false, 
                                                 LocalDateTime.now().plusDays(5), 0);
        assertFalse(result.confirmation);
        assertEquals(0, result.totalPrice);
        assertEquals(0, result.refundAmount);
        assertFalse(result.pointsUsed);
    }

    @Test
    void testRegularBooking() {
        BookingResult result = system.bookFlight(2, LocalDateTime.now(), 5, 150.0, 30, false, 
                                                 LocalDateTime.now().plusDays(3), 0);
        assertTrue(result.confirmation);
        assertEquals(2 * 150 * (30.0 / 100.0) * 0.8, result.totalPrice);
        assertEquals(0, result.refundAmount);
        assertFalse(result.pointsUsed);
    }

    @Test
    void testBookingWithLastMinuteFee() {
        BookingResult result = system.bookFlight(1, LocalDateTime.now(), 3, 200.0, 50, false, 
                                                 LocalDateTime.now().plusHours(20), 0);
        assertTrue(result.confirmation);
        assertEquals(200 * (50.0 / 100.0) * 0.8 + 100, result.totalPrice);
        assertEquals(0, result.refundAmount);
        assertFalse(result.pointsUsed);
    }

    @Test
    void testBookingWithRewardPoints() {
        BookingResult result = system.bookFlight(3, LocalDateTime.now(), 5, 180.0, 40, false, 
                                                 LocalDateTime.now().plusDays(2), 1000);
        assertTrue(result.confirmation);
        double expectedPrice = 3 * 180 * (40.0 / 100.0) * 0.8 - 1000 * 0.01;
        assertEquals(expectedPrice, result.totalPrice, 0.01);
        assertEquals(0, result.refundAmount);
        assertTrue(result.pointsUsed);
    }

    @Test
    void testGroupBookingDiscount() {
        BookingResult result = system.bookFlight(6, LocalDateTime.now(), 10, 120.0, 20, false, 
                                                 LocalDateTime.now().plusDays(4), 0);
        double expectedPrice = 6 * 120 * (20.0 / 100.0) * 0.8 * 0.95; // Group discount
        assertTrue(result.confirmation);
        assertEquals(expectedPrice, result.totalPrice, 0.01);
    }

    @Test
    void testCancellationMoreThan48Hours() {
        BookingResult result = system.bookFlight(3, LocalDateTime.now(), 5, 180.0, 40, true, 
                                                 LocalDateTime.now().plusDays(3), 0);
        assertFalse(result.confirmation);
        double expectedPrice = 3 * 180 * (40.0 / 100.0) * 0.8;
        assertEquals(expectedPrice, result.refundAmount, 0.01);
    }

    @Test
    void testCancellationLessThan48Hours() {
        BookingResult result = system.bookFlight(3, LocalDateTime.now(), 5, 180.0, 40, true, 
                                                 LocalDateTime.now().plusHours(40), 0);
        assertFalse(result.confirmation);
        double expectedPrice = 3 * 180 * (40.0 / 100.0) * 0.8 * 0.5; // 50% refund
        assertEquals(expectedPrice, result.refundAmount, 0.01);
    }

}

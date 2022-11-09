package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentMatchers;

import thirdparty.paymentgateway.TicketPaymentService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class TicketServiceImplTest {

    TicketServiceImpl ticketService;

    TicketTypeRequest ticketTypeRequestMock;

    TicketPaymentService ticketPaymentService;

    @BeforeEach
    void setUp() {
        ticketService = mock(TicketServiceImpl.class);
        ticketPaymentService = mock(TicketPaymentService.class);
        ticketTypeRequestMock = mock(TicketTypeRequest.class);
    }

    @Test
    void purchaseTicketsSuccessTest() {

        doNothing().when(ticketService).purchaseTickets(anyLong(), ArgumentMatchers.<TicketTypeRequest>any());

        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest ticketTypeRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        TicketTypeRequest ticketTypeRequest3 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

        ticketService.purchaseTickets(1L, ticketTypeRequest, ticketTypeRequest2, ticketTypeRequest3);

        verify(ticketService, times(1)).purchaseTickets(1L, ticketTypeRequest, ticketTypeRequest2, ticketTypeRequest3);
    }

    @Test
    public void purchaseTicketsTestThrowExceptionWhenIdNull() {
        doThrow(new InvalidPurchaseException())
                .when(ticketService).purchaseTickets(eq(null), ArgumentMatchers.<TicketTypeRequest>any());

        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest ticketTypeRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(null, ticketTypeRequest, ticketTypeRequest2));
    }

    @Test
    public void purchaseTicketsTestThrowExceptionWhenTotalNoOfTicketsExceeds20() {
        doThrow(new InvalidPurchaseException())
                .when(ticketService).purchaseTickets(eq(null), ArgumentMatchers.<TicketTypeRequest>any());

        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 10);
        TicketTypeRequest ticketTypeRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 12);

        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(null, ticketTypeRequest, ticketTypeRequest2));
    }

    @Test
    public void purchaseTicketsTestThrowExceptionWhenBookingChildWithoutAdult() {
        doThrow(new InvalidPurchaseException())
                .when(ticketService).purchaseTickets(eq(null), ArgumentMatchers.<TicketTypeRequest>any());

        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(null, ticketTypeRequest));
    }

    @Test
    public void purchaseTicketsTestThrowExceptionWhenBookingInfantWithoutAdult() {
        doThrow(new InvalidPurchaseException())
                .when(ticketService).purchaseTickets(eq(null), ArgumentMatchers.<TicketTypeRequest>any());

        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(null, ticketTypeRequest));
    }
}
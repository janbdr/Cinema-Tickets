package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.HashMap;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */

    TicketPaymentService ticketPaymentService;
    SeatReservationService seatReservationService;

    /**
     *
     * @param accountId
     * @param ticketTypeRequests
     * @throws InvalidPurchaseException
     */
    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {

        boolean validAccountId = validateAccountId(accountId);

        if(validAccountId && ticketTypeRequests.length != 0){
            HashMap<String, Integer> seatsPerType = calculateSeatsPerType(ticketTypeRequests);

            int totalAmountToPay = calculateTotalAmount(seatsPerType);

            int totalSeatsToAllocate = calculateTotalNoOfSeats(seatsPerType);

            if(totalSeatsToAllocate != 0 && totalSeatsToAllocate <= 20){
                ticketPaymentService.makePayment(accountId, totalAmountToPay);
                seatReservationService.reserveSeat(accountId, totalSeatsToAllocate);
            } else {
                throw new InvalidPurchaseException();
            }
        } else {
            throw new InvalidPurchaseException();
        }

    }


    /**
     * Returns a HashMap of - Total no of seats per each Type (ADULT, CHILD, INFANT)
     * @param ticketTypeRequests
     * @return seatsPerType
     */
    private HashMap<String, Integer> calculateSeatsPerType(TicketTypeRequest... ticketTypeRequests){

        HashMap<String, Integer> seatsPerType = new HashMap<>();

        int noOfAdults = 0;
        int noOfChildren = 0;
        int noOfInfants = 0;

        for(TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {
            switch( ticketTypeRequest.getTicketType() ) {
                case ADULT: noOfAdults++; break;
                case CHILD: noOfChildren++; break;
                case INFANT: noOfInfants++; break;
            }
        }

        if(noOfAdults == 0){ // At least one adult should be present
            throw new InvalidPurchaseException();
        } else {
            seatsPerType.put("Adults", noOfAdults);
            seatsPerType.put("Children", noOfChildren);
            seatsPerType.put("Infants", noOfInfants);
        }

        return seatsPerType;
    }

    /**
     * Calculates the total no of seats.(Excluding Infants)
     * @param seatsPerType
     * @return
     */
    private int calculateTotalNoOfSeats(HashMap<String, Integer> seatsPerType){
        return seatsPerType.get("Adults") + seatsPerType.get("Children");
    }

    /**
     * Validating Account Id
     * @param accountId
     * @return
     */
    private boolean validateAccountId(Long accountId){
        if(accountId != null && accountId > 0){
            return true;
        }
        return false;
    }

    /**
     * Calculate total amount needed to pay (Excluding Infants)
     * @param seatsPerType
     * @return
     */
    private int calculateTotalAmount(HashMap<String, Integer> seatsPerType){
        return seatsPerType.get("Adults") * 20 + seatsPerType.get("Children") * 10;
    }

}

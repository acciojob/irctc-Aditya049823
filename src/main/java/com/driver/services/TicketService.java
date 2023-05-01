package com.driver.services;


import com.driver.EntryDto.BookTicketEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Station;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.PassengerRepository;
import com.driver.repository.TicketRepository;
import com.driver.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    TrainRepository trainRepository;

    @Autowired
    PassengerRepository passengerRepository;


    public Integer bookTicket(BookTicketEntryDto bookTicketEntryDto)throws Exception{

        //Check for validity
        //Use bookedTickets List from the TrainRepository to get bookings done against that train
        // Incase the there are insufficient tickets
        // throw new Exception("Less tickets are available");
        //otherwise book the ticket, calculate the price and other details
        //Save the information in corresponding DB Tables
        //Fare System : Check problem statement
        //Incase the train doesn't pass through the requested stations
        //throw new Exception("Invalid stations");
        //Save the bookedTickets in the train Object
        //Also in the passenger Entity change the attribute bookedTickets by using the attribute bookingPersonId.
       //And the end return the ticketId that has come from db

      Train train=trainRepository.findById(bookTicketEntryDto.getTrainId()).get();
      List<Ticket>bookedTickets=train.getBookedTickets();
      int count=0;
      for(Ticket t:bookedTickets)
      {
          count+=t.getPassengersList().size();
      }
      if(count+bookTicketEntryDto.getNoOfSeats()>train.getNoOfSeats())
      {
          throw new Exception("Less tickets are available");
      }
      String[]station=train.getRoute().split(",");
      List<Passenger>passengers=new ArrayList<>();
      List<Integer>id=bookTicketEntryDto.getPassengerIds();
      for(int ids:id)
      {
          passengers.add(passengerRepository.findById(ids).get());
      }
      int x=-1;
      int y=-1;
      for(int i=0;i< station.length;i++)
      {
          if(bookTicketEntryDto.getFromStation().equals(station[i]))
          {
              x=i;
              break;
          }
      }
      for(int i=0;i< station.length;i++)
      {
          if (bookTicketEntryDto.getToStation().equals(station[i]))
          {
              y=i;
              break;
          }
      }
      if(x==-1 || y==-1 || y-x<0)
      {
          throw new Exception("Invalid stations");
      }
      Ticket ticket=new Ticket();
      ticket.setPassengersList(passengers);
      ticket.setFromStation(bookTicketEntryDto.getFromStation());
      ticket.setToStation(bookTicketEntryDto.getToStation());

      int fare=0;
      fare=bookTicketEntryDto.getNoOfSeats()*(y-x)*30;

      ticket.setTotalFare(fare);
      ticket.setTrain(train);

      train.getBookedTickets().add(ticket);
      train.setNoOfSeats(train.getNoOfSeats()-bookTicketEntryDto.getNoOfSeats());

      Passenger passenger=passengerRepository.findById(bookTicketEntryDto.getBookingPersonId()).get();
      passenger.getBookedTickets().add(ticket);

      trainRepository.save(train);
      return ticketRepository.save(ticket).getTicketId();

    }
}

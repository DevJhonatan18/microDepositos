package com.example.demo_deposit.consumer;

import java.util.Optional;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.example.demo_deposit.domain.Transaction;
import com.example.demo_deposit.producer.DepositEventProducer;
import com.example.demo_deposit.service.ITransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

@Component
public class DepositEventConsumer {
	
	private Logger log = LoggerFactory.getLogger(DepositEventConsumer.class);
	
	
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
    KafkaTemplate<Integer,String> kafkaTemplate;
	
	@Autowired
	private ITransactionService transactionService; 
	
	public void processLibraryEvent(ConsumerRecord<Integer,String> consumerRecord) throws JsonProcessingException {
        Transaction libraryEvent = objectMapper.readValue(consumerRecord.value(), Transaction.class);
        
        
        log.info("libraryEvent : {} ", libraryEvent);

        if(libraryEvent.getId() != null && libraryEvent.getId() == 000){
            throw new RecoverableDataAccessException("Temporary Network Issue");
        }

        switch(libraryEvent.getType()){
            case "1":
                 save(libraryEvent);
            	 log.info("Opc : {} ", "1");
            	break;
            case "2":
            	log.info("Opc : {} ", "2");
                //validate the libraryevent
                 validate(libraryEvent);
                 save(libraryEvent);
                break;
            default:
                log.info("Invalid Library Event Type");
        }

    }
	
	private void save(Transaction libraryEvent) {
        // libraryEvent.getBook().setLibraryEvent(libraryEvent);
		// transactionService.save(libraryEvent);
        log.info("Successfully Persisted the libary Event {} ", libraryEvent);
    }
	
    private void validate(Transaction libraryEvent) {
        if(libraryEvent.getId()  ==null){
            throw new IllegalArgumentException("Library Event Id is missing");
        }
        
        log.info("---- ID ------ : {} ", libraryEvent.getId() );
        
        log.info("---- ID ------ : {} ", transactionService.findById(libraryEvent.getId()) );
        
        Optional<Transaction> libraryEventOptional = transactionService.findById(libraryEvent.getId());
        if(!libraryEventOptional.isPresent()){
            throw new IllegalArgumentException("Not a valid library Event");
        }
        log.info("Validation is successful for the library Event : {} ", libraryEventOptional.get());
    }
    
    public void handleRecovery(ConsumerRecord<Integer,String> record){

        Integer key = record.key();
        String message = record.value();

        ListenableFuture<SendResult<Integer,String>> listenableFuture = kafkaTemplate.sendDefault(key, message);
        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                handleFailure(key, message, ex);
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                handleSuccess(key, message, result);
            }
        });
    }
    
    
    private void handleFailure(Integer key, String value, Throwable ex) {
        log.error("Error Sending the Message and the exception is {}", ex.getMessage());
        try {
            throw ex;
        } catch (Throwable throwable) {
            log.error("Error in OnFailure: {}", throwable.getMessage());
        }
    }
    
    
    private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
        log.info("Message Sent SuccessFully for the key : {} and the value is {} , partition is {}", key, value, result.getRecordMetadata().partition());
    }
    
    
    
    @KafkaListener(topics = {"transaction-events"})
    public void onMessage(ConsumerRecord<Integer,String> consumerRecord) throws JsonProcessingException {

        log.info("ConsumerRecord : {} ", consumerRecord );
        processLibraryEvent(consumerRecord);

    }
    
	
	
	 

}

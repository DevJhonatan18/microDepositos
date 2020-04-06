package com.example.demo_deposit.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo_deposit.dao.TransactionDAO;
import com.example.demo_deposit.domain.Transaction;

@Service
public class TransactionServiceImpl implements ITransactionService{
 
	 @Autowired
	 private TransactionDAO transactionDAO;

	@Override
	public Transaction save(Transaction transaction) {
		return transactionDAO.save(transaction);
	}

	@Override
	public Optional<Transaction> findById(Integer id) {
		// TODO Auto-generated method stub
		return transactionDAO.findById(id);
	}
	 
	 
	 
	
}

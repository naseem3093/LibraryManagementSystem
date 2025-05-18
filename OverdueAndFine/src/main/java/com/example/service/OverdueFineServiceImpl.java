package com.example.service;

import com.example.exceptions.FineNotFoundException;
import com.example.model.OverdueFine;
import com.example.repository.OverdueFineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OverdueFineServiceImpl implements OverdueFineService {

	private static final Logger logger = LoggerFactory.getLogger(OverdueFineServiceImpl.class);

	@Autowired
	private OverdueFineRepository repository;

	@Override
	public int generateFine(int transactionId) {
		logger.info("Generating fine for Transaction ID: {}", transactionId);

		OverdueFine fine = new OverdueFine();
		fine.setTransactionId(transactionId);
		fine.setFineAmount(calculateFine(transactionId));
		fine.setFineStatus("Pending");

		OverdueFine savedFine = repository.save(fine);
		logger.info("Fine generated successfully - Fine ID: {}, Amount: ₹{}", savedFine.getFineId(),
				savedFine.getFineAmount());

		return savedFine.getFineId();
	}

	private double calculateFine(int transactionId) {
		logger.info("Calculating fine for Transaction ID: {}", transactionId);

		Optional<OverdueFine> transaction = repository.findByTransactionId(transactionId);
		if (transaction.isPresent()) {
			int overdueDays = transaction.get().getOverdueDays();
			double fineAmount = overdueDays * 5.0; // ₹5 per overdue day
			logger.info("Fine calculated - Overdue Days: {}, Fine Amount: ₹{}", overdueDays, fineAmount);
			return fineAmount;
		}

		logger.warn("Transaction ID {} not found, applying default fine.", transactionId);
		return 50.0; // Default fine if transaction isn't found
	}

	@Override
	public void trackOverdueFines() {
		logger.info("Tracking overdue fines for date: {}", LocalDate.now());

		List<OverdueFine> overdueTransactions = repository.findOverdueTransactions(LocalDate.now());
		for (OverdueFine fine : overdueTransactions) {
			int overdueDays = fine.getOverdueDays();
			double fineAmount = overdueDays * 5.0;
			fine.setFineAmount(fineAmount);
			fine.setFineStatus("Pending");
			repository.save(fine);

			logger.info("Updated fine - Fine ID: {}, Overdue Days: {}, New Fine Amount: ₹{}", fine.getFineId(),
					overdueDays, fineAmount);
		}
	}

	@Override
	public OverdueFine getFineDetails(int fineId) throws FineNotFoundException {
		logger.info("Fetching fine details for Fine ID: {}", fineId);

		Optional<OverdueFine> fine = repository.findByTransactionId(fineId);
		OverdueFine overdueFine = fine.orElseThrow(() -> {
			logger.error("Fine not found for Transaction ID: {}", fineId);
			return new FineNotFoundException("Fine not found for transaction ID: " + fineId);
		});

		logger.info("Fine details retrieved successfully - Fine ID: {}, Amount: ₹{}", overdueFine.getFineId(),
				overdueFine.getFineAmount());
		return overdueFine;
	}

	@Override
	public String payFine(int fineId, double amountPaid) throws FineNotFoundException {
		logger.info("Processing fine payment - Fine ID: {}, Amount Paid: ₹{}", fineId, amountPaid);

		Optional<OverdueFine> fine = repository.findById(fineId);
		if (fine.isPresent()) {
			OverdueFine overdueFine = fine.get();

			if (amountPaid >= overdueFine.getFineAmount()) { // Checks payment amount
				overdueFine.setFineStatus("Paid");
				repository.save(overdueFine);
				logger.info("Fine paid successfully - Fine ID: {}", fineId);
				return "Fine paid successfully!";
			} else {
				logger.warn("Insufficient payment for Fine ID: {} - Required: ₹{}, Paid: ₹{}", fineId,
						overdueFine.getFineAmount(), amountPaid);
				return "Insufficient amount, please pay full fine!";
			}
		} else {
			logger.error("Fine ID {} not found", fineId);
			throw new FineNotFoundException("Fine ID " + fineId + " not found.");
		}
	}

	@Override
	public List<OverdueFine> getPendingFines() {
		logger.info("Fetching pending fines...");
		List<OverdueFine> pendingFines = repository.findByFineStatus("Pending");
		logger.info("Total pending fines found: {}", pendingFines.size());
		return pendingFines;
	}
}

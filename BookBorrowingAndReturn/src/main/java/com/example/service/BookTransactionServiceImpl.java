package com.example.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.client.OverdueFinesClient;
import com.example.exceptions.TransactionNotFoundException;
import com.example.exceptions.BorrowingLimitExceededException;
import com.example.model.BookTransaction;
import com.example.repository.BookTransactionRepository;

@Service
public class BookTransactionServiceImpl implements BookTransactionService {

    private static final Logger logger = LoggerFactory.getLogger(BookTransactionServiceImpl.class);

    @Autowired
    private BookTransactionRepository repository;

    @Autowired
    private OverdueFinesClient overdueFinesClient;

    @Override
    public BookTransaction borrowBook(BookTransaction transaction) {
        logger.info("Attempting to borrow book - Book ID: {}, Member ID: {}", transaction.getBookId(), transaction.getMemberId());

        // Validate Borrowing Limit (max 10 books per member)
        long borrowedBooks = repository.countBorrowedBooks(transaction.getMemberId());
        if (borrowedBooks >= 10) {
            logger.warn("Borrowing limit exceeded for member ID: {}", transaction.getMemberId());
            throw new BorrowingLimitExceededException("Borrowing limit reached (10 books max).");
        }

        // Validate Book Availability Before Borrowing
        if (!validateBookAvailability(transaction.getBookId())) {
            logger.warn("Book ID {} is currently unavailable.", transaction.getBookId());
            throw new IllegalArgumentException("Book is currently borrowed and unavailable.");
        }

        // Set Borrowing Dates and Default Status
        transaction.setBorrowDate(LocalDate.now());
        transaction.setDueDate(LocalDate.now().plusDays(14));
        transaction.setStatus(Optional.ofNullable(transaction.getStatus()).orElse("Borrowed"));

        BookTransaction savedTransaction = repository.save(transaction);
        logger.info("Book borrowed successfully - Transaction ID: {}", savedTransaction.getTransactionId());
        return savedTransaction;
    }

    @Override
    public String returnBook(int transactionId) throws TransactionNotFoundException {
        logger.info("Returning book - Transaction ID: {}", transactionId);

        Optional<BookTransaction> optional = repository.findById(transactionId);
        if (optional.isPresent()) {
            BookTransaction transaction = optional.get();
            transaction.setReturnDate(LocalDate.now());

            if (transaction.getReturnDate().isAfter(transaction.getDueDate())) {
                logger.warn("Late return detected - Transaction ID: {}", transactionId);
                applyFine(transaction);
                repository.save(transaction);
                return "Book returned late. Fine ID generated: " + transaction.getFineId();
            } else {
                transaction.setStatus("Returned");
                repository.save(transaction);
                logger.info("Book returned successfully - Transaction ID: {}", transactionId);
                return "Book returned successfully!";
            }
        } else {
            logger.error("Transaction not found - ID: {}", transactionId);
            throw new TransactionNotFoundException("Transaction ID not found.");
        }
    }

    @Override
    public boolean validateBookAvailability(int bookId) {
        logger.info("Checking availability for Book ID: {}", bookId);
        boolean isAvailable = repository.countByBookIdAndStatus(bookId, "Borrowed") == 0;
        logger.info("Book availability check - Book ID: {}, Available: {}", bookId, isAvailable);
        return isAvailable;
    }

    @Override
    public List<BookTransaction> trackOverdueBooks() {
        logger.info("Tracking overdue books...");
        List<BookTransaction> overdueBooks = repository.findByStatus("Overdue");
        logger.info("Total overdue books found: {}", overdueBooks.size());
        return overdueBooks;
    }

    @Override
    public String extendLoanPeriod(int transactionId, int extraDays) throws TransactionNotFoundException {
        logger.info("Extending loan period for Transaction ID: {} by {} days", transactionId, extraDays);

        Optional<BookTransaction> optional = repository.findById(transactionId);
        if (optional.isPresent()) {
            BookTransaction transaction = optional.get();
            transaction.setDueDate(transaction.getDueDate().plusDays(extraDays));
            repository.save(transaction);
            logger.info("Loan period extended successfully - Transaction ID: {}", transactionId);
            return "Loan period extended successfully!";
        } else {
            logger.error("Transaction not found - ID: {}", transactionId);
            throw new TransactionNotFoundException("Transaction ID not found.");
        }
    }

    // Fine Calculation Method (Extracted from returnBook)
    private void applyFine(BookTransaction transaction) {
        logger.info("Applying overdue fine - Transaction ID: {}", transaction.getTransactionId());
        int fineId = overdueFinesClient.generateFine(transaction.getTransactionId());
        transaction.setStatus("Overdue");
        transaction.setFineId(fineId);
        logger.info("Fine applied successfully - Fine ID: {}", fineId);
    }
}

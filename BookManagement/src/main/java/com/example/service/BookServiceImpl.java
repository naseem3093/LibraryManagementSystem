package com.example.service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.exceptions.BookNotFound;
import com.example.model.Book;
import com.example.repository.BookRepository;

@Service
public class BookServiceImpl implements BookService {
    private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);

    @Autowired
    private BookRepository repository;

    @Override
    public String saveBook(Book book) {
        logger.info("Saving book: {}", book);
        repository.save(book);
        logger.info("Book saved successfully");
        return "Book saved successfully";
    }

    @Override
    public Book updateBook(Book book) {
        logger.info("Updating book with ID: {}", book.getBookId());
        Book updatedBook = repository.save(book);
        logger.info("Book updated successfully: {}", updatedBook);
        return updatedBook;
    }

    @Override
    public Book getBook(int bookId) throws BookNotFound {
        logger.info("Fetching book with ID: {}", bookId);
        Optional<Book> optional = repository.findById(bookId);
        if (optional.isPresent()) {
            logger.info("Book found: {}", optional.get());
            return optional.get();
        } else {
            logger.error("Book with ID {} not found", bookId);
            throw new BookNotFound("Book ID is not valid");
        }
    }

    @Override
    public List<Book> getAllBooks() {
        logger.info("Fetching all books...");
        List<Book> books = repository.findAll();
        logger.info("Total books found: {}", books.size());
        return books;
    }

    @Override
    public String deleteBook(int bookId) throws BookNotFound {
        logger.info("Attempting to delete book with ID: {}", bookId);
        if (!repository.existsById(bookId)) {
            logger.error("Book with ID {} not found", bookId);
            throw new BookNotFound("Book with ID " + bookId + " not found!");
        }

        repository.deleteById(bookId);
        logger.info("Book with ID {} deleted successfully", bookId);
        return "Book deleted successfully!";
    }
}

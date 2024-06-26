package com.sonam.ecommerce.ecommercebackend.controllers;

import com.sonam.ecommerce.ecommercebackend.dto.BookDto;
import com.sonam.ecommerce.ecommercebackend.entity.Book;
import com.sonam.ecommerce.ecommercebackend.exception.ResourceNotFoundException;
import com.sonam.ecommerce.ecommercebackend.service.implementation.BookServiceImpl;
import com.sonam.ecommerce.ecommercebackend.service.implementation.GenreServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookApiController {

    @Autowired
    BookServiceImpl bookService;

    @Autowired
    GenreServiceImpl genreService;

    // localhost:8080/api/admin/addBook
    @PostMapping(value = "/admin/addBook", headers = ("content-type=multipart/*"))
    public ResponseEntity<?> addBook(@RequestPart(value = "file", required = false) MultipartFile file,
                                     @RequestPart("data") BookDto bookDto) throws Exception {

        Book book = new Book();
        BeanUtils.copyProperties(bookDto, book);
        Book bookAdded = null;
        if (file != null && !file.isEmpty()) {
            bookAdded = bookService.addBook(book, file);
        } else{
            Book b = Book.builder().title(bookDto.getTitle())
                    .genre(bookDto.getGenre())
                    .bookId(bookDto.getBookId())
                    .price(bookDto.getPrice())
                    .author(bookDto.getAuthor())
                    .description(bookDto.getDescription())
                    .copiesAvailable(bookDto.getCopiesAvailable()).build();
            return new ResponseEntity<>(b, HttpStatus.OK);
        }

        return new ResponseEntity<>(bookAdded, HttpStatus.OK);
    }

    // localhost:8080/api/admin/getBook/1
    @GetMapping("/admin/getBook/{bookId}")
    public ResponseEntity<BookDto> getBook(@PathVariable int bookId) throws ResourceNotFoundException {
        Book book = bookService.getBook(bookId);
        BookDto bookResponse = new BookDto();
        BeanUtils.copyProperties(book, bookResponse);
        return new ResponseEntity<>(bookResponse, HttpStatus.OK);
    }

    // localhost:8080/api/admin/updateBook/4
    @PutMapping(value="/admin/updateBook/{bookId}", headers = ("content-type=multipart/*"))
    public ResponseEntity<?> updateBook(@RequestPart(value = "file", required = false) MultipartFile file,
                                        @RequestPart("data") Book book, @PathVariable int bookId) throws Exception {
        Book b = bookService.bookExists(bookId);
        if(b != null){
            Book updateBook = Book.builder()
                    .title(book.getTitle())
                    .author(book.getAuthor())
                    .genre(book.getGenre())
                    .price(book.getPrice())
                    .description(book.getDescription())
                    .copiesAvailable(book.getCopiesAvailable())
                    .fileName(book.getFileName())
                    .fileType(book.getFileType())
                    .data(book.getData())
                    .build();
            bookService.deleteBook(bookId); // delete previous book.
            Book updatedBook = bookService.updateBook(updateBook, file);
            return new ResponseEntity<>(updatedBook, HttpStatus.OK);
        }
        return new ResponseEntity<>("Book could not be updated.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    // localhost:8080/api/admin/deleteBook/2
    @DeleteMapping("/admin/deleteBook/{bookId}")
    public ResponseEntity<?> deleteBook(@PathVariable int bookId) throws ResourceNotFoundException {
        Book book = bookService.bookExists(bookId);
        if(book != null){
            bookService.deleteBook(bookId);
            return new ResponseEntity<>(book.getTitle()+ " has been deleted", HttpStatus.OK);
        }
        return new ResponseEntity<>("Book could not deleted", HttpStatus.OK);
    }

    // localhost:8080/api/admin/getAllBooks
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @GetMapping("/getAllBooks")
    public ResponseEntity<?> getAllBooks() throws ResourceNotFoundException {
        List<Book> bookList = bookService.getAllBooks();
        return new ResponseEntity<>(bookList, HttpStatus.OK);
    }

    // localhost:8080/api/admin/copiesAvailable/6
    @Secured({"ROLE_ADMIN", "ROLE_USER"}) //Allow both admin and user roles to access this endpoint
    @GetMapping("/copiesAvailable/{bookId}")
    public ResponseEntity<?> getCopiesAvailable(@PathVariable int bookId) throws ResourceNotFoundException {
        int num = bookService.copiesAvailable(bookId);
        return new ResponseEntity<>(num, HttpStatus.OK);
    }

    // localhost:8080/api/admin/search/by-title-keyword?title=Algorithms
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @GetMapping("/search/by-title-keyword")
    public ResponseEntity<?> findBooksByTitleContaining(@RequestParam("title") String title) throws ResourceNotFoundException {
        List<Book> books = bookService.findByTitleContaining(title);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    // localhost:8080/api/admin/search/by-author?name=Haruki
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @GetMapping("/search/by-author")
    public ResponseEntity<?> findBooksByAuthor(@RequestParam("name") String name) throws ResourceNotFoundException {
        List<Book> books = bookService.findByAuthor(name);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    // localhost:8080/api/admin/search/by-genre?genre=science+fiction
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @GetMapping("/search/by-genre")
    public ResponseEntity<?> findByGenre(@RequestParam("genre") String genre) throws ResourceNotFoundException {
        List<Book> books = bookService.findByGenre(genre);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

}

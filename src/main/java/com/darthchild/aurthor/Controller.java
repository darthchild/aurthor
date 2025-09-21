package com.darthchild.aurthor;

import com.darthchild.aurthor.models.BookDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class Controller {

    @Autowired
    private BookService service;

    @GetMapping("/all")
    public List<BookDTO> getAll(){
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getById(@PathVariable("id") Long bookId){
        return ResponseEntity.ok(service.getById(bookId));
    }

    @PostMapping
    public BookDTO addBook(@RequestBody BookDTO bookDTO){
        return service.addBook(bookDTO);
    }

    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable("id") Long id){
        service.deleteBook(id);
        return "Success";
    }

    @PutMapping("/{id}")
    public BookDTO updateBook(@PathVariable("id") Long id, @RequestBody BookDTO bookDTO){
        return service.updateBook(id,bookDTO);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("abc", "123");
        headers.add("cdf", "321");

        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(headers)
                .body("lol");
    }
}

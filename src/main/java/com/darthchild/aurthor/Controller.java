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
    public ResponseEntity<BookDTO> addBook(@RequestBody BookDTO bookDTO){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addBook(bookDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable("id") Long id){
        service.deleteBook(id);
        return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT)
                .body("Record deleted successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable("id") Long id, @RequestBody BookDTO bookDTO){
        return ResponseEntity.ok(service.updateBook(id,bookDTO));
    }

}

package com.darthchild.aurthor.books;

import com.darthchild.aurthor.exception.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {

    @Autowired
    BookRepository repository;
    @Autowired
    ModelMapper modelMapper;

    public BookDTO addBook(BookDTO bookDTO){
        Book savedBook = repository.save(modelMapper.map(bookDTO, Book.class));
        return modelMapper.map(savedBook, BookDTO.class);
    }

    public List<BookDTO> getAll(){
        List<BookDTO> list = new ArrayList<>();
        for(Book item : repository.findAll())
            list.add(modelMapper.map(item, BookDTO.class));
        return list;
    }

    public BookDTO getById(Long id){
        return modelMapper.map(
                repository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id)),
                BookDTO.class
        );
    }

    public void deleteBook(Long id){
        if(repository.existsById(id))
            repository.deleteById(id);
        else
            throw new ResourceNotFoundException("Book not found with id: " + id);
    }

    public BookDTO updateBook(Long id, BookDTO updatedBook) {

        Book existingBook = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setAuthor(updatedBook.getAuthor());
        existingBook.setPrice(updatedBook.getPrice());

        return modelMapper.map(repository.save(existingBook), BookDTO.class);
    }

}

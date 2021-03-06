package com.example.reactivelibrary.Router;

import com.example.reactivelibrary.DTO.BookDto;
import com.example.reactivelibrary.Enums.BookType;
import com.example.reactivelibrary.Mapper.BookMapper;
import com.example.reactivelibrary.Model.Book;
import com.example.reactivelibrary.Repository.LibraryRepository;
import com.example.reactivelibrary.UseCase.CreateBookUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.sql.Date;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@WebFluxTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CreateBookRouter.class, CreateBookUseCase.class, BookMapper.class})
class CreateBookRouterTest {

    @MockBean
    private LibraryRepository libraryRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void createBookTest(){

        Book book = new Book();
        book.setId("xxx");
        book.setName("name");
        book.setBookType(BookType.cuento);
        book.setAvailable(true);
        book.setLastBorrowed(Date.from(Instant.now()));

        BookDto bookDto = new BookDto(book.getId(),
                book.getName(),
                book.getBookType(),
                book.getAvailable(),
                book.getLastBorrowed());

        Mono<Book> bookMono = Mono.just(book);

        Mockito.when(libraryRepository.save(any())).thenReturn(bookMono);

        webTestClient.post()
                .uri("/libros/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(bookDto), BookDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .value(response ->{
                    Assertions.assertEquals(response.getId(), book.getId());
                    Assertions.assertEquals(response.getName(), book.getName());
                    Assertions.assertEquals(response.getBookType(), book.getBookType());
                    Assertions.assertEquals(response.getAvailable(), book.getAvailable());

                });
        Mockito.verify(libraryRepository, Mockito.times(1)).save(any());
    }

}
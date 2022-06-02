package site.metacoding.blogv4junit.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import net.bytebuddy.agent.VirtualMachine.ForHotSpot.Connection.Response;
import site.metacoding.blogv4junit.domain.book.Book;
import site.metacoding.blogv4junit.domain.book.BookRepository;
import site.metacoding.blogv4junit.web.dto.BookRespDto;
import site.metacoding.blogv4junit.web.dto.BookSaveReqDto;

/**
 * 1. 실제환경과 동일하게 테스트할 수 있다. (@SpringbootTest : 모든게 메모리에 다 올라감 = 통합 테스트)
 * 2. 내가 원하는 컨트롤러, 서비스, 레파지토리만 분리해서 메모리에 올리고 테스트할 수 있다.
 * (@SpringbootTest(class={BookApiController.class, BookService.class,
 * BookRepository.class}))
 * 3. Controller, ControllerAdvice, Filter, WebMvcConfigurer(web.xml) : 컨트롤러
 * 앞단 @WebMvcTest
 * 
 * @WebMvcTest를 사용하기 위해서는 stub이 무조건 필요함.
 * 
 *              컨트롤러는 통합 테스트 추천
 * 
 *              클라리언트는 컨트롤러와 소통
 *              클라이언트(브라우저, 핸드폰앱, 포스트맨 등)가 컨트롤러에게 요청한다.
 *              서비스나 레파지토리를 Mockito로 처리하면 불안하다.
 *              통합 테스트를 해주는게 좋다.
 */

// 통합테스트
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BookApiControllerTest {

    @Autowired
    private TestRestTemplate rt;

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void save_테스트() throws JsonProcessingException {
        // given
        BookSaveReqDto reqDto = new BookSaveReqDto();
        reqDto.setTitle("제목1");
        reqDto.setAuthor("메타코딩");

        String body = new ObjectMapper().writeValueAsString(reqDto);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        // when
        ResponseEntity<String> response = rt.exchange("/api/book", HttpMethod.POST, request, String.class);

        System.out.println(response.getBody());

        // then
        DocumentContext dc = JsonPath.parse(response.getBody());
        String author = dc.read("$.author");
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("메타코딩", author);
    }

    @Test
    public void bookFindOne_테스트() throws JsonProcessingException {

        // given
        Long id = 1L;
        bookRepository.save(new Book(1L, "제목1", "메타코딩"));

        // when
        ResponseEntity<String> response = rt.exchange("/api/book/" + id, HttpMethod.GET, null, String.class);

        System.out.println(response.getBody());

        // then
        DocumentContext dc = JsonPath.parse(response.getBody());
        String author = dc.read("$.author");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("메타코딩", author);

    }
}

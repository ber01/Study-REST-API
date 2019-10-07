package com.kyunghwan.demorestapi.events;

import com.kyunghwan.demorestapi.common.BaseControllerTest;
import com.kyunghwan.demorestapi.common.TestDescription;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EventControllerTests extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;

    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST-API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 9, 19, 13, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 9, 20, 13, 0))
                .beginEventDateTime(LocalDateTime.of(2019, 9, 21, 13, 0))
                .endEventDateTime(LocalDateTime.of(2019, 9, 22, 13, 0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("경성대학교 건학기념관")
                .build();

        mockMvc.perform(post("/api/events/")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update an existing"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.ACCEPT).description("accept header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("이벤트 이름"),
                                fieldWithPath("description").description("이벤트 내용"),
                                fieldWithPath("beginEnrollmentDateTime").description("이벤트 등록 시작 시간"),
                                fieldWithPath("closeEnrollmentDateTime").description("이벤트 등록 마감 시간"),
                                fieldWithPath("beginEventDateTime").description("이벤트 시작 시간"),
                                fieldWithPath("endEventDateTime").description("이벤트 마감 시간"),
                                fieldWithPath("location").description("이벤트 장소"),
                                fieldWithPath("basePrice").description("이벤트 참여 기본 요금"),
                                fieldWithPath("maxPrice").description("이벤트 참여 최대 요금"),
                                fieldWithPath("limitOfEnrollment").description("이벤트 최대 인원")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("이벤트 번호"),
                                fieldWithPath("name").description("이벤트 이름"),
                                fieldWithPath("description").description("이벤트 내용"),
                                fieldWithPath("beginEnrollmentDateTime").description("이벤트 등록 시작 시간"),
                                fieldWithPath("closeEnrollmentDateTime").description("이벤트 등록 마감 시간"),
                                fieldWithPath("beginEventDateTime").description("이벤트 시작 시간"),
                                fieldWithPath("endEventDateTime").description("이벤트 마감 시간"),
                                fieldWithPath("location").description("이벤트 장소"),
                                fieldWithPath("basePrice").description("이벤트 참여 기본 요금"),
                                fieldWithPath("maxPrice").description("이벤트 참여 최대 요금"),
                                fieldWithPath("limitOfEnrollment").description("이벤트 최대 인원"),
                                fieldWithPath("offline").description("오프라인 유/무"),
                                fieldWithPath("free").description("무료 유/무"),
                                fieldWithPath("eventStatus").description("이벤트 상태"),
                                fieldWithPath("_links.self.href").description("현재 이벤트"),
                                fieldWithPath("_links.query-events.href").description("이벤트 목록"),
                                fieldWithPath("_links.update-event.href").description("현재 이벤트 수정"),
                                fieldWithPath("_links.profile.href").description("이벤트 정보")
                        )
                ))
        ;
    }

    @Test
    @TestDescription("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("REST-API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 9, 19, 13, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 9, 20, 13, 0))
                .beginEventDateTime(LocalDateTime.of(2019, 9, 21, 13, 0))
                .endEventDateTime(LocalDateTime.of(2019, 9, 22, 13, 0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("경성대학교 건학기념관")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("입력 값이 잘못 된 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Wrong_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST-API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 9, 22, 13, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 9, 21, 13, 0))
                .beginEventDateTime(LocalDateTime.of(2019, 9, 20, 13, 0))
                .endEventDateTime(LocalDateTime.of(2019, 9, 19, 13, 0))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("경성대학교 건학기념관")
                .build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @TestDescription("30개의 이벤트를 10개씩 두 번째 페이지 조회하기")
    public void queryEvents() throws Exception {
        // Given, 이벤트 30개 생성
        IntStream.range(0, 30).forEach(this::generateEvent);

        // When & Then, 두 번째 페이지 조회
        this.mockMvc.perform(get("/api/events")
                    .param("page", "1")
                    .param("size", "10")
                    .param("sort", "id,DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"))
        ;
    }

    @Test
    @TestDescription("기존의 이벤트 하나 조회하기")
    public void getEvent() throws Exception {
        // Given, 이벤트 하나 생성
        Event event = this.generateEvent(100);

        // When & Then, 하나의 이벤트 조회
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("description").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-an-event"))
        ;
    }

    @Test
    @TestDescription("없는 이벤트를 조회하였을 때 404 응답")
    public void getEvent404() throws Exception {
        // When & Then, 하나의 이벤트 조회
        this.mockMvc.perform(get("/api/events/123123"))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @TestDescription("이벤트를 정상적으로 수정하는 경우")
    public void updateEvent() throws Exception {
        // Given
        Event event = generateEvent(123213);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);

        String name = "updateupdate";
        eventDto.setName(name);

        // When & Then
        mockMvc.perform(put("/api/events/{id}", event.getId())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(name))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("update-event"))
        ;
    }

    @Test
    @TestDescription("입력값이 비어있는 경우 수정 실패")
    public void updateEvent400_Empty() throws Exception {
        // Given
        Event event = generateEvent(123213);
        EventDto eventDto = EventDto.builder().build();

        // When & Then
        mockMvc.perform(put("/api/events/{id}", event.getId())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("입력값이 올바르지 않은 경우 수정 실패")
    public void updateEvent400_Wrong() throws Exception {
        // Given
        Event event = generateEvent(123213);
        event.setBasePrice(300000);
        event.setMaxPrice(100);
        EventDto eventDto = EventDto.builder().build();

        // When & Then
        mockMvc.perform(put("/api/events/{id}", event.getId())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("없는 이벤트를 수정하려고 할 때패")
    public void updateEvent404() throws Exception {
        // Given
        Event event = generateEvent(123213);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);

        // When & Then
        mockMvc.perform(put("/api/events/{id}", 123123213)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    private Event generateEvent(int index) {
        Event event = Event.builder()
                .name("event " + index)
                .description("test event")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 9, 19, 13, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 9, 20, 13, 0))
                .beginEventDateTime(LocalDateTime.of(2019, 9, 21, 13, 0))
                .endEventDateTime(LocalDateTime.of(2019, 9, 22, 13, 0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("경성대학교 건학기념관")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();
        return this.eventRepository.save(event);
    }

}

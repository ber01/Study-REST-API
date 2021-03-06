package com.kyunghwan.demorestapi.events;

import com.kyunghwan.demorestapi.accounts.Account;
import com.kyunghwan.demorestapi.accounts.AccountRepository;
import com.kyunghwan.demorestapi.accounts.AccountRole;
import com.kyunghwan.demorestapi.accounts.AccountService;
import com.kyunghwan.demorestapi.common.AppProperties;
import com.kyunghwan.demorestapi.common.BaseControllerTest;
import com.kyunghwan.demorestapi.common.TestDescription;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EventControllerTests extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AppProperties appProperties;

    @Before
    public void setUp() {
        this.eventRepository.deleteAll();
        this.accountRepository.deleteAll();
    }

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
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
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
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 토큰"),
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
                                fieldWithPath("manager.id").description("이벤트 생성자의 번호"),
                                fieldWithPath("_links.self.href").description("현재 이벤트"),
                                fieldWithPath("_links.query-events.href").description("이벤트 목록"),
                                fieldWithPath("_links.update-event.href").description("현재 이벤트 수정"),
                                fieldWithPath("_links.profile.href").description("이벤트 생성 프로필")
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
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
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
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
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
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
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
    @TestDescription("이벤트 조회")
    public void queryEventsTest() throws Exception {
        // Given, 이벤트 30개 생성
        IntStream.range(0, 30).forEach(this::generateEvent);

        // When & Then, 두 번째 페이지 조회
        this.mockMvc.perform(get("/api/events"))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    @TestDescription("30개의 이벤트를 5개씩 세 번째 페이지 조회하기")
    public void queryEvents() throws Exception {
        Account account = this.createAccount();

        // Given, 이벤트 30개 생성
        IntStream.range(0, 30).forEach(index -> generateEvent(index, account));

        // When & Then, 두 번째 페이지 조회
        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/api/events")
                        .param("page", "2")
                        .param("size", "5")
                        .param("sort", "id,DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events",
                        requestParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("조회 할 이벤트 수"),
                                parameterWithName("sort").description("정렬 방법")
                        ),
                        responseHeaders(
                                headerWithName("Content-Type").description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.eventList[*].id").description("번호"),
                                fieldWithPath("_embedded.eventList[*].name").description("이름"),
                                fieldWithPath("_embedded.eventList[*].description").description("설명"),
                                fieldWithPath("_embedded.eventList[*].beginEnrollmentDateTime").description("등록 시작 시간"),
                                fieldWithPath("_embedded.eventList[*].closeEnrollmentDateTime").description("등록 종료 시간"),
                                fieldWithPath("_embedded.eventList[*].beginEventDateTime").description("시작 시간"),
                                fieldWithPath("_embedded.eventList[*].endEventDateTime").description("종료 시간"),
                                fieldWithPath("_embedded.eventList[*].location").description("장소"),
                                fieldWithPath("_embedded.eventList[*].basePrice").description("최소 금액"),
                                fieldWithPath("_embedded.eventList[*].maxPrice").description("최대 금액"),
                                fieldWithPath("_embedded.eventList[*].limitOfEnrollment").description("최대 인원"),
                                fieldWithPath("_embedded.eventList[*].offline").description("오프라인 유/무"),
                                fieldWithPath("_embedded.eventList[*].free").description("무료 유/무"),
                                fieldWithPath("_embedded.eventList[*].eventStatus").description("형태"),
                                fieldWithPath("_embedded.eventList[*].manager.id").description("작성자 번호"),
                                fieldWithPath("_embedded.eventList[*]._links.self.href").description("현재 이벤트 링크"),
                                fieldWithPath("_links.first.href").description("첫 페이지"),
                                fieldWithPath("_links.prev.href").description("이 전 페이지"),
                                fieldWithPath("_links.self.href").description("현재 페이지"),
                                fieldWithPath("_links.next.href").description("다음 페이지"),
                                fieldWithPath("_links.last.href").description("마지막 페이지"),
                                fieldWithPath("_links.profile.href").description("이벤트 목록 프로필"),
                                fieldWithPath("page.size").description("페이지 크기"),
                                fieldWithPath("page.totalElements").description("총 개수"),
                                fieldWithPath("page.totalPages").description("페이지 개수"),
                                fieldWithPath("page.number").description("페이지 번호")
                        ),
                        links(
                                linkWithRel("first").description("첫 페이지"),
                                linkWithRel("prev").description("이 전 페이지"),
                                linkWithRel("self").description("현재 페이지"),
                                linkWithRel("next").description("다음 페이지"),
                                linkWithRel("last").description("마지막 페이지"),
                                linkWithRel("profile").description("이벤트 목록 조회 프로필")
                        )
                ))
        ;


    }

    @Test
    @TestDescription("인증 정보를 가지고 30개의 이벤트를 5개씩 두 번째 페이지 조회하기")
    public void queryEventsWithAuthentication() throws Exception {
        Account account = Account.builder()
                .email("test@teset.com")
                .password("test123")
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();

        Account testAccount = accountRepository.save(account);

        // Given, 이벤트 30개 생성
        IntStream.range(0, 30).forEach(index -> generateEvent(index, testAccount));

        // When & Then, 두 번째 페이지 조회
        this.mockMvc.perform(get("/api/events")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "id,DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.create-event").exists())
                .andDo(document("query-events-auth",
                        requestParameters(
                            parameterWithName("page").description("페이지 번호"),
                            parameterWithName("size").description("조회 할 이벤트 수"),
                            parameterWithName("sort").description("정렬 방법")
                        ),
                        requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description("인증 토큰")
                        ),
                        responseHeaders(
                                headerWithName("Content-Type").description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.eventList[*].id").description("번호"),
                                fieldWithPath("_embedded.eventList[*].name").description("이름"),
                                fieldWithPath("_embedded.eventList[*].description").description("설명"),
                                fieldWithPath("_embedded.eventList[*].beginEnrollmentDateTime").description("등록 시작 시간"),
                                fieldWithPath("_embedded.eventList[*].closeEnrollmentDateTime").description("등록 종료 시간"),
                                fieldWithPath("_embedded.eventList[*].beginEventDateTime").description("시작 시간"),
                                fieldWithPath("_embedded.eventList[*].endEventDateTime").description("종료 시간"),
                                fieldWithPath("_embedded.eventList[*].location").description("장소"),
                                fieldWithPath("_embedded.eventList[*].basePrice").description("최소 금액"),
                                fieldWithPath("_embedded.eventList[*].maxPrice").description("최대 금액"),
                                fieldWithPath("_embedded.eventList[*].limitOfEnrollment").description("최대 인원"),
                                fieldWithPath("_embedded.eventList[*].offline").description("오프라인 유/무"),
                                fieldWithPath("_embedded.eventList[*].free").description("무료 유/무"),
                                fieldWithPath("_embedded.eventList[*].eventStatus").description("형태"),
                                fieldWithPath("_embedded.eventList[*].manager.id").description("작성자 번호"),
                                fieldWithPath("_embedded.eventList[*]._links.self.href").description("현재 이벤트 링크"),
                                fieldWithPath("_links.first.href").description("첫 페이지"),
                                fieldWithPath("_links.prev.href").description("이 전 페이지"),
                                fieldWithPath("_links.self.href").description("현재 페이지"),
                                fieldWithPath("_links.next.href").description("다음 페이지"),
                                fieldWithPath("_links.last.href").description("마지막 페이지"),
                                fieldWithPath("_links.profile.href").description("이벤트 목록 프로필"),
                                fieldWithPath("_links.create-event.href").description("이벤트 목록 프로필"),
                                fieldWithPath("page.size").description("페이지 크기"),
                                fieldWithPath("page.totalElements").description("총 개수"),
                                fieldWithPath("page.totalPages").description("페이지 개수"),
                                fieldWithPath("page.number").description("페이지 번호")
                        ),
                        links(
                                linkWithRel("first").description("첫 페이지"),
                                linkWithRel("prev").description("이 전 페이지"),
                                linkWithRel("self").description("현재 페이지"),
                                linkWithRel("next").description("다음 페이지"),
                                linkWithRel("last").description("마지막 페이지"),
                                linkWithRel("profile").description("이벤트 목록 조회 프로필"),
                                linkWithRel("create-event").description("이벤트 생성")
                        )
                ))
        ;
    }

    @Test
    @TestDescription("기존의 이벤트 하나 조회하기")
    public void getEvent() throws Exception {
        // Given, 이벤트 하나 생성
        Account account = this.createAccount();
        Event event = this.generateEvent(100, account);

        // When & Then, 하나의 이벤트 조회
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("description").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-an-event",
                        responseHeaders(
                                headerWithName("Content-Type").description("content type header")
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
                                fieldWithPath("manager.id").description("이벤트 생성자의 번호"),
                                fieldWithPath("_links.self.href").description("현재 이벤트"),
                                fieldWithPath("_links.profile.href").description("이벤트 조회 프로필"),
                                fieldWithPath("_links.query-events.href").description("이벤트 목록")
                        ),
                        links(
                                linkWithRel("self").description("현재 페이지"),
                                linkWithRel("profile").description("이벤트 조회 프로필"),
                                linkWithRel("query-events").description("이벤트 목록")
                        )
                ))
        ;
    }

    @Test
    @TestDescription("이벤트 생성자가 기존의 이벤트 하나 조회하기")
    public void getEventWithEqualsUser() throws Exception {
        // Given, 이벤트 하나 생성
        Account account = this.createAccount();
        Event event = this.generateEvent(100, account);

        // When & Then, 하나의 이벤트 조회
        this.mockMvc.perform(get("/api/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken(false)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("description").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document("get-an-event-equals-user",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 토큰")
                        ),
                        responseHeaders(
                                headerWithName("Content-Type").description("content type header")
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
                                fieldWithPath("manager.id").description("이벤트 생성자의 번호"),
                                fieldWithPath("_links.self.href").description("현재 이벤트"),
                                fieldWithPath("_links.update-event.href").description("이벤트 수정"),
                                fieldWithPath("_links.profile.href").description("이벤트 조회 프로필"),
                                fieldWithPath("_links.query-events.href").description("이벤트 목록")
                        ),
                        links(
                                linkWithRel("self").description("현재 페이지"),
                                linkWithRel("update-event").description("이벤트 수정"),
                                linkWithRel("profile").description("이벤트 조회 프로필"),
                                linkWithRel("query-events").description("이벤트 목록")
                        )
                ))
        ;
    }

    @Test
    @TestDescription("인증된 유저가 기존의 이벤트 하나 조회하기")
    public void getEventWithAuthentication() throws Exception {
        // Given, 이벤트 하나 생성
        Account account = accountRepository.save(
                Account.builder()
                        .email("test_test@email.com")
                        .password("testPassword")
                        .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build());

        Event event = this.generateEvent(100, account);

        // When & Then, 하나의 이벤트 조회
        this.mockMvc.perform(get("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("description").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-an-event-authentication",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 토큰")
                        ),
                        responseHeaders(
                                headerWithName("Content-Type").description("content type header")
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
                                fieldWithPath("manager.id").description("이벤트 생성자의 번호"),
                                fieldWithPath("_links.self.href").description("현재 이벤트"),
                                fieldWithPath("_links.profile.href").description("이벤트 조회 프로필"),
                                fieldWithPath("_links.query-events.href").description("이벤트 목록")
                        ),
                        links(
                                linkWithRel("self").description("현재 페이지"),
                                linkWithRel("profile").description("이벤트 조회 프로필"),
                                linkWithRel("query-events").description("이벤트 목록")
                        )
                ))
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
        Account account = this.createAccount();
        Event event = generateEvent(200, account);

        EventDto eventDto = this.modelMapper.map(event, EventDto.class);

        String name = "updateupdate";
        eventDto.setName(name);

        // When & Then
        mockMvc.perform(put("/api/events/{id}", event.getId())
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken(false))
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(name))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("update-event",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 토큰"),
                                headerWithName("Content-Type").description("content type header")
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
                                headerWithName("Content-Type").description("content type header")
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
                                fieldWithPath("manager.id").description("이벤트 생성자의 번호"),
                                fieldWithPath("_links.self.href").description("현재 이벤트"),
                                fieldWithPath("_links.profile.href").description("이벤트 수정 프로필"),
                                fieldWithPath("_links.query-events.href").description("이벤트 목록")
                        ),
                        links(
                                linkWithRel("self").description("현재 페이지"),
                                linkWithRel("profile").description("이벤트 조회 프로필"),
                                linkWithRel("query-events").description("이벤트 목록")
                        )
                ))
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
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
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
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
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
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    private Event generateEvent(int index, Account account) {
        Event event = buildEvent(index);
        event.setManager(account);
        return this.eventRepository.save(event);
    }

    private Event generateEvent(int index) {
        Event event = buildEvent(index);
        return this.eventRepository.save(event);
    }

    private Event buildEvent(int index) {
        return Event.builder()
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
    }

    private String getBearerToken(boolean needToCreateAccount) throws Exception {
        return "Bearer " + getAccessToken(needToCreateAccount);
    }

    private String getAccessToken(boolean needToCreateAccount) throws Exception {
        // Given
        if (needToCreateAccount) {
            createAccount();
        }

        ResultActions perform = this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
                .param("username", appProperties.getUserUsername())
                .param("password", appProperties.getUserPassword())
                .param("grant_type", "password"));

        String contentAsString = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();
        return parser.parseMap(contentAsString).get("access_token").toString();
    }

    private Account createAccount() {
        Account account = Account.builder()
                .email(appProperties.getUserUsername())
                .password(appProperties.getUserPassword())
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        return this.accountService.saveAccount(account);
    }

}

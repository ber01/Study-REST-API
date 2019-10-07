package com.kyunghwan.demorestapi.events;

import com.kyunghwan.demorestapi.accounts.Account;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
@Entity
public class Event {

    @Id @GeneratedValue
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime; // 시작 일시
    private LocalDateTime closeEnrollmentDateTime; // 종료 일시
    private LocalDateTime beginEventDateTime; // 이벤트 시작 일시
    private LocalDateTime endEventDateTime; // 이벤트 종료 일시
    private String location; // (optional) 없으면 온라인 모임
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;
    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus = EventStatus.DRAFT;
    @ManyToOne
    private Account manager;

    public void update() {
        // update Free
        this.free = this.basePrice == 0 && this.maxPrice == 0;
        // update offline
        this.offline = !(this.location == null || this.location.trim().equals(""));
    }
}

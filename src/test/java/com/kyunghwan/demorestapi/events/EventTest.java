package com.kyunghwan.demorestapi.events;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class EventTest {

    @Test
    public void builder() {
        Event event = Event.builder()
                .name("Study Spring REST API")
                .description("REST API development with Spring")
                .build();
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean(){
        // Given
        String name = "Event";
        String description = "Spring boot";

        // When
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        // Then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }

    @Test
    public void testFree() {
        // Given, 무료 모임
        Event event = Event.builder()
                .basePrice(0)
                .maxPrice(0)
                .build();
        // When
        event.update();
        // Then
        assertThat(event.isFree()).isTrue();

        // Given, 유료 모임 base price > 0
        event = Event.builder()
                .basePrice(100)
                .maxPrice(0)
                .build();
        // When
        event.update();
        // Then
        assertThat(event.isFree()).isFalse();

        // Given, 유료 모임 max price > 0
        event = Event.builder()
                .basePrice(0)
                .maxPrice(100)
                .build();
        // When
        event.update();
        // Then
        assertThat(event.isFree()).isFalse();
    }

    @Test
    public void testOffline() {
        // Given, 장소 O, 오프라인 모임
        Event event = Event.builder()
                .location("KSU 공대 C동")
                .build();
        // When
        event.update();
        // Then
        assertThat(event.isOffline()).isTrue();

        // Given, 장소 X, 온라인 모임
        event = Event.builder()
                .build();
        // When
        event.update();
        // Then
        assertThat(event.isOffline()).isFalse();

    }
}
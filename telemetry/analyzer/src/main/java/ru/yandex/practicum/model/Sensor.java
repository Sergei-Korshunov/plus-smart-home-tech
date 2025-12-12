package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sensors", schema = "public")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Sensor {

    @Id
    private String id;

    @Column(name = "hub_id")
    private String hubId;
}

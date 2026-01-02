package ru.yandex.practicum.dto.store;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageableDTO {

    @Min(0)
    private Integer page;

    @Min(1)
    private Integer size;

    private List<String> sort;
}
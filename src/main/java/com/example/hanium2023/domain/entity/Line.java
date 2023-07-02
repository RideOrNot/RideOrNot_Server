package com.example.hanium2023.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Line {
    @Id
    private Integer lineId;
    private String lineName;
    @OneToMany(mappedBy = "line", cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<Station> stations  = new ArrayList<>();
}

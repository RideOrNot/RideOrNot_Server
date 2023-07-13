package com.example.hanium2023.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StationExitID implements Serializable {
    private String exitName;
    private Station station;
}

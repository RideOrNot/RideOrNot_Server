package com.example.hanium2023.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StationExitPK implements Serializable {
    private Integer station;
    private String exitName;
}

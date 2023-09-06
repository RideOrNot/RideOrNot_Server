package com.example.hanium2023.domain.dto.publicapi.location;

import com.example.hanium2023.enums.TrainStatusCodeEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationInfoResponse {
    //    (0 : 상행/내선, 1 : 하행/외선)
    private Integer direction;

    private String lineName;

    private Integer lineId;

    private String createdAt;

    private String stationName;

    private Integer stationId;

    private String lastStationName;

    private Integer lastStationId;

    private String trainStatus;

    private Integer isExpress;

    private Integer isLastTrain;

    public LocationInfoResponse(LocationInfoApiResult locationInfoApiResult) {
        this.direction = locationInfoApiResult.getDirectionCode();
        this.lineName = locationInfoApiResult.getLineName();
        this.lineId = locationInfoApiResult.getLineId();
        this.createdAt = locationInfoApiResult.getCreatedAt();
        this.stationName = locationInfoApiResult.getStationName();
        this.stationId = locationInfoApiResult.getStationId();
        this.lastStationName = locationInfoApiResult.getStationName();
        this.lastStationId = locationInfoApiResult.getStationId();
        this.trainStatus = TrainStatusCodeEnum.getEnumByCode(locationInfoApiResult.getTrainStatusCode()).getStatus();
        this.isExpress = locationInfoApiResult.getIsExpress();
        this.isLastTrain = locationInfoApiResult.getIsLastTrain();
    }
}

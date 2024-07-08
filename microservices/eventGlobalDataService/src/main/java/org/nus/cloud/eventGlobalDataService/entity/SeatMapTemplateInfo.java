package org.nus.cloud.eventGlobalDataService.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatMapTemplateInfo {

    private String label;

    private String value;

    private ArrayList<SeatMapTemplateInfo> children = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SeatMapTemplateInfo that = (SeatMapTemplateInfo) o;
        return Objects.equals(label, that.label);
    }
}

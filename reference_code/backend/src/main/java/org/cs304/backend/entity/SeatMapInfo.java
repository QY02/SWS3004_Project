package org.cs304.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatMapInfo {

    private String label;

    private String value;

    private ArrayList<SeatMapInfo> children = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SeatMapInfo that = (SeatMapInfo) o;
        return Objects.equals(label, that.label);
    }
}

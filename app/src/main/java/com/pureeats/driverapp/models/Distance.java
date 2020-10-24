package com.pureeats.driverapp.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class Distance {
    private String text;
    private long value;

    public Distance(String text, long value) {
        this.text = text;
        this.value = value;
    }
}

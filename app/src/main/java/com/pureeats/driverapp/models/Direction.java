package com.pureeats.driverapp.models;

import com.pureeats.driverapp.commons.Constants;
import com.pureeats.driverapp.utils.TimeUtils;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class Direction {
    private  Distance distance;
    private  Duration duration;
    private String flag;

    public Direction(Distance distance, Duration duration) {
        this.distance = distance;
        this.duration = duration;
    }

    public String getDeliveryDuration(){
        long actualTimeInSecond = this.duration.getValue();
        long holdingTimeInSecond = (long)(Constants.DELIVERY_TIME_HOLD_IN_MINUTE) * 60;
        long modifiedTimeInSecond = holdingTimeInSecond + actualTimeInSecond;

        long modifiedTimeInMilliSecond = modifiedTimeInSecond * 1000;
        String text = TimeUtils.getRoundUpRemainingTime(modifiedTimeInMilliSecond);

        return text;
    }


    public long getDeliveryDurationVal(){
        long actualTimeInSecond = this.duration.getValue();
        long holdingTimeInSecond = (long)(Constants.DELIVERY_TIME_HOLD_IN_MINUTE) * 60;
        long modifiedTimeInSecond = holdingTimeInSecond + actualTimeInSecond;
        return modifiedTimeInSecond;
    }
}

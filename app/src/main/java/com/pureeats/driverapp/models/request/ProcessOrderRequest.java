package com.pureeats.driverapp.models.request;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcessOrderRequest extends RequestToken {
    @SerializedName("order_id")
    private int orderId;
    @SerializedName("bill_photo")
    private String billPhoto;

    @SerializedName("unique_order_id")
    private String uniqueOrderId;

    public ProcessOrderRequest(int orderId) {
        super();
        this.orderId = orderId;
    }

    public ProcessOrderRequest(RequestToken requestToken, int orderId) {
        super(requestToken.getUserId(), requestToken.getToken());
        this.orderId = orderId;
    }

    public ProcessOrderRequest(String uniqueOrderId) {
        super();
        this.uniqueOrderId = uniqueOrderId;
    }
}

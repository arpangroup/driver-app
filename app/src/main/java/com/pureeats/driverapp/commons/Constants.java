package com.pureeats.driverapp.commons;

import com.google.android.gms.maps.model.LatLng;
import com.pureeats.driverapp.views.order.AcceptOrderFragment;
import com.pureeats.driverapp.views.order.DeliverOrderFragment;
import com.pureeats.driverapp.views.order.PickOrderFragment;
import com.pureeats.driverapp.views.order.ReachDirectionFragment;
import com.pureeats.driverapp.views.order.TripDetailsFragment;

public class Constants {
    public static final String GOOGLE_MAP_AUTH_KEY = "AIzaSyCt_14My2CYghVw6eZFSYFlFPBOK29lkww";
    public static final int GPS_REQUEST_CODE = 100;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    public static final int PLAY_SERVICE_ERROR_CODE = 102;
    public static final int REQUEST_GOOGLE_PLACE_AUTOCOMPLETE_SEARCH_ACTIVITY = 103;
    public static final int REQUEST_CALL_ACTIVITY = 104;
    public static final int REQUEST_IMAGE = 105;
    public static final int REQUEST_IMAGE_CAPTURE = 106;
    public static final int REQUEST_IMAGE_EXTERNAL = 107;
    public static final int REQUEST_LOCATION_PERMISSION = 108;
    public static final int REQUEST_NO_INTERNET_ACTIVITY = 200;

    public static final int PER_KM_DELIVERY_TIME_IN_MINUTE = 6;
    public static final int DELIVERY_TIME_HOLD_IN_MINUTE = 30;
    public static final long ONE_SECOND = 1000;
    public static int BILL_PHOTO_REQUIRED = 1;

    private static LatLng DEFAULT_LOCATION_KOLKATA = new LatLng(22.5714427, 88.3428709);
    private static LatLng DEFAULT_LOCATION_WESTBENGAL = new LatLng(22.9868, 87.8550);
    public static LatLng DEFAULT_LOCATION = DEFAULT_LOCATION_WESTBENGAL;
    public static final String WEBSITE_URL = "https://admin.pureeatstest.xyz/";
//    public static final String WEBSITE_URL = "http://192.168.0.100:8000/";
//    public static final String WEBSITE_URL = "https://admin.pureeats.in/";
    public static final String DELIVERY_IMAGE_URL = WEBSITE_URL + "assets/img/delivery/";

//    public static final int ORDER_ACCEPT_WAITING_TIME = 1000*60*1;//1-minute
    public static final int ORDER_ACCEPT_WAITING_TIME = 1000*30;//30-sec
    public static final int ORDER_READY_WAITING_TIME = 1000*60*15;//15-min


    public static final String STR_TITLE = "title";
    public static final String STR_MESSAGE = "message";
    public static final String STR_ORDER_ID = "order_id";
    public static final String STR_UNIQUE_ORDER_ID = "unique_order_id";
    public static final String STR_BADGE = "badge";
    public static final String STR_ICON = "icon";
    public static final String STR_CLICK_ACTION = "click_action";
    public static final String STR_NOTIFICATION_TYPE = "notification_type";
    public static final String STR_ORDER_STATUS_ID = "order_status_id";
    public static final String STR_USER_ID = "user_id";

    public static final String STR_ORDER_JSON = "order_json";
    public static final String STR_IS_ORDER_PICKED= "is_order_picked";


    public static final String ORDER_ARRIVE_FRAGMENT = AcceptOrderFragment.class.getName();
    public static final String REACH_DIRECTION_FRAGMENT = ReachDirectionFragment.class.getName();
    public static final String PICK_ORDER_FRAGMENT = PickOrderFragment.class.getName();
    public static final String DELIVER_ORDER_FRAGMENT = DeliverOrderFragment.class.getName();
    public static final String TRIP_DETAILS_FRAGMENT = TripDetailsFragment.class.getName();



}

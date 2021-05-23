package com.example.gardener;

public class Const {
    public static final int SLEEP_TIME = 5000;
    public static final String DATABASE_NAME = "gardener.db";
    //name of tables
    public static final String TABLE_PLANT_LIST = "PLANT_LIST";
    public static final String TABLE_PLANT_ITEMS = "PLANT_ITEMS";
    public static final String TABLE_EVENT_LIST = "EVENT_LIST";
    public static final String TABLE_OWN_GARDEN = "OWN_GARDEN";
    public static final String TABLE_OWN_IMAGES = "OWN_IMAGES";
    public static final String TABLE_FAVOURITE_PLANTS = "FAVOURITE_PLANTS";
    public static final String TABLE_ADVICES = "ADVICES";

    //Table contents

    //#1
    public static final String LIST_ID = "_id";
    public static final String LIST_NAME = "name";
    public static final String LIST_IMAGE = "image";
    public static final String LIST_TYPE = "type";

    //#2

    public static final String ITEMS_ID = "_id";
    public static final String ITEMS_DESC = "desc";
    public static final String ITEMS_COND = "cond";
    public static final String ITEMS_CARE = "care";

    //#3

    public static final String EVENT_ID = "_id";
    public static final String EVENT_NAME = "name";
    public static final String EVENT_DESC = "desc";
    public static final String EVENT_IS_CHECKED = "is_checked";
    public static final String EVENT_DATE = "date";
    public static final String EVENT_TIME = "time";

    //#4

    public static final String OWN_GARDEN_ID = "id";
    public static final String OWN_GARDEN_NAME = "name";
    public static final String OWN_GARDEN_DESC = "desc";
    public static final String OWN_GARDEN_DATE = "date";

    //#5

    public static final String OWN_IMAGES_ID = "_id";
    public static final String OWN_IMAGES_PATH = "img_path";

    //#6

    public static final String FAVOURITE_PLANT_ID = "id";

    //#7

    public static final String ADVICE_ID = "id";
    public static final String ADVICE_TEXT = "advice_text";

    //Settings

    public static final String APP_SETTINGS_NAME = "settings";

    public static final String APP_LANGUAGE = "language";
    public static final String APP_THEME = "theme";
    public static final String APP_TEXT_SIZE = "text_size";
    public static final String APP_USER_FULL_NAME = "full_name";
    public static final String APP_CLICK = "click";
    public static final String APP_color = "color";
    public static final String APP_try = "try";
    public static final String APP_paid = "paid";

   // public static final String CHANEL_ID = "notify";

}

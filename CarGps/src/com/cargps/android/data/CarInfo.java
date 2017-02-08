package com.cargps.android.data;

public class CarInfo {
    // {
    // "_id": ObjectId("52b57c916c915fc8474438da"),
    // "check_record": 1135,
    // "ct_engine": -1,
    // "ct_gspeed": 0,
    // "ct_imsi": NumberLong("460020727499673"),
    // "ct_isrun": 0,
    // "ct_lat": 31.21114158630371, （车辆运行 位置）
    // "ct_lng": 121.5952911376953,
    // "ct_lock": 0, （1 布防 0撤防）
    // "ct_lockset": 1,
    // "ct_maxspeed": 0,
    // "ct_maxspeed_t": 0,
    // "ct_power": 1, (0 断电 1 上电)
    // "ct_speed": 4.699999809265137,
    // "ct_standby": -1,
    // "ct_star": 0,
    // "ct_switch": 8, 7 （打开电门锁，） 8 关闭电门锁
    // "ct_time": 1387694729, 与当前时间差大于5分钟 认为是休眠
    // "ct_chrg": （充电状态）
    // "ct_error": （故障检测）
    // "ct_bvol": (电池电压)
    // "ct_bvol": (电池电压) 42v ~ 56v
    // "ct_bvol": (电池电压)
    // "ct_version": 3.599999904632568,
    // "i": NumberLong("861300003000062"),
    // "ip_src": "117.136.8.99:13589",
    // "k": 8,
    // "lock_lat": 31.26635360717773,
    // "lock_lng": 121.584098815918,
    // "lock_state": 0,
    // "lock_time": 0,
    // "meters_month": NumberLong(0),
    // "meters_today": NumberLong(0),
    // "meters_total": NumberLong(38485),
    // "park_lat": 31.21109008789062, （停车后位置）
    // "park_lng": 121.5951766967773,
    // "park_time": 1387694719,
    // "set_autolock": 1,
    // "set_autounlock": 1,
    // "set_calllimit": 3,
    // "set_hwsensity": 7, （报警灵明度） 1～8
    // "set_hwsentimer": 15, （报警时间） 小于20 10秒 20 ～ 40 30秒 大于40 40秒
    // "set_imsi": NumberLong("460020727499673"),
    // "set_sensity": 20,
    // "speed_max": 76,
    // "speed_max_day": NumberLong(0),
    // "speed_max_day_t": NumberLong(0),
    // "speed_max_mon": NumberLong(0),
    // "speed_max_mon_t": NumberLong(0),
    // "speed_max_t": NumberLong(1387671082),
    // "stat_call": 0,
    // "time_update": 1387767609,
    // "val_lat": 31.21114158630371,
    // "val_lng": 121.5952911376953,
    // "val_time": 1387694729
    // }

    //public String _id;
//	public String check_record;
//	public String ct_engine;
//	public String ct_gspeed;
//	public String ct_imsi;
//	public String ct_isrun;
//	public double ct_lat;
//	public double ct_lng;
//	public String ct_lock;
//	public String ct_lockset;
//	public String ct_maxspeed;
//	public String ct_maxspeed_t;
//	public String ct_power;
//	public String ct_speed;
//	public String ct_standby;
//	public String ct_star;
//	public String ct_switch;
//	public long ct_time;
//	public int ct_chrg;
//	public int ct_error;
//	public String ct_bvol;
//	public double lock_lat;
//	public double lock_lng;
//	public long lock_time;
//	public double val_lat;
//	public double val_lng;
//	public long val_time;
//	public int set_hwsensity;
//	public float ct_pvol;

//	"imei" : "860720020011820",
//    "grade" : 6,
//    "battery" : 100,
//    "longitude" : 121.35368347167969,
//    "latitude" : 31.224380493164062,
//    "fired" : false,
//    "locked" : false,
//    "charging" : true,
//    "sleep" : true

    public String imei;
    public int grade;
    public int battery;
    public double longitude;
    public double latitude;
    public boolean fired;
    public boolean locked;
    public boolean charging;
    public boolean sleep;


}

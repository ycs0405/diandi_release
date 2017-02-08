package com.cargps.android;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.cargps.android.data.LoginUserInfo;
import com.cargps.android.data.UserInfo;
import com.cargps.android.interfaces.ICallBack;
import com.cargps.android.net.HttpExecute;
import com.cargps.android.net.HttpRequest;
import com.cargps.android.net.HttpResponseListener;
import com.cargps.android.net.responseBean.ChrgResponse;
import com.cargps.android.net.responseBean.UserInfoResponse;
import com.cargps.android.utils.CrashHandler;
import com.cargps.android.utils.SPUtils;
import com.cargps.android.utils.SdUtil;
import com.fu.baseframe.FrameApplication;
import com.fu.baseframe.net.NoHttpRequest;
import com.fu.baseframe.utils.LogUtils;
import com.fu.baseframe.utils.SystemOpt;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.tencent.bugly.crashreport.CrashReport;
import com.widget.android.utils.SettingShareData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/***
 * 扩展application
 *
 * @author fu
 */
@SuppressLint("NewApi")
public class MyApplication extends FrameApplication {

    public SystemOpt systemOpt = SystemOpt.getInstance();

    public static MainActivity mainActivity;
    public static String INPUT_IMEI = "";

    public LoginUserInfo rootUserInfo;

    public UserInfo userInfo;

    public static boolean isShowLoginPage = false;

    Handler handler = new Handler();

    public static boolean isActivity = false;

    private static MyApplication application;

    public static MyApplication getInstance() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // native log collection
        CrashHandler.getInstance().init(this);
        //bugly collection
        initBugly();
        INPUT_IMEI = (String) SPUtils.get(this, "CAR_IMEI", "");
        Log.w("MyApplication ", "INPUT_IMEI ==" + INPUT_IMEI);
        application = this;

        JPushInterface.setDebugMode(true);
        JPushInterface.init(getApplicationContext());

        initSystem();
        initImageLoader(this);

        rootUserInfo = getLocalUserinfo();
        if (rootUserInfo != null) {
            setPushAlias(rootUserInfo.mobileNo);
        }

        setNetUrl("http://demo.motorgps.net/api/gms_rest.php?");

        NoHttpRequest.dialogLayout = R.layout.dialog_loading_layout;

        getUserInfoData();

        copyloginToSd();

    }

    private void initBugly() {
        Context context = getApplicationContext();
        // 获取当前包名
        String packageName = context.getPackageName();
        // 获取当前进程名
        String processName = getProcessName(android.os.Process.myPid());
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        // 初始化Bugly
        CrashReport.initCrashReport(context, "e06e1adcda", true, strategy);
    }

    private String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    public void setSystemBar(Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(activity, true);
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            //tintManager.setStatusBarTintColor(activity.getResources().getColor(R.color.bg));//通知栏颜色
            //tintManager.setStatusBarAlpha(0f);
            tintManager.statusTextColor(activity, true);
        }
    }


    @TargetApi(19)
    public void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);

//        if(VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
//            Window window = activity.getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                    );
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                      
//                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//           // window.setNavigationBarColor(Color.TRANSPARENT);
//        }
    }


    public void init() {
        systemOpt.init(this);//读取系统信息

//		NoHttpRequest.getInstance().init(this);

        widthPixels = systemOpt.widthPixels;
        heightPixels = systemOpt.heightPixels;

        initImageLoader(this);

    }

    /***
     * 密码规则
     *
     * @param password
     * @return
     */
    public boolean isValidPassword(String password) {
        if (password.isEmpty()) return false;
        String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public boolean isTelPhone(String phone) {
        if (phone.isEmpty()) return false;
        String regex = "^(1(([357][0-9])|(47)|[8][012356789]))\\d{8}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    public boolean isEmail(String email) {
        if (email.isEmpty()) return false;
        String regex = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public LoginUserInfo getLocalUserinfo() {
        String str = SettingShareData.getInstance(this).getKeyValueString("login_userInfo", "");
        if (str.isEmpty()) return null;
        LoginUserInfo info = new Gson().fromJson(str, LoginUserInfo.class);
        rootUserInfo = info;
        if (rootUserInfo != null) {
            return rootUserInfo;
        }
        return null;
    }

    public UserInfo getUserInfo() {
        String str = SettingShareData.getInstance(this).getKeyValueString("userInfo", "");
        if (str.isEmpty()) return null;
        UserInfo info = new Gson().fromJson(str, UserInfo.class);
        userInfo = info;
        if (userInfo != null) {
            return userInfo;
        }
        return null;
    }

    public void setUserInfo(UserInfo userInfo) {
        if (userInfo == null) return;
        this.userInfo = userInfo;
        SettingShareData.getInstance(this).setKeyValue("userInfo", new Gson().toJson(userInfo));
    }

    public void setLocalUserinfo(LoginUserInfo loginUserInfo) {
        if (loginUserInfo != null) {
            SettingShareData.getInstance(this).setKeyValue("login_userInfo", new Gson().toJson(loginUserInfo));
            this.rootUserInfo = loginUserInfo;
        }

    }


    public void loginOut(final ICallBack back) {

        HttpRequest<ChrgResponse> httpRequest = new HttpRequest<ChrgResponse>(mainActivity, MyContacts.MAIN_URL + "v1.0/userInfo/getAccounts", new HttpResponseListener<ChrgResponse>() {

            @Override
            public void onResult(ChrgResponse result) {
                if (result != null && result.data != null) {
                    if (result.statusCode == 200) {

                        rootUserInfo = null;
                        userInfo = null;
                        SettingShareData.getInstance(application).setKeyValue("userInfo", "");
                        SettingShareData.getInstance(application).setKeyValue("login_userInfo", "");
                        Toast.makeText(application, "注销成功", Toast.LENGTH_SHORT).show();

                        if (back != null) {
                            back.callback();
                        }

                    }
                }

            }

            @Override
            public void onFail(int code) {
                // TODO Auto-generated method stub

            }
        }, ChrgResponse.class, null, "GET", true);

        httpRequest.addHead("mobileNo", rootUserInfo.mobileNo);
        httpRequest.addHead("mobiledeviceId", SystemOpt.getInstance().getAppSysInfo().getDeviceId());
        httpRequest.addHead("accesstoken", rootUserInfo.accessToken);

        HttpExecute.getInstance().addRequest(httpRequest);
    }

    /**
     * 初始化ImageLoader
     */
    @SuppressWarnings("deprecation")
    public static void initImageLoader(Context context) {
        File cacheDir = StorageUtils.getOwnCacheDirectory(context, SdUtil.IMG_CAHCE);//获取到缓存的目录地址
        //创建配置ImageLoader(所有的选项都是可选的,只使用那些你真的想定制)，这个可以设定在APPLACATION里面，设置为全局的配置参数
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024 / 2);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(context)
                //.memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽
                //.discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75, null) // Can slow ImageLoader, use it carefully (Better don't use it)设置缓存的详细信息，最好不要设置这个
                .threadPoolSize(3)//线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(maxMemory)) // You can pass your own memory cache implementation你可以通过自己的内存缓存实现
                //.memoryCacheSize(2 * 1024 * 1024)
                ///.discCacheSize(50 * 1024 * 1024)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密
                //.discCacheFileNameGenerator(new HashCodeFileNameGenerator())//将保存的时候的URI名称用HASHCODE加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                //.discCacheFileCount(100) //缓存的File数量
                .discCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径
                //.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                //.imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);//全局初始化此配置
    }

    public void setPushAlias(final String alias) {
        JPushInterface.setAlias(MyApplication.getInstance(), alias, new TagAliasCallback() {

            @Override
            public void gotResult(int code, String arg1, Set<String> arg2) {
                if (code == 0) {
                    LogUtils.logDug("set push alias success!");
                } else {
                    LogUtils.logDug("set push alias fail ! 60s try set");
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            setPushAlias(alias);
                        }
                    }, 1000 * 60);
                }
            }
        });

    }

    //隐藏虚拟键盘
    public void HideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

        }
    }

    public boolean isLogin() {
        return getLocalUserinfo() == null;
    }

    public void getUserInfoData() {
        if (rootUserInfo == null) return;
        String urlStr = MyContacts.MAIN_URL + "v1.0/userInfo/getInfo";
        Map<String, String> param = new HashMap<String, String>();
        HttpRequest<UserInfoResponse> httpRequest = new HttpRequest<UserInfoResponse>(this, urlStr, new HttpResponseListener<UserInfoResponse>() {

            @Override
            public void onResult(UserInfoResponse result) {
                if (result != null && result.data != null) {
                    if (result.statusCode == 200) {
                        setUserInfo(result.data);
                        if (mainActivity != null) {
                            if (mainActivity.leftMenu != null) {
                                mainActivity.leftMenu.showUserInfo();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFail(int code) {

            }
        }, UserInfoResponse.class, param, "GET", false);

        httpRequest.addHead("mobileNo", MyApplication.getInstance().rootUserInfo.mobileNo);
        httpRequest.addHead("mobiledeviceId", SystemOpt.getInstance().getAppSysInfo().getDeviceId());
        httpRequest.addHead("accesstoken", MyApplication.getInstance().rootUserInfo.accessToken);

        HttpExecute.getInstance().addRequest(httpRequest);
    }

    public void copyloginToSd() {
        if (!SdUtil.fileExists("dd_logo.png")) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    File file = SdUtil.getFile("dd_logo.png", false);
                    FileOutputStream fos = null;
                    InputStream is = null;
                    try {
                        is = application.getAssets().open("icon.png");
                        fos = new FileOutputStream(file);

                        byte[] buffer = new byte[1024 * 4];
                        int temp = -1;
                        while ((temp = is.read(buffer)) > 0) {
                            fos.write(buffer, 0, temp);
                        }
                        fos.flush();

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } finally {
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                        if (is != null) {
                            try {
                                is.close();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
        }
    }

}

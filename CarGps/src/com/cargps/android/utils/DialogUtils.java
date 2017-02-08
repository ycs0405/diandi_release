package com.cargps.android.utils;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.cargps.android.MyApplication;
import com.cargps.android.R;
import com.cargps.android.data.ConsumeOrder;
import com.cargps.android.interfaces.DialogCallback;

/***
 * dialog  工具类。
 *
 * @author fu
 */
public class DialogUtils {
    private static DialogUtils dialogUtils;

    public static DialogUtils getInstance() {
        return dialogUtils == null ? dialogUtils = new DialogUtils() : dialogUtils;
    }

    public Dialog createDialog(Context ctx, int resLayout) {
        Dialog dialog = new Dialog(ctx, R.style.custom_dialog);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = MyApplication.getInstance().widthPixels;
        lp.height = MyApplication.getInstance().heightPixels;
        dialog.setContentView(resLayout);
        return dialog;
    }

    public void showSelectPicture(Context ctx, final DialogCallback callback) {
        final Dialog dialog = createDialog(ctx, R.layout.dialog_select_picture_layout);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.findViewById(R.id.cameraBtn).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (callback != null) {
                    callback.camareClick();
                }
            }
        });

        dialog.findViewById(R.id.picBtn).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (callback != null) {
                    callback.picClick();
                }
            }
        });

        dialog.show();
    }


    public void showPushMsgDialog(Context ctx, String title, ConsumeOrder order, final DialogCallback callback) {

        final Dialog dialog = createDialog(ctx, R.layout.push_show_order_layout);
        TextView titleTv = ((TextView) dialog.findViewById(R.id.titleTv));
        TextView startTimeTv = ((TextView) dialog.findViewById(R.id.startTimeTv));
        TextView stopTimeTv = ((TextView) dialog.findViewById(R.id.stopTimTv));
        TextView timeTv = ((TextView) dialog.findViewById(R.id.timeTv));
        TextView priceTv = ((TextView) dialog.findViewById(R.id.priceTv));
        TextView orderIdTv = ((TextView) dialog.findViewById(R.id.orderIdtV));

        titleTv.setText(title);
        orderIdTv.setText("订单编号：" + order.orderId);
        startTimeTv.setText("开始时间：" + order.startTime);
        stopTimeTv.setText("结束时间：" + order.endTime);
        timeTv.setText("骑行时间：" + order.minutes + "分钟");
        priceTv.setText("订单金额：" + order.consume + "元");

        dialog.findViewById(R.id.cancelBtn).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (callback != null) {
                    callback.cancle();
                }
            }
        });

        dialog.findViewById(R.id.confirmBtn).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.confirm();
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * 得到自定义的progressDialog
     *
     * @param context
     * @param msg
     * @return
     */
    public static Dialog createLoadingDialog(Context context, String msg) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view  
//        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局  
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字  
        tipTextView.setText(msg);// 设置加载信息  

        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog  

        loadingDialog.setCancelable(false);// 不可以用“返回键”取消  

        WindowManager.LayoutParams lp = loadingDialog.getWindow().getAttributes();
        lp.width = MyApplication.getInstance().widthPixels;
        lp.height = MyApplication.getInstance().heightPixels;

        loadingDialog.setContentView(v);// 设置布局  
        return loadingDialog;

    }
}




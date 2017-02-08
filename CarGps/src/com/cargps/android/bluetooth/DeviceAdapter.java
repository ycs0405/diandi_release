package com.cargps.android.bluetooth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cargps.android.R;

import java.util.ArrayList;
import java.util.List;

public class DeviceAdapter extends BaseAdapter {
    private Context context;
    private List<DeviceDB.Record> mDevices = new ArrayList<DeviceDB.Record>();
    private int[] iconArray = {R.drawable.bicycle1, R.drawable.bicycle2, R.drawable.bicycle3};
    private int[] textColorArray = {R.color.textB1, R.color.textB2, R.color.textB3, R.color.textB4, R.color.textB5};

    public DeviceAdapter(Context context, List<DeviceDB.Record> mDevices) {
        this.context = context;
        this.mDevices = mDevices;
    }

    @Override
    public int getCount() {
        return mDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

/*	@Override
    public View getView(int i, View view, ViewGroup viewGroup) {
		// General ListView optimization code.
		View row =  LayoutInflater.from(context).inflate(R.layout.listitem_device,viewGroup,false);

		updateListItem(row, getDevice(i));
		return row;
	}*/

    // Public members.
    public void addDevice(DeviceDB.Record r) {
        boolean isExit = false;
        for (DeviceDB.Record device : mDevices) {
            if ((device.name).equals(r.name)) {
                isExit = true;
            }
        }
        if (!isExit) {
            mDevices.add(r);
            notifyDataSetChanged();
        }
    }

    public void reset() {
        mDevices.clear();
        DeviceDB.Record rec = DeviceDB.load(context);
        if (rec != null)
            this.addDevice(rec);
        notifyDataSetChanged();
    }

    DeviceDB.Record getDevice(int index) {
        return mDevices.get(index);
    }

    private void updateListItem(View row, DeviceDB.Record rec) {
        TextView txt;
        txt = (TextView) row.findViewById(R.id.device_name);
        txt.setText(rec.name);
        txt = (TextView) row.findViewById(R.id.device_address);
        txt.setText(rec.identifier);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_device, null);
            convertView.setTag(new ViewHolder(convertView));
        }
        initializeViews((DeviceDB.Record) getItem(position), (ViewHolder) convertView.getTag(), position);
        return convertView;
    }

    private void initializeViews(DeviceDB.Record rec, ViewHolder holder, int position) {
        //TODO implement
        holder.deviceName.setText(rec.name);
        holder.deviceAddress.setText(rec.identifier);

        holder.icon.setImageResource(iconArray[position / 3]);
        holder.ll.setBackgroundColor(textColorArray[position / 5]);
    }


    protected static class ViewHolder {
        private TextView deviceName;
        private TextView deviceAddress;
        private ImageView icon;
        private LinearLayout ll;

        public ViewHolder(View view) {
            deviceName = (TextView) view.findViewById(R.id.device_name);
            deviceAddress = (TextView) view.findViewById(R.id.device_address);
            icon = (ImageView) view.findViewById(R.id.bike_icon);
            ll = (LinearLayout) view.findViewById(R.id.ll_back);
        }
    }

}

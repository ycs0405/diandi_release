package com.widget.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.widget.android.view.CustomListView;
import com.widget.android.view.SideLayout;
import com.widget.android.view.SideLayout.OnCustomClickListenrr;

public class MainActivity extends Activity {
    CustomListView customListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_layout);
//		customListView = (CustomListView) findViewById(R.id.listview);
//		customListView.setAdapter(new MyAdapter());
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 30;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(MainActivity.this, R.layout.test_list_item, null);
            ((SideLayout) view.findViewById(R.id.side_view)).setCustomClickListenrr(new OnCustomClickListenrr() {

                @Override
                public void custiomClickLirtener() {
                    // TODO Auto-generated method stub
                    Toast.makeText(MainActivity.this, "点击了", Toast.LENGTH_SHORT).show();

                }
            });
            return view;
        }

    }

}

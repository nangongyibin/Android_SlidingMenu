package com.example.slidingmenu;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ngyb.slidingmenu.SlidingMenu;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_slidemenu);
        ListView menuListView = findViewById(R.id.menu_listview);
        ListView mainListView = findViewById(R.id.main_listview);
        mainListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constant.names));
        menuListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constant.cheeseeStrings) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.WHITE);
                return textView;
            }
        });


//        slidingMenu
//        setContentView(R.layout.activity_main);
//        final SlidingMenu sm = findViewById(R.id.sm);
//        Button back = findViewById(R.id.back);
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sm.switchMenu();
//            }
//        });
    }
}

package com.sam.contactslist;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Contact> mylist = new ArrayList<>();
    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private CheckBox selectall;
    private Button done;
    private SearchView search;
    private ImageView mSearchCloseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        selectall = findViewById(R.id.selectall);
        done = findViewById(R.id.done);
        search = findViewById(R.id.search);
        mAdapter = new MyAdapter(mylist);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        search.setFocusable(false); //removes curser
        search.setIconified(false); // expands searchview
        search.clearFocus(); //removes curser

        try {
            // https://en.it1352.com/article/1bb627b0b91744dbb24cc96874b1b4c2.html
            // https://stackoverflow.com/questions/25930380/android-search-widget-how-to-hide-the-close-button-in-search-view-by-default
            Field searchField = SearchView.class.getDeclaredField("mCloseButton");
            searchField.setAccessible(true);
            mSearchCloseButton = (ImageView) searchField.get(search);
            if (mSearchCloseButton != null) {
                mSearchCloseButton.setEnabled(false);
                mSearchCloseButton.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.transparent), android.graphics.PorterDuff.Mode.SRC_IN);
                //mSearchCloseButton.setImageDrawable(getResources().getDrawable(android.R.drawable.transparent));
            }
        } catch (Exception e) {
            System.out.println("SAM: error finding close button");
            e.printStackTrace();
        }

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });

        search.setOnQueryTextFocusChangeListener((view, b) -> {
            if(b){
                mSearchCloseButton.clearColorFilter();
            }else{
                mSearchCloseButton.setEnabled(false);
                mSearchCloseButton.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.transparent), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        });

//        selectall.setOnClickListener(v -> {
//            for(int i=0; i<mylist.size(); i++){
//                mylist.get(i).setChecked(true);
//            }
//            mAdapter.notifyDataSetChanged();
//        });

        selectall.setOnCheckedChangeListener((compoundButton, b) -> {
//            for(int i=0; i<mylist.size(); i++){
//                mylist.get(i).setChecked(b);
//            }
//            mAdapter.notifyDataSetChanged();
            mAdapter.checkFilterData(b);
        });

        done.setOnClickListener(v -> {
            JSONArray jsonArray = new JSONArray();
            for(int i=0; i<mylist.size(); i++){
                if(mylist.get(i).isChecked){
                    jsonArray.put(mylist.get(i).getJSONObject());
                }
            }

            System.out.println("SAM: jsonArray: "+jsonArray);

        });

        init();
    }

    private void init(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            prepareData();
        }else{
            Dexter.withContext(this)
                    .withPermission(Manifest.permission.READ_CONTACTS)
                    .withListener(new PermissionListener() {
                        @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                            prepareData();
                        }
                        @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                            Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                        }
                        @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();
        }
    }

    private void prepareData(){
//        Item item1 = new Item("Somnath Pal", "+91-7318909970", false);
//        Item item2 = new Item("Bhavesh Mandal", "+91-7893479590", false);
//        Item item3 = new Item("Sameer Mishra", "+91-9735635352", false);
//        Item item4 = new Item("Vinay Tiwari", "+91-8376018385", false);
//        Item item5 = new Item("Priya Malhotra", "+91-8129481730", false);
//
//        mylist.add(item1);
//        mylist.add(item2);
//        mylist.add(item3);
//        mylist.add(item4);
//        mylist.add(item5);


        mylist.addAll(Objects.requireNonNull(Util.getContacts(MainActivity.this)));
        mAdapter.notifyDataSetChanged();
    }


}
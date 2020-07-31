    package com.example.musicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

import static android.media.MediaPlayer.*;
import static com.karumi.dexter.Dexter.withActivity;

    public class MainActivity extends AppCompatActivity{

        ListView myListViewForSongs;
        String[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myListViewForSongs = (ListView) findViewById(R.id.mySongListView);

        runtimePermission();


    }

        public void runtimePermission()
        {
            Dexter.withActivity(this)
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            display();
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                            permissionToken.continuePermissionRequest();
                        }
                    }).check();
        }
        public ArrayList<File> findSong(File file){

            ArrayList<File> arrayList = new ArrayList<>();
            File[] files = file.listFiles();
            for(File singleFile: files){
                if(singleFile.isDirectory() && !singleFile.isHidden()){
                    arrayList.addAll(findSong(singleFile));
                }
                else{
                    if(singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")){
                        arrayList.add(singleFile);
                    }
                }
            }

            return arrayList;
        }

        void display(){
            final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());

            items = new String[mySongs.size()];
            for(int i=0; i<mySongs.size(); i++){
                items[i] = mySongs.get(i).getName().toString().replace(".mp3","").replace(".wav","");
            }

            ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);
            myListViewForSongs.setAdapter(myAdapter);

            myListViewForSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    String songName = myListViewForSongs.getItemAtPosition(i).toString();

                    startActivity(new Intent(getApplicationContext(),PlayerActivity.class)
                    .putExtra("songs",mySongs).putExtra("songName",songName)
                    .putExtra("pos",i));
                }
            });
        }
}

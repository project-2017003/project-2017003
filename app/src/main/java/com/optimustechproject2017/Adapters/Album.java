package com.optimustechproject2017.Adapters;


import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;


public class Album {


    String gflag;
    Target target = new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    String PATH = Environment.getExternalStorageDirectory()+ "/"+"Food Fun"+"/";
                    File folder = new File(PATH);
                    if(!folder.exists()){
                        folder.mkdir();//If there is no folder it will be created.
                    }

                    File file = new File(PATH + "/saved.jpg");


                    System.out.println(Environment.getExternalStorageDirectory().getPath()
                            + "/saved.jpg");
                    try {
                        file.createNewFile();
                        FileOutputStream ostream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,ostream);
                        ostream.close();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {}

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {}
    };


    private String name;
    private String items;
    private int thumbnail;
    private String time;
    private String URL;
    private String RestID;

    public Album() {
    }

//    public Album(final int i, Context context){
//        this.name = ParticipantsList.get(i).get("EVENTNAME");
//        System.out.println( ParticipantsList.get(i).get("NAME"));
//        this.URL = ParticipantsList.get(i).get("URL");
//        this.gflag=ParticipantsList.get(i).get("Gflag");
//        Picasso.with(context).load(this.URL).into(new Target() {
//            @Override
//            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        String PATH = Environment.getExternalStorageDirectory()+ "/"+"Food Fun"+"/";
//                        File folder = new File(PATH);
//                        if(!folder.exists()){
//                            folder.mkdir();//If there is no folder it will be created.
//                        }
//
//                        File file = new File(PATH + i+".jpg");
//
//
//                        System.out.println(PATH +i);
//                        try {
//                            file.createNewFile();
//                            FileOutputStream ostream = new FileOutputStream(file);
//                            bitmap.compress(Bitmap.CompressFormat.JPEG,100,ostream);
//                            ostream.close();
//                        }
//                        catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
//            }
//
//            @Override
//            public void onBitmapFailed(Drawable errorDrawable) {
//
//            }
//
//            @Override
//            public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//            }
//        });
//
//
//    }



//        this.URL = ParticipantsList.get(i).get("URL");

    public Album(String name, String items, String url, String time, String RestID) {
        this.name = name;
        this.items = items;
        this.thumbnail = thumbnail;
        this.time = time;
        this.URL = url;
        this.RestID = RestID;

    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRestID() {
        return RestID;
    }

    public String getItems() {
        return items;
    }

    public void setitems(String items) {
        this.items = items;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }

}




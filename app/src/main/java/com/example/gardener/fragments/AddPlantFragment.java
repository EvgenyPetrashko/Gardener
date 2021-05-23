package com.example.gardener.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.gardener.Const;
import com.example.gardener.InternetDisconnectionActivity;
import com.example.gardener.MainActivity;
import com.example.gardener.R;
import com.example.gardener.adapters.GridPhotoAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddPlantFragment extends Fragment {
    private View mainView;
    private ImageButton back_arrow;
    private Button save_button;
    private SQLiteDatabase db;
    private SharedPreferences settings;
    private TextView date_text;
    private Button change_date;
    private ImageButton add_photo_button;
    private GridView grid;
    private GridPhotoAdapter adapter_gr;
    private ArrayList<Drawable> draw_array = new ArrayList<>();
    private boolean theme;
    private AdView ad_block;
    public AddPlantFragment(SQLiteDatabase db){
        this.db = db;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.add_plant_my_garden_layout, container, false);
        settings = getContext().getSharedPreferences(Const.APP_SETTINGS_NAME, Context.MODE_PRIVATE);
        init();
        applyTheme(settings.getBoolean(Const.APP_THEME, true));
        TurnOffBNV();
        if (!isOnline()){
            Intent intent = new Intent(getContext(), InternetDisconnectionActivity.class);
            startActivity(intent);
        }else {
            showAds();
        }
        return mainView;
    }

    private void init() {
        back_arrow = mainView.findViewById(R.id.add_fragment_back_arrow);
        back_arrow.setOnClickListener(OnBackClickListener());
        save_button = mainView.findViewById(R.id.add_fr_save_button);
        save_button.setOnClickListener(OnSavePlantListener());

        date_text = mainView.findViewById(R.id.add_fr_date_text);
        date_text.setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date()));
        change_date = mainView.findViewById(R.id.add_fr_change_date_button);
        change_date.setOnClickListener(OnChangeDateListener());

        add_photo_button = mainView.findViewById(R.id.add_photo_button);
        add_photo_button.setOnClickListener(OnAddPhotoListener());


       grid = mainView.findViewById(R.id.gridViewPhoto);
       adapter_gr = new GridPhotoAdapter(draw_array, theme);
       grid.setAdapter(adapter_gr);
    }

    private void showAds(){
        ad_block = mainView.findViewById(R.id.add_plant_my_garden_add);
        AdRequest adRequest = new AdRequest.Builder().build();
        ad_block.loadAd(adRequest);
    }

    private void applyTheme(boolean is_dark){
        theme = is_dark;
        int back_color = Color.BLACK;
        int text_color = Color.WHITE;
        if (!is_dark){
            back_color = Color.WHITE;
            text_color = Color.BLACK;
        }
        ((TextView)(mainView.findViewById(R.id.add_fr_name_title))).setTextColor(text_color);
        ((EditText)(mainView.findViewById(R.id.add_fr_name_text))).setTextColor(text_color);
        ((EditText)(mainView.findViewById(R.id.add_fr_name_text))).setHintTextColor(Color.DKGRAY);
        ((TextView)(mainView.findViewById(R.id.add_fr_desc_title))).setTextColor(text_color);
        ((EditText)(mainView.findViewById(R.id.add_fr_desc_text))).setTextColor(text_color);
        ((EditText)(mainView.findViewById(R.id.add_fr_desc_text))).setHintTextColor(Color.DKGRAY);
        ((TextView)mainView.findViewById(R.id.add_fr_photo_title)).setTextColor(text_color);
        ((TextView)mainView.findViewById(R.id.add_fr_date_title)).setTextColor(text_color);
        mainView.findViewById(R.id.add_plant_fragment_back).setBackgroundColor(back_color);
    }

    private View.OnClickListener OnBackClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext(), (R.style.DialogThemeLight))
                        .setTitle("Do you really want to go back?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment(db)).commit();
                                TurnOnBNV();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

            }
        };
    }

    private View.OnClickListener OnChangeDateListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar temp = Calendar.getInstance();
                int temp_year = temp.get(Calendar.YEAR);
                int temp_month = temp.get(Calendar.MONTH);
                int temp_day = temp.get(Calendar.DAY_OF_MONTH);
            new DatePickerDialog(getContext(), (R.style.DialogThemeLight),new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    temp.set(Calendar.YEAR, year);
                    temp.set(Calendar.MONTH, month);
                    temp.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    date_text.setText(new SimpleDateFormat("dd.MM.yyyy").format(temp.getTime()));
                }
            }, temp_year, temp_month, temp_day).show();
            }
        };
    }

    private View.OnClickListener OnAddPhotoListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 101:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    ShowDialog();
                }else{

                }
                break;
        }
    }

    private View.OnClickListener OnSavePlantListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues contentValues = new ContentValues();
                EditText name = mainView.findViewById(R.id.add_fr_name_text);
                EditText desc = mainView.findViewById(R.id.add_fr_desc_text);
                contentValues.put(Const.OWN_GARDEN_NAME, name.getText().toString());
                contentValues.put(Const.OWN_GARDEN_DESC, desc.getText().toString());
                contentValues.put(Const.OWN_GARDEN_DATE, date_text.getText().toString());
                db.insert(Const.TABLE_OWN_GARDEN, null, contentValues);
                Cursor temp_count = db.query(Const.TABLE_OWN_GARDEN, null, null, null, null, null, null);
                temp_count.moveToLast();
                int code = 0;
                try {
                    code = temp_count.getInt(0);
                }catch (CursorIndexOutOfBoundsException e){
                    code = 0;
                }
                for (int i = 0; i < draw_array.size() ; i++) {
                    Drawable temp = draw_array.get(i);
                    if (temp instanceof BitmapDrawable){
                        Bitmap bitmap = ((BitmapDrawable)temp).getBitmap();
                        String temp_path = saveImage(bitmap, code, i);
                        ContentValues cv = new ContentValues();
                        cv.put(Const.OWN_IMAGES_ID, code);
                        cv.put(Const.OWN_IMAGES_PATH, temp_path);
                        db.insert(Const.TABLE_OWN_IMAGES, null, cv);
                    }
                }
                Toast.makeText(getContext(), R.string.add_fr_result, Toast.LENGTH_SHORT);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment(db)).commit();
                TurnOnBNV();
            }
        };
    }

    public void ShowDialog(){
        //ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dv = getLayoutInflater().inflate(R.layout.camera_gallery_dialog, null);
        LinearLayout camera = dv.findViewById(R.id.camera_button);
        camera.setOnClickListener(OpenCamera());
        LinearLayout gallery = dv.findViewById(R.id.gallery_button);
        gallery.setOnClickListener(OpenGallery());
        builder.setView(dv);
        builder.show();
    }


    private int REQUEST_IMAGE_CAPTURE = 1;
    private int REQUEST_GALLERY_CAPTURE = 2;

    private View.OnClickListener OpenCamera(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
              startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        };
    }

    private View.OnClickListener OpenGallery(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_GALLERY_CAPTURE);
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_IMAGE_CAPTURE  && resultCode == Activity.RESULT_OK)){
            Toast.makeText(getContext(), "Successfully", Toast.LENGTH_SHORT).show();
            if(data != null)
            {

                Bitmap bitmap = (Bitmap)data.getExtras().get("data");
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                draw_array.add(drawable);
                adapter_gr.notifyDataSetChanged();
            }
        } else if (requestCode == REQUEST_GALLERY_CAPTURE  && resultCode == Activity.RESULT_OK){
            if (data != null){
                Uri uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                    Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                    draw_array.add(drawable);
                    adapter_gr.notifyDataSetChanged();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String saveImage(Bitmap finalBitmap, int block, int number) {
        if (isExternalStorageWritable()){

           File path = Environment.getExternalStorageDirectory();
           File dir = new File(path + "/saved_images/");
           dir.mkdirs();
           String location  = "Gardener_"+ block + "_" + number +".png";
           File file = new File(dir, location);
            try {
                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return location;
        }else {
            return "";
        }
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private void TurnOffBNV(){
        MainActivity.bottom_menu.setVisibility(View.GONE);
    }

    private void TurnOnBNV(){
        MainActivity.bottom_menu.setVisibility(View.VISIBLE);
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }


}

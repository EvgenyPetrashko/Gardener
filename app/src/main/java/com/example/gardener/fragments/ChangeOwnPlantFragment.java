package com.example.gardener.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.fragment.app.Fragment;

import com.example.gardener.Const;
import com.example.gardener.DrawablePro;
import com.example.gardener.InternetDisconnectionActivity;
import com.example.gardener.R;
import com.example.gardener.adapters.GridPhotoOwnAdapter;
import com.example.gardener.adapters.PhotoAdapterPro;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ChangeOwnPlantFragment extends Fragment {
    private View mainView;
    private SQLiteDatabase db;
    private boolean theme;
    private int local_id;
    private String old_name;
    private String old_desc;
    private String old_date;
    private SharedPreferences settings;
    private ImageButton back_arrow;
    private Button change_button;
    private EditText name;
    private EditText desc;
    private TextView date;
    private Button change_date_button;
    private ImageButton add_photo_button;
    private GridView grid;
    private PhotoAdapterPro adapter_gr;
    private ArrayList<DrawablePro> draw_array;
    private AdView ad_block;
    public ChangeOwnPlantFragment(SQLiteDatabase db){
        this.db = db;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.change_plant_my_garden_layout , container, false);
        settings = getContext().getSharedPreferences(Const.APP_SETTINGS_NAME, Context.MODE_PRIVATE);
        init();
        Configure();
        applyTheme(settings.getBoolean(Const.APP_THEME, true));
        if (!isOnline()){
            Intent intent = new Intent(getContext(), InternetDisconnectionActivity.class);
            startActivity(intent);
        }else {
            showAds();
        }
        return mainView;
    }

    private void Configure() {
        Bundle bundle = getArguments();
        local_id = bundle.getInt(Const.OWN_GARDEN_ID);
        old_name = bundle.getString(Const.OWN_GARDEN_NAME);
        old_desc = bundle.getString(Const.OWN_GARDEN_DESC);
        old_date = bundle.getString(Const.OWN_GARDEN_DATE);
        name.setText(old_name);
        desc.setText(old_desc);
        date.setText(old_date);

        Cursor temp_cursor = db.query(Const.TABLE_OWN_IMAGES, null, Const.OWN_IMAGES_ID + "= '" + local_id + "'", null, null, null, null);
        temp_cursor.moveToFirst();
        draw_array = new ArrayList<>();
        while (!temp_cursor.isAfterLast()){
            String path;
            try {
                path = temp_cursor.getString(1);
                draw_array.add(new DrawablePro(getDrawable(path), path, false));
            }catch (Exception e){
                break;
            }
            temp_cursor.moveToNext();
        }
        adapter_gr = new PhotoAdapterPro(draw_array, db, theme);
        grid.setAdapter(adapter_gr);

    }

    private void showAds(){
        ad_block = mainView.findViewById(R.id.change_own_plant_add);
        AdRequest adRequest = new AdRequest.Builder().build();
        ad_block.loadAd(adRequest);
    }

    private void applyTheme(boolean is_dark) {
        theme = is_dark;
        int back_color = Color.BLACK;
        int text_color = Color.WHITE;
        if (!is_dark){
            back_color = Color.WHITE;
            text_color = Color.BLACK;
        }
        ((TextView)(mainView.findViewById(R.id.change_own_fr_name_title))).setTextColor(text_color);
        ((EditText)(mainView.findViewById(R.id.change_own_fr_name_text))).setTextColor(text_color);
        ((EditText)(mainView.findViewById(R.id.change_own_fr_name_text))).setHintTextColor(Color.DKGRAY);
        ((TextView)(mainView.findViewById(R.id.change_own_fr_desc_title))).setTextColor(text_color);
        ((EditText)(mainView.findViewById(R.id.change_own_fr_desc_text))).setTextColor(text_color);
        ((EditText)(mainView.findViewById(R.id.change_own_fr_desc_text))).setHintTextColor(Color.DKGRAY);
        ((TextView)mainView.findViewById(R.id.change_own_fr_photo_title)).setTextColor(text_color);
        ((TextView)mainView.findViewById(R.id.change_own_fr_date_title)).setTextColor(text_color);
        mainView.findViewById(R.id.change_own_fr_back).setBackgroundColor(back_color);
    }

    private void init() {
        back_arrow = mainView.findViewById(R.id.change_own_fr_back_arrow);
        back_arrow.setOnClickListener(OnBackClickListener());
        change_button = mainView.findViewById(R.id.change_own_fr_button_change);
        change_button.setOnClickListener(OnChangePlantClickListener());
        name = mainView.findViewById(R.id.change_own_fr_name_text);
        desc = mainView.findViewById(R.id.change_own_fr_desc_text);
        date = mainView.findViewById(R.id.change_own_fr_date_text);
        change_date_button = mainView.findViewById(R.id.change_own_fr_change_date_button);
        change_date_button.setOnClickListener(OnChangeDateClickListener());
        add_photo_button = mainView.findViewById(R.id.add_photo_button);
        add_photo_button.setOnClickListener(OnAddPhotoClickListener());

        grid= mainView.findViewById(R.id.gridViewPhoto);
    }

    private View.OnClickListener OnBackClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoOwnPlantFragment fragment = new InfoOwnPlantFragment(db);
                Bundle bundle = getArguments();
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        };
    }

    private View.OnClickListener OnChangeDateClickListener(){
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
                        date.setText(new SimpleDateFormat("dd.MM.yyyy").format(temp.getTime()));
                    }
                }, temp_year, temp_month, temp_day).show();
            }
        };
    }

    private View.OnClickListener OnAddPhotoClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View dv = getLayoutInflater().inflate(R.layout.camera_gallery_dialog, null);
                LinearLayout camera = dv.findViewById(R.id.camera_button);
                camera.setOnClickListener(OpenCamera());
                LinearLayout gallery = dv.findViewById(R.id.gallery_button);
                gallery.setOnClickListener(OpenGallery());
                builder.setView(dv);
                builder.show();
            }
        };
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

    private View.OnClickListener OnChangePlantClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String new_name = name.getText().toString();
                if (!new_name.equals("")){
                    String new_desc = desc.getText().toString();
                    String new_date = desc.getText().toString();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Const.OWN_GARDEN_NAME, new_name);
                    contentValues.put(Const.OWN_GARDEN_DESC, new_desc);
                    contentValues.put(Const.OWN_GARDEN_DATE, new_date);
                    db.update(Const.TABLE_OWN_GARDEN, contentValues, Const.OWN_GARDEN_ID + "= '" + local_id + "'", null);
                    adapter_gr.ApplyDeleting();
                    int rand = (int) (Math.random()*10000 + 1);
                    for (int i = 0; i <draw_array.size() ; i++) {
                        if (draw_array.get(i).is_new){
                            Drawable temp = draw_array.get(i).img;
                            if (temp instanceof BitmapDrawable){
                                Bitmap bitmap = ((BitmapDrawable)temp).getBitmap();
                                String temp_path = saveImage(bitmap, local_id, i, rand);
                                ContentValues cv = new ContentValues();
                                cv.put(Const.OWN_IMAGES_ID, local_id);
                                cv.put(Const.OWN_IMAGES_PATH, temp_path);
                                db.insert(Const.TABLE_OWN_IMAGES, null, cv);
                            }
                        }
                    }
                    Toast.makeText(getContext(), R.string.add_fr_result, Toast.LENGTH_SHORT);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment(db)).commit();
                }else {
                    Toast.makeText(getContext(), getResources().getString(R.string.toast_on_dismiss_text), Toast.LENGTH_SHORT).show();
                }
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
                draw_array.add(new DrawablePro(drawable, "NEW", true));
                adapter_gr.notifyDataSetChanged();
                if (draw_array.size() % 3 == 1){
                    grid.setAdapter(adapter_gr);
                }
            }
        } else if (requestCode == REQUEST_GALLERY_CAPTURE  && resultCode == Activity.RESULT_OK){
            if (data != null){
                Uri uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                    Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                    draw_array.add(new DrawablePro(drawable, "NEW", true));
                    adapter_gr.notifyDataSetChanged();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Drawable getDrawable(String path){
        String location = Environment.getExternalStorageDirectory().getAbsolutePath()+"/saved_images/" + path;
        Bitmap bitmap = BitmapFactory.decodeFile(location);
        return new BitmapDrawable(getResources(), bitmap);
    }

    private String saveImage(Bitmap finalBitmap, int block, int number, int random_num) {
        if (isExternalStorageWritable()){

            File path = Environment.getExternalStorageDirectory();
            File dir = new File(path + "/saved_images/");
            dir.mkdirs();
            String location  = "Gardener_"+ block + "_" + number + "_" + random_num + ".png";
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

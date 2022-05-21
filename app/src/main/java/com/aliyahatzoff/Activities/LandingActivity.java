package com.aliyahatzoff.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.aliyahatzoff.Fragments.BlankProfile;
import com.aliyahatzoff.Fragments.HomeScreenFragment;
import com.aliyahatzoff.Fragments.SearchFragment;
import com.aliyahatzoff.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.sanojpunchihewa.updatemanager.UpdateManager;
import com.sanojpunchihewa.updatemanager.UpdateManagerConstant;

public class LandingActivity extends AppCompatActivity implements View.OnClickListener {
    public static SpaceNavigationView spaceNavigationView;
    Context context;
    boolean isselected = false;
    UpdateManager mUpdateManager;
    BottomNavigationView bottomNavigationView;
    FragmentManager fragmentManager;
    public static ImageView centerheart;
    LinearLayout home, search, filter, profile;
    ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        mUpdateManager = UpdateManager.Builder(this).mode(UpdateManagerConstant.IMMEDIATE);
        mUpdateManager.start();
        centerheart = findViewById(R.id.heart);
        home = findViewById(R.id.tab_home);
        search = findViewById(R.id.tab_discover);
        profile = findViewById(R.id.tab_profile);
        filter = findViewById(R.id.tab_filter);
        context = LandingActivity.this;
        home.setOnClickListener(this);
        search.setOnClickListener(this);
        profile.setOnClickListener(this);
        filter.setOnClickListener(this);
        fragmentManager = getSupportFragmentManager();
        Boolean permission = check_permissions();
        openFragment(HomeScreenFragment.newInstance());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tab_home:
                fragmentManager.beginTransaction().replace(R.id.container, new HomeScreenFragment()).commit();
                break;
            case R.id.tab_discover:
                fragmentManager.beginTransaction().replace(R.id.container, new SearchFragment()).commit();
                break;
            case R.id.tab_filter:
                startActivity(new Intent(LandingActivity.this, RecordingActivity.class));
                break;
            case R.id.tab_profile:
                fragmentManager.beginTransaction().replace(R.id.container, new BlankProfile()).commit();
                break;
        }
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
    //Manifest.permission.READ_EXTERNAL_STORAGE,
    //android.Manifest.permission.WRITE_EXTERNAL_STORAGE,

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean check_permissions() {
        String[] PERMISSIONS = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE

        };
        if (!hasPermissions(context, PERMISSIONS)) {
            requestPermissions(PERMISSIONS, 2);
        } else {

            return true;
        }
        return false;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    void takepermissions(){
        if(Checkstoragepermision()){
            //Toast.makeText(this,"Permission Already Granted",Toast.LENGTH_SHORT).show();
        }
        else
        {
            takepermission();
        }
    }
    public  boolean Checkstoragepermision(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        }
        else{
            int readExternalStoragePermission= ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            return readExternalStoragePermission==PackageManager.PERMISSION_GRANTED;
        }
    }
    public  void takepermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try{
                Intent intent=new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                // startActivityForResult(intent,100);
                launcher.launch(intent);
            }
            catch(Exception e){
                Intent intent=new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                //startActivityForResult(intent,100);
                launcher.launch(intent);
            }
        }
        else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},101);
        }
    }

  /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==100){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if(Environment.isExternalStorageManager()){
                        Toast.makeText(this,"Permission Granted 11",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        takepermission();
                    }
                }
            }
        }
    }
*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0){
            if(requestCode==101){
            boolean readExternalstorage=grantResults[0]==PackageManager.PERMISSION_GRANTED;
            if(readExternalstorage){
                Toast.makeText(this,"Permission granted 10",Toast.LENGTH_SHORT).show();
            }
            else {
                takepermission();
            }
            }
        }
    }
}
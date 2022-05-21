package com.aliyahatzoff.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.aliyahatzoff.Activities.Delete;
import com.aliyahatzoff.Activities.ProfileEditActivity;
import com.aliyahatzoff.Api.Common;
import com.aliyahatzoff.Models.UserInfoModel;
import com.aliyahatzoff.R;
import com.aliyahatzoff.utils.Functions;
import com.aliyahatzoff.utils.Sharedhelper;
import com.aliyahatzoff.notuseddesign.ViewPagerAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Profile extends Fragment {
    private Toolbar toolbar;
    private TabLayout tableLayout;
    private ViewPager viewPager;
    private ImageView paid_unpaid;
    TextView txt_username, txt_profession, txt_follow, txt_likes;
    LinearLayout edit;
    File imgFile;
    Button btndelete;
    String imageFilePath;
    ImageView profilepic, change_pic,delete;
    private int PICK_IMAGE_REQUEST = 1;
    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 0;
    ActivityResultLauncher<Intent> launcher;


    public Profile() {
        // Required empty public constructor
    }

    public static Profile newInstance() {
        Profile fragment = new Profile();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupviewpager(viewPager);
        tableLayout.setupWithViewPager(viewPager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);
        tableLayout = (TabLayout) view.findViewById(R.id.tablayout);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        txt_username = view.findViewById(R.id.txt_username);
        txt_profession = view.findViewById(R.id.txt_profession);
        txt_follow = view.findViewById(R.id.txt_following);
        delete=view.findViewById(R.id.delete);
        //btndelete=view.findViewById(R.id.delete);
        txt_likes = view.findViewById(R.id.txt_likes);
//        txt_username.setText(Sharedhelper.getKey(getContext(), "name"));
        paid_unpaid = view.findViewById(R.id.paid_unpaid);
        edit = (LinearLayout) view.findViewById(R.id.edit);
        profilepic = view.findViewById(R.id.profilepic);
        change_pic = view.findViewById(R.id.camera);

        launcher= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Uri uri = result.getData().getData();
                            try {
                                Bitmap selectedImage;
                                Uri imageUri = null;
                                if (result.getData().getData() == null) {
                                    selectedImage = (Bitmap) result.getData().getExtras().get("data");

                                } else {
                                    imageUri = result.getData().getData();
                                    selectedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), result.getData().getData());

                                }
                                profilepic.setImageBitmap(selectedImage);
                                imgFile = new File(getContext().getCacheDir(), "front.jpg");
                                imgFile.createNewFile();


                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                                byte[] bitmapdata = bos.toByteArray();
                                FileOutputStream fos = null;
                                fos = new FileOutputStream(imgFile);
                                fos.write(bitmapdata);
                                fos.flush();
                                fos.close();


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });



        setprofile();
        delete.setOnClickListener(v ->{
            Intent intent = new Intent(getContext(), Delete.class);
            startActivity(intent);


        });
        change_pic.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    selectImage(getContext());
                } else {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 101);
                }
            } else {
                selectImage(getContext());
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileEditActivity.class);
                startActivity(intent);
            }
        });
        if (Sharedhelper.getKey(getContext(), "profilepic").contains("http"))
            imageFilePath = Sharedhelper.getKey(getContext(), "profilepic");
        else {
            imageFilePath = getContext().getResources().getString(R.string.cloudfront)+"/"+Sharedhelper.getKey(getContext(), "profilepic");
        }
        try {
            Picasso.get().
                    load(imageFilePath)
                    .resize(150, 150)
                    .placeholder(getContext().getResources().getDrawable(R.drawable.profile_image_placeholder))
                    .into(profilepic);

        } catch (Exception e) {

        }

        return view;
    }

    private void setprofile() {
        txt_username.setText(Sharedhelper.getKey(getContext(), "name"));
        txt_profession.setText(Sharedhelper.getKey(getContext(), "interest"));
        txt_follow.setText(Sharedhelper.getKey(getContext(), "follow"));
        txt_likes.setText(Sharedhelper.getKey(getContext(), "like"));

    }

    private void setupviewpager(ViewPager viewPager) {

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(new PostVideoFragment(), "Posts");
        viewPagerAdapter.addFragment(new GalleryVideoFragment(), "Gallery");
        viewPagerAdapter.addFragment(new SaveVideoFragment(), "saved");
        viewPager.setAdapter(viewPagerAdapter);
    }

    protected void saveprofile() {
        Functions.showLoadingDialog(getContext());
        MultipartBody.Part filePart = null;
        if (imgFile != null) {
            filePart = MultipartBody.Part.createFormData("image", imgFile.getName(), RequestBody.create(MediaType.parse("image/*"), imgFile));
        } else {
            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            filePart = MultipartBody.Part.createFormData("image", "", attachmentEmpty);
        }
        Common.getHatzoffApi().changepic(filePart).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    System.out.println(response.errorBody().string());
                } catch (Exception e) {
                }
                if (response.isSuccessful()) {
                    Functions.cancelLoading();
                    try {
                        // System.out.println(response.body().string());
                        Sharedhelper.putKey(getContext(), "profilepic", response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.cancelLoading();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 101:
                if (grantResults.length > 0) {
                    boolean permission1 = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean permission2 = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (permission1 && permission2) {
                        selectImage(getContext());
                    } else {
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                "Please Grant Permissions to upload Profile",
                                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 101);
                                    }
                                }).show();
                    }
                }
                break;
        }
    }


    private void selectImage(Context context) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your Profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    launcher.launch(new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE));

                } else if (options[item].equals("Choose from Gallery")) {
                    launcher.launch(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI));


                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }

            }
        });
        builder.show();
    }
    /*
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode != 0) {
                if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
                    try {
                        Bitmap selectedImage;
                        Uri imageUri = null;
                        if (data.getData() == null) {
                            selectedImage = (Bitmap) data.getExtras().get("data");
                            //   System.out.println(data.getExtras().get("data"));

                        } else {
                            imageUri = data.getData();
                            selectedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());

                        }
                        profilepic.setImageBitmap(selectedImage);
                        imgFile = new File(getContext().getCacheDir(), "front.jpg");
                        imgFile.createNewFile();

                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                        byte[] bitmapdata = bos.toByteArray();
                        FileOutputStream fos = null;
                        fos = new FileOutputStream(imgFile);
                        fos.write(bitmapdata);
                        fos.flush();
                        fos.close();
                        saveprofile();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (resultCode == RESULT_OK && data != null && requestCode == 103) {
                    try {
                        Uri imageUri = data.getData();
                        Bitmap selectedImage;
                        if (data.getData() == null) {
                            selectedImage = (Bitmap) data.getExtras().get("data");
                        } else {
                            selectedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());

                        }
                        selectedImage = getResizedBitmap(selectedImage, 300);// 400 is for example, replace with desired size
                        selectedImage = rotateImageIfRequired(selectedImage, getContext(), imageUri);
                        profilepic.setImageBitmap(selectedImage);
                        imgFile = new File(getContext().getCacheDir(), "front.jpg");
                        imgFile.createNewFile();
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                        byte[] bitmapdata = bos.toByteArray();
                        FileOutputStream fos = null;
                        fos = new FileOutputStream(imgFile);
                        fos.write(bitmapdata);
                        fos.flush();
                        fos.close();
                        saveprofile();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        }
    */
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static Bitmap rotateImageIfRequired(Bitmap img, Context context, Uri selectedImage) throws IOException {

        if (selectedImage.getScheme().equals("content")) {
            String[] projection = {MediaStore.Images.ImageColumns.ORIENTATION};
            Cursor c = context.getContentResolver().query(selectedImage, projection, null, null, null);
            if (c.moveToFirst()) {
                final int rotation = c.getInt(0);
                c.close();
                return rotateImage(img, rotation);
            }
            return img;
        } else {
            ExifInterface ei = new ExifInterface(selectedImage.getPath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(img, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(img, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(img, 270);
                default:
                    return img;
            }
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        return rotatedImg;
    }

    @Override
    public void onStart() {
        super.onStart();
        checkPaidUnpaid();
    }

    private void checkPaidUnpaid() {
        Common.getHatzoffApi().userInfo().enqueue(new Callback<UserInfoModel>() {
            @Override
            public void onResponse(Call<UserInfoModel> call, Response<UserInfoModel> response) {
                if (response.isSuccessful()) {
                    String status = response.body().getPaid();
                    txt_username.setText(response.body().getName());
                    if (status.equals("Notpaid")) {
                        paid_unpaid.setVisibility(View.GONE);
                    } else {
                        paid_unpaid.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserInfoModel> call, Throwable t) {

            }
        });
    }
}
package com.mita.mqtt.athlete.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.system.ErrnoException;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.google.gson.JsonObject;
import com.mita.athlete.login.AppHelper;
import com.mita.athlete.login.UserActivity;
import com.mita.mqtt.athlete.R;
import com.mita.mqtt.athlete.model.AthleteDetailsResponseModel;
import com.mita.retrofit_api.APIService;
import com.mita.retrofit_api.ApiUtils;
import com.mita.utils.GlobalConstants;
import com.mita.utils.SharedPref;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AthleteProfileUpdateActivity extends AppCompatActivity {
    private APIService mAPIService;

    private EditText et_full_name, et_email, et_mobile, et_address, et_age, et_description, et_profileUrl;
    private String SEX = "o";
    RadioGroup radiofamily;
    Button BtnSubmit;
    private Handler handlerOne;
    private Runnable runnableOne;
    private Dialog mDialog;
    private String shareVoice;
    private String sharePhoto;
    private RadioGroup radiofamilySharePhoto;
    private RadioGroup radiofamilyShareVoice;
    private String Photo;
    private String Voice;

    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 88;
    private ImageView Iv_profile;
    private Uri mCropImageUri;
    CropImageView mCropImageView;
    private RelativeLayout releCrop;
    private LinearLayout llMain;
    private int selection;
    File filePath = null;
    Button CropImageViewNo, CropImageView1, CropRotate;
    String profileImageBase64;
    String mediaPath;
    private String FilePath;
    private MultipartBody.Part body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athlete_profile_update);


        // initializing retrofit service
        mAPIService = ApiUtils.getAPIService();

        // initializing cognito
        AppHelper.init(getApplicationContext());

        // initializing sharedPreff
        SharedPref.init(this);

        // initialzing viwes
        initViews();

        // check storege and camera permission
        checkPermission();
    }

    public void initViews() {
        et_full_name = findViewById(R.id.et_full_name);
        et_email = findViewById(R.id.et_email);
        et_mobile = findViewById(R.id.et_mobile);
        et_address = findViewById(R.id.et_address);
        et_description = findViewById(R.id.et_description);
        et_profileUrl = findViewById(R.id.et_profileUrl);
        et_age = findViewById(R.id.et_age);
        Iv_profile = findViewById(R.id.Iv_profile);

        radiofamilySharePhoto = (RadioGroup) findViewById(R.id.radiofamilySharePhoto);
        radiofamilyShareVoice = (RadioGroup) findViewById(R.id.radiofamilyShareVoice);

        BtnSubmit = findViewById(R.id.BtnSubmit);
        BtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    UpdateAthleteDetails();
                    UploadProfilePhoto();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        radiofamily = (RadioGroup) findViewById(R.id.radiofamily);

        ImageView Iv_Close = findViewById(R.id.Iv_Close);
        Iv_Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        try {
            getAthleteDetails();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImageView Iv_Camera = (ImageView) findViewById(R.id.Iv_Camera);
        Iv_Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selection = 1;
                startActivityForResult(getPickImageChooserIntent(), 200);
            }
        });
        releCrop = (RelativeLayout) findViewById(R.id.releCrop);
        llMain = (LinearLayout) findViewById(R.id.llMain);
        CropImageViewNo = (Button) findViewById(R.id.CropImageViewNo);
        CropImageView1 = (Button) findViewById(R.id.CropImageView1);
        //  CropRotate = (Button) findViewById(R.id.CropRotate);
        mCropImageView = (CropImageView) findViewById(R.id.CropImageView);

        CropImageViewNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releCrop.setVisibility(View.GONE);
                llMain.setVisibility(View.VISIBLE);

            }
        });


        CropImageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap cropped = mCropImageView.getCroppedImage();
                if (cropped != null)
                    releCrop.setVisibility(View.GONE);
                llMain.setVisibility(View.VISIBLE);
                convertImageToBase64(cropped);
                Iv_profile.setImageBitmap(cropped);
                try {
                    saveIntoFile(cropped);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("==errSaveFil", e.getMessage());
                }

            }
        });
    }

    // permission check
    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED

                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED

                && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(AthleteProfileUpdateActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
    }

    // fetch athlet details from DB
    public void getAthleteDetails() throws IOException {
        final ProgressDialog dialog;
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCanceledOnTouchOutside(false);
     //   dialog.show();

        String AthleteId = SharedPref.read(SharedPref.ATH_USER_ID, "");
        Call<AthleteDetailsResponseModel> call = mAPIService.getAtheletById(AthleteId);     //getStringScalar(name, age, phone, email);

        call.enqueue(new Callback<AthleteDetailsResponseModel>() {
            @Override
            public void onResponse(Call<AthleteDetailsResponseModel> call, Response<AthleteDetailsResponseModel> response) {
                if (response.body() != null) {
                    String description = response.body().getContent().getDecription();
                    String name = response.body().getContent().getName();
                    String email = response.body().getContent().getEmail();
                    String mobilno = response.body().getContent().getPhone();
                    SEX = response.body().getContent().getSex();
                    String address = response.body().getContent().getAddress();
                    String photoUrl = response.body().getContent().getPhotoUrl();
                    String age = response.body().getContent().getAge();
                    String profileUrl = response.body().getContent().getProfileURL();

                    // displaying image using picaso lib
                    Picasso.with(AthleteProfileUpdateActivity.this)
                            .load( GlobalConstants.getPhotoPathAthelete() + photoUrl)
                            .placeholder(R.drawable.athlete_image)
                            .into(Iv_profile);


                    et_full_name.setText(name);
                    et_address.setText(address);
                    et_age.setText(age);
                    et_email.setText(email);
                    et_mobile.setText(mobilno);
                    et_description.setText(description);
                    et_profileUrl.setText(profileUrl);

                    // gender radio button set
                    radiofamily.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {

                            if (checkedId == R.id.MALE) {
                                SEX = "M";

                            } else if (checkedId == R.id.FEMALE) {
                                SEX = "F";
                            }

                        }
                    });

                    if (SEX != null) {
                        if (SEX.equals("M")) {
                            radiofamily.check(R.id.MALE);
                        } else if (SEX.equals("F")) {
                            radiofamily.check(R.id.FEMALE);
                        }
                    }
                    sharePhoto = String.valueOf(response.body().getContent().getShareParmissionPhoto());
                    sharePhoto = String.valueOf(response.body().getContent().getSharePermissionVoice());
                    RadioButton Yes = (RadioButton) findViewById(R.id.yesSharePhoto);
                    RadioButton no = (RadioButton) findViewById(R.id.noSharePhoto);
                    radiofamilySharePhoto.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {

                            if (checkedId == R.id.yesSharePhoto) {
                                //do work when radioButton2 is active
                                sharePhoto = "true";

                            } else if (checkedId == R.id.noSharePhoto) {
                                //do work when radioButton3 is active
                                sharePhoto = "false";
                            }

                        }
                    });

                    if (sharePhoto != null) {
                        if (sharePhoto.equals("true")) {
                            radiofamilySharePhoto.check(R.id.yesSharePhoto);
                        } else if (sharePhoto.equals("false")) {
                            radiofamilySharePhoto.check(R.id.noSharePhoto);
                        }
                    }

                    RadioButton Yesvoice = (RadioButton) findViewById(R.id.yesSharePhoto);
                    RadioButton novoice = (RadioButton) findViewById(R.id.noShareVoice);
                    radiofamilyShareVoice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {

                            if (checkedId == R.id.yesShareVoice) {
                                //do work when radioButton2 is active
                                shareVoice = "true";

                            } else if (checkedId == R.id.noShareVoice) {
                                //do work when radioButton3 is active
                                shareVoice = "false";
                            }

                        }
                    });

                    if (shareVoice != null) {
                        if (shareVoice.equals("true")) {
                            radiofamilyShareVoice.check(R.id.yesShareVoice);
                        } else if (shareVoice.equals("false")) {
                            radiofamilyShareVoice.check(R.id.noShareVoice);
                        }
                    }

                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<AthleteDetailsResponseModel> call, Throwable t) {
                Log.i("===ErrorResponse", t.getMessage().toString());
//                Toast.makeText(AdditionalDetails.this, "Error" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });
    }


    // updating profile detail API call
    public void UpdateAthleteDetails() throws IOException {
        final ProgressDialog dialog;
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCanceledOnTouchOutside(false);
     //   dialog.show();

        JsonObject jsonObj = new JsonObject();
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String dateToStr = format.format(today);
        jsonObj.addProperty("athJoingDate", dateToStr);
        jsonObj.addProperty("profileType", "Self");
        jsonObj.addProperty("profileURL", et_profileUrl.getText().toString());
        jsonObj.addProperty("name", et_full_name.getText().toString());
        jsonObj.addProperty("address", et_address.getText().toString());
        jsonObj.addProperty("phone", et_mobile.getText().toString());
        jsonObj.addProperty("email", et_email.getText().toString());
        jsonObj.addProperty("age", et_age.getText().toString());
        jsonObj.addProperty("sex", SEX);
        jsonObj.addProperty("photoUrl", et_profileUrl.getText().toString());
        jsonObj.addProperty("decription", et_description.getText().toString());
        jsonObj.addProperty("shareParmissionPhoto", sharePhoto);
        jsonObj.addProperty("sharePermissionVoice", shareVoice);
//        jsonObj.addProperty("acceptPolicy", "true");
        String AthleteId = SharedPref.read(SharedPref.ATH_USER_ID, "");

        Call<JsonObject> call = mAPIService.UpdateAthleteProfile(AthleteId, jsonObj);     //getStringScalar(name, age, phone, email);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.i("===Response", String.valueOf(response.body()));
//                Toast.makeText(AdditionalDetails.this, "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
                if (response.body() != null) {

                    if (response.isSuccessful()) {
                        ShowSuccessDialog();

                    }
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.i("===ErrorResponse", t.getMessage().toString());
//                Toast.makeText(AdditionalDetails.this, "Error" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();

                dialog.dismiss();

            }
        });

    }

    public void UploadProfilePhoto() throws IOException {
        final ProgressDialog dialog;
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCanceledOnTouchOutside(false);
   //     dialog.show();
        String AthleteId = SharedPref.read(SharedPref.ATH_USER_ID, "");
        //pass file path local
        File file = new File(String.valueOf(filePath));
        Log.i("==FilePath", "File Path : " + String.valueOf(filePath));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        body = MultipartBody.Part.createFormData("uploadingFiles", file.getName(), requestFile);

        // add another part within the multipart request
        RequestBody aId = RequestBody.create(MediaType.parse("multipart/form-data"), AthleteId);

        Call<ResponseBody> call = mAPIService.updateProfilePhoto(aId, body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    String JsonRes = String.valueOf(response.body());
                    Log.i("==SaveRes", String.valueOf(response.body()));
                    if (response.isSuccessful()) {
                        //  ShowSuccessDialog();
                        Log.i("===FileSize", String.valueOf(filePath.length() / 1024 + " kb"));
                        Log.i("===FilePath", String.valueOf(filePath));
                    } else {
                    }

                    dialog.dismiss();
                } else {
                    Log.i("==errnull", "resnull");
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("===ErrorResponse", t.getMessage().toString());

                dialog.dismiss();

            }
        });


    }


    // show success dialog
    private void ShowSuccessDialog() {
        mDialog = new Dialog(this, R.style.AppBaseTheme);
        mDialog.setContentView(R.layout.activity_success);
        TextView tv_sub_title = mDialog.findViewById(R.id.tv_sub_title);
        tv_sub_title.setText("Profile Updated Successfully.!");
        mDialog.show();

        handlerOne = new Handler();
        runnableOne = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                startActivity(intent);
                handlerOne.postDelayed(this, 3000);
                handlerOne.removeCallbacks(runnableOne);
                mDialog.dismiss();
                finish();

            }
        };

        handlerOne.postDelayed(runnableOne, 3000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(AthleteProfileUpdateActivity.this, "Permission denied !", Toast.LENGTH_SHORT).show();
                }
                return;
            }


            // other 'case' lines to check for other
            // permissions this app might request

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Uri imageUri = getPickImageResultUri(data);
            releCrop.setVisibility(View.VISIBLE);
            llMain.setVisibility(View.GONE);
            boolean requirePermissions = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    isUriRequiresPermissions(imageUri)) {
                requirePermissions = true;
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CAMERA);
            }

            if (!requirePermissions) {
                //mCropImageView.setImageUriAsync(imageUri);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(AthleteProfileUpdateActivity.this.getResources(), bitmap);
                    circularBitmapDrawable.setCircular(true);
                    mCropImageView.setImageBitmap(circularBitmapDrawable.getBitmap());
//                    mCropImageView.rotateImage(90);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (resultCode == 0) {
            releCrop.setVisibility(View.GONE);
            llMain.setVisibility(View.VISIBLE);
        }

    }

    /*
     * Create a chooser intent to select the  source to get image from.
     * The source can be camera's  (ACTION_IMAGE_CAPTURE) or gallery's
     * (ACTION_GET_CONTENT).
     * All possible sources are added to the intent chooser.
     */
    public Intent getPickImageChooserIntent() {
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    /*
        Get URI to image received from capture  by camera.
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }

    /*
     * Get the URI of the selected image from
     * getPickImageChooserIntent().
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the  activity result
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }


    /**
     * Test if we can open the given Android URI to test if permission
     * required error is thrown.
     * Only relevant for API version 23 and above.
     *
     * @param uri the result URI of image pick.
     */
    public boolean isUriRequiresPermissions(Uri uri) {
        try {
            ContentResolver resolver = getContentResolver();
            InputStream stream = resolver.openInputStream(uri);
            stream.close();
            return false;
        } catch (FileNotFoundException e) {
            if (e.getCause() instanceof ErrnoException) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    /*
       Temporary storage of user selected profile picture in
       user's device. This file's path will be used to send image file
       to server.

       @param bitmap selected by user.
    */
    public void saveIntoFile(Bitmap bitmap) throws IOException {

        String AthleteId = SharedPref.read(SharedPref.ATH_USER_ID, "");
        Random random = new Random();
        int ii = 100000;
        ii = random.nextInt(ii);
        String fname = "000_" + AthleteId + ".jpg";
//        String fname = "MyPic1.jpg";
        File direct = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fname);

        if (!direct.exists()) {
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/");
            wallpaperDirectory.mkdirs();
        }

        File mainfile = new File(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"), fname);
        if (mainfile.exists()) {
            mainfile.delete();
        }

        FileOutputStream fileOutputStream;
        fileOutputStream = new FileOutputStream(mainfile);

        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        filePath = new File(mainfile.toString());
        Log.i("===FilePath", "File path is " + filePath.toString());

    }

    private void convertImageToBase64(Bitmap bm) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 50, baos); //bm is the bitmap object
        byte[] byteArrayImage = baos.toByteArray();
        profileImageBase64 = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        //Log.i("==Base64",profileImageBase64);

    }
}

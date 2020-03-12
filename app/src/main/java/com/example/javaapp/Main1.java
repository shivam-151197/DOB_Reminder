package com.example.javaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Main1<i, size> extends AppCompatActivity {
    TextView Name, Phone, DOB, Email,  name1, date1, name2, date2,name3, date3,name4, date4, name5, date5;
    FirebaseAuth fAuth;
    ImageView imageView3;
    Button btm;
    FirebaseFirestore fstore;
    String userid;
    int TAKE_ACTIVITY_IMAGE = 10001;
    private static final String TAG = "Main";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        Name = findViewById(R.id.Name_dis);
        Phone = findViewById(R.id.phone_dis);
        imageView3 = findViewById(R.id.imageView3);
        DOB = findViewById(R.id.DOB_dis);
        btm = findViewById(R.id.Logout);
        name1 = findViewById(R.id.det1_name);
        date1 =findViewById(R.id.det1_date);

        name2 = findViewById(R.id.det2_name);
        date2 =findViewById(R.id.det2_date);

        name3 = findViewById(R.id.det3_name);
        date3 =findViewById(R.id.det3_date);

        /*name4 = findViewById(R.id.det4_name);
        date4 =findViewById(R.id.det4_date);

        name5 = findViewById(R.id.det5_name);
        date5 =findViewById(R.id.det5_date);*/

        Email = findViewById(R.id.email_dis);
        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        userid = fAuth.getCurrentUser().getUid();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user.getPhotoUrl() != null){
            Glide.with(this).load(user.getPhotoUrl()).into(imageView3);
        }
        DocumentReference dR = fstore.collection("users").document(userid);
        dR.addSnapshotListener(Main1.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Phone.setText(documentSnapshot.getString("phone"));
                Name.setText(documentSnapshot.getString("fname"));
                DOB.setText(documentSnapshot.getString("DOB"));
                Email.setText(documentSnapshot.getString("email"));
            }
        });

        final HashMap<Date, String> map = new HashMap<>();
        final HashMap<Long, Date>map2 = new HashMap<>();
        final DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        final Date new1 = new Date();
        df.format((new1));
        Task<QuerySnapshot> documentReference = fstore.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot d : list) {
                        try {
                            Date date = new SimpleDateFormat("MM/dd/yyyy").parse(d.getString("DOB"));
                            date.setYear(new1.getYear());
                            long diffInMillies = (date.getTime() - new1.getTime());
                            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                            //System.out.println(diff);

                            if(diffInMillies > 0){
                                map2.put(diff, date);
                                map.put(date, d.getString("fname"));
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    ArrayList<Long> sortedKeys = new ArrayList<Long>(map2.keySet());
                    Collections.sort(sortedKeys);
                    int size = sortedKeys.size();
                    int i = 0;
                    if(size >= 1) {
                        name1.setText(map.get(map2.get(sortedKeys.get(i))).toString());
                        date1.setText(df.format(map2.get(sortedKeys.get(i))).toString());
                        i += 1;
                    }
                    if(size >= 2) {
                        name2.setText(map.get(map2.get(sortedKeys.get(i))).toString());
                        date2.setText(df.format(map2.get(sortedKeys.get(i))).toString());
                        i += 1;
                    }if(size >= 3) {
                        name3.setText(map.get(map2.get(sortedKeys.get(i))).toString());
                        date3.setText(df.format(map2.get(sortedKeys.get(i))).toString());
                        i += 1;
                    }/*if(size >= 4) {
                        name4.setText(map.get(map2.get(sortedKeys.get(i))).toString().trim());
                        date4.setText(df.format(map2.get(sortedKeys.get(i))).toString());
                        i+=1;
                    }
                    if(size >= 5) {
                        name5.setText(map.get(map2.get(sortedKeys.get(i))).toString());
                        date5.setText(df.format(map2.get(sortedKeys.get(i))).toString());
                    }*/
                }
            }
        });
        btm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                startActivity(new Intent(getApplicationContext(), login.class));
            }
        });

    }

    public void imgClick(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, TAKE_ACTIVITY_IMAGE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TAKE_ACTIVITY_IMAGE){
            switch (resultCode){
                case RESULT_OK:
                    Bitmap bitmap = (Bitmap)data.getExtras().get("data");
                    imageView3.setImageBitmap(bitmap);
                    handleUpload(bitmap);
            }
        }
    }

    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bs);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference ref = FirebaseStorage.getInstance().getReference().child("profileImages").child(uid+".jpeg");
        ref.putBytes(bs.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "onSuccess: "+uri);
                        setUserProfileUrl(uri);
                    }
                });
            }
            private  void setUserProfileUrl(Uri uri){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().
                        setPhotoUri(uri).build();
                user.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Main1.this, "Profile Picture Uploaded Successfully..", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Main1.this, "Profile image Upload Failed..", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"onFailure: ", e.getCause());
            }
        });
    }
}

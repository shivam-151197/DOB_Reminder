package com.example.javaapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class signup extends AppCompatActivity {
    private EditText Fullname;
    private EditText email;
    private EditText password;
    private EditText password2;
    private EditText phone;
    private EditText dob;
    private String gend;
    private RadioGroup rg;
    private FirebaseAuth Auth;
    private FirebaseFirestore fstore;
    private String userid;
    private ProgressBar progressBar;
    private Button btn,gnddr;
    private static final String TAG = "signup";
    private DatePickerDialog.OnDateSetListener mdl;
    @SuppressLint({"ClickableViewAccessibility", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Fullname = (EditText) findViewById(R.id.Fname);
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.pass1);
        password2 = (EditText)findViewById(R.id.pass2);
        phone = (EditText)findViewById(R.id.phn);
        dob = (EditText)findViewById(R.id.dob);
        btn = findViewById(R.id.button2);
        Auth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        fstore = FirebaseFirestore.getInstance();

        rg = findViewById(R.id.gndr);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup Group, int checkedid) {
                RadioButton gender = findViewById(checkedid);
                gend = gender.getText().toString();

            }
        });
        dob.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int day = c.get(DAY_OF_MONTH);
                int month = c.get(MONTH);
                int year = c.get(YEAR);
                DatePickerDialog date = new DatePickerDialog(signup.this,
                        android.R.style.Theme_DeviceDefault_Dialog_MinWidth, mdl, year, month, day);
                date.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLUE));
                date.show();
            }
        });

        mdl = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month += 1;
                Log.d(TAG, "onDateSet: mm/dd/yy: " + month + "/" + day + "/" + year);
                String dat = month + "/" + day + "/" + year;
                dob.setText(dat);
                }
            };

        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final String lemail = email.getText().toString().trim();
                final String password1 = password.getText().toString().trim();
                final String fullname = Fullname.getText().toString().trim();
                final String password21 = password2.getText().toString().trim();
                final String phn = phone.getText().toString().trim();
                final String date = dob.getText().toString().trim();
                //System.out.println(lemail + password1 + date + phn + fullname + gend);
                if (Auth.getCurrentUser() != null) {
                        startActivity(new Intent(getApplicationContext(), Main1.class));
                        finish();
                }

                if (TextUtils.isEmpty(lemail)) {
                    email.setError("Email is Mandatory");
                    return;
                }
                if (TextUtils.isEmpty(password1)) {
                    password.setError("Password is Required");
                    return;
                }
                if (password1.length() < 6) {
                    password.setError("Password Length Must be greater than 6");
                    return;
                }
                if (!(password1.equals(password21))) {
                    password2.setError("Password Does not Match");
                    return;
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    Auth.createUserWithEmailAndPassword(lemail, password1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(signup.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                                userid = Auth.getCurrentUser().getUid();
                                DocumentReference documentReference = fstore.collection("users").document(userid);
                                Map<String, Object> user = new HashMap<>();
                                user.put("fname", fullname);
                                user.put("email", lemail);
                                user.put("phone", phn);
                                user.put("DOB", date);
                                user.put("Gender", gend);
                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>(){
                                    @Override
                                    public void onSuccess(Void aVoid){
                                        Log.d(TAG,"onSuccess: User Profile is saved Successfully"+ userid);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "OnFailure: "+e.toString());
                                    }
                                });
                                startActivity(new Intent(getApplicationContext(), login.class));
                            } else {
                                Toast.makeText(signup.this, "Error !.", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                }
            }
        });
    }

}

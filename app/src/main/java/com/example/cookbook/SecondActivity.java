package com.example.cookbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SecondActivity extends AppCompatActivity {
    EditText email,pswd,cpswd;
    MaterialButton regbtn;
    String emailPattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        email=findViewById(R.id.email);
        pswd=findViewById(R.id.pswd);
        cpswd=findViewById(R.id.cpswd);
        regbtn=findViewById(R.id.regbtn);
        progressDialog=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();

        EditText un=(EditText) findViewById(R.id.un);
        MaterialButton regbtn=(MaterialButton) findViewById(R.id.regbtn);
        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String un1=un.getText().toString();
                Toast.makeText(SecondActivity.this,"Username is "+un1,Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(SecondActivity.this,MainActivity2.class));
            }
        });
        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PerforAuth();
            }
        });
    }

    private void PerforAuth() {
        String semail=email.getText().toString();
        String spswd=pswd.getText().toString();
        String scpswd=cpswd.getText().toString();


        if(!semail.matches(emailPattern))
        {
            email.setError("Enter Email");
        }else if(spswd.isEmpty()||spswd.length()<6)
        {
            pswd.setError("Enter proper password");
        } else if (!spswd.equals(scpswd)) {
            cpswd.setError("Password does not");
        } else {
            progressDialog.setMessage("Please wait while Registration...");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(semail,spswd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        progressDialog.dismiss();
                        sendUserToNextActivity();
                        Toast.makeText(SecondActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    }else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(SecondActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                    }


                }
            });


        }
    }

    private void sendUserToNextActivity() {
        Intent intent=new Intent(SecondActivity.this,MainActivity2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
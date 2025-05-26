package com.example.smilejobportal.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.util.Linkify;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smilejobportal.R;

public class SuccessActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        TextView candidateDetailsTextView;

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_success);

//        getWindow().setLayout(
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT
//        );
//        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        getWindow().setDimAmount(0.5f);
//        new Handler().postDelayed(() -> {
//            finish();
//        }, 4000);
//    }

        candidateDetailsTextView = findViewById(R.id.candidateDetailsTextView);

        String fullName = getIntent().getStringExtra("fullName");
        String email = getIntent().getStringExtra("email");
        String contact = getIntent().getStringExtra("contact");
        String companyName = getIntent().getStringExtra("companyName");
        String positionName = getIntent().getStringExtra("positionName");
//        String resumeLink = getIntent().getStringExtra("resumeLink");

        String details = "Details : \n\n\n"+"👤 Name: " + fullName + "\n\n" +
                "📧 Email: " + email + "\n\n" +
                "📞 Contact: " + contact + "\n\n" +
                "🏢 Company: " + companyName + "\n\n" +
                "💼 Position: " + positionName + "\n\n" ;

        candidateDetailsTextView.setText(details);
        candidateDetailsTextView.setAutoLinkMask(Linkify.WEB_URLS);

        findViewById(R.id.backHomeButton).setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK))
        );

    }
}
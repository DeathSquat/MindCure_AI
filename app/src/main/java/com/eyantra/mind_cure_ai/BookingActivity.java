package com.eyantra.mind_cure_ai;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;
import android.view.LayoutInflater;

public class BookingActivity extends AppCompatActivity {
    private LinearLayout doctorsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Override default activity transition
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            getSupportActionBar().setTitle("Book a Session");
        }

        doctorsContainer = findViewById(R.id.doctorsContainer);
        showDoctors();
    }

    private void showDoctors() {
        Doctor[] doctors = {
            new Doctor(
                "Dr. B.L Dhaka",
                "Senior Psychiatrist",
                "15+ years",
                "General Psychiatry, Anxiety Disorders, Depression",
                "Available: Mon-Fri, 10 AM - 7 PM",
                "₹800/session",
                4.8f,
                R.drawable.doctor1
            ),
            new Doctor(
                "Dr. Sumit Kumar Gupta",
                "Addiction Psychiatrist",
                "12+ years",
                "Substance Abuse, Behavioral Addictions, Recovery Support",
                "Available: Tue-Sat, 11 AM - 8 PM",
                "₹1000/session",
                4.9f,
                R.drawable.doctor2
            ),
            new Doctor(
                "Dr. Rahul Mehta",
                "Clinical Psychologist",
                "10+ years",
                "Cognitive Behavioral Therapy, Trauma, Relationship Issues",
                "Available: Mon-Thu, 9 AM - 6 PM",
                "₹900/session",
                4.7f,
                R.drawable.doctor3
            ),
            new Doctor(
                "Dr. Neha Verma",
                "Mental Health Specialist",
                "8+ years",
                "Stress Management, Work-Life Balance, Personal Growth",
                "Available: Wed-Sun, 10 AM - 7 PM",
                "₹750/session",
                4.6f,
                R.drawable.doctor4
            ),
            new Doctor(
                "Dr. Rajesh Kumar",
                "Psychiatrist",
                "20+ years",
                "Mood Disorders, Sleep Problems, Psychiatric Medications",
                "Available: Mon-Sat, 9 AM - 8 PM",
                "₹1200/session",
                4.9f,
                R.drawable.doctor1
            ),
            new Doctor(
                "Dr. Ankit Gupta",
                "Consultant Psychiatrist",
                "10+ years",
                "Anxiety, Depression, OCD, ADHD",
                "Available: Tue-Sat, 11 AM - 7 PM",
                "₹950/session",
                4.8f,
                R.drawable.doctor2
            )
        };

        for (Doctor doctor : doctors) {
            View doctorCard = createDoctorCard(doctor);
            doctorsContainer.addView(doctorCard);
        }
    }

    private View createDoctorCard(Doctor doctor) {
        View card = LayoutInflater.from(this).inflate(R.layout.doctor_card, null);
        
        TextView nameText = card.findViewById(R.id.doctorName);
        TextView titleText = card.findViewById(R.id.doctorTitle);
        TextView experienceText = card.findViewById(R.id.doctorExperience);
        TextView specializationText = card.findViewById(R.id.doctorSpecialization);
        TextView availabilityText = card.findViewById(R.id.doctorAvailability);
        TextView priceText = card.findViewById(R.id.doctorPrice);
        Button bookButton = card.findViewById(R.id.bookButton);

        nameText.setText(doctor.name);
        titleText.setText(doctor.title);
        experienceText.setText("Experience: " + doctor.experience);
        specializationText.setText("Specialization: " + doctor.specialization);
        availabilityText.setText(doctor.availability);
        priceText.setText(doctor.price);

        bookButton.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + doctor.phoneNumber));
            startActivity(callIntent);
        });

        return card;
    }

    private static class Doctor {
        String name;
        String title;
        String experience;
        String specialization;
        String availability;
        String price;
        float rating;
        int imageResId;
        String phoneNumber;

        Doctor(String name, String title, String experience, String specialization, 
               String availability, String price, float rating, int imageResId) {
            this.name = name;
            this.title = title;
            this.experience = experience;
            this.specialization = specialization;
            this.availability = availability;
            this.price = price;
            this.rating = rating;
            this.imageResId = imageResId;
            
            // Set phone numbers based on the provided list
            switch (name) {
                case "Dr. B.L Dhaka":
                    this.phoneNumber = "9887083066";
                    break;
                case "Dr. Sumit Kumar Gupta":
                    this.phoneNumber = "1140036735";
                    break;
                case "Dr. Rahul Mehta":
                    this.phoneNumber = "8527676692";
                    break;
                case "Dr. Neha Verma":
                    this.phoneNumber = "8046961837";
                    break;
                case "Dr. Rajesh Kumar":
                    this.phoneNumber = "1141168734";
                    break;
                case "Dr. Ankit Gupta":
                    this.phoneNumber = "7042511847";
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

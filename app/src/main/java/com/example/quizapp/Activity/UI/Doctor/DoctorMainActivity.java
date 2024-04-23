package com.example.quizapp.Activity.UI.Doctor;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.quizapp.Activity.UI.Doctor.Fragment.ExamsFragment;
import com.example.quizapp.Activity.UI.Doctor.Fragment.HomeFragment;
import com.example.quizapp.Activity.UI.Doctor.Fragment.ProfileFragment;
import com.example.quizapp.R;
import com.example.quizapp.databinding.ActivityDoctorMainBinding;

public class DoctorMainActivity extends AppCompatActivity {

    ActivityDoctorMainBinding binding;

    HomeFragment homeFragment = new HomeFragment();
    ExamsFragment examsFragment = new ExamsFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.exam));
        binding.bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.home));
        binding.bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.user));

        meowNavigation();
        binding.bottomNavigation.show(2, true);
        getHomePage();

    }

    void meowNavigation() {
        binding.bottomNavigation.setOnClickMenuListener(model -> {
            switch (model.getId()) {
                case 1:
                    getExamsPage();
                    return null;
                case 2:
                    getHomePage();
                    return null;
                case 3:
                    getProfilePage();
                    return null;
            }
            return null;
        });
    }

    void getHomePage() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
    }

    void getExamsPage() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, examsFragment).commit();
    }

    void getProfilePage() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();
    }

}
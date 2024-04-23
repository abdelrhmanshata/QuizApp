package com.example.quizapp.Activity.UI.Student;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.quizapp.Activity.UI.Student.Fragment.StudentDegreesFragment;
import com.example.quizapp.Activity.UI.Student.Fragment.StudentHomeFragment;
import com.example.quizapp.Activity.UI.Student.Fragment.StudentProfileFragment;
import com.example.quizapp.R;
import com.example.quizapp.databinding.ActivityStudentMainBinding;

public class StudentMainActivity extends AppCompatActivity {

    ActivityStudentMainBinding binding;
    StudentHomeFragment homeFragment = new StudentHomeFragment();
    StudentDegreesFragment degreesFragment = new StudentDegreesFragment();
    StudentProfileFragment profileFragment = new StudentProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentMainBinding.inflate(getLayoutInflater());
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
                    getDegreesPage();
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

    void getDegreesPage() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, degreesFragment).commit();
    }

    void getProfilePage() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();
    }
}
package com.example.diplom;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.diplom.authorisation.User;
import com.example.diplom.reader.PdfReaderFragment;
import com.example.navdrav.R;
import com.example.navdrav.databinding.NavHeaderMainBinding;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.navdrav.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private PdfReaderFragment pdfReaderFragment;

    private static final int REQUEST_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.navdrav.databinding.ActivityMainBinding binding =
                ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());//Установка корневого элемента для отображения

        //заполнение заголовка бокового меню
        com.example.navdrav.databinding.NavHeaderMainBinding navHeaderMainBinding =
                NavHeaderMainBinding.inflate(getLayoutInflater());

        setSupportActionBar(binding.appBarMain.toolbar);//установка toolBar в интерфейс

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        //Создание бокового меню
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_news, R.id.nav_schedule, R.id.nav_autorisation,
                R.id.nav_pdf_reader)
                .setOpenableLayout(drawer)
                .build();
        //Настройка ActionBar
        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController,
                mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    public void checkPermission(PdfReaderFragment pdfReaderFragment) {
        this.pdfReaderFragment = pdfReaderFragment;
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            this.pdfReaderFragment.initViews(0);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_PERMISSION);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    this.pdfReaderFragment.initViews(0);
                } else {
// в разрешении отказано (в первый раз, когда чекбокс "Больше не спрашивать" ещё не показывается)
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        this.pdfReaderFragment.initViews(1);
                    } else {
                        this.pdfReaderFragment.initViews(1);
                    }
                }
                break;
            }
        }

    }

    @Override
    protected void onStart() {
        initializeHeader();
        super.onStart();
    }

    private void initializeHeader() {
        //Загружаем данные о пользователе
        User user = new User();
        user.load(getApplicationContext());

        //получаем данные о элементах интерфейса в заголовке
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView inst = (TextView) headerView.findViewById(R.id.nav_head_inst_box);
        TextView profile = (TextView) headerView.findViewById(R.id.nav_head_napr_box);
        TextView napr = (TextView) headerView.findViewById(R.id.nav_head_naprv_box);
        TextView course = (TextView) headerView.findViewById(R.id.nav_head_course_box);

        //устанавливаем значения о нём в заголовок меню
        inst.setText(user.getInstitute());
        profile.setText(user.getProfile());
        napr.setText(user.getNapravlennost());
        course.setText(user.getCourse());

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
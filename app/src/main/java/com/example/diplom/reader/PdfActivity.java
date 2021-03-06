package com.example.diplom.reader;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.navdrav.R;

import java.io.File;
import java.io.IOException;

public class PdfActivity extends AppCompatActivity implements View.OnClickListener {

    private String CURRENT_PAGE;

    boolean err;
    private String path;
    private ImageView imgView;
    private Button btnPrevious, btnNext;
    private int currentPage = 0;
    private ImageButton btn_zoomin, btn_zoomout;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page curPage;
    private ParcelFileDescriptor descriptor;
    private float currentZoomLevel = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        err = false;
        path = getIntent().getStringExtra("fileName");
        setTitle(getIntent().getStringExtra("keyName"));
        // если в банлде есть номер страницы - забираем его
        if (savedInstanceState != null) {
            currentPage = savedInstanceState.getInt(CURRENT_PAGE, 0);
        }
        imgView = findViewById(R.id.imgView);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btn_zoomin = findViewById(R.id.zoomin);
        btn_zoomout = findViewById(R.id.zoomout);
        // устанавливаем слушатели на кнопки
        btnPrevious.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btn_zoomin.setOnClickListener(this);
        btn_zoomout.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {//пробуем запустить отображение
            openPdfRenderer();
            displayPage(currentPage);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "PDF-файл защищен паролем или повреждён"
                    , Toast.LENGTH_SHORT).show();
        }
    }

    private void openPdfRenderer() {
        File file = new File(path);
        if (file.length() > 0) {
            descriptor = null;
            pdfRenderer = null;
            try {
                descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
                pdfRenderer = new PdfRenderer(descriptor);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Ошибка чтения файла", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Не удаётся прочесть файл", Toast.LENGTH_LONG).show();
            finish();
        }
    }


    private void displayPage(int index) {
        if (pdfRenderer != null) {
            if (pdfRenderer.getPageCount() <= index) return;
            // закрываем текущую страницу
            if (curPage != null) curPage.close();
            // открываем нужную страницу
            curPage = pdfRenderer.openPage(index);
            // определяем размеры Bitmap
            int newWidth = (int) (getResources().getDisplayMetrics()
                    .widthPixels * curPage.getWidth() / 72
                    * currentZoomLevel / 40);
            int newHeight =
                    (int) (getResources().getDisplayMetrics()
                            .heightPixels * curPage.getHeight() / 72
                            * currentZoomLevel / 64);
            Bitmap bitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
            Matrix matrix = new Matrix();
            float dpiAdjustedZoomLevel = currentZoomLevel * DisplayMetrics.DENSITY_MEDIUM
                    / getResources().getDisplayMetrics().densityDpi;
            matrix.setScale(dpiAdjustedZoomLevel, dpiAdjustedZoomLevel);

            curPage.render(bitmap, null, matrix, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            // отображаем результат рендера
            imgView.setImageBitmap(bitmap);
            // проверяем, нужно ли делать кнопки недоступными
            int pageCount = pdfRenderer.getPageCount();
            btnPrevious.setEnabled(0 != index);
            btnNext.setEnabled(index + 1 < pageCount);
            btn_zoomout.setEnabled(currentZoomLevel != 2);
            btn_zoomin.setEnabled(currentZoomLevel != 12);
        }
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        if (!err) {
            switch (v.getId()) {
                case R.id.btnPrevious: {
                    // получаем индекс предыдущей страницы
                    int index = 0;

                    index = curPage.getIndex() - 1;

                    displayPage(index);
                    break;
                }
                case R.id.btnNext: {
                    // получаем индекс следующей страницы
                    int index = curPage.getIndex() + 1;
                    displayPage(index);
                    break;
                }
                case R.id.zoomout: {
                    // уменьшаем зум
                    --currentZoomLevel;
                    displayPage(curPage.getIndex());
                    break;
                }
                case R.id.zoomin: {
                    // увеличиваем зум
                    ++currentZoomLevel;
                    displayPage(curPage.getIndex());
                    break;
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        if (curPage != null) {
            outState.putInt(CURRENT_PAGE, curPage.getIndex());
        }
    }

    @Override
    public void onStop() {

        try {
            closePdfRenderer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    private void closePdfRenderer() throws IOException {
        if (curPage != null) curPage.close();
        if (pdfRenderer != null) pdfRenderer.close();
        if (descriptor != null) descriptor.close();
    }
}
package com.example.diplom.reader;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.diplom.MainActivity;
import com.example.navdrav.R;
import com.example.navdrav.databinding.PdfActivityMainBinding;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

public class PdfReaderFragment extends Fragment {
    private ListView listView;
    private final ArrayList<PdfFile> list = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        com.example.navdrav.databinding.PdfActivityMainBinding binding =
                PdfActivityMainBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        listView = binding.listView;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ((MainActivity) requireActivity()).checkPermission(this);
        } else {
            initViews(0);
        }
        return root;
    }
    private final BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return list.size();
        }
        @Override
        public PdfFile getItem(int i) {
            return list.get(i);
        }
        @Override
        public long getItemId(int i) {
            return i;
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = view;
            if (v == null) {
                v = getLayoutInflater().inflate(R.layout.pdf_list_item, viewGroup, false);
            }
            PdfFile pdfFile = getItem(i);
            TextView name = v.findViewById(R.id.txtFileName);
            name.setText(pdfFile.getFileName());
            return v;
        }
    };

    public void initViews(int index) {
        // получаем путь до внешнего хранилища
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (index == 0) {
            initList(path);
            // устанавливаем адаптер в ListView
            listView.setAdapter(adapter);
            // когда пользователь выбирает PDF-файл из списка, открываем активность для просмотра
            listView.setOnItemClickListener((adapterView, view, i, l) -> {
                Intent intent = new Intent(requireContext(), PdfActivity.class);
                intent.putExtra("keyName", list.get(i).getFileName());
                intent.putExtra("fileName", list.get(i).getFilePath());
                startActivity(intent);
            });
        } else {
            listView.setAdapter(adapter);
            list.add(new PdfFile());
        }
    }
    private void initList(String path) {
        try {
            File file = new File(path);
            File[] fileList = file.listFiles();
            String fileName;
            if (fileList != null) {
                for (File f : fileList) {
                    if (f.isDirectory()) {
                        initList(f.getAbsolutePath());
                    } else {
                        fileName = f.getName();
                        if (fileName.endsWith(".pdf")) {
                            list.add(new PdfFile(fileName, f.getAbsolutePath()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package com.example.diplom.schedule;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.diplom.authorisation.User;
import com.example.diplom.schedule.ierarchy.TripleList;
import com.example.navdrav.R;
import com.example.navdrav.databinding.ActivityMainBinding;
import com.example.navdrav.databinding.FragmentScheduleBinding;
import com.example.navdrav.databinding.ReadScheduleLineBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ScheduleFragment extends Fragment {

    private FragmentScheduleBinding binding;
    private ReadScheduleLineBinding rslBinding;
    List<String> types;
    List<String> links;
    String hostname = "http://tt.vogu35.ru/files/";
    int a = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentScheduleBinding.inflate(inflater, container, false);
        com.example.navdrav.databinding.ActivityMainBinding activityMainBinding
                = ActivityMainBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //список различных типов расписания
        types = new ArrayList<String>();
        //список различных расписаний, соответсвующих типам
        links = new ArrayList<String>();

        //загрузка данных о пользователе
        User user = new User();
        user.load(requireContext());
        //инициализация визуальных компонент
        setHasOptionsMenu(false);
        ViewPager2 viewPager = binding.viewPager;
        TabLayout tabLayout = binding.tabs;
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        viewPager.setUserInputEnabled(false);
        //инициализация данных о расписаниях
        TripleList tl = user.getLinks();
        types = tl.getTypesByName(user.getCourse());
        links = tl.getValuesByName(user.getCourse());
        //установка адаптера для вкладок окна
        viewPager.setAdapter(new SchedulePageAdapter());
        //установка Медиатора для переключения отображаемого на экране вмместе со вкладкой
        TabLayoutMediator tli = new TabLayoutMediator(tabLayout,
                viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull @NotNull TabLayout.Tab tab, int position) {
                tab.setText(types.get(position));
            }
        });
        tli.attach();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class SchedulePageAdapter extends RecyclerView.Adapter<SchedulePageAdapter
            .SchedulePageAdapterHolder> {

        @NonNull
        @NotNull
        @Override
        public SchedulePageAdapterHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent,
                                                            int viewType) {
            SchedulePageAdapterHolder spah;
            return new SchedulePageAdapterHolder(getLayoutInflater().inflate(R.layout.schedule_page,
                    parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull SchedulePageAdapterHolder holder,
                                     int position) {

            holder.lookAtSchedule(position);
        }

        @Override
        public int getItemCount() {
            return types.size();
        }

        class SchedulePageAdapterHolder extends RecyclerView.ViewHolder {
           // EditText editText;
            WebView wv;
            FloatingActionButton downloadDoc;

            public SchedulePageAdapterHolder(@NonNull @NotNull View itemView) {
                super(itemView);

                //editText = itemView.findViewById(R.id.EdText);
                WebViewClient webViewClient = new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }

                    @TargetApi(Build.VERSION_CODES.N) @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                        view.loadUrl(request.getUrl().toString());
                        return true;
                    }

                    @Override public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        Toast.makeText(getContext(), "Страница загружена!", Toast.LENGTH_SHORT).show();
                    }

                    @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        Toast.makeText(getContext(), "Начата загрузка страницы", Toast.LENGTH_SHORT)
                                .show();
                    }
                };
                wv = itemView.findViewById(R.id.news_wv);
                wv.setWebViewClient(webViewClient);
            }

            @SuppressLint("SetJavaScriptEnabled")
            public void lookAtSchedule(int position) {
                    wv.getSettings().setJavaScriptEnabled(true);
                    wv.loadUrl("http://docs.google.com/viewer?url=" + links.get(position));
            }
        }
    }
}
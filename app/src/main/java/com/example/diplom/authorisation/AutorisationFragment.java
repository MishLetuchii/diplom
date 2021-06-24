package com.example.diplom.authorisation;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diplom.schedule.ierarchy.ListOfCouples;
import com.example.diplom.schedule.ierarchy.TripleList;

import com.example.navdrav.R;
import com.example.navdrav.databinding.FragmentAutorisationBinding;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class AutorisationFragment extends Fragment {

    private final String savePath = "settings.txt";
    private Handler handler;
    private String line = "";
    private String qualification = "";
    private String formObuch = "";
    private String suffix_q = "БАК|";
    private String suffix_f = "ЗУО|";
    private String suffix_i = "ИСИ|";
    private String institute = "";
    private String profile = "";
    private String napravlennost = "";
    private String course = "";


    private ListOfCouples instList = new ListOfCouples();
    private String[] institutes;
    private String[] profiles;
    private String[] napravlennostList;
    private String[] courseList;
    private String[] scheduleTypes;
    TripleList linksToSchedules = new TripleList();

    private final String[] qualificationList = {
            "Бакалавриат",
            "Прикладной Бакалавриат",
            "Магистратура",
            "Прикладная магистратура",
            "Специалитет",
            "Аспирантура"};

    private final String[] formObuchList = {
            "Очная",
            "Заочная",
            "Заочная (ускоренная)",
            "Очно-заочная"};


    private FragmentAutorisationBinding binding;

    private void refreshInstList(ListOfCouples loc) {
        instList = loc;
        saveInstList(requireActivity());
    }

    private void initializeSpinners() {
        institutes = instList.getValuesByName("Институт").toArray(new String[0]);
        setSpinner(binding.qualificationSpinner, qualificationList, statistics.QUALIFICATION);
        setSpinner(binding.teachingFormSpinner, formObuchList, statistics.FORM_OBUSCH);
        setSpinner(binding.instituteSpinner, institutes, statistics.INSTITUTE);
    }

    public AutorisationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAutorisationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        if (loadInstList(requireActivity())) {
            initializeSpinners();
        } else {
            Toast.makeText(getContext(),
                    "Загружаю данные с сайта, пожалуйста, подождите.",
                    Toast.LENGTH_LONG).show();
        }
        DownloadShedules();
        handler = new AutorisationFragment.MyHandler(this);

        Button doneBtn = binding.authorisationDone;
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSettings();
                linksToSchedules.getTypesByName("");

            }
        });

        return root;
    }

    private void structueRenew() {
        Toast.makeText(getContext(), "Структура обновлена", Toast.LENGTH_SHORT).show();
    }

    private void nothingIntresting() {
        Toast.makeText(getContext(), "Ничего нового", Toast.LENGTH_SHORT).show();
    }

    private void saveSettings() {
        if (!profile.equals("Нет направлений") || institute.equals("") || course.equals("")) {
            User us = new User();
            us.setQualification(qualification);
            us.setFormObuch(formObuch);
            us.setInstitute(institute);
            us.setProfile(profile);
            us.setNapravlennost(napravlennost);
            us.setCourse(course);
            us.setLinks(linksToSchedules);
            us.save(this.getActivity().getApplicationContext());
            NavigationView navigationView = (NavigationView) getActivity()
                    .findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0);
            TextView inst = (TextView) headerView.findViewById(R.id.nav_head_inst_box);
            TextView profile = (TextView) headerView.findViewById(R.id.nav_head_napr_box);
            TextView napr = (TextView) headerView.findViewById(R.id.nav_head_naprv_box);
            TextView course = (TextView) headerView.findViewById(R.id.nav_head_course_box);

            inst.setText(us.getInstitute());
            profile.setText(us.getProfile());
            napr.setText(us.getNapravlennost());
            course.setText(us.getCourse());


        } else {
            Toast.makeText(getContext(), "Невозможно сохранить", Toast.LENGTH_SHORT).show();
        }
    }


    private void setSpinner(Spinner spinner, String[] list, statistics marker) {
        ArrayAdapter<String> teachingFormAdapter =
                new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, list);
        teachingFormAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Выпадающее меню квалификации
        spinner.setAdapter(teachingFormAdapter);
        // заголовок
        spinner.setPrompt("Title");
        // выделяем элемент
        spinner.setSelection(0);
        // устанавливаем обработчик нажатия
        switch (marker) {
            case QUALIFICATION: {
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int position, long id) {
                        // показываем позиция нажатого элемента

                        switch (qualificationList[position]) {
                            case "Бакалавриат":
                                suffix_q = "БАК|";
                                break;
                            case "Прикладной Бакалавриат":
                                suffix_q = "ПБК|";
                                break;
                            case "Магистратура":
                                suffix_q = "МАГ|";
                                break;
                            case "Прикладная магистратура":
                                suffix_q = "ПМГ|";
                                break;
                            case "Специалитет":
                                suffix_q = "СПЦ|";
                                break;
                            case "Аспирантура":
                                suffix_q = "АСП|";
                                break;
                        }
                        qualification = qualificationList[position];
                        setProfileSpinner();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });
                break;
            }
            case FORM_OBUSCH: {
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int position, long id) {
                        // показываем позиция нажатого элемента

                        switch (formObuchList[position]) {
                            case "Очная":
                                suffix_f = "ОФО|";
                                break;
                            case "Заочная":
                                suffix_f = "ЗФО|";
                                break;
                            case "Заочная (ускоренная)":
                                suffix_f = "ЗУО|";
                                break;
                            case "Очно-заочная":
                                suffix_f = "ОЗО|";
                                break;
                        }
                        formObuch = formObuchList[position];
                        setProfileSpinner();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });
                break;
            }
            case INSTITUTE: {
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int position, long id) {
                        // показываем позиция нажатого элемента

                        institute = institutes[position];


                        switch (institute) {
                            case "Институт социальных и гуманитарных наук":
                                suffix_i = "СГН|";
                                break;
                            case "Институт культуры и туризма":
                                suffix_i = "ИКТ|";
                                break;
                            case "Институт математики, естественных и компьютерных наук":
                                suffix_i = "МЕК|";
                                break;
                            case "Институт машиностроения, энергетики и транспорта":
                                suffix_i = "МЭТ|";
                                break;
                            case "Институт педагогики, психологии и физического воспитания":
                                suffix_i = "ППФ|";
                                break;
                            case "Инженерно-строительный институт":
                                suffix_i = "ИСИ|";
                                break;
                            case "Институт управления, экономики и юриспруденции":
                                suffix_i = "УЭЮ|";
                                break;
                        }
                        setProfileSpinner();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });
                break;
            }
            case PROFILE: {
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int position, long id) {
                        // показываем позиция нажатого элемента

                        profile = profiles[position];

                        if (profile.equals("Нет направлений")) {

                            napravlennostList = new String[]{"Нет направлений"};
                        } else
                            napravlennostList =
                                    instList.getValuesByNameAndSuffix(
                                            getProfileSuffix() + profile,
                                            getProfileSuffix()).toArray(new String[0]);
                        setSpinner(binding.napravlennostSpinner,
                                napravlennostList, statistics.NAPRAVLENIE);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });
                break;
            }


            case NAPRAVLENIE: {
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int position, long id) {
                        // показываем позиция нажатого элемента

                        napravlennost = napravlennostList[position];

                        if (napravlennost.equals("Нет направлений")) {

                            courseList = new String[]{"Нет направлений"};
                        } else {
                            scheduleTypes = instList.getValuesByNameAndSuffix(
                                    getProfileSuffix() + napravlennost,
                                    getProfileSuffix() + napravlennost).toArray(new String[0]);


                            String[] cList = new String[0];
                            String[] cListLinks = new String[0];
                            linksToSchedules = new TripleList();
                            for (String t : scheduleTypes) {

                                cList = instList.getValuesByName
                                        ("з" + getProfileSuffix() + napravlennost + t)
                                        .toArray(new String[0]);
                                cListLinks = instList.getValuesByName
                                        ("с" + getProfileSuffix() + napravlennost + t)
                                        .toArray(new String[0]);

                                for (int a = 0; a < cList.length; a++) {
                                    linksToSchedules.newCouple(cList[a]);
                                    linksToSchedules.addValuesByName(cList[a], t, cListLinks[a]);
                                }

                            }
                            courseList = linksToSchedules.getNames().toArray(new String[0]);
                        }
                        setSpinner(binding.courseSpinner, courseList, statistics.COURSE);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });
                break;
            }


            case COURSE: {
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int position, long id) {
                        // показываем позиция нажатого элемента
                        course = courseList[position];
                        linksToSchedules.getTypesByName("");
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });
                break;
            }


        }
    }

    private void setProfileSpinner() {
        if (!institute.equals("")) {
            profiles = instList.getValuesByNameAndSuffix(institute,
                    getProfileSuffix()).toArray(new String[0]);
            if (profiles.length == 0) profiles = new String[]{"Нет направлений"};
            setSpinner(binding.napravlenieSpinner, profiles, statistics.PROFILE);

        }
    }

    private String getProfileSuffix() {
        return suffix_i + suffix_f + suffix_q;
    }

    private void DownloadShedules() {
        //Загрузка и парсинг доступных настроек
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                ListOfCouples loadedList = new ListOfCouples();

                String instituteSelector = "div.spoiler-title.facultet.closed";
                String naprSelector = "div.spoiler-body.facultet";
                try {

                    Document doc = Jsoup.connect("https://tt.vogu35.ru/")
                            .userAgent("Chrome/4.0.249.0 Safari/532.5")
                            .referrer("http://www.google.com")
                            .get();
                    Elements instituteNames = doc.select(instituteSelector);
                    Elements napravlenias = doc.select(naprSelector);


                    loadedList.newCouple("Институт");
                    for (int i = 0; i < instituteNames.size(); i++) {
                        if (!instituteNames.get(i).text().equals("")) {

                            loadedList.addValuesByName("Институт",
                                    instituteNames.get(i).text());
                            loadedList.newCouple(instituteNames.get(i).text());


                            Elements naprTitles = napravlenias.get(i).
                                    select("div.spoiler-title.napravlenie.closed");
                            Elements naprBodies = napravlenias.get(i).
                                    select("div.spoiler-body.napravlenie");
                            for (int j = 0; j < naprTitles.size(); j++) {


                                String nTitle = naprTitles.get(j).text();
                                List<String> profileTitleTextList = new ArrayList<String>();
                                Elements profiles = naprBodies.get(j).select("div.profil");

                                for (Element profile : profiles) {

                                    Elements profileTitles = profile.select("h3");

                                    for (Element profileTitle : profileTitles) {

                                        String profileTitleText = profileTitle.text();
                                        List<String> vidsValsList = new ArrayList<String>();

                                        Elements vids = profile.select(
                                                "div.profil> div.vid ");
                                        for (Element vid : vids) {
                                            Elements vidTitles = vid.select("h4");
                                            Elements vidBtns = vid.select("div.btns > a");

                                            for (Element vidTitle : vidTitles) {
                                                List<String> courseList = new ArrayList<String>();
                                                List<String> courseLinkList = new ArrayList<String>();
                                                for (Element btn : vidBtns) {
                                                    courseList.add(btn.text());
                                                    courseLinkList.add
                                                            (btn.attr("href"));
                                                    profileTitleText = checkQual(profileTitleText,
                                                            btn.attr("href"));
                                                    profileTitleText = checkFormOb(profileTitleText,
                                                            btn.attr("href"));
                                                    profileTitleText = addInstSuffix(profileTitleText,
                                                            instituteNames.get(i).text());
                                                    nTitle = checkQual
                                                            (nTitle, btn.attr("href"));
                                                    nTitle = checkFormOb
                                                            (nTitle, btn.attr("href"));
                                                    nTitle = addInstSuffix
                                                            (nTitle, instituteNames.get(i).text());
                                                }
                                                loadedList.addCouple("з"
                                                        + profileTitleText
                                                        + vidTitle.text(),
                                                        courseList);
                                                loadedList.addCouple("с"
                                                        + profileTitleText
                                                        + vidTitle.text(),
                                                        courseLinkList);
                                                vidsValsList.add(profileTitleText + vidTitle.text());
                                            }
                                        }
                                        loadedList.addCouple(profileTitleText, vidsValsList);
                                        profileTitleTextList.add(profileTitleText);
                                    }
                                }
                                loadedList.addValuesByName(instituteNames.get(i).text(), nTitle);
                                loadedList.addCouple(nTitle, profileTitleTextList);
                            }
                        }
                    }

                    Message msg;
                    if (!loadedList.equals(instList)) {
                        msg = handler.obtainMessage(0, loadedList);
                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        Log.d("Debug", "Thread done");
    }


    public void saveInstList(Context activity) {//сериализация

        try {
            File f = new File(activity.getFilesDir() + "/" + savePath);
            boolean a = f.delete();

            FileOutputStream output = activity.openFileOutput(savePath, Context.MODE_PRIVATE);
            ObjectOutputStream objOut = new ObjectOutputStream(output);
            objOut.writeObject(instList);

        } catch
        (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean loadInstList(Context activity) {

        //десериализация
        String pn = activity.getFilesDir() + "/" + savePath;
        File file = new File(pn);
        boolean res = false;
        if (file.exists()) {
            try {

                FileInputStream fileInput = activity.openFileInput(savePath);
                ObjectInputStream objInput = new ObjectInputStream(fileInput);
                BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(fileInput));
                while (bufferedReader.ready()) {
                    instList = ((ListOfCouples) objInput.readObject());
                }
                res = true;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return res;
    }


    private String addInstSuffix(String s, String institute) {
        if (s.contains("СГН|")) return s;
        else if (s.contains("ИКТ|")) return s;
        else if (s.contains("МЕК|")) return s;
        else if (s.contains("МЭТ|")) return s;
        else if (s.contains("ППФ|")) return s;
        else if (s.contains("ИСИ|")) return s;
        else if (s.contains("УЭЮ|")) return s;
        else if (institute.equals("Институт социальных и гуманитарных наук")) return "СГН|" + s;
        else if (institute.equals("Институт культуры и туризма")) return "ИКТ|" + s;
        else if (institute.equals("Институт математики, естественных и компьютерных наук"))
            return "МЕК|" + s;
        else if (institute.equals("Институт машиностроения, энергетики и транспорта"))
            return "МЭТ|" + s;
        else if (institute.equals("Институт педагогики, психологии и физического воспитания"))
            return "ППФ|" + s;
        else if (institute.equals("Инженерно-строительный институт")) return "ИСИ|" + s;
        else if (institute.equals("Институт управления, экономики и юриспруденции"))
            return "УЭЮ|" + s;
        else
            return s;
    }

    private String checkFormOb(String s, String ref) {
        if (s.contains("ОФО|")) return s;
        else if (s.contains("ЗФО|")) return s;
        else if (s.contains("ОЗО|")) return s;
        else if (s.contains("ЗУО|")) return s;
        else if (ref.contains("ofo")) return "ОФО|" + s;
        else if (ref.contains("zfo")) return "ЗФО|" + s;
        else if (ref.contains("ozfo")) return "ОЗО|" + s;
        else if (ref.contains("vqp")) return "ЗУО|" + s;
        else
            return s;
    }

    private String checkQual(String s, String ref) {
        if (s.contains("ПБК|")) return s;
        else if (s.contains("БАК|")) return s;
        else if (s.contains("ПМГ|")) return s;
        else if (s.contains("МАГ|")) return s;
        else if (s.contains("СПЦ|")) return s;
        else if (s.contains("АСП|")) return s;
        else if (ref.contains("pbak")) return "ПБК|" + s;
        else if (ref.contains("bak")) return "БАК|" + s;
        else if (ref.contains("pmag")) return "ПМГ|" + s;
        else if (ref.contains("mag")) return "МАГ|" + s;
        else if (ref.contains("spec")) return "СПЦ|" + s;
        else if (ref.contains("asp")) return "АСП|" + s;
        else
            return s;
    }


    static class MyHandler extends Handler {

        WeakReference<AutorisationFragment> wrActivity;

        public MyHandler(AutorisationFragment activity) {
            wrActivity = new WeakReference<AutorisationFragment>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ListOfCouples loc = (ListOfCouples) msg.obj;
            AutorisationFragment activity = wrActivity.get();

            if (activity != null) {
                activity.refreshInstList(loc);
                activity.initializeSpinners();
                activity.structueRenew();
            }
        }
    }
}
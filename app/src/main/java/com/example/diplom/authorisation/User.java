package com.example.diplom.authorisation;

import android.content.Context;

import com.example.diplom.schedule.ierarchy.TripleList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class User implements Serializable {

    private static final long serialVersionUID = 1L;//переменная сериализации

    private String formObuch;
    private String qualification;
    private String institute;
    private String profile;
    private String napravlennost;
    private String course;
    private final String savePath = "userSettings.txt";

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    private TripleList links;

    public TripleList getLinks() {
        return links;
    }

    public void setLinks(TripleList links) {
        this.links = links;
    }

    public String getFormObuch() {
        return formObuch;
    }

    public void setFormObuch(String formObuch) {
        this.formObuch = formObuch;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getNapravlennost() {
        return napravlennost;
    }

    public void setNapravlennost(String napravlennost) {
        this.napravlennost = napravlennost;
    }

    private void consume( User user)
    {
        this.formObuch = user.getFormObuch();
        this.qualification = user.getQualification();
        this.institute = user.getInstitute();
        this.profile = user.getProfile();
        this.napravlennost = user.getNapravlennost();
        this.course = user.getCourse();
        this.links = user.getLinks();
    }

    public void save(Context activity) {//сериализация записок

        try {
            File f = new File(activity.getFilesDir() + "/" + savePath);
            boolean a = f.delete();

            FileOutputStream output = activity.openFileOutput(savePath, Context.MODE_PRIVATE);
            ObjectOutputStream objOut = new ObjectOutputStream(output);
            objOut.writeObject(this);

        } catch
        (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean load(Context activity) {
        //десериализация заметок
        String pn = activity.getFilesDir() + "/" + savePath;
        File file = new File(pn);
        boolean res = false;
        setDummyStats();
        if (file.exists()) {
            try {
                FileInputStream fileInput = activity.openFileInput(savePath);
                ObjectInputStream objInput = new ObjectInputStream(fileInput);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInput));
                while (bufferedReader.ready()) {
                    this.consume((User) objInput.readObject());
                }
                res = true;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(this.institute.equals("")||
                this.napravlennost.equals("")||
                this.course.equals(""))
        {setDummyStats();}
            return res;
    }

    private void setDummyStats() {
        TripleList dummyList = new TripleList();
        dummyList.newCouple("Нет информации");
        dummyList.addValuesByName("Нет информации","Нет расписания",
                "Пожалуйста, укажите ваши данные в настройках");
        this.setQualification("Отсутствует");
        this.setInstitute("Нет информации о вашей группе");
        this.setLinks(dummyList);
        this.setProfile("Пожалйста, укажите данные в настройках");
        this.setCourse("Нет информации");
        this.setNapravlennost("Нет информации");
    }
}

package com.example.diplom.notes;

import com.example.navdrav.R;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Note implements Serializable, Comparable {// класс заметок
    private static final long serialVersionUID = 1L;//переменная сериализации

    private int id;//id заметки
    private String text;//текст заметки
    private int style;//цвет фона заметки
    private Date makeDate;//Время создания
    private Calendar notifyTime;//Время напоминания
    private Boolean notifyed;//Факт того, что нужно напомнить о заметке

    public Note()//конструктор по умолчанию
    {
        this.text = "Новая заметка";
        this.makeDate = new Date();
        this.style = R.drawable.notebox_white_bckg;
        this.notifyed = false;
        this.notifyTime = Calendar.getInstance();
    }

    public Note(String text)//конструктор с параметром
    {
        this.text = text;
        this.makeDate = new Date();
        this.style = R.drawable.notebox_white_bckg;
        this.notifyed = false;
        this.notifyTime = Calendar.getInstance();
    }

    public int getNoteColor() {
        switch (this.getStyle()) {
            case (R.drawable.notebox_white_bckg): {
                return R.color.white;
            }
            case (R.drawable.notebox_blue_bckg): {
                return R.color.note_blue;
            }
            case (R.drawable.notebox_yellow_bckg): {
                return R.color.note_yellow;
            }
            case (R.drawable.notebox_green_bckg): {
                return R.color.note_green;
            }
            case (R.drawable.notebox_purple_bckg): {
                return R.color.note_purple;
            }
            default:
                return R.color.white;
        }
    }

    public void setMakeDate(Date makeDate) {
        this.makeDate = makeDate;
    }

    public Date getMakeDate() {
        return makeDate;
    }

    //установка и получение текста заметки
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    //установка и получение времени напоминания
    public Calendar getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(Calendar notice) {
        this.notifyTime = notice;
    }

    public Boolean getNotifyed() {
        return notifyed;
    }

    public void setNotifyed(Boolean notifyed) {
        this.notifyed = notifyed;
    }

    @Override
    public int compareTo(Object o) {
        Note tmp = (Note) o;

        if (tmp.getNotifyed()) {
            if (this.getNotifyTime().getTimeInMillis() < tmp.getNotifyTime().getTimeInMillis()) {
                /* текущее меньше полученного */
                return -1;
            } else if (this.getNotifyTime().getTimeInMillis() > tmp.getNotifyTime().getTimeInMillis()) {
                /* текущее больше полученного */
                return 1;
            }
            /* текущее равно полученному */
            return 0;

        } else {
            if (this.notifyed == tmp.getNotifyed()) {
                return 0;
            } else return -1;
        }
    }
}


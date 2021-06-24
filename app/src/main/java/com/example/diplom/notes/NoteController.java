package com.example.diplom.notes;
import android.content.Context;

import com.example.diplom.notes.filters.Filter3Days;
import com.example.diplom.notes.filters.FilterByWhiteColor;
import com.example.diplom.notes.filters.FilterNone;
import com.example.diplom.notes.filters.IFilter;
import com.example.navdrav.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class NoteController implements Serializable {

    private List<Note> noteList = new ArrayList<>();//список записок
    private final String savePath = "notes.txt";
    private IFilter filterType;
    private final Context activity;

    public NoteController(Context activity) {
        this.activity = activity;
        filterType = new FilterNone();
        loadNotes();
    }

    public List<Note> getNoteList() {
        return noteList;
    }

    public void setNoteList(List<Note> noteList) {
        this.noteList = noteList;
    }

    public void clearNotes() {
        File f = new File(activity.getFilesDir() + "/" + savePath);
        boolean a = f.delete();
        noteList.clear();
    }

    public void saveNotes() {//сериализация заметок
        try {
            FileOutputStream output = activity.openFileOutput(savePath, Context.MODE_PRIVATE);
            ObjectOutputStream objOut = new ObjectOutputStream(output);
            for (int i = 0; i < noteList.size(); i++) {
                objOut.writeObject(noteList.get(i));
            }
        } catch
        (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadNotes() {
        //десериализация заметок
        String pn = activity.getFilesDir() + "/" + savePath;
        File file = new File(pn);

        if (file.exists()) {
            try {
                FileInputStream fileInput = activity.openFileInput(savePath);
                ObjectInputStream objInput = new ObjectInputStream(fileInput);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInput));
                while (bufferedReader.ready()) {
                    noteList.add((Note) objInput.readObject());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int checkIndex(int index) {
        if (index < 0)
            index = 0;
        else if (index > noteList.size())
            index = noteList.size() - 1;
        return index;
    }

    public void addNote(Note n) {
        noteList.add(n);
        activity.deleteFile(savePath);
        saveNotes();
        noteList.clear();
        loadNotes();
    }
}
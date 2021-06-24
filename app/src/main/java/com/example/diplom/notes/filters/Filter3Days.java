package com.example.diplom.notes.filters;

import java.util.Date;
import java.util.List;

import com.example.diplom.notes.Note;

public class Filter3Days implements IFilter {

    public List<Note> filter(List<Note> n) {
        List<Note> notes = null;
        for (Note note : n) {
            Date curDate = new Date();
            if (note.getMakeDate().getTime() > (curDate.getTime() - 1000 * 60 * 60 * 24 * 3)) {
                notes.add(note);
            }
        }
        return notes;
    }
}

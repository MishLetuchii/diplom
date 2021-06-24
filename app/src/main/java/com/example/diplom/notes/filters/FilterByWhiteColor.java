package com.example.diplom.notes.filters;
import com.example.navdrav.R;

import java.util.ArrayList;
import java.util.List;

import com.example.diplom.notes.Note;


public class FilterByWhiteColor implements IFilter {

    public List<Note> filter(List<Note> n) {
        List<Note> notes = new ArrayList<>();
        for(Note note: n)
        {
            if (note.getStyle()==R.color.white)
            {
                notes.add(note);
            }
        }
        return notes;
    }
}

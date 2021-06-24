package com.example.diplom.notes.filters;
import java.util.List;

import com.example.diplom.notes.Note;


public class FilterNone implements IFilter {

    public List<Note> filter(List<Note> n) {
        return n;
    }
}

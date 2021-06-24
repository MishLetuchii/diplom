package com.example.diplom.notes.filters;

import java.util.List;

import com.example.diplom.notes.Note;

public interface IFilter {
    public List<Note> filter(List<Note> n);
}

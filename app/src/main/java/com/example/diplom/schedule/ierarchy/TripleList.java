package com.example.diplom.schedule.ierarchy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TripleList implements Serializable {

    List <String> names;
    List <List <String> > types;
    List <List <String> > values;

    public TripleList()
    {
        names = new ArrayList<String>();
        values = new ArrayList<List<String>>();
        types = new ArrayList<List<String>>();
    }

    public void addTriple(String name,List<String> types, List<String> values)
    {
        this.names.add(name);
        this.types.add(types);
        this.values.add(values);
    }

    public List<String> getNames()
    {
        return names;
    }

 

    public List<String> getValuesByName(String name)
    {
        if(names.contains(name))
        return values.get(names.indexOf(name));
        else
            return null;
    }

    public List<String> getTypesByName(String name)
    {
        if(names.contains(name))
        return types.get(names.indexOf(name));
        else
            return null;
    }

    public void addValuesByName(String name,String type,String value)
    {
        types.get(names.indexOf(name)).add(type);
        values.get(names.indexOf(name)).add(value);
    }



    public void addNewValuesByName(String name,String type,String value)
    {
        types.get(names.indexOf(name)).add(type);
        values.get(names.indexOf(name)).add(value);
    }

    public void removeCouple(String name)
    {
        values.remove(names.indexOf(name));
        types.remove(names.indexOf(name));
        names.remove(name);
    }

    public void newCouple(String name)
    {
        if (!names.contains(name)) {
            names.add(name);
            values.add(new ArrayList<String>());
            types.add(new ArrayList<String>());
        }
    }



}

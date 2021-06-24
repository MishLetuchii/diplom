package com.example.diplom.schedule.ierarchy;

import android.os.Build;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListOfCouples implements Serializable {

    List <String> names;
    List <List <String> > values;

    public ListOfCouples()
    {
        names = new ArrayList<String>();
        values = new ArrayList<List<String>>();
    }

    public void addCouple(String name, List<String> values)
    {
        this.names.add(name);
        this.values.add(values);
    }

    public List<String> getValuesByNameAndSuffix(String name,String suffix)
    {
        List<String> res = new ArrayList<String>();
        for (String s:values.get(names.indexOf(name)) )
        {
            if(s.contains(suffix))
                res.add(s.substring(suffix.length()));
        }
        return  res;
    }

    public List<String> getValuesByName(String name)
    {
        return values.get(names.indexOf(name));
    }

    public void addValuesByName(String name,String value)
    {
        values.get(names.indexOf(name)).add(value);
    }

    public void removeCouple(String name)
    {
        values.remove(names.indexOf(name));
        names.remove(name);
    }

    public void newCouple(String name)
    {

        names.add(name);
        values.add(new ArrayList<String>());

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListOfCouples that = (ListOfCouples) o;

                return   that.names.equals(this.names)
                        && that.values.equals(this.values) ;

    }


}

package com.diguiet.nespresso.server.nespresso.model;

import java.util.ArrayList;

public class Category {
    public String id;
    public String name;
    public Object description;
    public Icon icon;
    public DetailsIcon detailsIcon;
    public Object url;
    public String capacityLabel;
    public RangeLink rangeLink;
    public ArrayList<String> subCategories;
    public ArrayList<String> superCategories;
}

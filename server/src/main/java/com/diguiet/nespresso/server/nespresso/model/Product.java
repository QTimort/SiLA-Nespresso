package com.diguiet.nespresso.server.nespresso.model;

import lombok.Getter;

import java.util.*;

@Getter
public class Product {
    public String id;
    public String internationalId;
    public String legacyId;
    public String name;
    public String internationalName;
    public Object description;
    public Image image;
    public String type;
    public String headline;
    public double price;
    public String url;
    public int salesMultiple;
    public int unitQuantity;
    public int maxOrderQuantity;
    public boolean available;
    public boolean inStock;
    public boolean pushRatingEnabled;
    public ArrayList<String> technologies;
    public boolean hidePrice;
    public int intensity;
    public ArrayList<String> ranges;
    public ArrayList<String> cupSizes;
    public ArrayList<String> flavors;
    public Object packagingType;

    public enum Size {
        RISTRETTO("ristretto"),
        ESPRESSO("espresso"),
        LUNGO("lungo");

        @Getter
        private final String name;

        Size(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    @Override
    public String toString() {
        return (
                name + " | " +
                        headline + " | " +
                        (isValid() ? "Available" : "Unavailable") + " | " +
                        "Intensity: " + intensity + " | " +
                        "Cup size: " + getCupSizes()
        );
    }

    public boolean isValid() {
        return (available && inStock && type.equalsIgnoreCase("capsule"));
    }

    public Set<Size> getCupSizes() {
        if (cupSizes == null) {
            return Collections.emptySet();
        }
        final Set<Size> sizes = new HashSet<>();
        cupSizes.forEach(size ->
                Arrays.stream(Size.values()).forEach(sizeName -> {
                    if (size.contains(sizeName.getName())) {
                        sizes.add(sizeName);
                    }
                })
        );
        return sizes;
    }
}

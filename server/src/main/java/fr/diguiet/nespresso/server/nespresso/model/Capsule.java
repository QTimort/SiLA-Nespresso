package fr.diguiet.nespresso.server.nespresso.model;

import java.util.*;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public class Capsule {
    @SerializedName("flavors")
    @Expose
    private List<Object> flavors = null;
    @SerializedName("image")
    @Expose
    private Image image;
    @SerializedName("ranges")
    @Expose
    private List<String> ranges = null;
    @SerializedName("available")
    @Expose
    @Getter
    private Boolean available;
    @SerializedName("description")
    @Expose
    private Object description;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("salesMultiple")
    @Expose
    private Integer salesMultiple;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("technologies")
    @Expose
    private List<String> technologies = null;
    @SerializedName("intensity")
    @Expose
    @Getter
    private Integer intensity;
    @SerializedName("price")
    @Expose
    @Getter
    private Double price;
    @SerializedName("unitQuantity")
    @Expose
    private Integer unitQuantity;
    @SerializedName("internationalId")
    @Expose
    private String internationalId;
    @SerializedName("name")
    @Expose
    @Getter
    private String name;
    @SerializedName("internationalName")
    @Expose
    private String internationalName;
    @SerializedName("legacyId")
    @Expose
    private String legacyId;
    @SerializedName("inStock")
    @Expose
    @Getter
    private Boolean inStock;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("cupSizes")
    @Expose
    private List<String> cupSizes = null;
    @SerializedName("headline")
    @Expose
    @Getter
    private String headline;
    @SerializedName("pushRatingEnabled")
    @Expose
    private Boolean pushRatingEnabled;
    @SerializedName("maxOrderQuantity")
    @Expose
    private Object maxOrderQuantity;
    @SerializedName("packagingType")
    @Expose
    private Object packagingType;

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


package com.diguiet.nespresso.server.nespresso;

import com.diguiet.nespresso.server.nespresso.model.Product;
import com.diguiet.nespresso.server.nespresso.model.Root;
import com.google.gson.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class NespressoUtils {
    // json to pojo https://json2csharp.com/json-to-pojo
    // todo be able to select between original and vertuo
    //private static final String NESPRESSO_CAPSULES_URL = "https://www.nespresso.com/ch/en/order/capsules/original";
    private static final String NESPRESSO_CAPSULES_URL = "https://www.nespresso.com/ch/en/order/capsules/vertuo";
    private static final Pattern PATTERN = Pattern.compile("window\\.ui\\.push\\((\\{\"id\":\"respProductListPLPCapsule.*?)\\);", Pattern.MULTILINE);

    public static Map<String, Product> getAvailableCoffees() {
        @NonNull final String pageHtml = getPageHtml();
        final Matcher matcher = PATTERN.matcher(pageHtml);
        matcher.find();
        final Root root = new Gson().fromJson(matcher.group(1), Root.class);
        return root.configuration.eCommerceData.products
                .stream()
                .filter(Product::isValid)
                .collect(Collectors.toMap(Product::getName, c -> c));
    }

    private static String getPageHtml() {
        try {
            final URLConnection connection =  new URL(NESPRESSO_CAPSULES_URL).openConnection();
            try (final Scanner scanner = new Scanner(connection.getInputStream())) {
                scanner.useDelimiter("\\Z");
                return scanner.next();
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}

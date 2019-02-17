package fr.diguiet.nespresso.server.nespresso;

import com.google.gson.*;
import fr.diguiet.nespresso.server.nespresso.model.Capsule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class NespressoUtils {
    private static final String NESPRESSO_CAPSULES_URL = "https://www.nespresso.com/ch/en/order/capsules/original";
    private static final Pattern PATTERN = Pattern.compile("window\\.ui\\.push\\((\\{\"id\":\"respProductListPLPCapsule.*?)\\);", Pattern.MULTILINE);

    public static Map<String, Capsule> getAvailableCoffees() {
        @NonNull final String pageHtml = getPageHtml();
        final Matcher matcher = PATTERN.matcher(pageHtml);
        matcher.find();
        final JsonObject json = new JsonParser().parse(matcher.group(1)).getAsJsonObject();
        final JsonArray array = json
                .get("configuration").getAsJsonObject()
                .get("eCommerceData").getAsJsonObject()
                .get("products").getAsJsonArray();

        return Arrays
                .stream(new Gson().fromJson(array, Capsule[].class))
                .filter(Capsule::isValid)
                .collect(Collectors.toMap(Capsule::getName, c -> c));
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

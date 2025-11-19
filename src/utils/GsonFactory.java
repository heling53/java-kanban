package utils;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GsonFactory {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static Gson createGson() {
        GsonBuilder gb = new GsonBuilder();

        gb.registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                return src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.format(FORMATTER));
            }
        });
        gb.registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                if (json == null || json.getAsString().isEmpty()) return null;
                return LocalDateTime.parse(json.getAsString(), FORMATTER);
            }
        });

        gb.registerTypeAdapter(Duration.class, new JsonSerializer<Duration>() {
            public JsonElement serialize(Duration src, Type typeOfSrc, JsonSerializationContext context) {
                return src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.toMinutes());
            }
        });
        gb.registerTypeAdapter(Duration.class, new JsonDeserializer<Duration>() {
            public Duration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                if (json == null || json.getAsString().isEmpty()) return null;
                long minutes = json.getAsLong();
                return Duration.ofMinutes(minutes);
            }
        });

        gb.setPrettyPrinting();
        return gb.create();
    }
}

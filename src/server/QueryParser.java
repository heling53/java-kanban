package server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QueryParser {
    public static Map<String, String> parse(String query) {
        if (query == null || query.isEmpty()) return Collections.emptyMap();
        Map<String, String> map = new HashMap<>();
        String[] pairs = query.split("&");
        for (String p : pairs) {
            String[] kv = p.split("=", 2);
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            } else {
                map.put(kv[0], "");
            }
        }
        return map;
    }
}

package com.tvd12.ezyhttp.core.net;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.tvd12.ezyfox.util.EzyEntry;

public final class PathVariables {

    private PathVariables() {}

    public static List<Entry<String, String>> getVariables(
        String template,
        String uri
    ) {
        String[] templatePaths = template.split("/");
        String[] uriPaths = uri.split("/");
        List<Entry<String, String>> answer = new ArrayList<>();
        for (int i = 0; i < templatePaths.length; ++i) {
            String templatePath = templatePaths[i];
            if (isPathVariable(templatePath)) {
                String varName = getVariableName(templatePath);
                String varValue = uriPaths[i];
                answer.add(EzyEntry.of(varName, varValue));
            }
        }
        return answer;
    }

    public static String getVariableName(String path) {
        return path.substring(1, path.length() - 1);
    }

    public static boolean isPathVariable(String path) {
        return path.startsWith("{") && path.endsWith("}");
    }
}

package br.com.akowalski.helpers;

import br.com.akowalski.pojos.KcgClass;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class KcgHelper {


    public static List<KcgClass> parse(String payload) {
        Gson gson = new Gson();
        return gson.fromJson(payload, new TypeToken<ArrayList<KcgClass>>() {
        }.getType());
    }

    public static List<KcgClass> parse(String json, File file) {
        try {
            AtomicReference<String> template = new AtomicReference<>();
            if (StringUtils.isNotEmpty(json)) {
                template.set(json);
            } else if (Objects.nonNull(file)) {
                template.set(new Scanner(file).useDelimiter("\\Z").next());
            }
            return parse(template.get());
        } catch (FileNotFoundException e) {
            ExceptionUtils.rethrow(e);
        }
        return List.of();
    }


}

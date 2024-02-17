package br.com.akowalski.helpers;

import br.com.akowalski.pojos.KcgClass;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class KcgHelper {


    public static List<KcgClass> parse(String payload) {
        Gson gson = new Gson();
        return gson.fromJson(payload, new TypeToken<ArrayList<KcgClass>>() {
        }.getType());
    }


}

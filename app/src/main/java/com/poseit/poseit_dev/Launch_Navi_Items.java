package com.poseit.poseit_dev;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dave on 01/03/15.
 */
public class Launch_Navi_Items {

    public static List<Launch_Navi_Item> ITEMS = new ArrayList<Launch_Navi_Item>();

    public static Map<String, Launch_Navi_Item> ITEM_MAP = new HashMap<String, Launch_Navi_Item>();

    static {
        addItem(new Launch_Navi_Item("Voting", VotingActivity.class));
    }

    private static void addItem(Launch_Navi_Item item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static class Launch_Navi_Item {
        public String id;
        public Class activity;

        public Launch_Navi_Item(String id, Class activity) {
            this.id = id;
            this.activity = activity;
        }

        @Override
        public String toString() {
            return id;
        }
    }
}

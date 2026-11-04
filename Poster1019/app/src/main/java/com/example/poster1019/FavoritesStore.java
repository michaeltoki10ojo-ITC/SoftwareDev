package com.example.poster1019;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Saves the favorite poster IDs on the device, so they survive closing the app.
 *
 */
public class FavoritesStore {
    private static final String PREFS_NAME = "favorites";
    private static final String KEY_IDS = "ids";
    private static final String SEPARATOR = ",";

    private final SharedPreferences preferences;

    //in-memory copy of the saved Ids
    private final List<String> ids;


    public FavoritesStore(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        ids = decode(preferences.getString(KEY_IDS,""));
    }

    //get IDS, in the order they were added (read-only)
    public List<String> getIds(){
        return Collections.unmodifiableList(ids);
    }

    public int addAll(List<String> newIds){
        int added = 0;
        for (String id:newIds){
            if(!ids.contains(id)){
                ids.add(id);
                added++;
            }
        }
        if(added>0){
            save();
        }
        return added;
    }

    //remove one ID, return true if it was there
    public boolean remove(String id){
        boolean removed = ids.remove(id);
        if(removed){
            save();
        }
        return removed;
    }

    //Drops saved ids that don't match any poster any more
    public void retainOnly(Collection<String> validIds){
        if(ids.retainAll(validIds)){
            save();
        }
    }

    private void save(){
        //apply() writes the file in the backgorund.
        preferences.edit().putString(KEY_IDS,encode(ids)).apply();
    }

    static String encode(List<String> ids){
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<ids.size();i++){
            if (i > 0) {
                builder.append(SEPARATOR);
            }
            builder.append(ids.get(i));
        }
        return builder.toString();
    }

    static List<String> decode(String saved){
        List<String> result = new ArrayList<>();
        if (saved == null || saved.isEmpty()){
            return result;
        }
        for(String id: saved.split(SEPARATOR)){
            if(!id.isEmpty() && !result.contains(id)){
                result.add(id);
            }
        }
        return result;
    }
}

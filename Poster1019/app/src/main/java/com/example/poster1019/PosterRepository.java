package com.example.poster1019;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The one place that owns the app's movie data: all posters, the selection, and the favorites.
 */
public class PosterRepository {

    // Notified whenever favorites are added or removed (Favorite List, badge).
    public interface FavoritesListener{
        void onFavoritesChanged();
    }

    private static PosterRepository instance;

    private final List<Posters> allPosters;
    private final FavoritesStore favoritesStore;
    private final List<FavoritesListener> favoritesListeners = new ArrayList<>();

    // Creates the single instance on first use. It keeps the application context,
    // never an Activity: holding an Activity in a singleton would leak it.
    public static synchronized PosterRepository getInstance(Context context) {
        if (instance == null) {
            instance = new PosterRepository(context.getApplicationContext());
        }
        return instance;
    }

    private PosterRepository(Context appContext) {
        allPosters = Collections.unmodifiableList(createPosters());
        favoritesStore = new FavoritesStore(appContext);

        // Forget saved favorites whose poster no longer exists in the app.
        List<String> validIds = new ArrayList<>();
        for (Posters poster : allPosters) {
            validIds.add(poster.id);
        }
        favoritesStore.retainOnly(validIds);
    }

    public List<Posters> getAllPosters(){
        return allPosters;
    }

    public Posters findById(String id){
        for(Posters posters:allPosters){
            if(posters.id.equals(id)){
                return posters;
            }
        }
        return null;
    }

    // deselects every poster
    public void clearSelection(){
        for(Posters poster : allPosters){
            poster.isSelected = false;
        }
    }

    //get favorite posters, in the order they were added
    public List<Posters> getFavorites(){
        List<Posters> favorites = new ArrayList<>();
        for (String id : favoritesStore.getIds()) {
            Posters poster = findById(id);
            if (poster != null) {
                favorites.add(poster);
            }
        }
        return favorites;
    }

    public int getFavoriteCount(){
        return favoritesStore.getIds().size();
    }

    // Adds posters to favorites, skipping ones already there.
    // Returns how many were new, so the page can show the right message.
    public int addFavorites(List<Posters> posters) {
        List<String> ids = new ArrayList<>();
        for (Posters poster : posters) {
            ids.add(poster.id);
        }
        int added = favoritesStore.addAll(ids);
        if (added > 0) {
            notifyFavoritesChanged();
        }
        return added;
    }

    // Removes one favorite. Returns true if it was a favorite.
    public boolean removeFavorite(String id) {
        boolean removed = favoritesStore.remove(id);
        if (removed) {
            notifyFavoritesChanged();
        }
        return removed;
    }

    // The listener (observer) pattern: pages register to hear about changes.
    // Whoever adds a listener must remove it again (onDestroyView / onDestroy).
    public void addFavoritesListener(FavoritesListener listener) {
        if (!favoritesListeners.contains(listener)) {
            favoritesListeners.add(listener);
        }
    }

    public void removeFavoritesListener(FavoritesListener listener) {
        favoritesListeners.remove(listener);
    }

    private void notifyFavoritesChanged() {
        for(FavoritesListener listener : new ArrayList<>(favoritesListeners)){
            listener.onFavoritesChanged();
        }
    }

    private static List<Posters> createPosters() {
        List<Posters> posters = new ArrayList<>();

        Posters interstellar = new Posters();
        interstellar.id = "interstellar";
        interstellar.image = R.drawable.interstellar;
        interstellar.name = "Interstellar";
        interstellar.rating = 4.8f;
        interstellar.createdBy = "Christopher Nolan";
        interstellar.story = "A team of explorers travels through a wormhole in space to search for a new home for humanity.";
        posters.add(interstellar);

        Posters creed = new Posters();
        creed.id = "creed";
        creed.image = R.drawable.creed;
        creed.name = "Creed";
        creed.rating = 4.5f;
        creed.createdBy = "Ryan Coogler";
        creed.story = "Adonis Creed trains with Rocky Balboa while trying to build his own name in boxing.";
        posters.add(creed);

        Posters darkKnight = new Posters();
        darkKnight.id = "dark_knight";
        darkKnight.image = R.drawable.dark_knight;
        darkKnight.name = "The Dark Knight";
        darkKnight.rating = 4.9f;
        darkKnight.createdBy = "Christopher Nolan";
        darkKnight.story = "Batman faces the Joker as Gotham is pushed into chaos.";
        posters.add(darkKnight);

        Posters blackPanther = new Posters();
        blackPanther.id = "black_panther";
        blackPanther.image = R.drawable.black_panther;
        blackPanther.name = "Black Panther";
        blackPanther.rating = 4.6f;
        blackPanther.createdBy = "Ryan Coogler";
        blackPanther.story = "T'Challa returns to Wakanda and must defend his kingdom from a powerful challenger.";
        posters.add(blackPanther);

        Posters coachCarter = new Posters();
        coachCarter.id = "coach_carter";
        coachCarter.image = R.drawable.coach_carter;
        coachCarter.name = "Coach Carter";
        coachCarter.rating = 4.4f;
        coachCarter.createdBy = "Thomas Carter";
        coachCarter.story = "A basketball coach pushes his players to succeed both on the court and in the classroom.";
        posters.add(coachCarter);

        return posters;

    }


}
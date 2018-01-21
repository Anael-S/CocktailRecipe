package anaels.com.cocktailrecipe.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import anaels.com.cocktailrecipe.api.model.DrinkRecipe;

/**
 * Helper to manage the favorites
 */
public class FavoriteHelper {
    private static final String MyPREFERENCES = "RecipeFavorite";

    public static final String KEY_FAVORITE = "keyFav";

    /**
     * Write the current favorite list inside the shared pref
     * @param context the context
     * @param listRecipe the list of recipe to save
     */
    public static void setFavorite(Context context, ArrayList<DrinkRecipe> listRecipe) {
        SharedPreferences sharedPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String jsonRecipe = SerializeHelper.serializeJson(listRecipe);
        editor.putString(KEY_FAVORITE, jsonRecipe);
        editor.apply();
    }

    /**
     * Recover the list of favorite previously stored in the Shared Pref
     * @param context the context
     * @return the recipe list
     */
    public static ArrayList<DrinkRecipe> getFavorite(Context context) {
        ArrayList<DrinkRecipe> listRecipe;
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String jsonRecipe = sharedPreferences.getString(KEY_FAVORITE, "");
        Type returnType = new TypeToken<ArrayList<DrinkRecipe>>() {
        }.getType();
        listRecipe = SerializeHelper.deserializeJson(jsonRecipe, returnType);
        if (listRecipe == null){
            listRecipe = new ArrayList<>();
        }
        return listRecipe;
    }

}

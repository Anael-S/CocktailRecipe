package anaels.com.cocktailrecipe.api;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import anaels.com.cocktailrecipe.api.model.DrinkRecipe;
import anaels.com.cocktailrecipe.api.model.ListDrink;
import anaels.com.cocktailrecipe.helper.SerializeHelper;


/**
 * Service used to get the json containing the recipes
 */
public class NetworkService {

    private static final String BASE_URL_API = "http://www.thecocktaildb.com/api/json/v1/";
    private static final String TEMP_TOKEN = "1/";
    private static final String URL_RANDOM_RECIPE = "random.php";

    public interface OnRecipeRecovered {
        void onRecipeRecovered(ArrayList<DrinkRecipe> drinkRecipeList);
    }

    public interface OnError {
        void onError();
    }

    /**
     * Get a random cocktail recipe
     *
     * @param context           the context
     * @param onRecipeRecovered callback
     * @param onError           callback on error
     */
    public static void getRandomRecipe(Context context, final OnRecipeRecovered onRecipeRecovered, final OnError onError) {
        RequestQueue queueVolley;
        queueVolley = Volley.newRequestQueue(context);
        StringRequest requestRecipe = new StringRequest(com.android.volley.Request.Method.GET, BASE_URL_API + TEMP_TOKEN + URL_RANDOM_RECIPE, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Type returnType = new TypeToken<ListDrink>() {}.getType();
                ListDrink drinkList = SerializeHelper.deserializeJson(response, returnType);
                if (drinkList != null && drinkList.getDrinkRecipes() != null && !drinkList.getDrinkRecipes().isEmpty()) {
                    onRecipeRecovered.onRecipeRecovered(new ArrayList<>(drinkList.getDrinkRecipes()));
                } else {
                    onError.onError();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onError.onError();
            }
        });
        queueVolley.add(requestRecipe);

    }
}

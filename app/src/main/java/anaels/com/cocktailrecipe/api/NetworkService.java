package anaels.com.cocktailrecipe.api;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import anaels.com.cocktailrecipe.api.model.Recipe;
import anaels.com.cocktailrecipe.helper.SerializeHelper;


/**
 * Service used to get the json containing the recipes
 */
public class NetworkService {

    private static final String BASE_URL_RECIPE = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    private static final String ALTERNATIVE_ENDPOINT_WITH_IMAGES = "https://d17h27t6h515a5.cloudfront.net/topher/2017/March/58d1537b_baking/baking.json";

    public interface OnRecipeRecovered {
        void onRecipeRecovered(ArrayList<Recipe> recipeList);
    }

    public interface OnError {
        void onError();
    }

    /**
     * Ge the recipe
     *
     * @param context           the context
     * @param onRecipeRecovered callback
     * @param onError           callback on error
     */
    public static void getRecipes(Context context, final OnRecipeRecovered onRecipeRecovered, final OnError onError) {
        RequestQueue queueVolley;
        queueVolley = Volley.newRequestQueue(context);
        StringRequest requestRecipe = new StringRequest(com.android.volley.Request.Method.GET, BASE_URL_RECIPE, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Type returnType = new TypeToken<ArrayList<Recipe>>() {
                }.getType();
                ArrayList<Recipe> recipeList = SerializeHelper.deserializeJson(response, returnType);
                if (recipeList != null && !recipeList.isEmpty()) {
                    onRecipeRecovered.onRecipeRecovered(recipeList);
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

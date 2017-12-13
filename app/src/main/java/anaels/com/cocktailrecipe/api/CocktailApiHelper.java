package anaels.com.cocktailrecipe.api;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import anaels.com.cocktailrecipe.BuildConfig;
import anaels.com.cocktailrecipe.api.model.DrinkRecipe;
import anaels.com.cocktailrecipe.api.model.ListDrink;
import anaels.com.cocktailrecipe.helper.SerializeHelper;


/**
 * Service used to get the json containing the recipes
 */
public class CocktailApiHelper {

    private static final String BASE_URL_API = "http://www.thecocktaildb.com/api/json/v1/";
    private static final String API_TOKEN = BuildConfig.API_KEY + "/";
    private static final String URL_RANDOM_RECIPE = "random.php";
    private static final String URL_SEARCH_BY_NAME = "search.php?s=";
    private static final String URL_SEARCH_BY_INGREDIENT = "filter.php?i=";
    private static final String URL_SEARCH_BY_FILTER = "filter.php?";
    private static final String URL_DETAIL_COCKTAIL = "lookup.php?i=";
    private static final String URL_LIST_INGREDIENTS = "list.php?i=list";
    public static final String FILTER_ALCOHOLIC = "a=Alcoholic";
    public static final String FILTER_NON_ALCOHOLIC = "a=Non_Alcoholic";
    public static final String FILTER_ORDINARY_DRINK = "c=Ordinary_Drink";
    public static final String FILTER_COCKTAIL = "c=Cocktail";


    public interface OnCocktailRecipeRecovered {
        void onCocktailRecipeRecovered(ArrayList<DrinkRecipe> drinkRecipeList);
    }

    public interface OnIngredientListRecovered {
        void onIngredientListRecovered(ArrayList<String> ingredientList);
    }

    public interface OnError {
        void onError();
    }

    public static void searchCocktailByFilters(Context context, ArrayList<String> ingredientsList, String filterAlcoholic, String filterTypeOfDrink, final OnCocktailRecipeRecovered onCocktailRecipeRecovered, final OnError onError) {
        RequestQueue queueVolley;
        queueVolley = Volley.newRequestQueue(context);
        String filterUrl = URL_SEARCH_BY_FILTER;
        //Filter ingredient
        boolean needsPrefix = true;
        if (ingredientsList != null && !ingredientsList.isEmpty()) {
            for (String lIngredient : ingredientsList) {
                if (needsPrefix) {
                    filterUrl += "i=" + lIngredient;
                } else {
                    filterUrl += "&i=" + lIngredient;
                }
                needsPrefix = false;
            }
        }
        //If we don't need the prefix it means that we have some ingredient, so we need to add a '&'
        if (!needsPrefix) {
            filterUrl += "&";
        }
        //Filter alcoholic / non alcoholic
        if (filterAlcoholic != null && !filterAlcoholic.isEmpty()) {
            filterUrl += filterAlcoholic;
        } else {
            filterUrl += FILTER_ALCOHOLIC + "&" + FILTER_NON_ALCOHOLIC;
        }
        filterUrl += "&";
        //Filter type of drink
        if (filterTypeOfDrink != null && !filterTypeOfDrink.isEmpty()) {
            filterUrl += filterTypeOfDrink;
        } else {
            filterUrl += FILTER_ORDINARY_DRINK + "&" + FILTER_COCKTAIL;
        }
        StringRequest requestRecipe = new StringRequest(com.android.volley.Request.Method.GET, BASE_URL_API + API_TOKEN + URL_SEARCH_BY_NAME + filterUrl, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Type returnType = new TypeToken<ListDrink>() {
                }.getType();
                ListDrink drinkList = SerializeHelper.deserializeJson(response, returnType);
                if (drinkList != null && drinkList.getDrinkRecipes() != null && !drinkList.getDrinkRecipes().isEmpty()) {
                    onCocktailRecipeRecovered.onCocktailRecipeRecovered(new ArrayList<>(drinkList.getDrinkRecipes()));
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

    public static void searchCocktailByIngredients(Context context, ArrayList<String> ingredientsList, final OnCocktailRecipeRecovered onCocktailRecipeRecovered, final OnError onError) {
        RequestQueue queueVolley;
        queueVolley = Volley.newRequestQueue(context);
        String filterIngredients = URL_SEARCH_BY_INGREDIENT;
        boolean firstTimeLoop = true;
        for (String lIngredient : ingredientsList) {
            if (firstTimeLoop) {
                filterIngredients += lIngredient;
            } else {
                filterIngredients += "&i=" + lIngredient;
            }
            firstTimeLoop = false;
        }
        StringRequest requestRecipe = new StringRequest(com.android.volley.Request.Method.GET, BASE_URL_API + API_TOKEN + URL_SEARCH_BY_NAME + filterIngredients, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Type returnType = new TypeToken<ListDrink>() {
                }.getType();
                ListDrink drinkList = SerializeHelper.deserializeJson(response, returnType);
                if (drinkList != null && drinkList.getDrinkRecipes() != null && !drinkList.getDrinkRecipes().isEmpty()) {
                    onCocktailRecipeRecovered.onCocktailRecipeRecovered(new ArrayList<>(drinkList.getDrinkRecipes()));
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

    public static void getIngredientList(Context context, final OnIngredientListRecovered onIngredientListRecovered, final OnError onError) {
        RequestQueue queueVolley;
        queueVolley = Volley.newRequestQueue(context);
        StringRequest requestRecipe = new StringRequest(com.android.volley.Request.Method.GET, BASE_URL_API + API_TOKEN + URL_LIST_INGREDIENTS, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Type returnType = new TypeToken<ArrayList<String>>() {
                }.getType();
                ArrayList<String> ingredientList = SerializeHelper.deserializeJson(response, returnType);
                if (ingredientList != null && !ingredientList.isEmpty()) {
                    onIngredientListRecovered.onIngredientListRecovered(ingredientList);
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

    public static void searchCocktailByName(Context context, String cocktailName, final OnCocktailRecipeRecovered onCocktailRecipeRecovered, final OnError onError) {
        RequestQueue queueVolley;
        queueVolley = Volley.newRequestQueue(context);
        StringRequest requestRecipe = new StringRequest(com.android.volley.Request.Method.GET, BASE_URL_API + API_TOKEN + URL_SEARCH_BY_NAME + cocktailName, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Type returnType = new TypeToken<ListDrink>() {
                }.getType();
                ListDrink drinkList = SerializeHelper.deserializeJson(response, returnType);
                if (drinkList != null && drinkList.getDrinkRecipes() != null && !drinkList.getDrinkRecipes().isEmpty()) {
                    onCocktailRecipeRecovered.onCocktailRecipeRecovered(new ArrayList<>(drinkList.getDrinkRecipes()));
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

    public static void searchCocktailById(Context context, String cocktailId, final OnCocktailRecipeRecovered onCocktailRecipeRecovered, final OnError onError) {
        RequestQueue queueVolley;
        queueVolley = Volley.newRequestQueue(context);
        StringRequest requestRecipe = new StringRequest(com.android.volley.Request.Method.GET, BASE_URL_API + API_TOKEN + URL_DETAIL_COCKTAIL + cocktailId, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Type returnType = new TypeToken<ListDrink>() {
                }.getType();
                ListDrink drinkList = SerializeHelper.deserializeJson(response, returnType);
                if (drinkList != null && drinkList.getDrinkRecipes() != null && !drinkList.getDrinkRecipes().isEmpty()) {
                    onCocktailRecipeRecovered.onCocktailRecipeRecovered(new ArrayList<>(drinkList.getDrinkRecipes()));
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

    /**
     * Get a random cocktail recipe
     *
     * @param context                   the context
     * @param onCocktailRecipeRecovered callback
     * @param onError                   callback on error
     */
    public static void getRandomCocktail(Context context, final OnCocktailRecipeRecovered onCocktailRecipeRecovered, final OnError onError) {
        RequestQueue queueVolley;
        queueVolley = Volley.newRequestQueue(context);
        StringRequest requestRecipe = new StringRequest(com.android.volley.Request.Method.GET, BASE_URL_API + API_TOKEN + URL_RANDOM_RECIPE, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Type returnType = new TypeToken<ListDrink>() {
                }.getType();
                ListDrink drinkList = SerializeHelper.deserializeJson(response, returnType);
                if (drinkList != null && drinkList.getDrinkRecipes() != null && !drinkList.getDrinkRecipes().isEmpty()) {
                    onCocktailRecipeRecovered.onCocktailRecipeRecovered(new ArrayList<>(drinkList.getDrinkRecipes()));
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

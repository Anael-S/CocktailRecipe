package anaels.com.cocktailrecipe.api;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import anaels.com.cocktailrecipe.BuildConfig;
import anaels.com.cocktailrecipe.api.model.DrinkRecipe;
import anaels.com.cocktailrecipe.api.model.ListDrink;
import anaels.com.cocktailrecipe.helper.SerializeHelper;


/**
 * Service used to get the json containing the recipes
 */
public class RecipeApiHelper {

    private static final String BASE_URL_API = "http://www.thecocktaildb.com/api/json/v1/";
    private static final String API_TOKEN = BuildConfig.API_KEY + "/";
    private static final String URL_RANDOM_RECIPE = "random.php";
    private static final String URL_SEARCH_BY_NAME = "search.php?s=";
    private static final String URL_SEARCH_BY_INGREDIENT = "filter.php?i=";
    private static final String URL_SEARCH_BY_FILTER = "filter.php?";
    private static final String URL_DETAIL_COCKTAIL = "lookup.php?i=";
    private static final String URL_LIST_INGREDIENTS = "list.php?i=list";
    private static final String URL_IMAGE_INGREDIENT = "http://www.thecocktaildb.com/images/ingredients/";
    private static final String URL_IMAGE_INGREDIENT2 = "-Small.png";
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

    public static String getUrlImageByIngredient(String ingredient) {
        return URL_IMAGE_INGREDIENT + ingredient + URL_IMAGE_INGREDIENT2;
    }

    public static void searchCocktailByFilters(Context context, String filterAlcoholic, String filterTypeOfDrink, final OnCocktailRecipeRecovered onCocktailRecipeRecovered, final OnError onError) {
        RequestQueue queueVolley;
        queueVolley = Volley.newRequestQueue(context);
        String filterUrl = URL_SEARCH_BY_FILTER;
        //Filter ingredient
        boolean withoutFilter = true;
        //Filter alcoholic / non alcoholic
        if (filterAlcoholic != null && !filterAlcoholic.isEmpty()) {
            filterUrl += filterAlcoholic;
            withoutFilter = false;
            if (filterTypeOfDrink != null && !filterTypeOfDrink.isEmpty()) {
                filterUrl += "&";
            }
        }
        //Filter type of drink
        if (withoutFilter && filterTypeOfDrink != null && !filterTypeOfDrink.isEmpty()) {
            filterUrl += filterTypeOfDrink;
            withoutFilter = false;
        }

        if (withoutFilter) {
            filterUrl += "i=";
        }
        StringRequest requestRecipe = new StringRequest(com.android.volley.Request.Method.GET, BASE_URL_API + API_TOKEN + filterUrl, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Type returnType = new TypeToken<ListDrink>() {
                }.getType();
                ListDrink drinkList = SerializeHelper.deserializeJson(response, returnType);
                if (drinkList != null) {
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
     * HOTFIX : The (free :))API is not really on point and we cannot called it with several argument (ingredient here)
     * So we just call it one time for every ingredient and merge the lists
     *
     * @param context
     * @param ingredientsList
     * @param onCocktailRecipeRecovered
     * @param onError
     */
    public static void searchCocktailByIngredients(final Context context, List<DrinkRecipe> recoveredRecipe, ArrayList<String> ingredientsList, final OnCocktailRecipeRecovered onCocktailRecipeRecovered, final OnError onError) {
        RequestQueue queueVolley;
        queueVolley = Volley.newRequestQueue(context);
        if (recoveredRecipe == null) {
            recoveredRecipe = new ArrayList<>();
        }
        final List<DrinkRecipe> recoveredRecipeFromIngredient = recoveredRecipe;
        String filterIngredients = URL_SEARCH_BY_INGREDIENT;
        final ArrayList<String> updatedIngredientsList = new ArrayList<>(ingredientsList);
        if (ingredientsList != null && !ingredientsList.isEmpty()) {
            filterIngredients += ingredientsList.get(0);
            updatedIngredientsList.remove(0);
        }
        StringRequest requestRecipe = new StringRequest(com.android.volley.Request.Method.GET, BASE_URL_API + API_TOKEN + filterIngredients, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Type returnType = new TypeToken<ListDrink>() {
                }.getType();
                ListDrink drinkList = SerializeHelper.deserializeJson(response, returnType);
                List<DrinkRecipe> recoveredRecipeTemp = new ArrayList<>();
                if (drinkList != null && drinkList.getDrinkRecipes() != null && !drinkList.getDrinkRecipes().isEmpty()) {
                    if (recoveredRecipeFromIngredient.isEmpty()) {
                        recoveredRecipeTemp = drinkList.getDrinkRecipes();
                    } else {
                        //We merge our list
                        for (DrinkRecipe lRecipe : drinkList.getDrinkRecipes()) {
                            //It is inside the two list, so we need to add it
                            if (recoveredRecipeFromIngredient.contains(lRecipe)) {
                                recoveredRecipeTemp.add(lRecipe);
                            }
                        }
                    }
                    if (updatedIngredientsList.size() > 0) {
                        searchCocktailByIngredients(context, recoveredRecipeTemp, updatedIngredientsList, onCocktailRecipeRecovered, onError);
                    } else {
                        onCocktailRecipeRecovered.onCocktailRecipeRecovered(new ArrayList<>(recoveredRecipeTemp));
                    }
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
                Type returnType = new TypeToken<ListDrink>() {
                }.getType();
                ListDrink drinkList = SerializeHelper.deserializeJson(response, returnType);
                ArrayList<String> ingredientList = new ArrayList<>();
                for (DrinkRecipe lRecipe : drinkList.getDrinkRecipes()) {
                    if (lRecipe != null && !lRecipe.getStrIngredient1().isEmpty()) {
                        ingredientList.add(lRecipe.getStrIngredient1());
                    }
                }
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

package anaels.com.cocktailrecipe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import anaels.com.cocktailrecipe.adapter.RecipeAdapter;
import anaels.com.cocktailrecipe.api.CocktailApiHelper;
import anaels.com.cocktailrecipe.api.model.DrinkRecipe;
import anaels.com.cocktailrecipe.helper.InternetConnectionHelper;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Main activity, display the list of recipes
 */
public class HomeActivity extends AppCompatActivity {

    ArrayList<DrinkRecipe> mRecipeList;
    RecipeAdapter mRecipeAdapter;
    Context mContext;

    public static final String KEY_INTENT_RECIPE = "keyIntentRecipe";
    public static final String KEY_INTENT_LIST_RECIPE = "keyIntentRecipeList";


    @BindView(R.id.recyclerViewRecipes)
    RecyclerView recyclerViewRecipes;
    @BindView(R.id.fabSearch)
    FloatingActionButton fabSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
        }
        ButterKnife.bind(this);
        mContext = this;

        //Button onClick
        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, SearchActivity.class);
                startActivity(i);
            }
        });

        //If we already got our recipe lists, we recover them
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            mRecipeList = savedInstanceState.getParcelableArrayList(KEY_INTENT_LIST_RECIPE);
            initRecyclerView();
        } else {
            loadRecipes();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_INTENT_LIST_RECIPE, mRecipeList);
        super.onSaveInstanceState(outState);
    }


    /**
     * Load the recipes fron internet if we have an internet connection
     * Load them fron a local json file otherwise
     */
    private void loadRecipes() {
        //If we have an internet connection
        if (InternetConnectionHelper.isNetworkAvailable(this)) {
            //We get our recipe from the network
            CocktailApiHelper.getRandomCocktail(this, new CocktailApiHelper.OnCocktailRecipeRecovered() {
                @Override
                public void onCocktailRecipeRecovered(ArrayList<DrinkRecipe> recipeList) {
                    if (mRecipeList == null) mRecipeList = new ArrayList<DrinkRecipe>();
                    mRecipeList.addAll(recipeList);
                    //HOTFIX //TODO //FIXME //RM just to have some recipe to start
                    if (mRecipeList.size() <3){
                        loadRecipes();
                    } else {
                        initRecyclerView();
                    }
                }
            }, new CocktailApiHelper.OnError() {
                @Override
                public void onError() {
                    Toast.makeText(mContext,getString(R.string.no_internet),Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * Initialize the recyclerview and his adapter
     */
    private void initRecyclerView() {
        recyclerViewRecipes.setHasFixedSize(true);
        if (getResources().getBoolean(R.bool.isTablet)){
            recyclerViewRecipes.setLayoutManager(new GridLayoutManager(this, getResources().getInteger(R.integer.number_column)));
        } else {
            recyclerViewRecipes.setLayoutManager(new LinearLayoutManager(this));
        }
        if (mRecipeAdapter == null) {
            mRecipeAdapter = new RecipeAdapter(this, mRecipeList, new RecipeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(DrinkRecipe item) {
                    Intent i = new Intent(mContext, RecipeActivity.class);
                    i.putExtra(KEY_INTENT_RECIPE, item);
                    i.putExtra(HomeActivity.KEY_INTENT_LIST_RECIPE, mRecipeList);
                    startActivity(i);
                }
            });
            recyclerViewRecipes.setAdapter(mRecipeAdapter);
        } else {
            //We update the adapter
            mRecipeAdapter.setListRecipe(mRecipeList);
            mRecipeAdapter.notifyDataSetChanged();
        }
    }
}

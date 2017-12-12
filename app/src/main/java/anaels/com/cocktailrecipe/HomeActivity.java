package anaels.com.cocktailrecipe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;

import anaels.com.cocktailrecipe.adapter.RecipeAdapter;
import anaels.com.cocktailrecipe.api.NetworkService;
import anaels.com.cocktailrecipe.api.model.Recipe;
import anaels.com.cocktailrecipe.helper.InternetConnectionHelper;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Main activity, display the list of recipes
 */
public class HomeActivity extends AppCompatActivity {

    ArrayList<Recipe> mRecipeList;
    RecipeAdapter mRecipeAdapter;
    Context mContext;

    public static final String KEY_INTENT_RECIPE = "keyIntentRecipe";
    public static final String KEY_INTENT_LIST_RECIPE = "keyIntentRecipeList";


    @BindView(R.id.recyclerViewRecipes)
    RecyclerView recyclerViewRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
        }
        ButterKnife.bind(this);
        mContext = this;

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
            NetworkService.getRecipes(this, new NetworkService.OnRecipeRecovered() {
                @Override
                public void onRecipeRecovered(ArrayList<Recipe> recipeList) {
                    mRecipeList = recipeList;
                    initRecyclerView();
                }
            }, new NetworkService.OnError() {
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
                public void onItemClick(Recipe item) {
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

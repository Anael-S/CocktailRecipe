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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.ramotion.foldingcell.FoldingCell;

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
public class SearchActivity extends AppCompatActivity {

    ArrayList<DrinkRecipe> mRecipeList;
    ArrayList<DrinkRecipe> listFavRecipe;
    RecipeAdapter mRecipeAdapter;
    Context mContext;

    public static final String KEY_INTENT_LIST_RECIPE = "keyIntentRecipeList";


    @BindView(R.id.recyclerViewRecipes)
    RecyclerView recyclerViewRecipes;
    @BindView(R.id.folding_cell)
    FoldingCell foldingCell;
    @BindView(R.id.fabSearch)
    FloatingActionButton fabSearch;
    @BindView(R.id.checkBoxAlcoholic)
    CheckBox checkBoxAlcoholic;
    @BindView(R.id.checkBoxNonalcoholic)
    CheckBox checkBoxNonalcoholic;
    @BindView(R.id.checkBoxCocktail)
    CheckBox checkBoxCocktail;
    @BindView(R.id.checkBoxOrdinaryDrink)
    CheckBox checkBoxOrdinaryDrink;
    @BindView(R.id.nameEditText)
    EditText nameEditText;


    boolean isSearchLayoutFolded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
        }
        ButterKnife.bind(this);
        mContext = this;
        isSearchLayoutFolded = true;

        //OnClick listeners
        foldingCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSearchLayoutFolded) {
                    //We empty the name search
                    nameEditText.setText("");
                }
                foldingCell.toggle(!isSearchLayoutFolded);
                isSearchLayoutFolded = !isSearchLayoutFolded;
            }
        });
        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRecipes();
            }
        });

        //If we already got our recipe lists, we recover them
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            mRecipeList = savedInstanceState.getParcelableArrayList(KEY_INTENT_LIST_RECIPE);
            initRecyclerView();
        }

        listFavRecipe = getIntent().getParcelableArrayListExtra(HomeActivity.KEY_INTENT_LIST_FAV_RECIPE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_INTENT_LIST_RECIPE, mRecipeList);
        super.onSaveInstanceState(outState);
    }


    private void loadRecipes() {
        //If we have an internet connection
        if (InternetConnectionHelper.isNetworkAvailable(this)) {
            //If the name is filled up, we search by name
            if (!nameEditText.getText().toString().isEmpty()) {
                loadCocktailByName();
            } else { //otherwise we search them by filters : ingredients + type
                loadCocktailByFilter();
            }
        } else {
            Toast.makeText(mContext, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    private void loadCocktailByName() {
        String lNameCocktail = nameEditText.getText().toString();
        //We get our recipe from the network
        CocktailApiHelper.searchCocktailByName(this, lNameCocktail, new CocktailApiHelper.OnCocktailRecipeRecovered() {
            @Override
            public void onCocktailRecipeRecovered(ArrayList<DrinkRecipe> recipeList) {
                mRecipeList = new ArrayList<DrinkRecipe>();
                if (recipeList != null && recipeList.size() > 0) {
                    mRecipeList.addAll(recipeList);
                } else {
                    Toast.makeText(mContext, getString(R.string.no_result), Toast.LENGTH_LONG).show();
                }
                initRecyclerView();
            }
        }, new CocktailApiHelper.OnError() {
            @Override
            public void onError() {
                Toast.makeText(mContext, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadCocktailByFilter() {
        String filterAlcoholic = null;
        String filterTypeOfDrink = null;
        if (checkBoxAlcoholic.isChecked() && !checkBoxNonalcoholic.isChecked()) {
            filterAlcoholic = CocktailApiHelper.FILTER_ALCOHOLIC;
        } else if (checkBoxNonalcoholic.isChecked() && !checkBoxAlcoholic.isChecked()) {
            filterAlcoholic = CocktailApiHelper.FILTER_NON_ALCOHOLIC;
        }
        if (checkBoxOrdinaryDrink.isChecked() && !checkBoxCocktail.isChecked()) {
            filterTypeOfDrink = CocktailApiHelper.FILTER_ORDINARY_DRINK;
        } else if (checkBoxCocktail.isChecked() && !checkBoxOrdinaryDrink.isChecked()) {
            filterTypeOfDrink = CocktailApiHelper.FILTER_COCKTAIL;
        }
        //We get our recipe from the network
        CocktailApiHelper.searchCocktailByFilters(this, null, filterAlcoholic, filterTypeOfDrink, new CocktailApiHelper.OnCocktailRecipeRecovered() {
            @Override
            public void onCocktailRecipeRecovered(ArrayList<DrinkRecipe> recipeList) {
                mRecipeList = new ArrayList<DrinkRecipe>();
                if (recipeList != null && recipeList.size() > 0) {
                    mRecipeList.addAll(recipeList);
                } else {
                    Toast.makeText(mContext, getString(R.string.no_result), Toast.LENGTH_LONG).show();
                }
                initRecyclerView();
            }
        }, new CocktailApiHelper.OnError() {
            @Override
            public void onError() {
                Toast.makeText(mContext, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Initialize the recyclerview and his adapter
     */
    private void initRecyclerView() {
        recyclerViewRecipes.setHasFixedSize(true);
        recyclerViewRecipes.setLayoutManager(new GridLayoutManager(this, getResources().getInteger(R.integer.number_column_search)));
        if (mRecipeAdapter == null) {
            mRecipeAdapter = new RecipeAdapter(this, false, mRecipeList, new RecipeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(DrinkRecipe item) {
                    Intent i = new Intent(mContext, RecipeActivity.class);
                    i.putExtra(HomeActivity.KEY_INTENT_RECIPE, item);
                    i.putExtra(HomeActivity.KEY_INTENT_LIST_FAV_RECIPE, listFavRecipe);
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

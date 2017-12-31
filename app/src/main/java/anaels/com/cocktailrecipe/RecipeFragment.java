package anaels.com.cocktailrecipe;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import anaels.com.cocktailrecipe.adapter.IngredientAdapter;
import anaels.com.cocktailrecipe.adapter.StepAdapter;
import anaels.com.cocktailrecipe.api.model.DrinkRecipe;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Display the detail of a specific recipe
 */
public class RecipeFragment extends Fragment {

    DrinkRecipe mRecipe;
    Context mContext;
    ArrayList<DrinkRecipe> mRecipeList;

    public static final String KEY_INTENT_POSITION_INGREDIENT_LIST = "keyIntentPositionIngredientList";
    public static final String KEY_INTENT_POSITION_STEP_LIST = "keyIntentPositionStepList";


    //UI
    @BindView(R.id.recyclerViewIngredientsRecipes)
    RecyclerView recyclerViewIngredientsRecipes;
    @BindView(R.id.recyclerViewStepRecipes)
    RecyclerView recyclerViewStepRecipes;

    IngredientAdapter mIngredientAdapter;
    StepAdapter mStepAdapter;

    Parcelable positionIngredientList;
    Parcelable positionStepList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_recipe, container, false);
        ButterKnife.bind(this, rootView);
        mContext = getContext();

        Bundle currentBundle = null;
        if (getArguments() != null) {
            currentBundle = getArguments();
        }
        if (savedInstanceState != null) {
            currentBundle = savedInstanceState;
        }
        if (currentBundle != null) {
            mRecipe = currentBundle.getParcelable(HomeActivity.KEY_INTENT_RECIPE);
            mRecipeList = currentBundle.getParcelableArrayList(HomeActivity.KEY_INTENT_LIST_RECIPE);
            positionIngredientList = currentBundle.getParcelable(KEY_INTENT_POSITION_INGREDIENT_LIST);
            positionStepList = currentBundle.getParcelable(KEY_INTENT_POSITION_STEP_LIST);
        }

        //UI
        if (mRecipe != null) {
            initRecyclerViewIngredient();
            initRecyclerViewStep();
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(HomeActivity.KEY_INTENT_RECIPE, mRecipe);
        outState.putParcelableArrayList(HomeActivity.KEY_INTENT_LIST_RECIPE, mRecipeList);
        if (recyclerViewIngredientsRecipes != null) {
            outState.putParcelable(KEY_INTENT_POSITION_INGREDIENT_LIST, recyclerViewIngredientsRecipes.getLayoutManager().onSaveInstanceState());
            outState.putParcelable(KEY_INTENT_POSITION_STEP_LIST, recyclerViewStepRecipes.getLayoutManager().onSaveInstanceState());
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * Initialize the recyclerview for the ingredients and his adapter
     */
    private void initRecyclerViewIngredient() {
        //Handle the maximum height of the recyclerview
        //If we got less than 4 ingredient, than we wrap content
        //Otherwise we set a limited height to be able to display the instructions step also
        if (mRecipe.getIngredients().size() <= 3) {
            ViewGroup.LayoutParams params = recyclerViewIngredientsRecipes.getLayoutParams();
            params.height = RecyclerView.LayoutParams.WRAP_CONTENT;
            recyclerViewIngredientsRecipes.setLayoutParams(params);
        } else {
            float height = getResources().getDimension(R.dimen.view_ingredient_height);
            ViewGroup.LayoutParams params_new = recyclerViewIngredientsRecipes.getLayoutParams();
            params_new.height = Math.round(height);
            recyclerViewIngredientsRecipes.setLayoutParams(params_new);
        }


        recyclerViewIngredientsRecipes.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (mIngredientAdapter == null) {
            mIngredientAdapter = new IngredientAdapter(getActivity(), mRecipe.getIngredients(), mRecipe.getMeasures());
            recyclerViewIngredientsRecipes.setAdapter(mIngredientAdapter);
        } else {
            mIngredientAdapter.setListIngredient(new ArrayList<>(mRecipe.getIngredients()));
            mIngredientAdapter.notifyDataSetChanged();
        }
        recyclerViewIngredientsRecipes.getLayoutManager().onRestoreInstanceState(positionIngredientList);
        recyclerViewIngredientsRecipes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager myLayoutManager = (LinearLayoutManager) recyclerViewIngredientsRecipes.getLayoutManager();
                positionIngredientList = myLayoutManager.onSaveInstanceState();
            }
        });
    }

    /**
     * Initialize the recyclerview for the steps and his adapter
     */
    private void initRecyclerViewStep() {
        recyclerViewStepRecipes.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (mStepAdapter == null) {
            mStepAdapter = new StepAdapter(getActivity(), new ArrayList<>(mRecipe.getSteps()));
            recyclerViewStepRecipes.setAdapter(mStepAdapter);
        } else {
            mStepAdapter.setListStep(new ArrayList<>(mRecipe.getSteps()));
            mStepAdapter.notifyDataSetChanged();
        }
        recyclerViewStepRecipes.getLayoutManager().onRestoreInstanceState(positionStepList);
        recyclerViewStepRecipes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager myLayoutManager = (LinearLayoutManager) recyclerViewStepRecipes.getLayoutManager();
                positionStepList = myLayoutManager.onSaveInstanceState();
            }
        });
    }
}

package anaels.com.cocktailrecipe.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import anaels.com.cocktailrecipe.R;
import anaels.com.cocktailrecipe.api.model.Ingredient;
import anaels.com.cocktailrecipe.helper.StepHelper;


/**
 * Display the ingredient on recipe activity
 */
public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {
    private ArrayList<Ingredient> listIngredient;
    private Activity mActivity;


    public IngredientAdapter(Activity activity, ArrayList<Ingredient> listIngredient) {
        this.mActivity = activity;
        this.listIngredient = listIngredient;
    }

    @Override
    public IngredientAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_ingredient, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        //Text
        viewHolder.nameIngredientTextView.setText(mActivity.getString(R.string.ingredient_name, listIngredient.get(i).getIngredient()));
        viewHolder.quantityIngredientTextView.setText(StepHelper.formatQuantityForDisplay(listIngredient.get(i).getQuantity()));
        viewHolder.measureIngredientTextView.setText(listIngredient.get(i).getMeasure().toLowerCase());
        //Lowe bar
        if (i == listIngredient.size()-1){
            viewHolder.separatorView.setVisibility(View.GONE);
        } else {
            viewHolder.separatorView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return listIngredient.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameIngredientTextView;
        TextView quantityIngredientTextView;
        TextView measureIngredientTextView;
        View separatorView;

        public ViewHolder(View view) {
            super(view);
            nameIngredientTextView = (TextView) view.findViewById(R.id.nameIngredientTextView);
            quantityIngredientTextView = (TextView) view.findViewById(R.id.quantityIngredientTextView);
            measureIngredientTextView = (TextView) view.findViewById(R.id.measureIngredientTextView);
            separatorView = view.findViewById(R.id.separatorView);
        }
    }

    public void setListIngredient(ArrayList<Ingredient> listIngredient) {
        this.listIngredient = listIngredient;
    }
}
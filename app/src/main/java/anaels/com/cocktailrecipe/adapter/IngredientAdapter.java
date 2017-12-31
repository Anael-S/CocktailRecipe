package anaels.com.cocktailrecipe.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import anaels.com.cocktailrecipe.R;


/**
 * Display the ingredient on recipe activity
 */
public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {
    private ArrayList<String> listIngredient;
    private ArrayList<String> listQuantity;
    private Activity mActivity;


    public IngredientAdapter(Activity activity, ArrayList<String> listIngredient, ArrayList<String> listQuantity) {
        this.mActivity = activity;
        this.listIngredient = listIngredient;
        this.listQuantity=listQuantity;
    }

    @Override
    public IngredientAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_ingredient, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        //Text
        if (i < listIngredient.size()) {
            viewHolder.nameIngredientTextView.setText(mActivity.getString(R.string.ingredient_name, listIngredient.get(i)));
        }
        if (i < listQuantity.size()) {
            viewHolder.quantityIngredientTextView.setText(listQuantity.get(i));
        }
        //Lower bar
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
        View separatorView;

        public ViewHolder(View view) {
            super(view);
            nameIngredientTextView = (TextView) view.findViewById(R.id.nameIngredientTextView);
            quantityIngredientTextView = (TextView) view.findViewById(R.id.quantityIngredientTextView);
            separatorView = view.findViewById(R.id.separatorView);
        }
    }

    public void setListIngredient(ArrayList<String> listIngredient) {
        this.listIngredient = listIngredient;
    }
}
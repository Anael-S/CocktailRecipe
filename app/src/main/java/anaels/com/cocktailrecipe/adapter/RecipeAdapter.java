package anaels.com.cocktailrecipe.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import anaels.com.cocktailrecipe.R;
import anaels.com.cocktailrecipe.api.model.DrinkRecipe;


/**
 * Display the recipe on the main activity
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    private ArrayList<DrinkRecipe> listRecipe;
    private Activity mActivity;
    private final OnItemClickListener listener;


    public interface OnItemClickListener {
        void onItemClick(DrinkRecipe item);
    }


    public RecipeAdapter(Activity activity, ArrayList<DrinkRecipe> listRecipe, OnItemClickListener listener) {
        this.mActivity = activity;
        this.listRecipe = listRecipe;
        this.listener = listener;

    }

    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_recipe, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        //Text
        viewHolder.titleRecipeTextView.setText(listRecipe.get(i).getStrDrink());
        viewHolder.servingRecipeTextView.setText(String.valueOf(listRecipe.get(i).getStrCategory()));
        //Image
        Picasso.with(mActivity).load(listRecipe.get(i).getStrDrinkThumb()).error(R.drawable.placeholder).placeholder(R.drawable.placeholder).into(viewHolder.recipeImageView);
    }

    @Override
    public int getItemCount() {
        return listRecipe.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleRecipeTextView;
        TextView servingRecipeTextView;
        ImageView recipeImageView;
        View mView;

        public ViewHolder(View view) {
            super(view);
            mView = itemView;
            titleRecipeTextView = (TextView) view.findViewById(R.id.titleRecipeTextView);
            servingRecipeTextView = (TextView) view.findViewById(R.id.typeRecipeTextView);
            recipeImageView = (ImageView) view.findViewById(R.id.recipeImageView);

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    if (listRecipe != null && position >= 0 && position <= listRecipe.size() - 1 && listRecipe.get(position) != null) {
                        listener.onItemClick(listRecipe.get(position));
                    }
                }
            });
        }
    }

    public void setListRecipe(ArrayList<DrinkRecipe> listRecipe) {
        this.listRecipe = listRecipe;
    }
}
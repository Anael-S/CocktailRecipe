package anaels.com.cocktailrecipe.adapter;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import anaels.com.cocktailrecipe.R;


/**
 * Display the Step on recipe activity
 */
public class StepAdapter extends RecyclerView.Adapter<StepAdapter.ViewHolder> {
    private ArrayList<String> listStep;
    private Activity mActivity;

    public StepAdapter(Activity activity, ArrayList<String> listStep) {
        this.mActivity = activity;
        this.listStep = listStep;
    }

    @Override
    public StepAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_step, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        //Text
        viewHolder.stepNumberTextView.setText(mActivity.getString(R.string.step_number, i + 1));
        viewHolder.stepShortDescriptionTextView.setText(listStep.get(i));
    }

    @Override
    public int getItemCount() {
        return listStep.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView stepNumberTextView;
        TextView stepShortDescriptionTextView;
        View mView;
        CardView cardViewIngredientsRecipes;

        public ViewHolder(View view) {
            super(view);
            mView = itemView;
            stepNumberTextView = (TextView) view.findViewById(R.id.stepNumberTextView);
            stepShortDescriptionTextView = (TextView) view.findViewById(R.id.stepShortDescriptionTextView);
            cardViewIngredientsRecipes = (CardView) view.findViewById(R.id.cardViewIngredientsRecipes);
        }
    }

    public void setListStep(ArrayList<String> listStep) {
        this.listStep = listStep;
    }
}
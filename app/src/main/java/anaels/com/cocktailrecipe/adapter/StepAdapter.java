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
import anaels.com.cocktailrecipe.api.model.Step;
import anaels.com.cocktailrecipe.helper.StepHelper;


/**
 * Display the Step on recipe activity
 */
public class StepAdapter extends RecyclerView.Adapter<StepAdapter.ViewHolder> {
    private ArrayList<Step> listStep;
    private Activity mActivity;
    private final OnItemClickListener listener;
    private int lastPosSelected = 0;

    private static final String RECIPE_INTRODUCTION_VIDEO = "Recipe Introduction";

    public interface OnItemClickListener {
        void onItemClick(Step item);
    }

    public StepAdapter(Activity activity, ArrayList<Step> listStep, OnItemClickListener listener) {
        this.mActivity = activity;
        this.listStep = listStep;
        this.listener = listener;
        lastPosSelected = StepHelper.getSelectStepPosition(listStep);
    }

    @Override
    public StepAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_step, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        //Background - we dont highlight on mobile
        if (mActivity.getResources().getBoolean(R.bool.isTablet)) {
            if (listStep.get(i).isSelected()) {
                viewHolder.cardViewIngredientsRecipes.setBackgroundResource(R.drawable.card_highlighted);
            } else {
                viewHolder.cardViewIngredientsRecipes.setBackgroundResource(R.drawable.card_default);
            }
        }
        //Text
        //If this is the introduction step, we dont display the step number
        if (listStep.get(i).getDescription().equals(RECIPE_INTRODUCTION_VIDEO)) {
            viewHolder.stepNumberTextView.setText("");
        } else {
            viewHolder.stepNumberTextView.setText(mActivity.getString(R.string.step_number, i));
        }
        viewHolder.stepShortDescriptionTextView.setText(listStep.get(i).getShortDescription());
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

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    if (listStep != null && position >= 0 && position <= listStep.size() - 1 && listStep.get(position) != null) {
                        //We unselect the last one selected
                        listStep.get(lastPosSelected).setSelected(false);
                        notifyItemChanged(lastPosSelected);
                        //We select the view
                        listStep.get(position).setSelected(true);
                        notifyItemChanged(position);
                        //We update the last position
                        lastPosSelected = position;

                        //We action the click
                        listener.onItemClick(listStep.get(position));
                    }
                }
            });
        }
    }

    public void setListStep(ArrayList<Step> listStep) {
        this.listStep = listStep;
    }
}
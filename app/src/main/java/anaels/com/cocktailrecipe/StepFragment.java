package anaels.com.cocktailrecipe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragment used to display a single step from the work order feature
 */
public class StepFragment extends Fragment {

    String mStep;
    ArrayList<String> mStepList;

    @BindView(R.id.instructionTextView)
    TextView instructionTextView;
    @BindView(R.id.imageStepImageView)
    ImageView imageStepImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_step, container, false);
        ButterKnife.bind(this, rootView);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mStep = bundle.getString(RecipeActivity.KEY_INTENT_STEP);
            mStepList = bundle.getStringArrayList(RecipeActivity.KEY_INTENT_STEP_LIST);
        }

        instructionTextView.setText(mStep);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        imageStepImageView.setVisibility(View.GONE);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
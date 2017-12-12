package anaels.com.cocktailrecipe;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import anaels.com.cocktailrecipe.adapter.StepPagerAdapter;
import anaels.com.cocktailrecipe.animations.ReaderViewPagerTransformer;
import anaels.com.cocktailrecipe.api.model.Step;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Display the detail of a specific recipe
 */
public class StepActivity extends AppCompatActivity {

    Step mStep;
    ArrayList<Step> mStepList;
    String mRecipeName;
    Context mContext;

    StepPagerAdapter mPagerAdapter;

    //Footer
    @BindView(R.id.layoutPreviousStep)
    CardView layoutPreviousStep;
    @BindView(R.id.layoutNextStep)
    CardView layoutNextStep;
    @BindView(R.id.pagerDetailStep)
    ViewPager pagerDetailStep;
    @BindView(R.id.tabDots)
    TabLayout tabDots;
    @BindView(R.id.footerView)
    RelativeLayout footerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        ButterKnife.bind(this);
        mContext = this;

        mStep = getIntent().getParcelableExtra(RecipeActivity.KEY_INTENT_STEP);
        mRecipeName = getIntent().getStringExtra(RecipeActivity.KEY_INTENT_RECIPE_NAME);
        mStepList = getIntent().getParcelableArrayListExtra(RecipeActivity.KEY_INTENT_STEP_LIST);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState != null && mStep == null) {
            mStep = savedInstanceState.getParcelable(RecipeActivity.KEY_INTENT_STEP);
        }

        //UI
        if (mStepList != null) {
            initUI();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(RecipeActivity.KEY_INTENT_STEP, mStep);
        super.onSaveInstanceState(outState);
    }

    private void initUI() {
        //ActionBar
        updateActionBar();

        //Footer
        if (shouldDisplayVideoFullMode()) {
            footerView.setVisibility(View.GONE);
        } else {
            footerView.setVisibility(View.VISIBLE);
            updateFooter();
        }

        layoutPreviousStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newPos = mStep.getId() - 1;
                updateStep(newPos);
            }
        });
        layoutNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newPos = mStep.getId() + 1;
                updateStep(newPos);
            }
        });

        //Detail step
        mPagerAdapter = new StepPagerAdapter(getSupportFragmentManager(), mStepList);
        pagerDetailStep.setAdapter(mPagerAdapter);
        pagerDetailStep.setPageTransformer(false, new ReaderViewPagerTransformer(ReaderViewPagerTransformer.TransformType.FLOW));
        pagerDetailStep.setOffscreenPageLimit(mStepList.size());
        if (shouldDisplayVideoFullMode()) {
            tabDots.setVisibility(View.GONE);
        } else {
            tabDots.setVisibility(View.VISIBLE);
        }
        tabDots.setupWithViewPager(pagerDetailStep, true);
        pagerDetailStep.setCurrentItem(mStep.getId());

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean shouldDisplayVideoFullMode() {
        boolean should = false;
        if (!getResources().getBoolean(R.bool.isTablet) &&
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            should = true;
        }
        return should;
    }

    private void updateFooter() {
        if (mStep.getId() == 0) {
            layoutPreviousStep.setVisibility(View.INVISIBLE);
        } else {
            layoutPreviousStep.setVisibility(View.VISIBLE);
        }
        if (mStep.getId() == mStepList.size() - 1) {
            layoutNextStep.setVisibility(View.INVISIBLE);
        } else {
            layoutNextStep.setVisibility(View.VISIBLE);
        }
    }

    private void updateStep(int newPosStep) {
        pagerDetailStep.setCurrentItem(newPosStep);
        mStep = mStepList.get(newPosStep);
        updateFooter();
    }

    private void updateActionBar() {
        if (mStep != null) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mRecipeName);
            }
        }
    }
}

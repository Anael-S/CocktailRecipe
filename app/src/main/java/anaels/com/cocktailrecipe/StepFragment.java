package anaels.com.cocktailrecipe;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import anaels.com.cocktailrecipe.api.model.Step;
import anaels.com.cocktailrecipe.helper.InternetConnectionHelper;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragment used to display a single step from the work order feature
 */
public class StepFragment extends Fragment {

    Step mStep;
    ArrayList<Step> mStepList;

    @BindView(R.id.videoPlayer)
    SimpleExoPlayerView videoPlayer;
    @BindView(R.id.instructionTextView)
    TextView instructionTextView;
    @BindView(R.id.imageStepImageView)
    ImageView imageStepImageView;

    protected SimpleExoPlayer player;

    private DataSource.Factory mediaDataSourceFactory;
    private DefaultTrackSelector trackSelector;
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private long positionPlayer = 100;

    private static final String KEY_SELECTED_POSITION_PLAYER = "keyPosPlayer";
    private boolean autoPlay = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_step, container, false);
        ButterKnife.bind(this, rootView);

        mediaDataSourceFactory = new DefaultDataSourceFactory(getContext(), Util.getUserAgent(getContext(), "mediaPlayerBakingRecipe"), BANDWIDTH_METER);

        if (savedInstanceState != null) {
            positionPlayer = savedInstanceState.getLong(KEY_SELECTED_POSITION_PLAYER, 100);
            autoPlay = true;
        } else {
            autoPlay = false;
        }


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mStep = bundle.getParcelable(RecipeActivity.KEY_INTENT_STEP);
            mStepList = bundle.getParcelableArrayList(RecipeActivity.KEY_INTENT_STEP_LIST);
        }

        instructionTextView.setText(mStep.getDescription());

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(KEY_SELECTED_POSITION_PLAYER, positionPlayer);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        imageStepImageView.setVisibility(View.GONE);
        //If we actually got a video
        if (mStep.getVideoURL() != null && !mStep.getVideoURL().isEmpty() && InternetConnectionHelper.isNetworkAvailable(getContext())) {
            initializePlayer();
            videoPlayer.setVisibility(View.VISIBLE);
        } else {
            videoPlayer.setVisibility(View.GONE);
            //Image
            if (mStep.getThumbnailURL() != null && !mStep.getThumbnailURL().isEmpty()) {
                imageStepImageView.setVisibility(View.VISIBLE);
                Picasso.with(getContext()).load(mStep.getThumbnailURL()).error(R.drawable.placeholder_recipe).into(imageStepImageView);
            }
        }
    }

    private void initializePlayer() {
        //if the URL is valid
        if (mStep.getVideoURL() != null && !mStep.getVideoURL().isEmpty()) {
            Uri lVideoURI;
            try {
                lVideoURI = Uri.parse(mStep.getVideoURL());
            } catch (Exception e) {
                lVideoURI = null;
            }
            if (lVideoURI != null) { // then we init the player
                videoPlayer.requestFocus();
                TrackSelection.Factory videoTrackSelectionFactory =
                        new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
                trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
                player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
                videoPlayer.setPlayer(player);
                player.setPlayWhenReady(autoPlay);

                MediaSource mediaSource = new ExtractorMediaSource(lVideoURI,
                        mediaDataSourceFactory, new DefaultExtractorsFactory(), null, null);
                player.prepare(mediaSource);
                player.seekTo(positionPlayer);
            }
        }

    }

    private void releasePlayer() {
        if (player != null) {
            positionPlayer = player.getCurrentPosition();
            player.release();
            player = null;
            trackSelector = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player == null) {
            initializePlayer();
        } else {
            player.seekTo(positionPlayer);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

}
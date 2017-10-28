package com.test.csc543.csc543_final;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment  {
    private SnakeView snakeView;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
       View view =
               inflater.inflate(R.layout.fragment_main, container, false);
        snakeView = (SnakeView) view.findViewById(R.id.snakeView);

        return view;
    }

    // set up volume control once Activity is created
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //launch sound resources from here
    }


    // when MainActivity is paused, terminate the game
    @Override
    public void onPause() {

        super.onPause();
        snakeView.stopGame(); // terminates the game
    }

    // when MainActivity is paused, MainActivityFragment releases resources
    @Override
    public void onDestroy() {

        super.onDestroy();
        snakeView.releaseResources();
    }
}

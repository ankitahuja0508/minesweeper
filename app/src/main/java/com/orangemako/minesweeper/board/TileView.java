package com.orangemako.minesweeper.board;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.LevelListDrawable;
import android.view.View;

import com.orangemako.minesweeper.MainApplication;
import com.orangemako.minesweeper.R;
import com.orangemako.minesweeper.drawable.BeveledTileDrawable;
import com.orangemako.minesweeper.drawable.ConcentricCirclesDrawable;
import com.orangemako.minesweeper.drawable.TextDrawable;
import com.orangemako.minesweeper.exceptions.InvalidArgumentException;
import com.orangemako.minesweeper.game.Game;
import com.orangemako.minesweeper.utilities.GraphicsUtils;
import com.squareup.otto.Bus;

import java.util.HashMap;
import java.util.Map;

public class TileView extends View {
    // Board Square states
    public static final int COVERED = 0;
    public static final int FLAGGED_AS_MINE = 1;
    public static final int UNCOVERED = 2;

    // User gestures
    public static final int CLICK = 0;
    public static final int LONG_CLICK = 1;

    private LevelListDrawable mDrawableContainer;
    private int mXGridCoordinate;
    private int mYGridCoordinate;

    private Bus mGameBus;

    static Map<Integer, Integer> sAdjacentMineCountToColorMap = new HashMap<>();
    static Map<Integer, Integer> sAdjacentMineCountToDrawableMap = new HashMap<>();

    // Colors for adjacent mines count
    static {
        sAdjacentMineCountToColorMap.put(1, Color.RED);
        sAdjacentMineCountToColorMap.put(2, Color.BLUE);
        sAdjacentMineCountToColorMap.put(3, Color.GREEN);
        sAdjacentMineCountToColorMap.put(4, Color.DKGRAY);
        sAdjacentMineCountToColorMap.put(5, Color.MAGENTA);
        sAdjacentMineCountToColorMap.put(6, Color.CYAN);
        sAdjacentMineCountToColorMap.put(7, Color.YELLOW);
        sAdjacentMineCountToColorMap.put(8, Color.RED);

        sAdjacentMineCountToDrawableMap.put(1, R.drawable.ic_tile_number_1);
        sAdjacentMineCountToDrawableMap.put(2, R.drawable.ic_tile_number_2);
        sAdjacentMineCountToDrawableMap.put(3, R.drawable.ic_tile_number_3);
        sAdjacentMineCountToDrawableMap.put(4, R.drawable.ic_tile_number_4);
        sAdjacentMineCountToDrawableMap.put(5, R.drawable.ic_tile_number_5);
        sAdjacentMineCountToDrawableMap.put(6, R.drawable.ic_tile_number_6);
        sAdjacentMineCountToDrawableMap.put(7, R.drawable.ic_tile_number_7);
        sAdjacentMineCountToDrawableMap.put(8, R.drawable.ic_tile_number_8);
    }

    public TileView(Context context, int xGridCoordinate, int yGridCoordinate) throws InvalidArgumentException {
        super(context);

        mXGridCoordinate = xGridCoordinate;
        mYGridCoordinate = yGridCoordinate;

        init();
    }

    private void init() throws InvalidArgumentException {
        mGameBus = MainApplication.getGameBus();

        setupDrawableBackgrounds();
        setupListeners();
    }

    private void setupListeners() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // Notify the game that the user has performed an action on this tile.
                mGameBus.post(new Game.TileViewActionEvent(TileView.this, CLICK));
            }
        });

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Notify the game that the user has performed an action on this tile.
                mGameBus.post(new Game.TileViewActionEvent(TileView.this, LONG_CLICK));

                // Return true to consume event.
                return true;
            }
        });
    }

    private void setupDrawableBackgrounds() throws InvalidArgumentException {
        Drawable coveredTile = setupCoveredTile();
        Drawable flaggedMineDrawable =  getResources().getDrawable( R.drawable.ic_tile_flagged);

        mDrawableContainer = new LevelListDrawable();
        mDrawableContainer.addLevel(0, COVERED, coveredTile);
        mDrawableContainer.addLevel(0, FLAGGED_AS_MINE, flaggedMineDrawable);

        setBackground(mDrawableContainer);
    }

    private Drawable setupCoveredTile() throws InvalidArgumentException {
        /*// TODO: Move this to a theme
        int colorInner = GraphicsUtils.getColor(getContext(), R.color.blue_200);
        int colorTop = GraphicsUtils.getColor(getContext(), R.color.blue_300);
        int colorLeft = GraphicsUtils.getColor(getContext(), R.color.blue_400);
        int colorBottom = GraphicsUtils.getColor(getContext(), R.color.blue_500);
        int colorRight = GraphicsUtils.getColor(getContext(), R.color.blue_600);

        int[] tileColors = new int[]{colorInner, colorLeft, colorTop, colorRight, colorBottom};*/

        return getResources().getDrawable( R.drawable.ic_tile_mask_default);
    }

    public void setupUncoveredTileDrawable(BoardSquare boardSquare) {
        Drawable uncoveredDrawable = getResources().getDrawable( R.drawable.ic_tile_blank);;

        if(boardSquare != null && boardSquare.doesContainMine()) {
            uncoveredDrawable = getResources().getDrawable( R.drawable.ic_tile_bomb);
        }
        else {
            String adjacentMineCountText;
            int textColor = 0;

            if(boardSquare == null) {
                adjacentMineCountText = "";
            }
            else {
                int adjacentMinesCount = boardSquare.getAdjacentMinesCount();

                uncoveredDrawable = getResources().getDrawable(sAdjacentMineCountToDrawableMap.get(adjacentMinesCount));

            }

        }
        mDrawableContainer.addLevel(0, UNCOVERED, uncoveredDrawable);
    }

    public int getXGridCoordinate() {
        return mXGridCoordinate;
    }

    public int getYGridCoordinate() {
        return mYGridCoordinate;
    }

    public int getState() {
        return mDrawableContainer.getLevel();
    }

    public void setState(int state) {
        mDrawableContainer.setLevel(state);
    }
}

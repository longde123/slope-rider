package com.sloperider.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.sloperider.ComponentFactory;
import com.sloperider.SlopeRider;
import com.sloperider.component.Level;
import com.sloperider.physics.PhysicsWorld;

/**
 * Created by jpx on 03/12/15.
 */
public class GameScreen extends Screen {
    private class UI {
        private Skin _skin;

        private Table _parent;

        UI(final Stage stage) {
            _skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

            _parent = new Table(_skin);

            final Button playButton = new Button(
                new Image(new TextureRegion(new Texture("ui/play_button.png"))),
                _skin
            );

            playButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    playButtonClicked(UI.this, (Button) actor);
                }
            });

            _parent.add(playButton).center();

            _parent.pack();

            stage.addActor(_parent);
        }

        public final void hide() {
            _parent.setTouchable(Touchable.disabled);
            _parent.setVisible(false);
        }

        public final void show() {
            _parent.setTouchable(Touchable.enabled);
            _parent.setVisible(true);
        }
    }

    private void playButtonClicked(final UI ui, final Button button) {
        _activeLevel.spawnSleigh();
    }

    private Stage _levelStage;
    private Stage _uiStage;
    private UI _ui;

    private PhysicsWorld _physicsWorld;

    private ComponentFactory _componentFactory;

    private Level _activeLevel;

    public GameScreen(final MasterScreen masterScreen) {
        _levelStage = new Stage();
        _uiStage = new Stage();
        _ui = new UI(_uiStage);

        _physicsWorld = new PhysicsWorld();

        _componentFactory = new ComponentFactory(_levelStage, masterScreen._assetManager, _physicsWorld);
        _componentFactory.ready();
    }

    @Override
    public void start() {
        Gdx.input.setInputProcessor(new InputMultiplexer(_uiStage, _levelStage));

        _activeLevel = _componentFactory.createLevel("level/level0.lvl");

        _activeLevel.setListener(new Level.Listener() {
            @Override
            public void stageChanged(final String state) {
                if (state.equals("playing")) {
                    _ui.hide();
                } else if (state.equals("editing")) {
                    _ui.show();
                }
            }
        });
    }

    @Override
    public void stop() {

    }

    @Override
    public void update(float deltaTime) {
        _physicsWorld.update(deltaTime);

        _levelStage.act(deltaTime);
        _uiStage.act(deltaTime);
    }

    @Override
    public void render() {
        _levelStage.draw();
        _uiStage.draw();

        _physicsWorld.render(_levelStage.getCamera());
    }

    @Override
    public void dispose() {
    }
}
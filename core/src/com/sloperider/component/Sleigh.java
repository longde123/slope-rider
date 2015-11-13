package com.sloperider.component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.sloperider.ComponentFactory;
import com.sloperider.SlopeRider;
import com.sloperider.physics.PhysicsActor;

/**
 * Created by jpx on 08/11/15.
 */
public class Sleigh extends Component {
    private static final float _spriteRatio = 516.f / 298.f;

    private Texture _texture;
    private TextureRegion _textureRegion;

    private Body _body;

    public Sleigh() {
    }

    @Override
    protected void setParent(Group parent) {
        super.setParent(parent);

        final float baseSize = 1.f;

        setSize(baseSize, baseSize / _spriteRatio);
        setOrigin(getWidth() / 2.f, getHeight() / 2.f);
    }

    @Override
    protected void doReady(ComponentFactory componentFactory) {
        _textureRegion = new TextureRegion(_texture);
    }

    @Override
    protected void doAct(float delta) {

    }

    @Override
    protected void doDraw(Batch batch) {
        batch.draw(
            _textureRegion,
            getX() * SlopeRider.PIXEL_PER_UNIT,
            getY() * SlopeRider.PIXEL_PER_UNIT,
            getOriginX(),
            getOriginY(),
            getWidth(),
            getHeight(),
            getScaleX() * SlopeRider.PIXEL_PER_UNIT,
            getScaleY() * SlopeRider.PIXEL_PER_UNIT,
            getRotation()
        );
    }

    @Override
    public void requireAssets(AssetManager assetManager) {
        assetManager.load("texture/sleigh.png", Texture.class);
    }

    @Override
    public void manageAssets(AssetManager assetManager) {
        _texture = assetManager.get("texture/sleigh.png", Texture.class);
    }

    @Override
    public void initializeBody(World world) {

        BodyDef bodyDef = new BodyDef();

        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(getX(), getY());

        _body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();

        final float width = getWidth();
        final float height = getHeight();

        shape.set(new float[] {
            -width * 0.45f, -height * 0.35f,
            width * 0.38f, -height * 0.35f,
            width * 0.38f, height * 0.15f,
            -width * 0.45f, height * 0.15f
        });

        FixtureDef fixtureDef = new FixtureDef();

        fixtureDef.shape = shape;
        fixtureDef.density = 2.f;
        fixtureDef.friction = 0.1f;
        fixtureDef.restitution = 0.0f;

        Fixture fixture = _body.createFixture(fixtureDef);
    }

    @Override
    public void updateBody(World world) {
        final Vector2 position = _body.getPosition().cpy();;
        final float rotation = MathUtils.radiansToDegrees * _body.getAngle();

        setPosition(position.x, position.y);
        setRotation(rotation);
    }
}

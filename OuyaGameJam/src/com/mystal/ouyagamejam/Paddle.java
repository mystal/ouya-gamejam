package com.mystal.ouyagamejam;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Paddle {
    public Body paddle;
    public Body dexter, sinister;

    public Sprite[][] paddleSprites = initPaddleSprites();

    private World world;

    public OuyaGameJam.EntityColor color;
    public int frame;

    public Paddle(World world, Vector2 pos) {
        this(world, OuyaGameJam.EntityColor.green, pos);
    }

    public Paddle(World world, OuyaGameJam.EntityColor color, Vector2 pos) {
        this.world = world;
        this.color = color;

        frame = 0;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(pos);

        Pixmap paddlePixmap = new Pixmap(GameSettings.PADDLE_WIDTH, GameSettings.PADDLE_HEIGHT, Pixmap.Format.RGB888);
        paddlePixmap.setColor(1, 1, 1, 1);
        paddlePixmap.fill();
        Texture paddleTexture = new Texture(paddlePixmap);
        Sprite paddleSprite = new Sprite(paddleTexture);
        paddlePixmap.dispose();

        paddle = world.createBody(bodyDef);
        paddle.setUserData(paddleSprite);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(GameSettings.PADDLE_WIDTH/2.0f*GameSettings.WORLD_TO_BOX, GameSettings.PADDLE_HEIGHT/2.0f*GameSettings.WORLD_TO_BOX);
        paddle.createFixture(polygonShape, 1f);
        polygonShape.dispose();

        if (GameSettings.KATAMARI_CONTROLS) {
            bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.KinematicBody;

            Pixmap anchorPixmap = new Pixmap(GameSettings.PADDLE_HEIGHT, GameSettings.PADDLE_HEIGHT, Pixmap.Format.RGB888);
            anchorPixmap.setColor(0.5f, 0.5f, 0.5f, 1.0f);
            anchorPixmap.fillCircle(GameSettings.PADDLE_HEIGHT/2, GameSettings.PADDLE_HEIGHT/2, GameSettings.PADDLE_HEIGHT/2);
            Sprite anchorSprite = new Sprite(new Texture(anchorPixmap));
            anchorPixmap.dispose();

            bodyDef.position.set((pos.x + (GameSettings.PADDLE_WIDTH/2) + 20)*GameSettings.WORLD_TO_BOX, pos.y);
            dexter = world.createBody(bodyDef);
            dexter.setUserData(anchorSprite);

            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(GameSettings.PADDLE_HEIGHT/2.0f*GameSettings.WORLD_TO_BOX);
            dexter.createFixture(circleShape, 0.5f);

            bodyDef.position.set((pos.x - (GameSettings.PADDLE_WIDTH/2) - 20)*GameSettings.WORLD_TO_BOX, pos.y);
            sinister = world.createBody(bodyDef);
            sinister.setUserData(anchorSprite);

            sinister.createFixture(circleShape, 0.5f);

            circleShape.dispose();
        }

//        DistanceJointDef jointDef = new DistanceJointDef();
//        jointDef.initialize(paddle, sinister, paddle.getWorldCenter().add(-GameSettings.PADDLE_WIDTH/2.0f*GameSettings.WORLD_TO_BOX, 0), sinister.getWorldCenter());
//        world.createJoint(jointDef);
//        jointDef.initialize(paddle, dexter, paddle.getWorldCenter().add(GameSettings.PADDLE_WIDTH/2.0f*GameSettings.WORLD_TO_BOX, 0), dexter.getWorldCenter());
//        world.createJoint(jointDef);
    }

    public Vector2 dexterVelocity() {
        Vector2 velocity = new Vector2();
        velocity.x = (Gdx.input.isKeyPressed(Input.Keys.RIGHT)?1:0) - (Gdx.input.isKeyPressed(Input.Keys.LEFT)?1:0);
        velocity.y = (Gdx.input.isKeyPressed(Input.Keys.UP)?1:0) - (Gdx.input.isKeyPressed(Input.Keys.DOWN)?1:0);
        return velocity;
    }

    public Vector2 sinisterVelocity() {
        Vector2 velocity = new Vector2();
        velocity.x = (Gdx.input.isKeyPressed(Input.Keys.D)?1:0) - (Gdx.input.isKeyPressed(Input.Keys.A)?1:0);
        velocity.y = (Gdx.input.isKeyPressed(Input.Keys.W)?1:0) - (Gdx.input.isKeyPressed(Input.Keys.S)?1:0);
        return velocity;
    }

    public void update() {
        //TODO: get anchor movement, transform into direction paddle is facing
        //TODO: add to the velocities of the anchors
        dexter.setLinearVelocity(dexterVelocity().rotate((float)(paddle.getAngle()*180f/Math.PI)));
        sinister.setLinearVelocity(sinisterVelocity().rotate((float) (paddle.getAngle() * 180f / Math.PI)));
        //TODO: compute the center point between them
        Vector2 center = dexter.getPosition().sub(sinister.getPosition());
        //TODO: set the paddle's position to that
        //TODO: change angle to face the new forward direction
        float newAngle = new Vector2(center.y, -center.x).angle();
        paddle.setTransform(center, newAngle);
        //TODO: pop anchors back where they belong, so don't bother using joints use this math
        dexter.setTransform(dexter.getPosition().sub(center).nor().mul(GameSettings.PADDLE_WIDTH/2).add(center), 0);
        sinister.setTransform(sinister.getPosition().sub(center).nor().mul(GameSettings.PADDLE_WIDTH/2).add(center), 0);
    }

    public void draw(SpriteBatch batch) {
        Sprite sprite = null;
        if (!GameSettings.USE_SPRITES) {
            sprite = (Sprite) paddle.getUserData();
        } else {
            sprite = paddleSprites[color.ordinal()][frame];
        }

        sprite.setPosition((paddle.getPosition().x*GameSettings.BOX_TO_WORLD) - sprite.getOriginX(),
                (paddle.getPosition().y*GameSettings.BOX_TO_WORLD) - sprite.getOriginY());
        sprite.setRotation((float)(paddle.getAngle()*180.0/Math.PI));
        sprite.draw(batch);

        if (GameSettings.KATAMARI_CONTROLS) {
            sprite = (Sprite) dexter.getUserData();
            sprite.setPosition((dexter.getPosition().x*GameSettings.BOX_TO_WORLD) - sprite.getOriginX(),
                    (dexter.getPosition().y*GameSettings.BOX_TO_WORLD) - sprite.getOriginY());
            sprite.draw(batch);
            sprite = (Sprite) sinister.getUserData();
            sprite.setPosition((sinister.getPosition().x*GameSettings.BOX_TO_WORLD) - sprite.getOriginX(),
                    (sinister.getPosition().y*GameSettings.BOX_TO_WORLD) - sprite.getOriginY());
            sprite.draw(batch);
        }
    }

    private static Sprite[][] initPaddleSprites() {
        Texture tex = new Texture(Gdx.files.internal("sprites/loadbar-spritesheet.png"));
        TextureRegion[][] regions = TextureRegion.split(tex, GameSettings.PADDLE_WIDTH, GameSettings.PADDLE_HEIGHT);

        Sprite[][] sprites = new Sprite[regions.length][regions[0].length];
        for (int j = 0; j < regions.length; j++) {
            sprites[j] = new Sprite[regions[0].length];
            for (int i = 0; i < regions[0].length; i++) {
                sprites[j][i] = new Sprite(regions[j][i]);
            }
        }

        return sprites;
    }
}

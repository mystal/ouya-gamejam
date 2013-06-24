package com.mystal.ouyagamejam;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;

public class Paddle {
    public Body paddle;
    public Body dexter, sinister;

    private World world;

    public Paddle(World world, float x, float y) {
        this.world = world;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(x*GameSettings.WORLD_TO_BOX, y*GameSettings.WORLD_TO_BOX);

        Pixmap paddlePixmap = new Pixmap(128, 32, Pixmap.Format.RGB888);
        paddlePixmap.setColor(1, 1, 1, 1);
        paddlePixmap.fillRectangle(0, 0, GameSettings.PADDLE_WIDTH, GameSettings.PADDLE_HEIGHT);
        Texture paddleTexture = new Texture(paddlePixmap);
        Sprite paddleSprite = new Sprite(paddleTexture, 0, 0, GameSettings.PADDLE_WIDTH, GameSettings.PADDLE_HEIGHT);
        paddlePixmap.dispose();

        paddle = world.createBody(bodyDef);
        paddle.setUserData(paddleSprite);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(GameSettings.PADDLE_WIDTH/2.0f*GameSettings.WORLD_TO_BOX, GameSettings.PADDLE_HEIGHT/2.0f*GameSettings.WORLD_TO_BOX);
        paddle.createFixture(polygonShape, 1f);
        polygonShape.dispose();

        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;

        Pixmap anchorPixmap = new Pixmap(32, 32, Pixmap.Format.RGB888);
        anchorPixmap.setColor(0.5f, 0.5f, 0.5f, 1.0f);
        anchorPixmap.fillCircle(GameSettings.PADDLE_HEIGHT/2, GameSettings.PADDLE_HEIGHT/2, GameSettings.PADDLE_HEIGHT/2);
        Sprite anchorSprite = new Sprite(new Texture(anchorPixmap), 0, 0, GameSettings.PADDLE_HEIGHT, GameSettings.PADDLE_HEIGHT);
        anchorPixmap.dispose();

        bodyDef.position.set((x + (GameSettings.PADDLE_WIDTH/2) + 20)*GameSettings.WORLD_TO_BOX, y);
        dexter = world.createBody(bodyDef);
        dexter.setUserData(anchorSprite);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(GameSettings.PADDLE_HEIGHT/2.0f*GameSettings.WORLD_TO_BOX);
        dexter.createFixture(circleShape, 0.5f);

        bodyDef.position.set((x - (GameSettings.PADDLE_WIDTH/2) - 20)*GameSettings.WORLD_TO_BOX, y);
        sinister = world.createBody(bodyDef);
        sinister.setUserData(anchorSprite);

        sinister.createFixture(circleShape, 0.5f);

        circleShape.dispose();

//        DistanceJointDef jointDef = new DistanceJointDef();
//        jointDef.initialize(paddle, sinister, paddle.getWorldCenter().add(-GameSettings.PADDLE_WIDTH/2.0f*GameSettings.WORLD_TO_BOX, 0), sinister.getWorldCenter());
//        world.createJoint(jointDef);
//        jointDef.initialize(paddle, dexter, paddle.getWorldCenter().add(GameSettings.PADDLE_WIDTH/2.0f*GameSettings.WORLD_TO_BOX, 0), dexter.getWorldCenter());
//        world.createJoint(jointDef);
    }

    public Vector2 moveDexter() {
        return null;
    }

    public void update() {
        //TODO: get anchor movement, transform into direction paddle is facing
        //TODO: add to the positions of the anchors
        //TODO: compute the center point between them
        //TODO: set the paddle's position to that
        //TODO: change angle to face the new forward direction
        //transform.LookAt(Vector3.Cross(AnchorDexter - AnchorSinister, Vector3.up)+center, Vector3.up)
        //TODO: pop anchors back where they belong, so don't bother using joints use this math
        //AnchorDexter = (AnchorDexter - center).normalized * transform.localScale.x/2f+center;
    }

    public void draw(SpriteBatch batch) {
        Sprite sprite = (Sprite) paddle.getUserData();
        sprite.setPosition((paddle.getPosition().x*GameSettings.BOX_TO_WORLD) - sprite.getOriginX(),
                (paddle.getPosition().y*GameSettings.BOX_TO_WORLD) - sprite.getOriginY());
        sprite.setRotation((float)(paddle.getAngle()*180.0/Math.PI));
        sprite.draw(batch);
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

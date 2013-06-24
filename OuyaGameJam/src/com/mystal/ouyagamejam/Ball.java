package com.mystal.ouyagamejam;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Ball {
    private static Sprite sprite = initSprite();

    private static Color[] colors = {
        new Color(1, 0, 0, 1),
        new Color(0, 1, 0, 1),
        new Color(0, 0, 1, 1)
    };
    private static int nextColor = 0;

    private World world;

    public Body body;
    private Color myColor;

    public Ball(World world, Vector2 pos, float dir) {
        this.world = world;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(pos.x, pos.y);

        body = world.createBody(bodyDef);
        body.setUserData(sprite);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(GameSettings.BALL_RADIUS*GameSettings.WORLD_TO_BOX);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.density = 0.5f;
        fixtureDef.restitution = 1f;
        body.createFixture(fixtureDef);

        Vector2 velocity = new Vector2(0, GameSettings.BALL_SPEED);
        velocity = velocity.rotate(dir);
        body.setLinearVelocity(velocity);

        myColor = colors[nextColor];
        nextColor = (nextColor + 1)%3;
    }

    public void draw(SpriteBatch batch) {
        sprite.setColor(myColor);
        sprite.setPosition((body.getPosition().x*GameSettings.BOX_TO_WORLD) - sprite.getOriginX(),
                (body.getPosition().y*GameSettings.BOX_TO_WORLD) - sprite.getOriginY());
        sprite.draw(batch);
    }

    private static Sprite initSprite() {
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGB888);
        //pixmap.setColor(0.5f, 0.5f, 0.5f, 1);
        //pixmap.fill();
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fillCircle(GameSettings.BALL_RADIUS, GameSettings.BALL_RADIUS, GameSettings.BALL_RADIUS);
        Texture tex = new Texture(pixmap);
        Sprite sprite = new Sprite(tex, 0, 0, GameSettings.BALL_RADIUS*2, GameSettings.BALL_RADIUS*2);
        pixmap.dispose();

        return sprite;
    }
}

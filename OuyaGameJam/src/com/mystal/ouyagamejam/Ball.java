package com.mystal.ouyagamejam;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Ball {
    private static Sprite oldSprite = initOldSprite();
    public static Sprite[][] sprites = initSprites();

    private static Color[] colors = {
        Color.GREEN,
        Color.RED,
        Color.YELLOW,
        Color.BLUE
    };

    public static int frame = 0;

    private World world;

    public Body body;
    public OuyaGameJam.EntityColor color;

    public Ball(World world, OuyaGameJam.EntityColor color, Vector2 pos, float dir) {
        this.world = world;
        this.color = color;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(pos.x, pos.y);

        body = world.createBody(bodyDef);

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
    }

    public void draw(SpriteBatch batch) {
        Sprite sprite = null;
        if (!GameSettings.USE_SPRITES) {
            sprite = oldSprite;
            sprite.setColor(colors[color.ordinal()]);
        } else {
            sprite = sprites[color.ordinal()][frame];
        }
        sprite.setPosition((body.getPosition().x*GameSettings.BOX_TO_WORLD) - sprite.getOriginX(),
                (body.getPosition().y*GameSettings.BOX_TO_WORLD) - sprite.getOriginY());
        sprite.draw(batch);
    }

    private static Sprite initOldSprite() {
        Pixmap pixmap = new Pixmap(GameSettings.BALL_RADIUS*2, GameSettings.BALL_RADIUS*2, Pixmap.Format.RGB888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fillCircle(GameSettings.BALL_RADIUS, GameSettings.BALL_RADIUS, GameSettings.BALL_RADIUS);
        Texture tex = new Texture(pixmap);
        Sprite sprite = new Sprite(tex);
        pixmap.dispose();

        return sprite;
    }

    private static Sprite[][] initSprites() {
        Texture spriteSheet = new Texture(Gdx.files.internal("sprites/ballsheet-nopad.png"));
        TextureRegion[][] regions = TextureRegion.split(spriteSheet, GameSettings.BALL_RADIUS*2, GameSettings.BALL_RADIUS*2);

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

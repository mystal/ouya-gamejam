package com.mystal.ouyagamejam;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;
import java.util.LinkedList;

public class OuyaGameJam implements ApplicationListener {
    private World world;
    private Box2DDebugRenderer debugRenderer;

    private Paddle paddle;
    private Body walls;
    private ArrayList<Ball> balls;
    private LinkedList<Ball> ballsToRemove;

	private OrthographicCamera camera;
    private Matrix4 projMat;
	private SpriteBatch batch;

    @Override
	public void create() {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

        world = new World(new Vector2(0, 0), true);
        debugRenderer = new Box2DDebugRenderer();

		camera = new OrthographicCamera(w, h);
        projMat = camera.combined.cpy();
		batch = new SpriteBatch();

        Vector2[] wallVertices = {
            new Vector2(0, 0).mul(GameSettings.WORLD_TO_BOX),
            new Vector2(w, 0).mul(GameSettings.WORLD_TO_BOX),
            new Vector2(w, h).mul(GameSettings.WORLD_TO_BOX),
            new Vector2(0, h).mul(GameSettings.WORLD_TO_BOX)
        };
        BodyDef wallsDef = new BodyDef();
        wallsDef.type = BodyDef.BodyType.StaticBody;
        wallsDef.position.set(-w/4, -h/4);
        walls = world.createBody(wallsDef);
        ChainShape chainShape = new ChainShape();
        chainShape.createLoop(wallVertices);
        walls.createFixture(chainShape, 0);
        chainShape.dispose();

        paddle = new Paddle(world, 0, 0);

        balls = new ArrayList<Ball>();
        ballsToRemove = new LinkedList<Ball>();

        Gdx.input.setInputProcessor(new MyInputProcessor());
        world.setContactListener(new MyContactListener());
	}

	@Override
	public void dispose() {
		batch.dispose();
	}

	@Override
	public void render() {
		//Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.input.getY();
        //System.out.println("(" + mouseX + ", " + mouseY + ")");

        Vector2 velocity = new Vector2(0, 0);
        velocity.x = (Gdx.input.isKeyPressed(Input.Keys.D)?1:0) - (Gdx.input.isKeyPressed(Input.Keys.A)?1:0);
        velocity.y = (Gdx.input.isKeyPressed(Input.Keys.W)?1:0) - (Gdx.input.isKeyPressed(Input.Keys.S)?1:0);

        float angularVelocity = (Gdx.input.isKeyPressed(Input.Keys.UP)?1:0) - (Gdx.input.isKeyPressed(Input.Keys.DOWN)?1:0);

        paddle.paddle.setLinearVelocity(velocity.mul(50f));
        paddle.paddle.setAngularVelocity(angularVelocity * 5f);

        world.step(1/45f, 6, 2);

        for (Ball ball: ballsToRemove) {
            balls.remove(ball);
            world.destroyBody(ball.body);
        }
        ballsToRemove.clear();

        //System.out.println("Bodies: " + world.getBodyCount());

        //debugRenderer.render(world, projMat);

        batch.setProjectionMatrix(projMat);
        batch.begin();
        paddle.draw(batch);
        for (Ball ball: balls) {
            ball.draw(batch);
        }
        batch.end();
	}

    private class MyContactListener implements ContactListener {
        @Override
        public void beginContact(Contact contact) {
            Body bodyA = contact.getFixtureA().getBody();
            Body bodyB = contact.getFixtureB().getBody();

            Ball toRemove = null;

            if (bodyA == paddle.paddle) {
                for (Ball ball: balls) {
                    if (ball.body == bodyB) {
                        toRemove = ball;
                        break;
                    }
                }
            } else if (bodyB == paddle.paddle) {
                for (Ball ball: balls) {
                    if (ball.body == bodyA) {
                        toRemove = ball;
                        break;
                    }
                }
            }

            if (toRemove != null) {
                Vector2 collisionNormal = contact.getWorldManifold().getNormal().mul((toRemove.body == bodyB)?1:-1);
                Vector2 paddleNormal = new Vector2(0, 1f).rotate((float)(paddle.paddle.getAngle()*180f/Math.PI));
                //TODO: take dot product of normals, check if positive or negative
                if (paddleNormal.dot(collisionNormal) < 0) {
                    ballsToRemove.add(toRemove);
                }
            }
        }

        @Override
        public void endContact(Contact contact) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

    private void createBall() {
        Vector2 paddlePos = paddle.paddle.getPosition();
        float angle = (float)(paddle.paddle.getAngle()*180/Math.PI);

        Vector2 ballSpawn = new Vector2(0, GameSettings.PADDLE_HEIGHT/2 + 3);
        ballSpawn = ballSpawn.rotate(angle);

        balls.add(new Ball(world, paddlePos.add(ballSpawn), angle));
    }

    private class MyInputProcessor implements InputProcessor {
        @Override
        public boolean keyDown(int keycode) {
            if (keycode == Input.Keys.SPACE) {
                createBall();
                return true;
            }
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean scrolled(int amount) {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}

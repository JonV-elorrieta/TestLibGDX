package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.math.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
	Texture backgroudTexture;
	Texture bucketTexture;
	Texture dropTexture;
	Sound dropSound;
	Music music;
	
	SpriteBatch spriteBatch;
	FitViewport viewPort;
    
    Sprite bucketSprite;
    
    Vector2 touchPos;
    
    Array<Sprite> drops;
    
	float dropTimer;
	
	Rectangle bucketRectangle;
	Rectangle dropRectangle;
	
    

    @Override
    public void create() {
    	backgroudTexture = new Texture("background.png");
    	bucketTexture = new Texture("bucket.png");
    	dropTexture = new Texture("drop.png");
    	dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
    	music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
    	
    	spriteBatch = new SpriteBatch();
    	viewPort = new FitViewport(15, 8);
    	
    	bucketSprite = new Sprite(bucketTexture);
    	bucketSprite.setSize(1, 1);
    	
    	touchPos = new Vector2();
    	
    	drops = new Array<Sprite>();
    	
    	this.createDroplet();
    	
    	bucketRectangle = new Rectangle();
    	dropRectangle = new Rectangle();
     
    }
    
    @Override
	public void resize(int width, int height) {
		viewPort.update(width, height, true);
	}

    @Override
    public void render() {
    	input();
    	logic();
    	draw();
    }
    
	private void input() {
		float speed = 5f;
		float delta = Gdx.graphics.getDeltaTime();
		
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			bucketSprite.translateX(speed * delta);
		} else if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			bucketSprite.translateX(-speed * delta);
		}
		
		
		if (Gdx.input.isTouched()) {
			touchPos.set(Gdx.input.getX(), Gdx.input.getY());
			viewPort.unproject(touchPos);
			bucketSprite.setCenterX(touchPos.x);
		}
			
		

	}
	
	private void logic() {
		float width = viewPort.getWorldWidth();
		float height = viewPort.getWorldHeight();
		
		float bucketWidth = bucketSprite.getWidth();
		float bucketHeight = bucketSprite.getHeight();
		
		bucketSprite.setX(MathUtils.clamp(bucketSprite.getX(), 0, width - bucketWidth));
		
		float delta = Gdx.graphics.getDeltaTime();
		
		bucketRectangle.set(bucketSprite.getX(), bucketSprite.getY(), bucketWidth, bucketHeight);
		
		for (int i = drops.size - 1; i >= 0; i--) {
			Sprite drop = drops.get(i);
			float dropWidth = drop.getWidth();
			float dropHeight = drop.getHeight();
			
			drop.translateY((-2f * delta));
			dropRectangle.set(drop.getX(), drop.getY(), dropWidth, dropHeight);
			
			if (drop.getY() < -dropHeight) {
				drops.removeIndex(i);
			}else if(bucketRectangle.overlaps(dropRectangle)){
				drops.removeIndex(i);
			}
		}
		
		
		dropTimer += delta;
		
		if (dropTimer >= 1f) {
			dropTimer = 0;
			this.createDroplet();
		}
		
		
	}
	
	private void draw() {
		ScreenUtils.clear(Color.BLACK);
		viewPort.apply();
		spriteBatch.setProjectionMatrix(viewPort.getCamera().combined);
		spriteBatch.begin();
		
		float width = viewPort.getWorldWidth();
		float height = viewPort.getWorldHeight();
		spriteBatch.draw(backgroudTexture, 0, 0, width, height);
		
		bucketSprite.draw(spriteBatch);
		
		for (Sprite drop : drops) {
			drop.draw(spriteBatch);
		}
		
		spriteBatch.end();
	}
	
	private void createDroplet() {
		float dropWidth = 1f;
		float dropHeight = 1f;
		float worldWidth = viewPort.getWorldWidth();
		float worldHeight = viewPort.getWorldHeight();
		
		Sprite drop = new Sprite(dropTexture);
		drop.setSize(dropWidth, dropHeight);
		drop.setX(MathUtils.random(0f, worldWidth - dropWidth));
		drop.setY(worldHeight);
		
		drops.add(drop);
	}

    @Override
    public void dispose() {

    }
}

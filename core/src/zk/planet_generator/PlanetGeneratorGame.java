package zk.planet_generator;

import com.badlogic.gdx.*;
import com.badlogic.gdx.utils.Json;

/**
 * Planet shader code: https://gamedev.stackexchange.com/questions/9346/2d-shader-to-draw-representation-of-rotating-sphere
 * Noise generator code: http://devmag.org.za/2009/04/25/perlin-noise/
 */
public class PlanetGeneratorGame extends ApplicationAdapter {
    private Scene scene;
    private InputMultiplexer inputMultiplexer;

    @Override
    public void create() {
//        VisUI.load(VisUI.SkinScale.X2);

        scene = new Scene();

        inputMultiplexer = new InputMultiplexer();
//        inputMultiplexer.addProcessor(scene);
        Gdx.input.setInputProcessor(inputMultiplexer);

    }

    @Override
    public void render() {
        super.render();

        float delta = Gdx.graphics.getDeltaTime();

        scene.update(delta);
    }

    @Override
    public void dispose() {
        super.dispose();
        scene.dispose();
//        VisUI.dispose();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    public Scene getScene() {
        return scene;
    }


    public void addProcessor(InputProcessor inputProcessor) {
        inputMultiplexer.addProcessor(inputProcessor);
    }

    public void removeProcessor(InputProcessor inputProcessor) {
        inputMultiplexer.removeProcessor(inputProcessor);
    }

    public void saveScene(String path) {
        Json json = new Json();
        json.toJson(scene, Gdx.files.absolute(path));
    }

    public void loadScene(String path) {
        Json json = new Json();
        scene.dispose();
        scene = json.fromJson(Scene.class, Gdx.files.absolute(path));
    }
}
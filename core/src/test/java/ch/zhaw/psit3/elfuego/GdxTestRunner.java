/*
 Copyright 2015 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package ch.zhaw.psit3.elfuego;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;

public class GdxTestRunner extends BlockJUnit4ClassRunner implements ApplicationListener {

    private final Map<FrameworkMethod, RunNotifier> invokeInRender = new HashMap<>();

    public GdxTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
        HeadlessApplicationConfiguration conf = new HeadlessApplicationConfiguration();

        new HeadlessApplication(this, conf);
        Gdx.gl = mock(GL20.class);
    }

    @Override
    public void create() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void render() {
        synchronized (invokeInRender) {
            for (Map.Entry<FrameworkMethod, RunNotifier> each : invokeInRender.entrySet()) {
                super.runChild(each.getKey(), each.getValue());
            }
            invokeInRender.clear();
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void dispose() {
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        synchronized (invokeInRender) {
            // add for invoking in render phase, where gl context is available
            invokeInRender.put(method, notifier);
        }
        // wait until that test was invoked
        waitUntilInvokedInRenderMethod();
    }

    /**
     *
     */
    private void waitUntilInvokedInRenderMethod() {
        try {
            while (true) {
                Thread.sleep(10);
                synchronized (invokeInRender) {
                    if (invokeInRender.isEmpty())
                        break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
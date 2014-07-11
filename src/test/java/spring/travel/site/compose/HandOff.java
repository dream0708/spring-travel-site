/**
 * Copyright 2014 Andy Godwin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spring.travel.site.compose;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class HandOff<T> {

    private volatile T t;

    private CountDownLatch latch = new CountDownLatch(1);

    public void put(T t) {
        this.t = t;
        latch.countDown();
    }

    public T get(int seconds) throws InterruptedException {
        latch.await(seconds, TimeUnit.SECONDS);
        return t;
    }
}

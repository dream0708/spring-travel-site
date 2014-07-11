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
package spring.travel.site.model.user;

public class Profile {

    private LifeCycle lifecycle;

    private Spending spending;

    private Gender gender;

    public Profile() {
    }

    public Profile(LifeCycle lifecycle, Spending spending, Gender gender) {
        this.lifecycle = lifecycle;
        this.spending = spending;
        this.gender = gender;
    }

    public LifeCycle getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(LifeCycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    public Spending getSpending() {
        return spending;
    }

    public void setSpending(Spending spending) {
        this.spending = spending;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}

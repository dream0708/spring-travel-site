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
package spring.travel.site.controllers;

import spring.travel.site.model.weather.Location;
import spring.travel.site.request.Request;

import java.util.Optional;

public class GeoLocator {

    private Location centralLondon = new Location(2643741, 51.512791, -0.091840);

    public Location locate(Request request) {
        Optional<Location> location = request.getUser().flatMap(
            u -> u.getAddress().map(
                a -> a.getLocation()
            )
        );

        // if no user data, try ip address

        return location.orElse(centralLondon);
    }
}

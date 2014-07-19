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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.ModelAndView;
import spring.travel.site.request.Request;
import spring.travel.site.request.RequestInfo;
import spring.travel.site.services.news.NewsService;
import spring.travel.site.view.model.NewsPage;

import java.util.Collections;
import java.util.Map;

@Controller
@RequestMapping("/news")
public class NewsController extends OptionalUserController {

    @Autowired
    private NewsService newsService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<ModelAndView> news(@RequestInfo Request requestInfo) {
        return withOptionalUser(requestInfo,
            (request, response) -> {
                newsService.news().onCompletion(
                    (newsItems) -> {
                        Map<String, ?> map =
                            NewsPage.from(request.getUser(), newsItems.orElse(Collections.emptyList()));
                        response.setResult(new ModelAndView("news", map));
                    }
                ).execute();
            }
        );
    }
}

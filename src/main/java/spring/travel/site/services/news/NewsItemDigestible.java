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
package spring.travel.site.services.news;

import org.apache.commons.lang3.StringEscapeUtils;
import spring.travel.site.model.NewsItem;

public class NewsItemDigestible {

    private String headline;

    private String standFirst;

    private String link;

    private Image image;

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getStandFirst() {
        return standFirst;
    }

    public void setStandFirst(String standFirst) {
        String unescaped = StringEscapeUtils.unescapeHtml4(standFirst);
        if (unescaped.startsWith("<p>")) {
            unescaped = unescaped.substring(3, unescaped.indexOf('<', 3));
        } else {
            unescaped = unescaped.substring(0, unescaped.indexOf('<'));
        }
        this.standFirst = stripWhitespace(unescaped).trim();
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        if (this.image == null) {
            this.image = image;
        } else {
            if (image.isLargerThan(this.image)) {
                this.image = image;
            }
        }
    }

    public NewsItem toNewsItem() {
        NewsItem item = new NewsItem();
        item.setHeadline(this.headline);
        item.setStandFirst(this.standFirst);
        item.setLink(this.link);
        item.setImage(this.image.getUrl());
        return item;
    }

    private String stripWhitespace(String s) {
        return s.replaceAll("\\s+", " ");
    }
}

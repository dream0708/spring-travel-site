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

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXException;
import spring.travel.site.model.NewsItem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class NewsDigester {

    public List<NewsItem> from(InputStream is) throws IOException {
        try {
            Digester digester = new Digester();
            digester.setValidating(false);

            digester.push(new ArrayList<NewsItemDigestible>());

            digester.addObjectCreate("rss/channel/item", NewsItemDigestible.class.getName());
            digester.addSetNext("rss/channel/item", "add");
            digester.addBeanPropertySetter("rss/channel/item/title", "headline");
            digester.addBeanPropertySetter("rss/channel/item/description", "standFirst");
            digester.addBeanPropertySetter("rss/channel/item/guid", "link");
            digester.addObjectCreate("rss/channel/item/media:content", Image.class.getName());
            digester.addSetProperties("rss/channel/item/media:content");
            digester.addSetNext("rss/channel/item/media:content", "setImage");

            List<NewsItemDigestible> items =  digester.parse(is);

            List<NewsItem> newsItems = new ArrayList<>();
            for (NewsItemDigestible item : items) {
                newsItems.add(item.toNewsItem());
            }
            return newsItems;
        } catch (SAXException se) {
            throw new IOException("Failed reading rss feed", se);
        }
    }
}

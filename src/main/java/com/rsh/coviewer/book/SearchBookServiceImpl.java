package com.rsh.coviewer.book;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rsh.coviewer.book.entity.BookEntity;
import com.rsh.coviewer.book.service.BookRepository;
import com.rsh.coviewer.book.service.SearchBookService;
import com.rsh.coviewer.music.HttpUnits;
import com.rsh.coviewer.redis.IRedisUtils;
import com.rsh.coviewer.tool.Time;
import com.rsh.coviewer.tool.Tool;
import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @DESCRIPTION :
 * @AUTHOR : WuShukai1103
 * @TIME : 2018/2/15  15:00
 */
@Component
public class SearchBookServiceImpl implements SearchBookService {
    private static final String URL = "https://api.douban.com/v2/book/search?";//豆瓣读书的api

    private final BookRepository bookRepository;
    private final IRedisUtils redisUtils;

    @Autowired
    public SearchBookServiceImpl(BookRepository bookRepository, IRedisUtils redisUtils) {
        this.bookRepository = bookRepository;
        this.redisUtils = redisUtils;
    }

    @Override
    public List<BookEntity> searchBookByName(String name) {
        if (Tool.getInstance().isNullOrEmpty(name)) {
            return null;
        }
        List<BookEntity> result = new ArrayList<>();
        //从redis中获取
        String books = redisUtils.hget("book", name);
        if (Tool.getInstance().isNotNull(books)) {
            JSONArray array = JSONArray.parseArray(books);
            for (Object o : array) {
                try {
                    result.add(Tool.getInstance().jsonToBean(o.toString(), BookEntity.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return result;
        }
        //redis 中没有，从豆瓣获取
        String myUrl = URL + "q=" + name;
        String json;
        try {
            json = HttpUnits.urlToString(myUrl).text();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        //将
        JSONObject object = (JSONObject) JSONObject.parse(json);
        JSONArray array = JSONArray.parseArray(object.getString("books"));
        for (Object o : array) {
            object = (JSONObject) o;
            BookEntity model;
            try {
                model = Tool.getInstance().jsonToBean(o.toString(), BookEntity.class);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            String id = model.getId();
            if (Tool.getInstance().isNullOrEmpty(id)) {
                continue;
            }
            //获取评分，
            if (object.containsKey("rating")) {
                object = object.getJSONObject("rating");
                if (object.containsKey("average"))
                    model.setAverage(object.getString("average"));
            }
            //如果数据库没有，则保存到数据库
            if (Tool.getInstance().isNullOrEmpty(bookRepository.findOne(id))) {
                try {
                    bookRepository.save(model);
                } catch (DataException e) {
                    e.printStackTrace();
                    continue;
                }
            }
            result.add(model);
        }
        //保存到缓存，
        /*
          类型为 hash,存储格式为 book   -    name    -    与之相关的书籍
          保存一天
         */
        redisUtils.hset("book", name, Tool.getInstance().toJson(result), Time.ONE_DAY);
        return result;
    }

    @Override
    public List<BookEntity> randBook() {
        return bookRepository.randBook();
    }

    @Override
    public BookEntity findById(long id) {
        return bookRepository.findOne(id + "");
    }

    public static void main(String[] args) {
//        SearchBookService service = new SearchBookServiceImpl();
//        service.searchBookByName("小王子");
    }
}

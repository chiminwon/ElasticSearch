package com.ming.service;

import com.ming.HtmlTool;
import com.ming.bean.HtmlBean;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Test;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@Service
public class IndexService {
    public static final String DATA_DIR = "C:/Tools/data/";
    public static final String CLUSTER_NAME_KEY = "cluster.name";
    public static final String CLUSTER_NAME = "allencluster";

    public static Client client;

    static {
        Settings settings = Settings.builder()
                .put(CLUSTER_NAME_KEY, CLUSTER_NAME)
                .build();
        try {
            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        // on shutdown
        //client.close();
    }

    public static void main(String[] args) throws Throwable {
    }

    @Test
    public void createIndex() throws Exception {
        IndicesExistsResponse res = client.admin().indices().prepareExists(CLUSTER_NAME).execute().actionGet();
        if (res.isExists()) {
            client.admin().indices().prepareDelete(CLUSTER_NAME).execute().actionGet();
        }
        client.admin().indices().prepareCreate(CLUSTER_NAME).execute().actionGet();
        new XContentFactory();

        XContentBuilder builder = XContentFactory.jsonBuilder().startObject()
                .startObject("htmlbean").startObject("properties") //type
                .startObject("title").field("type", "text")
                .field("store", true ).field("analyzer", "ik_max_word")//指定保存分词量
                .field("search_analyzer", "ik_max_word").endObject()//搜索字符串亦需分词
                .startObject("content").field("type", "text")
                .field("store", true).field("analyzer", "ik_max_word")
                .field("search_analyzer", "ik_max_word").endObject()
                .startObject("url").field("type", "text")
                .field("store", true).field("analyzer", "ik_max_word")
                .field("search_analyzer", "ik_max_word").endObject()
                .endObject().endObject().endObject();
        PutMappingRequest mapping = Requests.putMappingRequest(CLUSTER_NAME).type("htmlbean").source(builder);
        client.admin().indices().putMapping(mapping).actionGet();
    }

    @Test
    public void addHtmlToES() {
        readHtml(new File(DATA_DIR));
    }

    public void readHtml(File file) {
        if (file.isDirectory()) {
            File[] fs = file.listFiles();
            for (int i = 0; i < fs.length; i++) {
                File _file = fs[i];
                readHtml(_file);
            }
        } else {
            HtmlBean bean;
            try {
                bean = HtmlTool.parseHtml(file.getPath());
                if (null != bean) {
                    Map<String, String> dataMap = new HashMap();
                    dataMap.put("title", bean.getTitle());
                    dataMap.put("content", bean.getContent());
                    dataMap.put("url", bean.getUrl());
                    //写索引
                    client.prepareIndex(CLUSTER_NAME, "htmlbean").setSource(dataMap).execute().actionGet();
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

        }
    }


}

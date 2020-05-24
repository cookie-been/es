package com.baizhi;

import static org.junit.Assert.assertTrue;

import com.sun.jndi.cosnaming.IiopUrl;
import com.sun.xml.internal.ws.wsdl.writer.document.http.Address;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.stats.IndexStats;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.Index;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    private TransportClient transportClient;
    @Before
    public void getes() throws UnknownHostException {
        TransportAddress transportAddress = new TransportAddress(InetAddress.getByName("192.168.6.131"), 9300);

        transportClient = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(transportAddress);
    }
    @Test
    public void shouldAnswerWithTrue() {

        //获取集中所有节点
        /*List<DiscoveryNode> discoveryNodes = transportClient.listedNodes();
        for (DiscoveryNode discoveryNode : discoveryNodes) {
            System.out.println(discoveryNode);
        }*/

        //获取集群中所有索引
        IndicesStatsResponse indicesStatsResponse = transportClient.admin().indices().prepareStats().execute().actionGet();
        Map<String, IndexStats> indices = indicesStatsResponse.getIndices();
        Collection<IndexStats> values = indices.values();
        for (IndexStats value : values) {
            System.out.println(value.getIndex());
        }
    }

    // 参数可变长
    @Test
    public void test1(){//创建索引
        CreateIndexResponse test = transportClient.admin().indices().prepareCreate("test").execute().actionGet();
        System.out.println(test.isAcknowledged());
    }
    @Test
    public void test2(){//删除索引
        DeleteIndexResponse test1 = transportClient.admin().indices().prepareDelete("test").execute().actionGet();
        System.out.println(test1.isAcknowledged());
    }
    @Test
    public void test3(){//判断索引是否存在
        IndicesExistsResponse test = transportClient.admin().indices().prepareExists("test","dangdang").execute().actionGet();
        System.out.println(test.isExists());
    }
    @Test
    public void test4() throws IOException {//创建索引,类型并指定类型的mapping`
        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder();
        xContentBuilder.startObject()
                .startObject("properties")
                .startObject("name")
                .field("type","text")
                .field("analyzer","ik_max_word")
                .endObject()
                .startObject("age")
                .field("type","integer")
                .endObject()
                .startObject("sex")
                .field("type","keyword")
                .endObject()
                .startObject("content")
                .field("type","text")
                .field("analyzer","ik_max_word")
                .endObject()
                .endObject()
                .endObject();
        //参数1: 索引   参数2: 类型   参数3: 类型对应的映射(要求json格式  用map集合比较繁琐)
        CreateIndexResponse createIndexResponse = transportClient.admin().indices().prepareCreate("dd").addMapping("bk", xContentBuilder).get();
        System.out.println(createIndexResponse.isAcknowledged());

    }
    @Test
    public void test5() throws IOException {//添加一条记录
        /**
         * 创建索引(指定生成文档id)
         */
        /*XContentBuilder xContentBuilder = XContentFactory.jsonBuilder().startObject()
                .field("name", "中国人")
                .field("age", 23)
                .field("sex", "男")
                .field("content", "他是一个中国人,这个中国人怎么样,挺好的").endObject();
        IndexResponse indexResponse = transportClient.prepareIndex("dd", "bk","1").setSource(xContentBuilder).get();
        System.out.println(indexResponse.status());*/

        /**
         * 创建索引(自动生成文档id)
         */
        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder().startObject()
                .field("name","中国人")
                .field("age",23)
                .field("sex","男")
                .field("content","他是一个中国人,这个中国人怎么样,挺好的").endObject();
        IndexResponse indexResponse = transportClient.prepareIndex("dd", "bk").setSource(xContentBuilder).get();
        System.out.println(indexResponse.status());
    }
    @Test
    public void test6() throws IOException {
        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder();
        xContentBuilder.startObject().field("name","小黑是中国人").endObject();
        UpdateResponse updateResponse = transportClient.prepareUpdate("dd", "bk", "1")
                .setDoc(xContentBuilder).get();
        System.out.println(updateResponse.status());
    }
    @Test
    public void test7()  {
       Integer i1=100,i2=100,i3=137,i4=137;
        System.out.println(i1==i2);
        System.out.println(i3==i4);
    }
}

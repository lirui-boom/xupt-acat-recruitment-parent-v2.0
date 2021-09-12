package cn.edu.xupt.acat.user.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;


@Configuration
public class ElasticSearchConfig {
    @Bean
    public ElasticsearchRestTemplate elasticsearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo("127.0.0.1:9201","127.0.0.1:9202","127.0.0.1:9203")
                .build();
        RestHighLevelClient client = RestClients.create(clientConfiguration).rest();
        return new ElasticsearchRestTemplate(client);
    }
}


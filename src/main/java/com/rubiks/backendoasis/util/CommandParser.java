package com.rubiks.backendoasis.util;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

public class CommandParser {
    private final String AND = "AND";
    private final String OR = "OR";
    private final String NOT = "NOT";

    //  a and b or c and b or D not E
    public QueryBuilder parseQuery(String query) {
        int formerIndex = 0;
        String formerType = AND; //规定为 and 可以不用管开头
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery(); // 遇到逻辑谓词，先做前面的query，然后更新formerType和index
        for (int i = 0; i + 2 < query.length(); i++) {
            if (query.substring(i, i+3).equals(AND)) {
                updateQueryBuilder(queryBuilder, formerType, query.substring(formerIndex, i));
                formerType = AND; formerIndex = i+4;
            }
            else if (query.substring(i, i+2).equals(OR)) {
                updateQueryBuilder(queryBuilder, formerType, query.substring(formerIndex, i));
                formerType = OR; formerIndex = i+3;

            }
            else if (query.substring(i, i+3).equals(NOT)) {
                updateQueryBuilder(queryBuilder, formerType, query.substring(formerIndex, i));
                formerType = NOT; formerIndex = i+4;
            }
        }

        updateQueryBuilder(queryBuilder, formerType, query.substring(formerIndex));  //处理最后一个谓词
        return queryBuilder;
    }

    private QueryBuilder updateQueryBuilder(BoolQueryBuilder queryBuilder, String formerType, String keyword) {
//        System.out.println(keyword);
        switch (formerType) {
            case AND:
                queryBuilder.must(getMatchQuery(keyword));
                break;
            case OR:
//                BoolQueryBuilder newQueryBuilder = QueryBuilders.boolQuery();
//                newQueryBuilder.should(queryBuilder);
//                newQueryBuilder.should(getMatchQuery(keyword));
//                queryBuilder = newQueryBuilder;
                queryBuilder.should(getMatchQuery(keyword));
                break;
            case NOT:
                queryBuilder.mustNot(getMatchQuery(keyword));
                break;
        }
        return queryBuilder;
    }

    private QueryBuilder getMatchQuery(String keyword) {
        QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(keyword)
                .field("authors.name", 2f)
                .field("authors.affiliation")
                .field("abstract")
                .field("title", 2f)
                .field("keywords")
                .field("publicationName");
        return queryBuilder;
    }
}

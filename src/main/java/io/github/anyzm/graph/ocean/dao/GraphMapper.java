/* Copyright (c) 2022 com.github.anyzm. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package io.github.anyzm.graph.ocean.dao;


import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.ClientServerIncompatibleException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import io.github.anyzm.graph.ocean.domain.GraphQuery;
import io.github.anyzm.graph.ocean.domain.impl.QueryResult;
import io.github.anyzm.graph.ocean.exception.NebulaException;
import com.vesoft.nebula.client.graph.exception.NotValidConnectionException;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.function.Function;

/**
 * Description  GraphMapper is used for
 *
 * @author Anyzm
 * Date  2021/7/19 - 14:16
 * @version 1.0.0
 */
public interface GraphMapper {

    /**
     * 批量保存顶点信息
     *
     * @param entities 顶点
     * @param <T> 顶点类型
     * @return 更新状态
     * @throws NebulaException nebula异常
     */
    <T> int saveVertexEntities(List<T> entities) throws NebulaException, ClientServerIncompatibleException, AuthFailedException, NotValidConnectionException, IOErrorException;


    /**
     * 批量保存边信息和顶点信息
     *
     * @param entities 实体
     * @param srcVertexEntityFunction 起点构造器
     * @param dstVertexEntityFunction 终点构造器
     * @param <S> 起点
     * @param <T> 终点
     * @param <E> 边
     * @return 更新状态码
     * @throws NebulaException nebula异常
     */
    <S, T, E> int saveEdgeEntitiesWithVertex(List<E> entities, Function<String, S> srcVertexEntityFunction,
                                             Function<String, T> dstVertexEntityFunction) throws NebulaException, ClientServerIncompatibleException, AuthFailedException, NotValidConnectionException, IOErrorException;


    /**
     * 批量保存边信息
     *
     * @param entities 边
     * @return 更新状态码
     * @throws NebulaException nebula异常
     */
    <S, T, E> int saveEdgeEntities(List<E> entities) throws NebulaException, ClientServerIncompatibleException, AuthFailedException, NotValidConnectionException, IOErrorException;


    /**
     * 批量执行更新语句
     *
     * @param space 图空间
     * @param sqlList sql列表
     * @return 更新状态码
     * @throws NebulaException nebula异常
     * @throws NotValidConnectionException 连接异常
     */
    int executeBatchUpdateSql(String space, List<String> sqlList) throws NebulaException, NotValidConnectionException, ClientServerIncompatibleException, AuthFailedException, IOErrorException;


    /**
     * 执行更新sql
     *
     * @param space 图空间
     * @param sql sql
     * @return 更新状态码
     * @throws NebulaException nebula异常
     * @throws NotValidConnectionException 连接异常
     */
    int executeUpdateSql(String space, String sql) throws NebulaException, NotValidConnectionException, IOErrorException, ClientServerIncompatibleException, AuthFailedException;


    /**
     * 执行更新sql
     *
     * @param sql sql
     * @return 更新状态码
     * @throws NebulaException nebula异常
     * @throws NotValidConnectionException 连接异常
     */
    int executeUpdateSql(String sql) throws NebulaException, NotValidConnectionException, IOErrorException, ClientServerIncompatibleException, AuthFailedException;

    /**
     * 执行查询
     *
     * @param sql sql
     * @return 查询结果
     * @throws NebulaException nebula异常
     */
    QueryResult executeQuerySql(String sql) throws NebulaException, ClientServerIncompatibleException, AuthFailedException, NotValidConnectionException, IOErrorException;


    /**
     * 执行查询
     *
     * @param space 图空间
     * @param sql sql
     * @return 查询结果
     * @throws NebulaException nebula异常
     */
    QueryResult executeQuerySql(String space, String sql) throws NebulaException, ClientServerIncompatibleException, AuthFailedException, NotValidConnectionException, IOErrorException;


    /**
     * 执行查询
     *
     * @param sql ngql
     * @param clazz 类类型
     * @param <T> 实体
     * @return 查询的实体列表
     * @throws NebulaException nebula异常
     */
    <T> List<T> executeQuerySql(String sql, Class<T> clazz) throws NebulaException, IllegalAccessException, InstantiationException, UnsupportedEncodingException, ClientServerIncompatibleException, AuthFailedException, NotValidConnectionException, IOErrorException;


    /**
     * 执行查询
     *
     * @param query 查询api
     * @return 查询结果
     * @throws NebulaException nebula异常
     */
    QueryResult executeQuery(GraphQuery query) throws NebulaException, ClientServerIncompatibleException, AuthFailedException, NotValidConnectionException, IOErrorException;


    /**
     * 指定空间执行查询
     *
     * @param space 图空间
     * @param query 查询API
     * @return 查询结果
     * @throws NebulaException nebula异常
     */
    QueryResult executeQuery(String space, GraphQuery query) throws NebulaException, ClientServerIncompatibleException, AuthFailedException, NotValidConnectionException, IOErrorException;


    /**
     * 执行查询
     *
     * @param query 查询API
     * @param clazz 类类型
     * @param <T> 顶点
     * @return 实体列表
     * @throws NebulaException nebula异常
     */
    <T> List<T> executeQuery(GraphQuery query, Class<T> clazz) throws NebulaException, IllegalAccessException, InstantiationException, UnsupportedEncodingException, ClientServerIncompatibleException, AuthFailedException, NotValidConnectionException, IOErrorException;


    /**
     * 查询边
     *
     * @param edgeClazz 边类类型
     * @param vertexIds 顶点id
     * @return 出边列表
     */
    <T> List<T> goOutEdge(Class<T> edgeClazz, String... vertexIds) throws UnsupportedEncodingException, IllegalAccessException, InstantiationException, ClientServerIncompatibleException, AuthFailedException, NotValidConnectionException, IOErrorException;

    /**
     * 查询反向边
     *
     * @param edgeClazz 边类类型
     * @param vertexIds 顶点id
     * @param <T> 入边类型
     * @return 入边列表
     */
    <T> List<T> goReverseEdge(Class<T> edgeClazz, String... vertexIds) throws UnsupportedEncodingException, IllegalAccessException, InstantiationException, ClientServerIncompatibleException, AuthFailedException, NotValidConnectionException, IOErrorException;

    /**
     * 查询tag
     *
     * @param vertexClazz 顶点类型
     * @param vertexIds 顶点id
     * @return 顶点列表
     */
    <T,S> List<T> fetchVertexTag(Class<T> vertexClazz, S... vertexIds) throws UnsupportedEncodingException, IllegalAccessException, InstantiationException, ClientServerIncompatibleException, AuthFailedException, NotValidConnectionException, IOErrorException;

}

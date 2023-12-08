/* Copyright (c) 2022 com.github.anyzm. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package io.github.anyzm.graph.ocean.dao;


import io.github.anyzm.graph.ocean.domain.impl.GraphVertexEntity;
import io.github.anyzm.graph.ocean.exception.NebulaException;

import java.util.List;

/**
 * Description  GraphUpdateVertexEngineFactory is used for
 *
 * @author Anyzm
 * Date  2021/7/19 - 10:31
 * @version 1.0.0
 */
public interface GraphUpdateVertexEngineFactory {

    /**
     *
     * @param graphVertexEntities 顶点实体
     * @param <T> 顶点
     * @return 顶点更新引擎
     * @throws NebulaException nebula异常
     */
    <T> VertexUpdateEngine build(List<GraphVertexEntity<T>> graphVertexEntities) throws NebulaException;

}

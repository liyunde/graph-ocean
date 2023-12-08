/* Copyright (c) 2022 com.github.anyzm. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package io.github.anyzm.graph.ocean.domain.impl;

import com.google.common.base.Objects;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

/**
 * @author Anyzm
 * date 2019/2/14
 */
@Getter
@ToString
public class GraphVertexEntity<T> extends GraphPropertyEntity {

    /**
     * 顶点 id
     */
    private final T id;
    /**
     * 图顶点类型
     */
    private GraphVertexType<T> graphVertexType;

    public GraphVertexEntity(GraphVertexType<T> graphVertexType, T id, Map<String, Object> props) {
        super(props);
        if (props == null) {
            throw new IllegalArgumentException("vertexTag or props not empty");
        }
        this.graphVertexType = graphVertexType;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GraphVertexEntity that = (GraphVertexEntity) o;
        return id == that.id &&
                Objects.equal(this.graphVertexType, that.graphVertexType);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, graphVertexType);
    }
}

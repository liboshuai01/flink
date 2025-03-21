/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.runtime.asyncprocessing.declare;

import org.apache.flink.api.common.typeutils.TypeSerializer;

import javax.annotation.Nullable;

import java.util.function.Supplier;

/** A variable declared in async state processing. The value could be persisted in checkpoint. */
public class DeclaredVariable<T> extends ContextVariable<T> {

    final TypeSerializer<T> typeSerializer;

    final String name;

    DeclaredVariable(
            DeclarationManager manager,
            int ordinal,
            TypeSerializer<T> typeSerializer,
            String name,
            @Nullable Supplier<T> initializer) {
        super(manager, ordinal, initializer);
        this.typeSerializer = typeSerializer;
        this.name = name;
    }
}
